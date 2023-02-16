package com.milk.open.ui.act

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.core.app.NotificationManagerCompat
import com.milk.open.R
import com.milk.open.constant.KvKey
import com.milk.open.databinding.ActivityLaunchBinding
import com.milk.open.friebase.FireBaseManager
import com.milk.open.friebase.FirebaseKey
import com.milk.open.repository.AppRepository
import com.milk.open.ui.dialog.OpenNotificationDialog
import com.milk.open.util.Notification
import com.milk.simple.ktx.*
import com.milk.simple.log.Logger
import com.milk.simple.mdr.KvManger
import java.security.MessageDigest

class LaunchActivity : AbstractActivity() {
    private val binding by lazy { ActivityLaunchBinding.inflate(layoutInflater) }
    private val openNotificationDialog by lazy { OpenNotificationDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        AppRepository.getConfig()
        val isFirst = KvManger.getBoolean(KvKey.FIRST_ENTER, true)
        if (isFirst) {
            binding.root.visible()
            KvManger.put(KvKey.FIRST_ENTER, false)
            initializeView()
            openNotification()
        } else {
            FireBaseManager.logEvent(FirebaseKey.ENTER_THE_STARTUP_PAGE)
            binding.root.gone()
            BackStackActivity.create(context = this, fromStartPage = true)
            finish()
        }
        // getHasKey()
    }

    private fun initializeView() {
        immersiveStatusBar(false)
        binding.ivSelect.isSelected = true
        binding.ivSelect.setOnClickListener(this)
        binding.tvStart.setOnClickListener(this)
        binding.tvPrivacy.setSpannableClick(
            Pair(string(R.string.launch_privacy),
                colorClickableSpan(color(R.color.FF0D5EFF)) {
                    val url = "https://res.duoglobalmaster.com/privacy.html"
                    WebActivity.create(this, url)
                })
        )
    }

    private fun openNotification() {
        val enable = NotificationManagerCompat.from(this).areNotificationsEnabled()
        if (!enable) {
            openNotificationDialog.show()
            openNotificationDialog.setConfirm {
                Notification.obtainNotification(this)
            }
        }
    }

    override fun onMultipleClick(view: View) {
        super.onMultipleClick(view)
        when (view) {
            binding.ivSelect -> {
                binding.ivSelect.isSelected = !binding.ivSelect.isSelected
            }
            binding.tvStart -> {
                if (binding.ivSelect.isSelected) {
                    FireBaseManager.logEvent(FirebaseKey.CLICK_START)
                    MainActivity.create(this)
                    finish()
                } else showToast(string(R.string.launch_privacy_agreement))
            }
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    private fun getHasKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Logger.d(
                    "包名是" + packageName + "密钥是：" + Base64.encodeToString(
                        md.digest(), Base64.DEFAULT
                    ), "KeyHash"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}