package com.milk.open.net

import com.milk.open.net.host.MainDomain
import com.milk.open.net.host.VpnDomain
import com.milk.open.net.reteceptor.ApiHeadInterceptor
import com.milk.open.net.reteceptor.ApiLogerInterceptor
import com.milk.open.util.JsonFormat
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private var mainRetrofit: Retrofit? = null
    private val mainClient: OkHttpClient
        get() {
            return OkHttpClient.Builder()
                .callTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .addInterceptor(ApiLogerInterceptor())
                .build()
        }
    private var vpnRetrofit: Retrofit? = null
    private val vpnClient: OkHttpClient
        get() {
            return OkHttpClient.Builder()
                .callTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .addInterceptor(ApiLogerInterceptor())
                .addInterceptor(ApiHeadInterceptor())
                .build()
        }

    fun obtainMain(): Retrofit {
        if (mainRetrofit == null)
            mainRetrofit = Retrofit.Builder()
                .baseUrl(MainDomain().realUrl)
                .client(mainClient)
                .addConverterFactory(
                    GsonConverterFactory.create(JsonFormat.gson)
                )
                .build()
        return checkNotNull(mainRetrofit)
    }

    fun obtainVpn(): Retrofit {
        if (vpnRetrofit == null)
            vpnRetrofit = Retrofit.Builder()
                .baseUrl(VpnDomain().realUrl)
                .client(vpnClient)
                .addConverterFactory(
                    GsonConverterFactory.create(JsonFormat.gson)
                )
                .build()
        return checkNotNull(vpnRetrofit)
    }
}