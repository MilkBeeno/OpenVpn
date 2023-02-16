package com.milk.open.repository

import com.milk.open.net.api.ApiService
import com.milk.open.net.tryRetrofit

object VpnRepo {

    suspend fun getVpnInfo(id: Long) = tryRetrofit { ApiService.vpn.getVpnInfo(id) }

    suspend fun getVpnListInfo() = tryRetrofit { ApiService.vpn.getVpnListInfo() }
}