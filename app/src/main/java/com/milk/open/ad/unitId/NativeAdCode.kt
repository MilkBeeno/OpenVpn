package com.milk.open.ad.unitId

import com.milk.open.BuildConfig

class NativeAdCode : AdCode {
    override fun debug(): String {
        return "ca-app-pub-3940256099942544/2247696110"
    }

    override fun release(): String {
        return "ca-app-pub-9835209825468303/4891416756"
    }

    companion object {
        val value = if (BuildConfig.DEBUG) {
            NativeAdCode().debug()
        } else {
            NativeAdCode().release()
        }
    }
}