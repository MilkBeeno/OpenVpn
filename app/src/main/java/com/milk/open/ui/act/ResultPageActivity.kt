package com.milk.open.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.milk.open.R
import com.milk.open.databinding.ActivityResultBinding
import com.milk.open.friebase.AnalyzeKey
import com.milk.open.friebase.AnalyzeManager
import com.milk.open.media.PictureLoader
import com.milk.open.repository.AppRepo
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.statusBarPadding
import com.milk.simple.ktx.string

class ResultPageActivity : BaseActivity() {
    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }
    private val vpnImage by lazy { intent.getStringExtra(VPN_IMAGE).toString() }
    private val vpnName by lazy { intent.getStringExtra(VPN_NAME).toString() }
    private val vpnPing by lazy { intent.getLongExtra(VPN_PING, 0) }
    private val isConnected by lazy { intent.getBooleanExtra(IS_CONNECTED, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        immersiveStatusBar()
        binding.ivBack.statusBarPadding()
        binding.ivBack.setOnClickListener { finish() }
        if (isConnected) {
            binding.ivResult.setBackgroundResource(R.drawable.result_link_success)
            binding.tvResult.text = string(R.string.result_connected)
        } else {
            binding.ivResult.setBackgroundResource(R.drawable.result_link_failure)
            binding.tvResult.text = string(R.string.result_failure)
        }
        if (vpnImage.isNotBlank()) {
            PictureLoader.Builder()
                .request(vpnImage)
                .target(binding.ivNetwork)
                .build()
        } else {
            binding.ivNetwork.setImageResource(R.drawable.main_network)
        }
        binding.tvNetwork.text =
            vpnName.ifBlank { string(R.string.common_auto_select) }
        binding.tvPing.text = vpnPing.toString()
        // 原生广告展示和统计事件
        binding.nativeView.setLoadFailureRequest {
            if (isConnected) {
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_failed_5)
            } else {
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_failed_6, it)
            }
        }
        binding.nativeView.setLoadSuccessRequest {
            if (isConnected) {
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_succeeded_5)
            } else {
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_succeeded_6)
            }
        }
        binding.nativeView.setClickRequest {
            if (isConnected) {
                AnalyzeManager.logEvent(AnalyzeKey.click_ad_5)
            } else {
                AnalyzeManager.logEvent(AnalyzeKey.click_ad_6)
            }
        }
        when {
            isConnected && AppRepo.showConnectedNativeAd -> {
                binding.nativeView.loadNativeAd()
                AnalyzeManager.logEvent(AnalyzeKey.Make_an_ad_request_5)
            }
            !isConnected && AppRepo.showDisconnectNativeAd -> {
                binding.nativeView.loadNativeAd()
                AnalyzeManager.logEvent(AnalyzeKey.Make_an_ad_request_6)
            }
        }
    }

    companion object {
        private const val IS_CONNECTED = "IS_CONNECTED"
        private const val VPN_IMAGE = "VPN_IMAGE"
        private const val VPN_NAME = "VPN_NAME"
        private const val VPN_PING = "VPN_PING"
        fun create(
            context: Context,
            isConnected: Boolean,
            vpnImage: String,
            vpnName: String,
            vpnPing: Long
        ) {
            val intent = Intent(context, ResultPageActivity::class.java)
            intent.putExtra(IS_CONNECTED, isConnected)
            intent.putExtra(VPN_IMAGE, vpnImage)
            intent.putExtra(VPN_NAME, vpnName)
            intent.putExtra(VPN_PING, vpnPing)
            context.startActivity(intent)
        }
    }
}