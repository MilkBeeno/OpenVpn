package com.milk.open.ui.act

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.jeremyliao.liveeventbus.LiveEventBus
import com.milk.open.R
import com.milk.open.constant.EventKey
import com.milk.open.data.VpnGroup
import com.milk.open.data.VpnNode
import com.milk.open.databinding.ActivitySwitchNodeBinding
import com.milk.open.friebase.AnalyzeManager
import com.milk.open.friebase.AnalyzeKey
import com.milk.open.repository.AppRepo
import com.milk.open.ui.vm.SwitchNodeViewModel
import com.milk.simple.ktx.collectLatest
import java.util.*

class SwitchVpnActivity : BaseActivity() {
    private val binding by lazy { ActivitySwitchNodeBinding.inflate(layoutInflater) }
    private val switchNodeViewModel by viewModels<SwitchNodeViewModel>()
    private val currentNodeId by lazy { intent.getLongExtra(CURRENT_NODE_ID, 0) }
    private val currentConnected by lazy { intent.getBooleanExtra(CURRENT_CONNECTED, false) }
    private val random by lazy { Random() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeView()
        loadNativeAd()
        initializeData()
    }

    private fun initializeView() {
        switchNodeViewModel.currentNodeId = currentNodeId
        switchNodeViewModel.currentConnected = currentConnected
        binding.ivBack.setOnClickListener(this)
        binding.ivRefresh.setOnClickListener(this)
        switchNodeViewModel.vpnGroups.collectLatest(this) {
            binding.rvNode.linear().setup {
                addType<VpnGroup>(R.layout.item_switch_group)
                addType<VpnNode>(R.layout.item_switch_node)
                R.id.ivGroupExpand.onClick {
                    expandOrCollapse()
                }
                R.id.ivGroupSelect.onClick {
                    val vpnGroup = this._data as VpnGroup
                    if (vpnGroup.isSelect) return@onClick
                    val nodes = vpnGroup.itemSublist
                    when {
                        nodes != null && nodes.isNotEmpty() -> {
                            AnalyzeManager.logEvent(AnalyzeKey.CLICK_ON_NORMAL_NODE)
                            if (currentConnected)
                                AnalyzeManager.logEvent(AnalyzeKey.CLICK_TO_SWITCH_NODE)
                            val index = random.nextInt(nodes.size)
                            val node = nodes[index] as VpnNode
                            LiveEventBus.get<ArrayList<String>>(EventKey.SWITCH_VPN_NODE)
                                .post(
                                    arrayListOf(
                                        node.nodeId.toString(),
                                        node.areaImage,
                                        node.areaName,
                                        node.ping.toString()
                                    )
                                )
                        }
                        else -> {
                            AnalyzeManager.logEvent(AnalyzeKey.CLICK_ON_THE_AUTOMATIC_NODE)
                            LiveEventBus.get<ArrayList<String>>(EventKey.SWITCH_VPN_NODE)
                                .post(arrayListOf("0", "", "", "0"))
                        }
                    }
                    finish()
                }
                R.id.ivNodeSelect.onClick {
                    val node = this._data as VpnNode
                    if (node.isSelect) return@onClick
                    AnalyzeManager.logEvent(AnalyzeKey.CLICK_ON_NORMAL_NODE)
                    if (currentConnected)
                        AnalyzeManager.logEvent(AnalyzeKey.CLICK_TO_SWITCH_NODE)
                    LiveEventBus.get<ArrayList<String>>(EventKey.SWITCH_VPN_NODE)
                        .post(
                            arrayListOf(
                                node.nodeId.toString(),
                                node.areaImage,
                                node.areaName,
                                node.ping.toString()
                            )
                        )
                    finish()
                }
            }.models = it
        }
    }

    private fun loadNativeAd() {
        if (AppRepo.showSwitchNativeAd) {
            AnalyzeManager.logEvent(AnalyzeKey.Make_an_ad_request_1)
            binding.nativeView.setLoadFailureRequest {
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_failed_1)
            }
            binding.nativeView.setLoadSuccessRequest {
                AnalyzeManager.logEvent(AnalyzeKey.Ad_request_succeeded_1)
            }
            binding.nativeView.setClickRequest {
                AnalyzeManager.logEvent(AnalyzeKey.click_ad_1)
            }
            binding.nativeView.loadNativeAd()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initializeData() {
        switchNodeViewModel.getVpnListInfo()
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivBack -> finish()
            binding.ivRefresh -> {
                switchNodeViewModel.getVpnListInfo()
            }
        }
    }

    companion object {
        private const val CURRENT_CONNECTED = "CURRENT_CONNECTED"
        private const val CURRENT_NODE_ID = "CURRENT_NODE_ID"
        fun create(context: Context, currentNodeId: Long, currentConnected: Boolean) {
            val intent = Intent(context, SwitchVpnActivity::class.java)
            intent.putExtra(CURRENT_NODE_ID, currentNodeId)
            intent.putExtra(CURRENT_CONNECTED, currentConnected)
            context.startActivity(intent)
        }
    }
}