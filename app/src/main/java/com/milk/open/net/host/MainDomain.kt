package com.milk.open.net.host

class MainDomain : ApiDomain {
    override fun releaseUrl(): String {
        return "https://api.duoglobalmaster.com"
    }

    override fun debugUrl(): String {
        return "http://api.duoglobalmastert.click"
    }
}