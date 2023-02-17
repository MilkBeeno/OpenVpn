package com.milk.open.data

import com.google.gson.annotations.SerializedName

data class AppConfigModel(
    @SerializedName("Vw2345671")
    var share_copy: String = "",
    @SerializedName("Vw2345672")
    var Shield_app_bundle: String = "",
    @SerializedName("Vw2345673")
    var mainNativeAd: String = "",
    @SerializedName("Vw2345674")
    var openAd: String = "",
    @SerializedName("Vw2345675")
    var connectedInsertAd: String = "",
    @SerializedName("Vw2345676")
    var connectedNativeAd: String = "",
    @SerializedName("Vw2345677")
    var disconnectInsertAd: String = "",
    @SerializedName("Vw2345678")
    var disconnectNativeAd: String = "",
    @SerializedName("Vw2345679")
    var switchNativeAd: String = "",
)