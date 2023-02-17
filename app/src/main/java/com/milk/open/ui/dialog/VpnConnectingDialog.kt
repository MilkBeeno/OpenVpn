package com.milk.open.ui.dialog

import android.os.Build.VERSION.SDK_INT
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.milk.open.R
import com.milk.open.databinding.DialogConnectingBinding

class VpnConnectingDialog(activity: FragmentActivity) :
    BaseDialog<DialogConnectingBinding>(activity) {

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        val imageLoader = ImageLoader.Builder(activity)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        binding.ivAnimation.load(R.drawable.vpn_connect_state, imageLoader)
    }

    override fun getViewBinding(): DialogConnectingBinding {
        return DialogConnectingBinding.inflate(LayoutInflater.from(activity))
    }
}