package com.milk.open.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.NotificationManagerCompat
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.open.R
import com.milk.open.constant.EventKey
import com.milk.open.databinding.ActivityMainBinding
import com.milk.open.friebase.AnalyzeKey
import com.milk.open.friebase.AnalyzeManager
import com.milk.open.media.PictureLoader
import com.milk.open.proxy.VpnProxy
import com.milk.open.repository.AppRepo
import com.milk.open.ui.dialog.VpnConnectFailureDialog
import com.milk.open.ui.dialog.VpnConnectingDialog
import com.milk.open.ui.dialog.VpnDisConnectDialog
import com.milk.open.ui.type.VpnConnectState
import com.milk.open.ui.vm.VpnViewModel
import com.milk.open.util.NotificationManager
import com.milk.simple.ktx.color
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.statusBarPadding
import com.milk.simple.ktx.string

class MainVpnActivity : BaseActivity() {
    private val vpnViewModel by viewModels<VpnViewModel>()
    private val vpnProxy by lazy { VpnProxy(this) }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var currentTime: Long = 0

    // 连接状态弹窗
    private val connectingDialog by lazy { VpnConnectingDialog(this) }
    private val disconnectDialog by lazy { VpnDisConnectDialog(this) }
    private val connectFailureDialog by lazy { VpnConnectFailureDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AnalyzeManager.logEvent(AnalyzeKey.ENTER_MAIN_PAGE)
        initializeView()
        loadNativeAd()
        initializeObserver()
    }

    private fun initializeView() {
        immersiveStatusBar(false)
        binding.flHeaderToolbar.statusBarPadding()
        binding.ivMenu.setOnClickListener(this)
        binding.ivShare.setOnClickListener(this)
        binding.llNetwork.setOnClickListener(this)
        binding.tvConnect.setOnClickListener(this)
        binding.llNetwork.setOnClickListener(this)
        binding.ivConnect.setOnClickListener(this)

        openVpnListener()
        vpnStateChangedListener()
        vpnDisconnect(false)
    }

    private fun openVpnListener() {
        vpnProxy.setVpnOpenedListener {
            connectingDialog.show()
            vpnChangingState(true)
            vpnViewModel.getVpnProfileInfo {
                if (it == null) {
                    binding.ivConnect.postDelayed({
                        vpnConnectFailure()
                    }, 4000)
                } else {
                    vpnProxy.connectVpn(it, vpnViewModel.vpnIsConnected)
                }
            }
        }
    }

    private fun vpnStateChangedListener() {
        vpnProxy.setVpnStateChangedListener { vpnState, success ->
            if (success) {
                when (vpnState) {
                    VpnConnectState.DISCONNECT -> {
                        vpnDisconnect()
                    }
                    VpnConnectState.CONNECTED -> {
                        vpnConnected()
                    }
                    else -> Unit
                }
            } else {
                vpnConnectFailure()
            }
            vpnViewModel.vpnIsConnected = success && vpnState == VpnConnectState.CONNECTED
        }
    }

