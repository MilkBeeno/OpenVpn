package com.milk.open.net.host

class MainDomain : ApiDomain {
    override fun releaseUrl(): String {
        return "https://api.openvpnsafeconnect.com"
    }

    override fun debugUrl(): String {
        return "http://api.openvpnsafet.click"
    }
}