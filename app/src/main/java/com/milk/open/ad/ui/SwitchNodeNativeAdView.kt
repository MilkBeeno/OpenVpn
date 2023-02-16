package com.milk.open.ad.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.milk.open.R
import com.milk.open.ad.unitId.NativeAdCode
import com.milk.simple.ktx.gone

class SwitchNodeNativeAdView : BaseNativeAdView {
    private var loadFailureListener: ((String) -> Unit)? = null
    private var loadSuccessListener: (() -> Unit)? = null
    private var clickListener: (() -> Unit)? = null

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet?, style: Int) : super(ctx, attrs, style)

    init {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.native_ad_view_switch_node, this)
    }

    fun setLoadFailureRequest(request: (String) -> Unit) {
        loadFailureListener = request
    }

    fun setLoadSuccessRequest(request: () -> Unit) {
        loadSuccessListener = request
    }

    fun setClickRequest(request: () -> Unit) {
        clickListener = request
    }

    fun loadNativeAd() {
        val adLoader = AdLoader.Builder(context, NativeAdCode.value)
            .forNativeAd { nativeAd ->
                setNativeAd(nativeAd)
                mediaView?.gone()
                secondaryView?.gone()
                ratingBar?.gone()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    loadFailureListener?.invoke(p0.message)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    loadSuccessListener?.invoke()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    clickListener?.invoke()
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}