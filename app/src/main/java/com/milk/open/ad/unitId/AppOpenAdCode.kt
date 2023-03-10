package com.milk.open.ad.unitId

import com.milk.open.BuildConfig

class AppOpenAdCode : AdCode {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/3419835294"
    }

    override fun release(): String {
        return "ca-app-pub-4684374725464850/3874546658"
    }

    companion object {
        val value = if (BuildConfig.DEBUG) {
            AppOpenAdCode().debug()
        } else {
            AppOpenAdCode().release()
        }
    }
}