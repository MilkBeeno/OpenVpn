package com.milk.open.net

import com.milk.open.net.host.MainHost
import com.milk.open.net.host.VpnHost
import com.milk.open.net.reteceptor.ApiHeaderInterceptor
import com.milk.open.net.reteceptor.ApiLogInterceptor
import com.milk.open.util.JsonConvert
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var mainRetrofit: Retrofit? = null
    private val mainClient: OkHttpClient
        get() {
            return OkHttpClient.Builder()
                .callTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .addInterceptor(ApiLogInterceptor())
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
                .addInterceptor(ApiLogInterceptor())
                .addInterceptor(ApiHeaderInterceptor())
                .build()
        }

    fun obtainMain(): Retrofit {
        if (mainRetrofit == null)
            mainRetrofit = Retrofit.Builder()
                .baseUrl(MainHost().realUrl)
                .client(mainClient)
                .addConverterFactory(
                    GsonConverterFactory.create(JsonConvert.gson)
                )
                .build()
        return checkNotNull(mainRetrofit)
    }

    fun obtainVpn(): Retrofit {
        if (vpnRetrofit == null)
            vpnRetrofit = Retrofit.Builder()
                .baseUrl(VpnHost().realUrl)
                .client(vpnClient)
                .addConverterFactory(
                    GsonConverterFactory.create(JsonConvert.gson)
                )
                .build()
        return checkNotNull(vpnRetrofit)
    }
}