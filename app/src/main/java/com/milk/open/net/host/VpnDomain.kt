package com.milk.open.net.host

class VpnDomain : ApiDomain {
    override fun releaseUrl(): String {
        return "https://apv.openvpnsafeconnect.com"
    }

    override fun debugUrl(): String {
        return "http://apv.openvpnsafet.click"
    }
}