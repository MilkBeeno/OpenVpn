package com.milk.open.ui.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.open.databinding.DialogDisconnectBinding

class VpnDisConnectDialog(activity: FragmentActivity) :
    BaseDialog<DialogDisconnectBinding>(activity) {

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding.disconnectLottieView.setAnimation("disconnect.json")
        binding.disconnectLottieView.playAnimation()
    }

    override fun getViewBinding(): DialogDisconnectBinding {
        return DialogDisconnectBinding.inflate(LayoutInflater.from(activity))
    }
}