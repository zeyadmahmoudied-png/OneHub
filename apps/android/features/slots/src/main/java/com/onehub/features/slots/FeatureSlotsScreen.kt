package com.onehub.features.slots

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.onehub.core.feature.FeatureIntegrationStatus
import com.onehub.core.feature.FeatureSlot
import com.onehub.core.feature.KnownFeatureSlots

@Composable
fun FeatureSlotsScreen(
    onOpen: (String) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text("التطبيقات المدمجة", style = MaterialTheme.typography.headlineMedium)
        Text(
            "لا يُعرض تطبيق على أنه مكتمل قبل دمج سورسه الحقيقي.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(KnownFeatureSlots.all, key = { it.id }) { slot ->
                FeatureSlotCard(slot, onClick = { onOpen(slot.id) })
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("رجوع")
        }
    }
}

@Composable
fun FeatureSlotDetailScreen(
    featureId: String,
    onBack: () -> Unit,
) {
    val slot = KnownFeatureSlots.all.find { it.id == featureId }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        OutlinedButton(onClick = onBack) { Text("رجوع إلى OneHub") }
        Spacer(modifier = Modifier.height(16.dp))
        if (slot == null) {
            Text("وحدة غير معروفة")
            return
        }
        Text(slot.titleAr, style = MaterialTheme.typography.headlineSmall)
        Text(slot.titleEn, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Text(statusLabel(slot.status), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(slot.summaryAr)
        if (slot.status != FeatureIntegrationStatus.INTEGRATED) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "الواجهة التفاعلية للتطبيق ستظهر هنا بعد مرحلة الدمج المخصصة.",
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun FeatureSlotCard(slot: FeatureSlot, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(slot.titleAr, style = MaterialTheme.typography.titleMedium)
            Text(statusLabel(slot.status), style = MaterialTheme.typography.labelMedium)
            Text(slot.summaryAr, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun statusLabel(status: FeatureIntegrationStatus): String = when (status) {
    FeatureIntegrationStatus.PENDING_SOURCE -> "بانتظار الدمج"
    FeatureIntegrationStatus.PARTIAL -> "جزئي"
    FeatureIntegrationStatus.INTEGRATED -> "مدمج"
}
