package com.onehub.app.data

data class AppsListResponse(
    val items: List<AppItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val limit: Int = 50,
    val totalPages: Int = 1,
)

data class AppItem(
    val id: String? = null,
    val appId: String,
    val displayNameEn: String,
    val displayNameAr: String,
    val descriptionEn: String? = null,
    val descriptionAr: String? = null,
    val packageName: String? = null,
    val status: String = "planned",
    val adapterType: String = "intent",
    val currentVersion: String? = null,
    val healthScore: Int = 0,
)

data class AuthRequest(
    val email: String,
    val password: String,
    val displayName: String? = null,
    val preferredLocale: String? = null,
)

data class AuthUser(
    val id: String,
    val email: String,
    val displayName: String,
    val role: String,
    val preferredLocale: String,
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val user: AuthUser,
)
