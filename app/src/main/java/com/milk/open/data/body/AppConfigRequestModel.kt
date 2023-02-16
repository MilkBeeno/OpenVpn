package com.milk.open.data.body

import com.milk.open.BuildConfig

data class AppConfigRequestModel(
    var appId: String = BuildConfig.AD_APP_ID,
    var channel: String = BuildConfig.AD_APP_CHANNEL,
    var pkgVersion: String = BuildConfig.AD_APP_VERSION
)