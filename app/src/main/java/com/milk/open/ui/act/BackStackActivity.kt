package com.milk.open.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.open.ad.AppOpenAd
import com.milk.open.databinding.ActivityBackStackBinding
import com.milk.open.friebase.AnalyzeKey
import com.milk.open.friebase.AnalyzeManager
import com.milk.open.repository.AppRepo
import com.milk.open.util.CustomTimer
import com.milk.simple.ktx.immersiveStatusBar

class BackStackActivity : BaseActivity() {
    private val appOpenAd by lazy { AppOpenAd() }
    private val binding by lazy { ActivityBackStackBinding.inflate(layoutInflater) }
    private val fromStartPage by lazy { intent.getBooleanExtra(FROM_START_PAGE, false) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initAd()
    }

    private fun initView() {
        immersiveStatusBar(false)
        binding.progressBar.max = 12000
    }

    private fun initAd() {
        CustomTimer.Builder()
            .setCountDownInterval(10)
            .setMillisInFuture(12000)
            .setOnTickListener { _, t ->
                binding.progressBar.progress = (12000 - t).toInt()
            }
            .setOnFinishedListener {
                if (!appOpenAd.isShowSuccessfulAd()) {
                    next()
                }
            }
            .build()
            .start()

        if (AppRepo.showOpenAd) {
            AnalyzeManager.logEvent(AnalyzeKey.Make_an_ad_request_3)
            appOpenAd.load(
                context = this,
                failure = {
                    next()
                    AnalyzeManager.logEvent(AnalyzeKey.Ad_request_failed_3, it)
                },
                success = {
                    showAppOpenAd()
                    AnalyzeManager.logEvent(AnalyzeKey.Ad_request_succeeded_3)
                }
            )
        }
    }

    private fun showAppOpenAd() {
        appOpenAd.show(
            activity = this,
            failure = {
                next()
                AnalyzeManager.logEvent(AnalyzeKey.Ad_show_failed_3, it)
            },
            success = {
                AnalyzeManager.logEvent(AnalyzeKey.The_ad_show_success_3)
            },
            click = {
                AnalyzeManager.logEvent(AnalyzeKey.click_ad_3)
            },
            close = {
                next()
            }
        )
    }

    private fun next() {
        if (fromStartPage) {
            MainVpnActivity.create(this)
        }
        finish()
    }

    override fun isInterceptKeyDownEvent(): Boolean = true

    companion object {
        private const val FROM_START_PAGE = "FROM_START_PAGE"
        fun create(context: Context, fromStartPage: Boolean = false) {
            val intent = Intent(context, BackStackActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(FROM_START_PAGE, fromStartPage)
            context.startActivity(intent)
        }
    }
}