package com.milk.open.ui.dialog

import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.milk.open.databinding.DialogConnectingBinding

class ConnectingDialog(activity: FragmentActivity) :
    SimpleDialog<DialogConnectingBinding>(activity) {

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding.connectingLottieView.setAnimation("connecting.json")
        binding.connectingLottieView.playAnimation()
    }

    override fun getViewBinding(): DialogConnectingBinding {
        return DialogConnectingBinding.inflate(LayoutInflater.from(activity))
    }
}