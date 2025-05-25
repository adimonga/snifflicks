package com.amonga.snifflicks.di.modules

import android.content.Context
import com.amonga.snifflicks.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Created by Aditya on 21/05/25.
 */

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {


    @Provides
    @Singleton
    fun providesRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(cache: Cache): OkHttpClient {
        val client = OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request =
                        chain.request().newBuilder().addHeader("Authorization", getOrSetApiKey())
                            .addHeader("accept", "application/json").addHeader("language", "en-US")
                            .build()
                    return chain.proceed(request)
                }

            })
            .cache(cache)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(logging)
        }

        return client.build()
    }

    @Provides
    @Singleton
    fun providesOkhttpCache(@ApplicationContext context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun providesRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory =
        RxJava2CallAdapterFactory.create()

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun providesGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    companion object {
        var apiKey = ""

        @Synchronized
        fun getOrSetApiKey(apiKey: String? = null): String {
            apiKey?.let {
                Companion.apiKey = apiKey
            }
            return Companion.apiKey
        }

        const val EncryptedApiKey =
            "rBT7IBa6edMEtKX/lo3c6Nnad0vQBCWZGhbaU7sIVFzGibqFptin73pxDS6RtPMYnup3d17uKsVA7P97uXZe/4SVwPgsP+4r7R0N5L3+EQQPy6A46qmkWG+ozcjq/FbMxNDQCqnJf9RC9ixM5VI9kafXLJnWg/iHDRvB3zdtcOG5Hu9kbZuFtjr5JNs/jmSqs6Du7mqmUyrBIWDqPTLRlXob7g+GvZQoQpJHcqTIupq7aKiVb33DrjU4CsePOx6SxenwndQ7c6Wok4zmLbUsdaBXXeeit+EvWZ5iioQ12aivIb3xZBiOCNy/lJhdQ3F78mtktx1W4VEhXQRVYgEjW/+ugxLgZQ+c4DfAAIhEQmo="
    }
}