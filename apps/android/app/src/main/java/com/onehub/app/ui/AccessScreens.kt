package com.onehub.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.onehub.accesscontrol.model.AccessControlSecurityNotes
import com.onehub.accesscontrol.model.Permission
import com.onehub.accesscontrol.repo.AccessRepository
import kotlinx.coroutines.launch

@Composable
fun OwnerSetupScreen(
    repo: AccessRepository,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("إعداد المالك", style = MaterialTheme.typography.headlineMedium)
        Text(
            AccessControlSecurityNotes.LOCAL_ONLY,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("الاسم") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور (8+)") },
            modifier = Modifier.fillMaxWidth(),
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        info?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        Button(
            onClick = {
                scope.launch {
                    error = null
                    repo.provisionOwner(name, password, System.currentTimeMillis())
                        .onSuccess {
                            info = "تم إنشاء المالك محليًا"
                            onDone()
                        }
                        .onFailure { error = it.message }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = password.length >= 8,
        ) { Text("إنشاء المالك") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("رجوع") }
    }
}

@Composable
fun OwnerLoginScreen(
    repo: AccessRepository,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("دخول المالك", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            modifier = Modifier.fillMaxWidth(),
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                scope.launch {
                    error = null
                    repo.loginOwner(password, System.currentTimeMillis())
                        .onSuccess { onDone() }
                        .onFailure { error = it.message }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("دخول") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("رجوع") }
    }
}

@Composable
fun RedeemCodeScreen(
    repo: AccessRepository,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var code by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("تفعيل كود صلاحية", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("اسم المستخدم المحلي") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("الكود") },
            modifier = Modifier.fillMaxWidth(),
        )
        message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(
            onClick = {
                scope.launch {
                    error = null
                    message = null
                    val user = repo.createUser(userName)
                    repo.switchUser(user.id)
                    repo.activateCode(code, System.currentTimeMillis())
                        .onSuccess {
                            message = "تم التفعيل. الصلاحيات النشطة: ${it.grants.keys.joinToString()}"
                        }
                        .onFailure { error = it.message }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("تفعيل") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("رجوع") }
    }
}

@Composable
fun OwnerAdminScreen(
    repo: AccessRepository,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var lastPlain by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("لوحة المالك", style = MaterialTheme.typography.headlineMedium)
        Text(
            "إصدار أكواد بصلاحيات محددة ومدة استخدام. لا يُمنح ADMIN عبر كود.",
            style = MaterialTheme.typography.bodySmall,
        )
        Button(
            onClick = {
                scope.launch {
                    error = null
                    val now = System.currentTimeMillis()
                    repo.issueCode(
                        permissionIds = listOf(
                            Permission.HUB_BASIC.id,
                            Permission.FEATURE_PHOTOCLEANER.id,
                            Permission.FEATURE_MYPLAN.id,
                            Permission.FEATURE_DARALBAYAN.id,
                        ),
                        validFromEpochMs = now,
                        validToEpochMs = now + 7L * 24 * 60 * 60 * 1000,
                        maxUses = 3,
                        label = "trial-7d",
                        nowEpochMs = now,
                    ).onSuccess {
                        lastPlain = it.plaintextOnce
                        status = "أُنشئ الكود (يُعرض مرة واحدة). uses≤3 · 7 أيام"
                    }.onFailure { error = it.message }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("إصدار كود تجريبي (التطبيقات الثلاثة + أساسي)") }

        lastPlain?.let {
            Text("الكود (انسخه الآن): $it", color = MaterialTheme.colorScheme.primary)
        }
        status.takeIf { it.isNotBlank() }?.let { Text(it) }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                scope.launch {
                    val codes = repo.listCodes()
                    status = codes.joinToString("\n") { c ->
                        "${c.label} · revoked=${c.revoked} · used=${c.usedCount}/${c.maxUses ?: "∞"}"
                    }.ifBlank { "لا أكواد" }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("عرض الأكواد") }

        Button(
            onClick = {
                scope.launch {
                    val codes = repo.listCodes()
                    val first = codes.firstOrNull { !it.revoked }
                    if (first == null) {
                        error = "لا يوجد كود نشط"
                    } else {
                        repo.revokeCode(first.id, System.currentTimeMillis())
                            .onSuccess { status = "أُلغي ${first.id}" }
                            .onFailure { error = it.message }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("إلغاء آخر كود نشط") }

        Button(
            onClick = {
                scope.launch {
                    val codes = repo.listCodes()
                    val first = codes.firstOrNull()
                    if (first == null) {
                        error = "لا أكواد"
                    } else {
                        val until = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
                        repo.renewCode(first.id, until, System.currentTimeMillis())
                            .onSuccess { status = "تجديد ${first.id} لـ 30 يومًا" }
                            .onFailure { error = it.message }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("تجديد أول كود +30 يوم") }

        Button(
            onClick = {
                scope.launch {
                    val events = repo.listAudit(20)
                    status = events.joinToString("\n") { "${it.action}: ${it.detail}" }
                        .ifBlank { "لا سجل" }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("سجل التدقيق") }

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("رجوع") }
    }
}
