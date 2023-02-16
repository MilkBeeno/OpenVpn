package com.milk.open

import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.milk.open.friebase.AnalyzeManager
import com.milk.open.media.LoaderManager
import com.milk.open.ui.act.BackStackActivity
import com.milk.open.ui.act.LaunchActivity
import com.milk.open.util.Backstage
import com.milk.simple.ktx.ioScope
import com.milk.simple.log.Logger
import com.milk.simple.mdr.KvManger

class MyApplication : Application() {
    companion object {
        lateinit var current: Application
    }

    override fun onCreate() {
        super.onCreate()
        current = this
        initialize()
    }

    private fun initialize() {
        // 保证在LaunchActivity#onCreate()前能够初始化完成
        KvManger.initialize(current)
        Logger.initialize(BuildConfig.DEBUG)
        ioScope {
            initializeAdmob(current)
            LoaderManager.initialize(current)
            AnalyzeManager.initialize(current)
            Backstage.backToForegroundMonitor(current) {
                if (it !is LaunchActivity && it !is BackStackActivity)
                    BackStackActivity.create(current)
            }
        }
    }

    private fun initializeAdmob(context: Context) {
        MobileAds.initialize(context) {
            if (BuildConfig.DEBUG) {
                val testDeviceNumbers =
                    listOf("c1aadd83-3bcd-474d-aac2-cd1e2e83ef22")
                MobileAds.setRequestConfiguration(
                    RequestConfiguration
                        .Builder()
                        .setTestDeviceIds(testDeviceNumbers)
                        .build()
                )
            }
        }
    }
}