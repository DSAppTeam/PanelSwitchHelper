package com.effective.android.panel.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.effective.android.panel.Constants
import com.effective.android.panel.LogTracker
import com.effective.android.panel.utils.DisplayUtil.dip2px
import com.effective.android.panel.utils.DisplayUtil.isPortrait

/**
 * panel helper
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
object PanelUtil {

    private var pHeight: Int = -1
    private var lHeight: Int = -1
    private const val LIMIT_MIN = 100

    @JvmStatic
    fun clearData(context: Context){
        pHeight = -1
        lHeight = -1
        val sp = context.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE)
        sp.edit().clear().apply()
    }

    @JvmStatic
    fun showKeyboard(context: Context, view: View): Boolean {
        view.requestFocus()
        val mInputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return mInputManager.showSoftInput(view, 0)
    }

    @JvmStatic
    fun hideKeyboard(context: Context, view: View): Boolean {
        val mInputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return mInputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @JvmStatic
    fun getKeyBoardHeight(context: Context): Int {
        val isPortrait = isPortrait(context)
        if (isPortrait && pHeight != -1) {
            return pHeight
        }
        if (!isPortrait && lHeight != -1) {
            return lHeight
        }
        val sp = context.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val key = if (isPortrait) Constants.KEYBOARD_HEIGHT_FOR_P else Constants.KEYBOARD_HEIGHT_FOR_L
        val defaultHeight = dip2px(context, if (isPortrait) Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_P else Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_L)
        val result = sp.getInt(key, defaultHeight)
        if (result != defaultHeight) {
            if (isPortrait) {
                pHeight = result
            } else {
                lHeight = result
            }
        }
        return result
    }

    @JvmStatic
    fun setKeyBoardHeight(context: Context, height: Int): Boolean {
        /**
         * 部分rom，比如findx，底部有4Px的像素用于处理用户手势。再比如一些支持用户从底部滑动来交互的 vivo/红米 版本，底部也有一个小的区域用于捕获用户手势。
         */
        if (height < LIMIT_MIN) {
            LogTracker.log("PanelUtil#onGlobalLayout", "KeyBoardHeight is : $height, it may be a wrong value, just ignore!")
            return false
        }
        val isPortrait = isPortrait(context)
        if (isPortrait && pHeight == height) {
            return false
        }
        if (!isPortrait && lHeight == height) {
            return false
        }
        val sp = context.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val key = if (isPortrait) Constants.KEYBOARD_HEIGHT_FOR_P else Constants.KEYBOARD_HEIGHT_FOR_L
        val result = sp.edit().putInt(key, height).commit()
        if (result) {
            if (isPortrait) {
                pHeight = height
            } else {
                lHeight = height
            }
        }
        return result
    }

    internal fun hasMeasuredKeyboard(context: Context): Boolean {
        getKeyBoardHeight(context)
        return pHeight != -1 || lHeight != -1
    }
}