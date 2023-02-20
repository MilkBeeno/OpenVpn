package com.milk.open.data

import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import com.drake.brv.item.ItemHover
import com.drake.brv.item.ItemPosition
import com.milk.open.R
import com.milk.open.databinding.ItemSwitchGroupBinding
import com.milk.open.media.PictureLoader
import com.milk.simple.ktx.gone
import com.milk.simple.ktx.string
import com.milk.simple.ktx.visible

class VpnGroup : ItemExpand, ItemHover, ItemPosition, ItemBind {
    var areaImage: String = ""
    var areaName: String = ""
    var isSelect: Boolean = false
    var isAutoSelectItem: Boolean = false
    override var itemExpand: Boolean = false
    override var itemGroupPosition: Int = 0
    override var itemSublist: List<Any?>? = null
    override var itemHover: Boolean = false
    override var itemPosition: Int = 0

    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = ItemSwitchGroupBinding.bind(holder.itemView)
        when {
            // 显示 VPN 列表节点
            itemSublist != null -> {
                // binding.vLine.gone()
                binding.llContent.visible()
                PictureLoader.Builder()
                    .request(areaImage)
                    .target(binding.ivGroupImage)
                    .build()
                binding.tvGroupName.text = areaName
                    .plus("(")
                    .plus(itemSublist?.size.toString())
                    .plus(")")
                binding.ivGroupExpand.visible()
                binding.ivGroupExpand.setImageResource(
                    if (itemExpand)
                        R.drawable.switch_node_packup
                    else
                        R.drawable.switch_node_expand
                )
                // if (itemExpand) binding.vLine.visible() else binding.vLine.gone()
            }
            // 显示 VPN 自动连接选项
            else -> {
                // binding.vLine.gone()
                binding.llContent.visible()
                binding.tvGroupName.text =
                    binding.root.context.string(R.string.common_auto_select)
                binding.ivGroupImage.setImageResource(R.drawable.main_network)
                binding.ivGroupExpand.gone()
            }
        }
        binding.ivGroupSelect.isSelected = isSelect
    }
}