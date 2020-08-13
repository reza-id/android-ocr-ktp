package dev.juara.ocrktp.di

import dev.juara.ocrktp.TakeViewModel
import dev.juara.ocrktp.data.KpuApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val koinModules = module {
    factory { UserAgentInterceptor(get()) }
    factory { provideHttpLogging() }
    factory { provideOkHttpClientBuilder(get(), get()) }
    single { provideRetrofitBuilder() }
    single { provideKpuApi(get(), get()) }

    viewModel { TakeViewModel(get()) }
}

fun provideHttpLogging() = HttpLoggingInterceptor().apply {
    apply { level = HttpLoggingInterceptor.Level.BODY }
}

fun provideOkHttpClientBuilder(
    userAgentInterceptor: UserAgentInterceptor,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient.Builder {
    val builder = OkHttpClient().newBuilder()

    val trustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    val trustAllCerts: Array<TrustManager> = arrayOf(trustManager)

    val sslContext: SSLContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())
    val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

    return builder
        .sslSocketFactory(sslSocketFactory, trustManager)
        .addInterceptor(loggingInterceptor)
        .addInterceptor(userAgentInterceptor)
}

fun provideRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())

fun provideKpuApi(
    retrofit: Retrofit.Builder,
    okHttpClient: OkHttpClient.Builder
): KpuApi = retrofit
    .client(okHttpClient.build())
    .baseUrl("https://infopemilu.kpu.go.id/pilkada2018/")
    .build()
    .create(KpuApi::class.java)