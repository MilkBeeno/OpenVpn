package com.milk.open.net.api

import com.milk.open.net.NetworkClient

object ApiService {
    val main: MainApiService =
        NetworkClient.obtainMain().create(MainApiService::class.java)
    val vpn: VpnApiService =
        NetworkClient.obtainVpn().create(VpnApiService::class.java)
}