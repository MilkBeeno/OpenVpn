package com.milk.open.net.host

class VpnDomain : ApiDomain {
    override fun releaseUrl(): String {
        return "https://apv.duoglobalmaster.com"
    }

    override fun debugUrl(): String {
        return "http://apv.duoglobalmastert.click"
    }
}