package com.onehub.accesscontrol.model

/**
 * Capability-based permissions — not tied only to app package names.
 * Backend can map the same string ids later.
 */
enum class Permission(
    val id: String,
    val titleAr: String,
) {
    HUB_BASIC("hub.basic", "استخدام OneHub الأساسي"),
    WORKSPACE("hub.workspace", "الوصول إلى Workspace"),
    ADMIN_TOOLS("hub.admin", "أدوات الإدارة"),
    DEVELOPER_TOOLS("hub.developer", "أدوات المطور"),
    AUTOMATION("hub.automation", "الأتمتة"),
    ADVANCED_FEATURES("hub.advanced", "ميزات متقدمة"),
    FEATURE_PHOTOCLEANER("feature.photocleaner", "استخدام منظف الصور"),
    FEATURE_ZOZ("feature.zozcreator", "استخدام ZOZ Creator"),
    FEATURE_YOUTUBECC("feature.youtubecc", "استخدام YouTube Control Center"),
    FEATURE_MYPLAN("feature.myplan", "استخدام خطتي"),
    FEATURE_DARALBAYAN("feature.daralbayan", "استخدام دار البيان");

    companion object {
        fun fromId(id: String): Permission? = entries.find { it.id == id }

        /** Owner-only; never grantable via a normal permission code. */
        val OWNER_EXCLUSIVE: Set<Permission> = setOf(ADMIN_TOOLS)
    }
}

enum class AccountStatus {
    ACTIVE,
    EXPIRED,
    REVOKED,
    NOT_PROVISIONED,
}

data class LocalUser(
    val id: String,
    val displayName: String,
    val isOwner: Boolean,
    /** Permission id → grant metadata */
    val grants: Map<String, Grant>,
)

data class Grant(
    val permissionId: String,
    val validFromEpochMs: Long,
    val validToEpochMs: Long?,
    val sourceCodeId: String?,
)

data class PermissionCode(
    val id: String,
    /** SHA-256 hex of the plaintext code — never store plaintext after issue. */
    val codeHash: String,
    val permissionIds: List<String>,
    val validFromEpochMs: Long,
    val validToEpochMs: Long?,
    val maxUses: Int?,
    val usedCount: Int,
    val revoked: Boolean,
    val boundUserId: String?,
    val label: String,
)

data class IssuedCode(
    val code: PermissionCode,
    /** Shown once at creation — not persisted. */
    val plaintextOnce: String,
)

data class AuditEvent(
    val id: String,
    val atEpochMs: Long,
    val action: String,
    val actorUserId: String?,
    val detail: String,
)

data class SessionState(
    val currentUserId: String?,
    val ownerProvisioned: Boolean,
)

/** Documented local security boundary. */
object AccessControlSecurityNotes {
    const val LOCAL_ONLY =
        "Local Access Control is for controlled devices and demos. " +
            "A user who can modify the APK or device storage can bypass it. " +
            "Server-side verification is required for real enforcement."
}
