package com.effective.android.panel.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.effective.android.panel.Constants
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

    @JvmStatic
    fun showKeyboard(context: Context, view: View) {
        view.requestFocus()
        val mInputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mInputManager.showSoftInput(view, 0)
    }

    @JvmStatic
    fun hideKeyboard(context: Context, view: View) {
        val mInputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mInputManager.hideSoftInputFromWindow(view.windowToken, 0)
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
        val defaultHeight = if (isPortrait) Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_P else Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_L
        val result = sp.getInt(key, dip2px(context, defaultHeight))
        if (isPortrait) {
            pHeight = result
        } else {
            lHeight = result
        }
        return result
    }

    @JvmStatic
    fun setKeyBoardHeight(context: Context, height: Int): Boolean {
        val isPortrait = isPortrait(context)
        if (isPortrait && pHeight == height) {
            return true
        }
        if (!isPortrait && lHeight == height) {
            return true
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
}