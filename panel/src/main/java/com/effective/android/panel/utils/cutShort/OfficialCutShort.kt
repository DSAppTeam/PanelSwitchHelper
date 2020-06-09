package com.effective.android.panel.utils.cutShort

import android.content.Context
import android.os.Build
import android.view.View

/**
 * 官方刘海计算
 * https://developer.android.com/guide/topics/display-cutout
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class OfficialCutShort : DeviceCutShort {

    override fun hasCutShort(context: Context): Boolean {
        return true
    }

    override fun isCusShortVisible(context: Context): Boolean {
        return true
    }

    override fun getCurrentCutShortHeight(view: View): Int {
        val context = view.context
        if (!isCusShortVisible(context)) {
            return 0
        }
        var displayCutoutHeight = 0
        return try {
            if (Build.VERSION.SDK_INT >= 28 && view.rootWindowInsets != null && view.rootWindowInsets.displayCutout != null) {
                displayCutoutHeight = view.rootWindowInsets.displayCutout!!.safeInsetTop
            }
            displayCutoutHeight
        } catch (var3: Exception) {
            0
        }
    }

    companion object {
        const val VENDOR = "Officail"
    }
}