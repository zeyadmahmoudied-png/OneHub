package com.onehub.core.feature

/**
 * Descriptor for an embeddable app inside OneHub.
 * Phase 2 registers slots only — no app source is claimed integrated.
 *
 * Current product scope (owner decision 2026-11-01):
 * 1. My Plan
 * 2. PhotoCleaner
 * 3. Dar Al Bayan
 * Other apps (ZOZ, YouTube CC, …) are deferred and not listed here.
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
            id = "myplan",
            titleAr = "خطتي",
            titleEn = "My Plan",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "ضمن النطاق. السورس يُراجع من مكتبة الملفات عند توفره في بيئة الدمج؛ لم يُدمج بعد.",
        ),
        FeatureSlot(
            id = "photocleaner",
            titleAr = "منظف الصور",
            titleEn = "PhotoCleaner",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "سورس متاح على GitHub. مرشّح الدمج الأول بعد اكتمال Integration Foundation.",
        ),
        FeatureSlot(
            id = "daralbayan",
            titleAr = "دار البيان",
            titleEn = "Dar Al Bayan",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "ضمن النطاق. السورس يُراجع من مكتبة الملفات عند توفره؛ لم يُدمج بعد.",
        ),
    )
}
