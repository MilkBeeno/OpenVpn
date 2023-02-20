package com.milk.open.data

import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.milk.open.R
import com.milk.open.databinding.ItemSwitchNodeBinding
import com.milk.open.media.PictureLoader

class VpnNode : ItemBind {
    var nodeId: Long = 0
    var areaImage: String = ""
    var areaName: String = ""
    var ping: Int = 0
    var isSelect: Boolean = false
    var itemSize: Int = 0
    var position: Int = 0
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = ItemSwitchNodeBinding.bind(holder.itemView)
        PictureLoader.Builder()
            .request(areaImage)
            .target(binding.ivNodeImage)
            .build()
        binding.ivNodeSelect.isSelected = isSelect
        binding.tvNodeName.text = areaName
        binding.tvPing.text = ping.toString()
        // if (position == itemSize - 1) binding.vLine.gone() else binding.vLine.visible()
    }
}