    private fun loadNativeAd() {
        if (AppRepo.showMainNativeAd) {
            AnalyzeManager.logEvent(AnalyzeKey.Make_an_ad_request)
            binding.nativeView.setLoadFailureRequest {
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_failed)
            }
            binding.nativeView.setLoadSuccessRequest {
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_succeeded)
            }
            binding.nativeView.setClickRequest {
                AnalyzeManager.logEvent(AnalyzeKey.click_ad)
            }
            binding.nativeView.loadNativeAd()
        }
    }

    private fun initializeObserver() {
        LiveEventBus.get<ArrayList<String>>(EventKey.SWITCH_VPN_NODE)
            .observe(this) {
                vpnViewModel.vpnNodeId = it[0].toLong()
                vpnViewModel.vpnImageUrl = it[1]
                vpnViewModel.vpnName = it[2]
                vpnViewModel.vpnPing = it[3].toLong()
                updateConnectInfo()
                vpnProxy.tryOpenVpn()
            }
    }

    private fun vpnDisconnect(showResult: Boolean = true) {
        updateConnectInfo()
        binding.ivConnect.setBackgroundResource(R.drawable.main_not_connect)
        binding.tvConnect.text = string(R.string.main_disconnect)
        binding.tvConnect.setBackgroundResource(R.drawable.shape_main_disconnect)
        binding.tvConnect.setTextColor(color(R.color.white))
        if (showResult && vpnViewModel.vpnIsConnected) {
            vpnConnectResult(false)
        } else {
            connectingDialog.dismiss()
            disconnectDialog.dismiss()
        }
    }

    private fun vpnChangingState(connecting: Boolean) {
        binding.tvConnect.text = if (connecting) {
            string(R.string.main_connecting)
        } else {
            string(R.string.main_disconnecting)
        }
        binding.tvConnect.setBackgroundResource(R.drawable.shape_main_connected)
        binding.tvConnect.setTextColor(color(R.color.FF31FFDA))
    }

    private fun vpnConnected() {
        updateConnectInfo()
        binding.ivConnect.setBackgroundResource(R.drawable.main_connected)
        binding.tvConnect.text = string(R.string.main_connected)
        binding.tvConnect.setBackgroundResource(R.drawable.shape_main_connected)
        binding.tvConnect.setTextColor(color(R.color.FF31FFDA))
        vpnConnectResult(true)
        // 发送通知
        val enabled = NotificationManagerCompat
            .from(this@MainVpnActivity).areNotificationsEnabled()
        if (enabled) {
            NotificationManager.showConnectedNotification(
                this@MainVpnActivity,
                vpnViewModel.vpnName.ifBlank { "United States" })
        }
    }

    private fun vpnConnectFailure() {
        connectingDialog.dismiss()
        disconnectDialog.dismiss()
        connectFailureDialog.show()
        vpnDisconnect(false)
        AnalyzeManager.logEvent(AnalyzeKey.CONNECT_FAILED)
    }

    /** 连接结果就是 1.加载广告 2.显示结果页面 */
    private fun vpnConnectResult(isConnected: Boolean) {
        if (isConnected) {
            AnalyzeManager.logEvent(AnalyzeKey.CONNECT_SUCCESSFULLY)
            when ((System.currentTimeMillis() - currentTime)) {
                in 0L until 3000L -> {
                    AnalyzeManager.logEvent(AnalyzeKey.CONNECTION_SUCCESSFUL_WITHIN_3S)
                }
                in 3L until 8000L -> {
                    AnalyzeManager.logEvent(AnalyzeKey.CONNECTION_SUCCESSFUL_WITHIN_3_8S)
                }
                in 8L until 15000L -> {
                    AnalyzeManager.logEvent(AnalyzeKey.CONNECTION_SUCCESSFUL_WITHIN_8_15S)
                }
                else -> {
                    AnalyzeManager.logEvent(AnalyzeKey.CONNECTION_SUCCESSFUL_FOR_MORE_THAN_15S)
                }
            }
        }
        when {
            isConnected && AppRepo.showConnectedInsertAd -> {
                showInsertAd(true)
            }
            !isConnected && AppRepo.showDisconnectInsertAd -> {
                showInsertAd(false)
            }
        }
    }

    private fun showInsertAd(isConnected: Boolean) {
        vpnViewModel.loadInterstitialAd(this) {
            connectingDialog.dismiss()
            disconnectDialog.dismiss()
            ResultPageActivity.create(
                this,
                isConnected,
                vpnViewModel.vpnImageUrl,
                vpnViewModel.vpnName,
                vpnViewModel.vpnPing
            )
        }
    }

    private fun updateConnectInfo() {
        if (vpnViewModel.vpnNodeId > 0) {
            PictureLoader.Builder()
                .request(vpnViewModel.vpnImageUrl)
                .target(binding.ivNetwork)
                .build()
            binding.tvNetwork.text = vpnViewModel.vpnName
        } else {
            binding.ivNetwork.setImageResource(R.drawable.main_network)
            binding.tvNetwork.text = string(R.string.common_auto_select)
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivMenu -> {
                AboutUsActivity.create(this)
                AnalyzeManager.logEvent(AnalyzeKey.CLICK_ON_MORE)
            }
            binding.ivShare -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, AppRepo.shareAppUrl)
                intent.type = "text/plain"
                startActivity(intent)
                AnalyzeManager.logEvent(AnalyzeKey.CLICK_THE_SHARE)
            }
            binding.llNetwork -> {
                currentTime = System.currentTimeMillis()
                SwitchVpnActivity.create(
                    this,
                    vpnViewModel.vpnNodeId,
                    vpnViewModel.vpnIsConnected
                )
                AnalyzeManager.logEvent(AnalyzeKey.CLICK_ON_THE_NODE_LIST_ENTRY)
            }
            binding.ivConnect,
            binding.tvConnect -> {
                if (vpnViewModel.vpnIsConnected) {
                    disconnectDialog.show()
                    vpnChangingState(false)
                    binding.tvConnect.postDelayed({
                        vpnProxy.closeVpn()
                    }, 4000)
                } else {
                    vpnProxy.tryOpenVpn()
                    AnalyzeManager.logEvent(AnalyzeKey.CLICK_TO_CONNECT_NODE)
                }
            }
        }
    }

    override fun onInterceptKeyDownEvent() = true

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, MainVpnActivity::class.java))
        }
    }
}