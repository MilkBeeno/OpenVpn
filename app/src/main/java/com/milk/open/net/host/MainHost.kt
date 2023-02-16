package com.milk.open.net.host

class MainHost : ApiHost {
    override fun releaseUrl(): String {
        return "https://api.duoglobalmaster.com"
    }

    override fun debugUrl(): String {
        return "http://api.duoglobalmastert.click"
    }
}