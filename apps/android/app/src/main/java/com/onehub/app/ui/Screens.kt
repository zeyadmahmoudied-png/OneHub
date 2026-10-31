package com.onehub.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.onehub.app.data.ApiClient
import com.onehub.app.data.AppItem
import com.onehub.app.data.AuthRequest
import com.onehub.app.data.AuthUser
import com.onehub.app.data.SessionStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(onApps: () -> Unit, onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("OneHub", style = MaterialTheme.typography.headlineLarge)
        Text(
            "منصة موحدة لإدارة التطبيقات المستقلة",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onApps, modifier = Modifier.fillMaxWidth()) { Text("التطبيقات") }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onLogin, modifier = Modifier.fillMaxWidth()) { Text("دخول") }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onRegister, modifier = Modifier.fillMaxWidth()) { Text("تسجيل") }
    }
}

@Composable
fun LoginScreen(onSuccess: () -> Unit, onRegister: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Text("تسجيل الدخول", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("البريد") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        error?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(msg, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                loading = true
                error = null
                scope.launch {
                    try {
                        val res = ApiClient.api.login(AuthRequest(email, password))
                        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                        val json = moshi.adapter(AuthUser::class.java).toJson(res.user)
                        SessionStore(context).saveSession(res.accessToken, res.refreshToken, json)
                        onSuccess()
                    } catch (e: Exception) {
                        error = e.message ?: "فشل الدخول"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading && email.isNotBlank() && password.length >= 8,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (loading) "..." else "دخول")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onRegister, modifier = Modifier.fillMaxWidth()) {
            Text("إنشاء حساب")
        }
    }
}

@Composable
fun RegisterScreen(onSuccess: () -> Unit, onLogin: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Text("إنشاء حساب", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("الاسم") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("البريد") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            modifier = Modifier.fillMaxWidth(),
        )
        error?.let { msg ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(msg, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                loading = true
                error = null
                scope.launch {
                    try {
                        val res = ApiClient.api.register(
                            AuthRequest(email, password, displayName = name, preferredLocale = "ar"),
                        )
                        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                        val json = moshi.adapter(AuthUser::class.java).toJson(res.user)
                        SessionStore(context).saveSession(res.accessToken, res.refreshToken, json)
                        onSuccess()
                    } catch (e: Exception) {
                        error = e.message ?: "فشل التسجيل"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading && name.length >= 2 && email.isNotBlank() && password.length >= 8,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (loading) "..." else "تسجيل")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onLogin, modifier = Modifier.fillMaxWidth()) { Text("لدي حساب") }
    }
}

@Composable
fun AppsScreen(onAppClick: (String) -> Unit) {
    var items by remember { mutableStateOf<List<AppItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            items = ApiClient.api.listApps().items
        } catch (e: Exception) {
            error = e.message ?: "تعذر التحميل"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text("التطبيقات", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        when {
            loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            items.isEmpty() -> Text("لا توجد تطبيقات. شغّل seed من الـ Backend.")
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items, key = { it.appId }) { app ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAppClick(app.appId) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(app.displayNameAr, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    app.appId,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(app.status, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppDetailScreen(appId: String, onBack: () -> Unit) {
    var app by remember { mutableStateOf<AppItem?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(appId) {
        loading = true
        try {
            app = ApiClient.api.getApp(appId)
        } catch (e: Exception) {
            error = e.message ?: "غير موجود"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        OutlinedButton(onClick = onBack) { Text("رجوع") }
        Spacer(modifier = Modifier.height(12.dp))
        when {
            loading -> CircularProgressIndicator()
            error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
            app != null -> {
                Text(app!!.displayNameAr, style = MaterialTheme.typography.headlineSmall)
                Text(app!!.displayNameEn, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(app!!.descriptionAr ?: app!!.descriptionEn ?: "—")
                Spacer(modifier = Modifier.height(8.dp))
                Text("الحالة: ${app!!.status}")
                Text("المحول: ${app!!.adapterType}")
            }
        }
    }
}
