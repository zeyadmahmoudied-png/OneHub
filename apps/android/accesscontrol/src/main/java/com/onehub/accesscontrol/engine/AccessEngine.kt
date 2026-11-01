package com.onehub.accesscontrol.engine

import com.onehub.accesscontrol.model.AccountStatus
import com.onehub.accesscontrol.model.AuditEvent
import com.onehub.accesscontrol.model.Grant
import com.onehub.accesscontrol.model.IssuedCode
import com.onehub.accesscontrol.model.LocalUser
import com.onehub.accesscontrol.model.Permission
import com.onehub.accesscontrol.model.PermissionCode
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

/**
 * Stateless rules engine — unit-testable without Android framework.
 */
class AccessEngine(
    private val random: SecureRandom = SecureRandom(),
    private val idFactory: () -> String = { UUID.randomUUID().toString() },
) {
    fun hashCodeValue(plaintext: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(plaintext.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, saltHex: String): String {
        val salt = saltHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        digest.update(password.toByteArray(Charsets.UTF_8))
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun newSalt(): String {
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    fun generatePlaintextCode(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val sb = StringBuilder("OH-")
        repeat(4) { sb.append(alphabet[random.nextInt(alphabet.length)]) }
        sb.append('-')
        repeat(4) { sb.append(alphabet[random.nextInt(alphabet.length)]) }
        return sb.toString()
    }

    fun issueCodeWithAudit(
        ownerId: String,
        permissionIds: List<String>,
        validFromEpochMs: Long,
        validToEpochMs: Long?,
        maxUses: Int?,
        label: String,
        nowEpochMs: Long,
    ): Result<Pair<IssuedCode, AuditEvent>> {
        if (permissionIds.isEmpty()) {
            return Result.failure(IllegalArgumentException("permissions required"))
        }
        if (permissionIds.any { it == Permission.ADMIN_TOOLS.id }) {
            return Result.failure(
                SecurityException("ADMIN_TOOLS cannot be granted via a permission code"),
            )
        }
        if (validToEpochMs != null && validToEpochMs <= validFromEpochMs) {
            return Result.failure(IllegalArgumentException("validTo must be after validFrom"))
        }
        if (maxUses != null && maxUses <= 0) {
            return Result.failure(IllegalArgumentException("maxUses must be positive"))
        }

        val plain = generatePlaintextCode()
        val code = PermissionCode(
            id = idFactory(),
            codeHash = hashCodeValue(plain),
            permissionIds = permissionIds.distinct(),
            validFromEpochMs = validFromEpochMs,
            validToEpochMs = validToEpochMs,
            maxUses = maxUses,
            usedCount = 0,
            revoked = false,
            boundUserId = null,
            label = label.ifBlank { "code" },
        )
        val issued = IssuedCode(code = code, plaintextOnce = plain)
        val audit = AuditEvent(
            id = idFactory(),
            atEpochMs = nowEpochMs,
            action = "code.issued",
            actorUserId = ownerId,
            detail = "codeId=${code.id} perms=${code.permissionIds.joinToString()}",
        )
        return Result.success(issued to audit)
    }

    fun activateCode(
        plaintext: String,
        codes: List<PermissionCode>,
        user: LocalUser,
        nowEpochMs: Long,
    ): Result<ActivationResult> {
        if (user.isOwner) {
            return Result.failure(IllegalStateException("Owner does not redeem codes"))
        }
        val hash = hashCodeValue(plaintext.trim())
        val code = codes.find { it.codeHash == hash }
            ?: return Result.failure(IllegalArgumentException("Invalid code"))
        if (code.revoked) return Result.failure(IllegalStateException("Code revoked"))
        if (nowEpochMs < code.validFromEpochMs) {
            return Result.failure(IllegalStateException("Code not yet valid"))
        }
        if (code.validToEpochMs != null && nowEpochMs > code.validToEpochMs) {
            return Result.failure(IllegalStateException("Code expired"))
        }
        if (code.maxUses != null && code.usedCount >= code.maxUses) {
            return Result.failure(IllegalStateException("Code usage limit reached"))
        }
        if (code.boundUserId != null && code.boundUserId != user.id) {
            return Result.failure(IllegalStateException("Code bound to another user"))
        }

        val newGrants = user.grants.toMutableMap()
        for (pid in code.permissionIds) {
            if (pid == Permission.ADMIN_TOOLS.id) continue
            newGrants[pid] = Grant(
                permissionId = pid,
                validFromEpochMs = maxOf(code.validFromEpochMs, nowEpochMs),
                validToEpochMs = code.validToEpochMs,
                sourceCodeId = code.id,
            )
        }
        val updatedUser = user.copy(grants = newGrants)
        val updatedCode = code.copy(
            usedCount = code.usedCount + 1,
            boundUserId = code.boundUserId ?: user.id,
        )
        val audit = AuditEvent(
            id = idFactory(),
            atEpochMs = nowEpochMs,
            action = "code.activated",
            actorUserId = user.id,
            detail = "codeId=${code.id}",
        )
        return Result.success(ActivationResult(updatedUser, updatedCode, audit))
    }

    fun revokeCode(code: PermissionCode, ownerId: String, nowEpochMs: Long): Pair<PermissionCode, AuditEvent> {
        val updated = code.copy(revoked = true)
        val audit = AuditEvent(
            id = idFactory(),
            atEpochMs = nowEpochMs,
            action = "code.revoked",
            actorUserId = ownerId,
            detail = "codeId=${code.id}",
        )
        return updated to audit
    }

    fun renewCode(
        code: PermissionCode,
        newValidToEpochMs: Long,
        ownerId: String,
        nowEpochMs: Long,
    ): Result<Pair<PermissionCode, AuditEvent>> {
        if (code.revoked) return Result.failure(IllegalStateException("Cannot renew revoked code"))
        if (newValidToEpochMs <= nowEpochMs) {
            return Result.failure(IllegalArgumentException("newValidTo must be in the future"))
        }
        val updated = code.copy(validToEpochMs = newValidToEpochMs, revoked = false)
        val audit = AuditEvent(
            id = idFactory(),
            atEpochMs = nowEpochMs,
            action = "code.renewed",
            actorUserId = ownerId,
            detail = "codeId=${code.id} until=$newValidToEpochMs",
        )
        return Result.success(updated to audit)
    }

    fun hasPermission(user: LocalUser, permission: Permission, nowEpochMs: Long): Boolean {
        if (user.isOwner) return true
        val grant = user.grants[permission.id] ?: return false
        if (nowEpochMs < grant.validFromEpochMs) return false
        if (grant.validToEpochMs != null && nowEpochMs > grant.validToEpochMs) return false
        return true
    }

    fun accountStatus(user: LocalUser, nowEpochMs: Long): AccountStatus {
        if (user.isOwner) return AccountStatus.ACTIVE
        if (user.grants.isEmpty()) return AccountStatus.NOT_PROVISIONED
        val anyActive = user.grants.values.any { g ->
            nowEpochMs >= g.validFromEpochMs &&
                (g.validToEpochMs == null || nowEpochMs <= g.validToEpochMs)
        }
        return if (anyActive) AccountStatus.ACTIVE else AccountStatus.EXPIRED
    }

    fun stripExpiredGrants(user: LocalUser, nowEpochMs: Long): LocalUser {
        if (user.isOwner) return user
        val kept = user.grants.filterValues { g ->
            g.validToEpochMs == null || nowEpochMs <= g.validToEpochMs
        }
        return user.copy(grants = kept)
    }

    data class ActivationResult(
        val user: LocalUser,
        val code: PermissionCode,
        val audit: AuditEvent,
    )
}
