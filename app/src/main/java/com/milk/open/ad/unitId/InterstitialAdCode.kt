package com.milk.open.ad.unitId

import com.milk.open.BuildConfig

class InterstitialAdCode : AdCode {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/1033173712"
    }

    override fun release(): String {
        return "ca-app-pub-4684374725464850/3438883642"
    }

    companion object {
        val value = if (BuildConfig.DEBUG) {
            InterstitialAdCode().debug()
        } else {
            InterstitialAdCode().release()
        }
    }
}