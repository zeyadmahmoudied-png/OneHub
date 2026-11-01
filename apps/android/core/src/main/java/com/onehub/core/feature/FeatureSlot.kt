package com.onehub.core.feature

/**
 * Descriptor for an embeddable app inside OneHub.
 * Phase 2 registers slots only — no app source is claimed integrated.
 *
 * Current product scope:
 * 1. My Plan
 * 2. PhotoCleaner
 * 3. Dar Al Bayan
 */
data class FeatureSlot(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val status: FeatureIntegrationStatus,
    val summaryAr: String,
)

enum class FeatureIntegrationStatus {
    PENDING_SOURCE,
    INTEGRATED,
    PARTIAL,
}

object KnownFeatureSlots {
    val all: List<FeatureSlot> = listOf(
        FeatureSlot(
            id = "myplan",
            titleAr = "خطتي",
            titleEn = "My Plan",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "سورس v2.23 (Java/Views + Supabase) مُستلم كأرشيف. لم يُدمج بعد.",
        ),
        FeatureSlot(
            id = "photocleaner",
            titleAr = "منظف الصور",
            titleEn = "PhotoCleaner",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "سورس GitHub متاح. مرشّح الدمج الأول بعد استقرار المرحلة 2.",
        ),
        FeatureSlot(
            id = "daralbayan",
            titleAr = "دار البيان",
            titleEn = "Dar Al Bayan",
            status = FeatureIntegrationStatus.PENDING_SOURCE,
            summaryAr = "أرشيف UI (React + Capacitor) مُستلم. لم يُدمج بعد.",
        ),
    )
}
