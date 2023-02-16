package com.milk.open.repository

import com.milk.open.net.api.ApiService
import com.milk.open.net.retrofit

object VpnRepository {

    suspend fun getVpnInfo(id: Long) = retrofit { ApiService.vpn.getVpnInfo(id) }

    suspend fun getVpnListInfo() = retrofit { ApiService.vpn.getVpnListInfo() }
}