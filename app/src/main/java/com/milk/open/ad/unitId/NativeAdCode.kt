package com.milk.open.ad.unitId

import com.milk.open.BuildConfig

class NativeAdCode : AdCode {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/2247696110"
    }

    override fun release(): String {
        return "ca-app-pub-4684374725464850/1972924581"
    }

    companion object {
        val value = if (BuildConfig.DEBUG) {
            NativeAdCode().debug()
        } else {
            NativeAdCode().release()
        }
    }
}