package com.effective.android.panel

import android.text.TextUtils
import android.view.View
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener
import com.effective.android.panel.interfaces.listener.OnViewClickListener
import com.effective.android.panel.view.PanelView

/**
 * single logTracker
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class LogTracker private constructor(private val openLog: Boolean) : OnEditFocusChangeListener, OnKeyboardStateListener, OnPanelChangeListener, OnViewClickListener {
    fun log(methodName: String, message: String) {
        if (TextUtils.isEmpty(methodName) || TextUtils.isEmpty(message)) {
            return
        }
        if (openLog) {
            android.util.Log.d(Constants.LOG_TAG, "$methodName -- $message")
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        log("$TAG#onFocusChange", "EditText has focus ( $hasFocus )")
    }

    override fun onKeyboardChange(show: Boolean) {
        log("$TAG#onKeyboardChange", "Keyboard is showing ( $show )")
    }

    override fun onKeyboard() {
        log("$TAG#onKeyboard", "panel： keyboard")
    }

    override fun onNone() {
        log("$TAG#onNone", "panel： none")
    }

    override fun onPanel(view: PanelView?) {
        log("$TAG#onPanel", "panel：" + (view?.toString() ?: "null"))
    }

    override fun onPanelSizeChange(panelView: PanelView?, portrait: Boolean, oldWidth: Int, oldHeight: Int, width: Int, height: Int) {
        log("$TAG#onPanelSizeChange", "panelView is " + (panelView?.toString()
                ?: "null" +
                " portrait : " + portrait +
                " oldWidth : " + oldWidth + " oldHeight : " + oldHeight +
                " width : " + width + " height : " + height))
    }

    override fun onClickBefore(view: View?) {
        log("$TAG#onViewClick", "view is " + (view?.toString() ?: " null "))
    }

    companion object {
        private val TAG = LogTracker::class.java.simpleName
        @Volatile
        private var sInstance: LogTracker? = null

        fun Log(methodName: String, message: String) {
            instance!!.log(methodName, message)
        }

        @JvmStatic
        val instance: LogTracker
            get() {
                if (sInstance == null) {
                    synchronized(LogTracker::class.java) {
                        if (sInstance == null) {
                            sInstance = LogTracker(Constants.DEBUG)
                        }
                    }
                }
                return sInstance!!
            }
    }

}