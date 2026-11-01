package com.onehub.core.feature

/**
 * Descriptor for an embeddable app inside OneHub.
 * Phase 2 only registers slots; Phase 3+ attach real Compose entry points.
 */
data class FeatureSlot(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val status: FeatureIntegrationStatus,
    val summaryAr: String,
)

enum class FeatureIntegrationStatus {
    /** Real module not integrated yet — UI must not claim functionality. */
    PENDING_SOURCE,

    /** Module present, core flows working. */
    INTEGRATED,

    /** Partial integration. */
    PARTIAL,
}

object KnownFeatureSlots {
    val all: List<FeatureSlot> = listOf(
        FeatureSlot(
            id = "photocleaner",
            titleAr = "منظف الصور",
            titleEn = "PhotoCleaner",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "سيُدمج في المرحلة 3 من سورس PhotoCleaner المتاح.",
        ),
        FeatureSlot(
            id = "zozcreator",
            titleAr = "ZOZ Creator",
            titleEn = "ZOZ Creator",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "سورس متاح (خاص). الدمج بعد استقرار الأساس.",
        ),
        FeatureSlot(
            id = "youtubecc",
            titleAr = "YouTube Control Center",
            titleEn = "YouTube Control Center",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "سورس متاح. يحتاج مواءمة Compose ومفتاح API.",
        ),
        FeatureSlot(
            id = "myplan",
            titleAr = "خطتي",
            titleEn = "My Plan",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "السورس غير متاح بعد — بانتظار المستودع.",
        ),
        FeatureSlot(
            id = "daralbayan",
            titleAr = "دار البيان",
            titleEn = "Dar Al Bayan",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "السورس غير متاح بعد — بانتظار الرابط.",
        ),
    )
}
