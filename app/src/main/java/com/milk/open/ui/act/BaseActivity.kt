package com.milk.open.ui.act

import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.milk.simple.listener.MultipleClickListener

abstract class BaseActivity : FragmentActivity(), View.OnClickListener {

    private val multipleClickListener by lazy {
        object : MultipleClickListener() {
            override fun onMultipleClick(view: View) =
                this@BaseActivity.onMultipleClick(view)
        }
    }

    override fun onClick(p0: View?) = multipleClickListener.onClick(p0)

    protected open fun onMultipleClick(view: View) = Unit

//    override fun getResources(): Resources {
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources())
//        }
//        return super.getResources()
//    }

    protected open fun isInterceptKeyDownEvent() = false

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && isInterceptKeyDownEvent()) {
            moveTaskToBack(true)
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}