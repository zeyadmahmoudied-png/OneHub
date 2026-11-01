package com.onehub.accesscontrol.repo

import com.onehub.accesscontrol.engine.AccessEngine
import com.onehub.accesscontrol.model.AuditEvent
import com.onehub.accesscontrol.model.IssuedCode
import com.onehub.accesscontrol.model.LocalUser
import com.onehub.accesscontrol.model.Permission
import com.onehub.accesscontrol.model.PermissionCode
import com.onehub.accesscontrol.model.SessionState
import java.util.UUID

/**
 * Abstraction so a future Backend-backed implementation can replace local storage
 * without changing feature modules.
 */
interface AccessRepository {
    suspend fun session(): SessionState
    suspend fun currentUser(): LocalUser?
    suspend fun listCodes(): List<PermissionCode>
    suspend fun listAudit(limit: Int = 100): List<AuditEvent>

    suspend fun provisionOwner(displayName: String, password: String, nowEpochMs: Long): Result<LocalUser>
    suspend fun loginOwner(password: String, nowEpochMs: Long): Result<LocalUser>
    suspend fun createUser(displayName: String): LocalUser
    suspend fun switchUser(userId: String): Result<LocalUser>
    suspend fun logout()

    suspend fun issueCode(
        permissionIds: List<String>,
        validFromEpochMs: Long,
        validToEpochMs: Long?,
        maxUses: Int?,
        label: String,
        nowEpochMs: Long,
    ): Result<IssuedCode>

    suspend fun activateCode(plaintext: String, nowEpochMs: Long): Result<LocalUser>
    suspend fun revokeCode(codeId: String, nowEpochMs: Long): Result<Unit>
    suspend fun renewCode(codeId: String, newValidToEpochMs: Long, nowEpochMs: Long): Result<Unit>

    suspend fun require(permission: Permission, nowEpochMs: Long): Boolean
}

/**
 * In-memory implementation for unit tests and early shell wiring.
 * Production UI uses the same engine with DataStore persistence in a follow-up file.
 */
class InMemoryAccessRepository(
    private val engine: AccessEngine = AccessEngine(),
) : AccessRepository {
    private var owner: LocalUser? = null
    private var ownerSalt: String? = null
    private var ownerPasswordHash: String? = null
    private val users = linkedMapOf<String, LocalUser>()
    private val codes = linkedMapOf<String, PermissionCode>()
    private val audit = mutableListOf<AuditEvent>()
    private var currentUserId: String? = null

    override suspend fun session() = SessionState(
        currentUserId = currentUserId,
        ownerProvisioned = owner != null,
    )

    override suspend fun currentUser(): LocalUser? {
        val id = currentUserId ?: return null
        val u = users[id] ?: return null
        return engine.stripExpiredGrants(u, System.currentTimeMillis()).also {
            users[id] = it
        }
    }

    override suspend fun listCodes() = codes.values.toList()
    override suspend fun listAudit(limit: Int) = audit.takeLast(limit).reversed()

    override suspend fun provisionOwner(displayName: String, password: String, nowEpochMs: Long): Result<LocalUser> {
        if (owner != null) return Result.failure(IllegalStateException("Owner already provisioned"))
        if (password.length < 8) return Result.failure(IllegalArgumentException("Password min 8 chars"))
        val salt = engine.newSalt()
        val hash = engine.hashPassword(password, salt)
        val user = LocalUser(
            id = UUID.randomUUID().toString(),
            displayName = displayName.ifBlank { "Owner" },
            isOwner = true,
            grants = emptyMap(),
        )
        owner = user
        ownerSalt = salt
        ownerPasswordHash = hash
        users[user.id] = user
        currentUserId = user.id
        audit += AuditEvent(UUID.randomUUID().toString(), nowEpochMs, "owner.provisioned", user.id, "local")
        return Result.success(user)
    }

    override suspend fun loginOwner(password: String, nowEpochMs: Long): Result<LocalUser> {
        val o = owner ?: return Result.failure(IllegalStateException("No owner"))
        val salt = ownerSalt ?: return Result.failure(IllegalStateException("Corrupt owner state"))
        val expected = ownerPasswordHash ?: return Result.failure(IllegalStateException("Corrupt owner state"))
        if (engine.hashPassword(password, salt) != expected) {
            return Result.failure(SecurityException("Invalid password"))
        }
        currentUserId = o.id
        audit += AuditEvent(UUID.randomUUID().toString(), nowEpochMs, "owner.login", o.id, "ok")
        return Result.success(o)
    }

    override suspend fun createUser(displayName: String): LocalUser {
        val user = LocalUser(
            id = UUID.randomUUID().toString(),
            displayName = displayName.ifBlank { "User" },
            isOwner = false,
            grants = emptyMap(),
        )
        users[user.id] = user
        return user
    }

    override suspend fun switchUser(userId: String): Result<LocalUser> {
        val u = users[userId] ?: return Result.failure(IllegalArgumentException("Unknown user"))
        currentUserId = userId
        return Result.success(u)
    }

    override suspend fun logout() {
        currentUserId = null
    }

    override suspend fun issueCode(
        permissionIds: List<String>,
        validFromEpochMs: Long,
        validToEpochMs: Long?,
        maxUses: Int?,
        label: String,
        nowEpochMs: Long,
    ): Result<IssuedCode> {
        val o = owner ?: return Result.failure(IllegalStateException("No owner"))
        if (currentUserId != o.id) return Result.failure(SecurityException("Owner only"))
        val pair = engine.issueCodeWithAudit(
            o.id, permissionIds, validFromEpochMs, validToEpochMs, maxUses, label, nowEpochMs,
        ).getOrElse { return Result.failure(it) }
        codes[pair.first.code.id] = pair.first.code
        audit += pair.second
        return Result.success(pair.first)
    }

    override suspend fun activateCode(plaintext: String, nowEpochMs: Long): Result<LocalUser> {
        val uid = currentUserId ?: return Result.failure(IllegalStateException("Not logged in"))
        val user = users[uid] ?: return Result.failure(IllegalStateException("Unknown user"))
        val result = engine.activateCode(plaintext, codes.values.toList(), user, nowEpochMs)
            .getOrElse { return Result.failure(it) }
        users[uid] = result.user
        codes[result.code.id] = result.code
        audit += result.audit
        return Result.success(result.user)
    }

    override suspend fun revokeCode(codeId: String, nowEpochMs: Long): Result<Unit> {
        val o = owner ?: return Result.failure(IllegalStateException("No owner"))
        if (currentUserId != o.id) return Result.failure(SecurityException("Owner only"))
        val code = codes[codeId] ?: return Result.failure(IllegalArgumentException("Unknown code"))
        val (updated, event) = engine.revokeCode(code, o.id, nowEpochMs)
        codes[codeId] = updated
        audit += event
        return Result.success(Unit)
    }

    override suspend fun renewCode(codeId: String, newValidToEpochMs: Long, nowEpochMs: Long): Result<Unit> {
        val o = owner ?: return Result.failure(IllegalStateException("No owner"))
        if (currentUserId != o.id) return Result.failure(SecurityException("Owner only"))
        val code = codes[codeId] ?: return Result.failure(IllegalArgumentException("Unknown code"))
        val (updated, event) = engine.renewCode(code, newValidToEpochMs, o.id, nowEpochMs)
            .getOrElse { return Result.failure(it) }
        codes[codeId] = updated
        audit += event
        return Result.success(Unit)
    }

    override suspend fun require(permission: Permission, nowEpochMs: Long): Boolean {
        val u = currentUser() ?: return false
        return engine.hasPermission(u, permission, nowEpochMs)
    }
}
