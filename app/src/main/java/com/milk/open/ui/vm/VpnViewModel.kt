package com.milk.open.ui.vm

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.freetech.vpn.data.VpnProfile
import com.freetech.vpn.data.VpnType
import com.milk.open.ad.InterstitialAd
import com.milk.open.data.VpnModel
import com.milk.open.friebase.AnalyzeManager
import com.milk.open.friebase.AnalyzeKey
import com.milk.open.repository.VpnRepo
import com.milk.open.util.CustomTimer
import com.milk.simple.ktx.ioScope
import com.milk.simple.ktx.withMain

class VpnViewModel : ViewModel() {
    private val interstitialAd by lazy { InterstitialAd() }

    // 当前连接的节点 ID 和是否是连接成功
    internal var vpnNodeId: Long = 0
    internal var vpnPing: Long = 0
    internal var vpnName: String = ""
    internal var vpnImageUrl: String = ""
    internal var vpnIsConnected: Boolean = false

    internal fun getVpnProfileInfo(vpnProfileRequest: (VpnProfile?) -> Unit) {
        ioScope {
            val response = VpnRepo.getVpnInfo(vpnNodeId)
            val result = response.data
            withMain {
                if (response.code == 2000 && result != null) {
                    vpnProfileRequest(getVpnProfile(result))
                } else vpnProfileRequest(null)
            }
        }
    }

    private fun getVpnProfile(vpnModel: VpnModel): VpnProfile {
        val vpnProfile = VpnProfile()
        vpnProfile.id = vpnModel.nodeId
        vpnProfile.name = vpnModel.nodeName
        vpnProfile.gateway = vpnModel.dns
        vpnProfile.username = vpnModel.userName
        vpnProfile.password = vpnModel.password
        vpnProfile.mtu = 1400
        vpnProfile.vpnType = VpnType.fromIdentifier("ikev2-eap")
        return vpnProfile
    }

    internal fun loadInterstitialAd(activity: Activity, finishRequest: () -> Unit) {
        CustomTimer.Builder()
            .setMillisInFuture(12000)
            .setOnFinishedListener {
                if (!interstitialAd.isShowSuccessfulAd()) {
                    finishRequest()
                }
            }
            .build()
            .start()

        AnalyzeManager.logEvent(AnalyzeKey.Make_an_ad_request_4)
        interstitialAd.load(
            context = activity,
            failure = {
                finishRequest()
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_failed_4, it)
            },
            success = {
                showInterstitialAd(activity, finishRequest)
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_succeeded_4)
            }
        )
    }

    private fun showInterstitialAd(activity: Activity, finishRequest: () -> Unit) {
        interstitialAd.show(
            activity = activity,
            failure = {
                finishRequest()
                AnalyzeManager.logEvent(AnalyzeKey.Ad_show_failed_4, it)
            },
            success = {
                AnalyzeManager.logEvent(AnalyzeKey.The_ad_show_success_4)
            },
            click = {
                AnalyzeManager.logEvent(AnalyzeKey.click_ad_4)
            },
            close = {
                finishRequest()
            }
        )
    }
}