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
import com.milk.open.friebase.FireBaseManager
import com.milk.open.friebase.FirebaseKey
import com.milk.open.media.ImageLoader
import com.milk.open.proxy.VpnProxy
import com.milk.open.repository.AppRepository
import com.milk.open.ui.dialog.ConnectingDialog
import com.milk.open.ui.dialog.DisConnectDialog
import com.milk.open.ui.dialog.FailureDialog
import com.milk.open.ui.type.VpnState
import com.milk.open.ui.vm.VpnViewModel
import com.milk.open.util.Notification
import com.milk.simple.ktx.color
import com.milk.simple.ktx.immersiveStatusBar
import com.milk.simple.ktx.statusBarPadding
import com.milk.simple.ktx.string

class MainActivity : AbstractActivity() {
    private val vpnViewModel by viewModels<VpnViewModel>()
    private val vpnProxy by lazy { VpnProxy(this) }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var currentTime: Long = 0

    // 连接状态弹窗
    private val connectingDialog by lazy { ConnectingDialog(this) }
    private val disconnectDialog by lazy { DisConnectDialog(this) }
    private val connectFailureDialog by lazy { FailureDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FireBaseManager.logEvent(FirebaseKey.ENTER_MAIN_PAGE)
        initializeView()
        loadNativeAd()
        initializeObserver()
    }

    private fun initializeView() {
        immersiveStatusBar(false)
        binding.llHeaderToolbar.statusBarPadding()
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
                    VpnState.DISCONNECT -> {
                        vpnDisconnect()
                    }
                    VpnState.CONNECTED -> {
                        vpnConnected()
                    }
                    else -> Unit
                }
            } else {
                vpnConnectFailure()
            }
            vpnViewModel.vpnIsConnected = success && vpnState == VpnState.CONNECTED
        }
    }

    private fun loadNativeAd() {
        if (AppRepository.showMainNativeAd) {
            FireBaseManager.logEvent(FirebaseKey.Make_an_ad_request)
            binding.nativeView.setLoadFailureRequest {
                FireBaseManager.logEvent(FirebaseKey.Ad_request_failed)
            }
            binding.nativeView.setLoadSuccessRequest {
                FireBaseManager.logEvent(FirebaseKey.Ad_request_succeeded)
            }
            binding.nativeView.setClickRequest {
                FireBaseManager.logEvent(FirebaseKey.click_ad)
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
        binding.root.setBackgroundResource(R.drawable.main_disconnect_background)
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

    private fun vpnConnected() {
        updateConnectInfo()
        binding.root.setBackgroundResource(R.drawable.main_connected_background)
        binding.ivConnect.setBackgroundResource(R.drawable.main_connected)
        binding.tvConnect.text = string(R.string.main_connected)
        binding.tvConnect.setBackgroundResource(R.drawable.shape_main_connected)
        binding.tvConnect.setTextColor(color(R.color.FF0C9AFF))
        vpnConnectResult(true)
        // 发送通知
        val enabled = NotificationManagerCompat
            .from(this@MainActivity).areNotificationsEnabled()
        if (enabled) {
            Notification.showConnectedNotification(
                this@MainActivity,
                vpnViewModel.vpnName.ifBlank { "United States" })
        }
    }

    private fun vpnConnectFailure() {
        connectingDialog.dismiss()
        disconnectDialog.dismiss()
        connectFailureDialog.show()
        vpnDisconnect(false)
        FireBaseManager.logEvent(FirebaseKey.CONNECT_FAILED)
    }

    /** 连接结果就是 1.加载广告 2.显示结果页面 */
    private fun vpnConnectResult(isConnected: Boolean) {
        if (isConnected) {
            FireBaseManager.logEvent(FirebaseKey.CONNECT_SUCCESSFULLY)
            when ((System.currentTimeMillis() - currentTime)) {
                in 0L until 3000L -> {
                    FireBaseManager.logEvent(FirebaseKey.CONNECTION_SUCCESSFUL_WITHIN_3S)
                }
                in 3L until 8000L -> {
                    FireBaseManager.logEvent(FirebaseKey.CONNECTION_SUCCESSFUL_WITHIN_3_8S)
                }
                in 8L until 15000L -> {
                    FireBaseManager.logEvent(FirebaseKey.CONNECTION_SUCCESSFUL_WITHIN_8_15S)
                }
                else -> {
                    FireBaseManager.logEvent(FirebaseKey.CONNECTION_SUCCESSFUL_FOR_MORE_THAN_15S)
                }
            }
        }
        when {
            isConnected && AppRepository.showConnectedInsertAd -> {
                showInsertAd(true)
            }
            !isConnected && AppRepository.showDisconnectInsertAd -> {
                showInsertAd(false)
            }
        }
    }

    private fun showInsertAd(isConnected: Boolean) {
        vpnViewModel.loadInterstitialAd(this) {
            connectingDialog.dismiss()
            disconnectDialog.dismiss()
            ResultActivity.create(
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
            ImageLoader.Builder()
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
                AboutActivity.create(this)
                FireBaseManager.logEvent(FirebaseKey.CLICK_ON_MORE)
            }
            binding.ivShare -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, AppRepository.shareAppUrl)
                intent.type = "text/plain"
                startActivity(intent)
                FireBaseManager.logEvent(FirebaseKey.CLICK_THE_SHARE)
            }
            binding.llNetwork -> {
                currentTime = System.currentTimeMillis()
                SwitchNodeActivity.create(
                    this,
                    vpnViewModel.vpnNodeId,
                    vpnViewModel.vpnIsConnected
                )
                FireBaseManager.logEvent(FirebaseKey.CLICK_ON_THE_NODE_LIST_ENTRY)
            }
            binding.ivConnect,
            binding.tvConnect -> {
                if (vpnViewModel.vpnIsConnected) {
                    disconnectDialog.show()
                    binding.tvConnect.postDelayed({
                        vpnProxy.closeVpn()
                    }, 4000)
                } else {
                    vpnProxy.tryOpenVpn()
                    FireBaseManager.logEvent(FirebaseKey.CLICK_TO_CONNECT_NODE)
                }
            }
        }
    }

    override fun onInterceptKeyDownEvent() = true

    companion object {
        fun create(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}