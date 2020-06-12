package com.effective.android.panel.utils.cutShort

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import com.effective.android.panel.LogTracker
import java.util.*

/**
 * 三星刘海计算
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class SamSungCutShort : DeviceCutShort {

    override fun hasCutShort(context: Context): Boolean {
        return try {
            val res = context.resources
            val resId = res.getIdentifier("config_mainBuiltInDisplayCutout", "string", "android")
            val spec = if (resId > 0) res.getString(resId) else null
            spec != null && !TextUtils.isEmpty(spec)
        } catch (var5: Exception) {
            LogTracker.log("cutShort#hasCutShort","try Samsung Device,but fail")
            false
        }
    }

    override fun isCusShortVisible(context: Context): Boolean {
        return true
    }

    override fun getCurrentCutShortHeight(view: View): Int {
        val context = view.context
        return if (!isCusShortVisible(context)) {
            0
        } else try {
            if (Build.VERSION.SDK_INT >= 23 && view != null && view.rootWindowInsets != null) {
                val windowInsets = view.rootWindowInsets
                val method = WindowInsets::class.java.getDeclaredMethod("getDisplayCutout")
                val displayCutoutInstance = method.invoke(windowInsets)
                val safeInsets = Rect()
                val boundingRects: MutableList<Rect> = mutableListOf()
                val cls: Class<*> = displayCutoutInstance.javaClass
                val top = cls.getDeclaredMethod("getSafeInsetTop").invoke(displayCutoutInstance) as Int
                val bottom = cls.getDeclaredMethod("getSafeInsetBottom").invoke(displayCutoutInstance) as Int
                val left = cls.getDeclaredMethod("getSafeInsetLeft").invoke(displayCutoutInstance) as Int
                val right = cls.getDeclaredMethod("getSafeInsetRight").invoke(displayCutoutInstance) as Int
                safeInsets[left, top, right] = bottom
                boundingRects.addAll(cls.getDeclaredMethod("getBoundingRects").invoke(displayCutoutInstance) as MutableList<Rect>)
                if (top != 0) {
                    return top
                }
                if (bottom != 0) {
                    return bottom
                }
                if (left != 0) {
                    return left
                }
                if (right != 0) {
                    return right
                }
            }
            0
        } catch (var11: Exception) {
            LogTracker.log("cutShort#getCurrentCutShortHeight","try Samsung Device,but fail")
            0
        }
    }
}