package com.milk.open.net.api

import com.milk.open.net.ApiClient

object ApiService {
    val main: MainApiService =
        ApiClient.obtainMain().create(MainApiService::class.java)
    val vpn: VpnApiService =
        ApiClient.obtainVpn().create(VpnApiService::class.java)
}