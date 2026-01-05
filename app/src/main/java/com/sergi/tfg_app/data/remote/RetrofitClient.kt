package com.sergi.tfg_app.data.remote

import com.sergi.tfg_app.data.remote.api.AuthApi
import com.sergi.tfg_app.data.remote.api.CvApi
import com.sergi.tfg_app.data.remote.interceptor.AuthInterceptor
import com.sergi.tfg_app.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    private val publicOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val publicRetrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(publicOkHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val authApi: AuthApi = publicRetrofit.create(AuthApi::class.java)

    fun createAuthenticatedCvApi(tokenProvider: () -> String?): CvApi {
        val authInterceptor = AuthInterceptor(tokenProvider)

        val authenticatedClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val authenticatedRetrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(authenticatedClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return authenticatedRetrofit.create(CvApi::class.java)
    }
}
