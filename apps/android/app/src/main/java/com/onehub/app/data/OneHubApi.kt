package com.onehub.app.data

import com.onehub.app.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class RefreshRequest(val refreshToken: String)

data class LogoutRequest(val refreshToken: String)

interface OneHubApi {
    @GET("apps")
    suspend fun listApps(): AppsListResponse

    @GET("apps/{appId}")
    suspend fun getApp(@Path("appId") appId: String): AppItem

    @POST("auth/login")
    suspend fun login(@Body body: AuthRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body body: AuthRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequest): Map<String, Boolean>

    @GET("auth/me")
    suspend fun me(@Header("Authorization") bearer: String): AuthUser

    @GET("health")
    suspend fun health(): Map<String, Any?>
}

object ApiClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            },
        )
        .build()

    /** Empty API_BASE_URL means release was built without a server — surface that clearly. */
    val baseUrlConfigured: Boolean
        get() = BuildConfig.API_BASE_URL.isNotBlank()

    val baseUrl: String
        get() {
            val raw = BuildConfig.API_BASE_URL.trim()
            if (raw.isEmpty()) return ""
            return if (raw.endsWith("/")) raw else "$raw/"
        }

    val api: OneHubApi by lazy {
        require(baseUrlConfigured) {
            "API_BASE_URL is empty. Pass -PAPI_BASE_URL=https://your-api for release builds."
        }
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OneHubApi::class.java)
    }

    fun friendlyNetworkError(e: Throwable): String {
        val msg = e.message ?: e.javaClass.simpleName
        return when {
            !baseUrlConfigured ->
                "لم يُضبط عنوان الخادم (API_BASE_URL). Backend غير منشور بعد."
            msg.contains("Failed to connect", ignoreCase = true) ||
                msg.contains("Unable to resolve", ignoreCase = true) ||
                msg.contains("timeout", ignoreCase = true) ||
                msg.contains("Connection refused", ignoreCase = true) ->
                "تعذر الاتصال بالخادم (${BuildConfig.API_BASE_URL}). تأكد أن Backend يعمل محليًا، أو أن الجهاز يصل للعنوان."
            else -> msg
        }
    }
}
