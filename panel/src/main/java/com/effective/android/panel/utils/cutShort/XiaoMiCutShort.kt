package com.effective.android.panel.utils.cutShort

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.View

/**
 * 小米刘海计算
 * https://dev.mi.com/console/doc/detail?pId=1293
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class XiaoMiCutShort : DeviceCutShort {

    override fun hasCutShort(context: Context): Boolean {
        var ret = false
        return try {
            val manufacturer = Build.MANUFACTURER
            var isXiaomi = false
            if (!TextUtils.isEmpty(manufacturer) && manufacturer.equals("Xiaomi", ignoreCase = true)) {
                isXiaomi = true
            }
            if (isXiaomi && getInt("ro.miui.notch", context) == 1) {
                ret = true
            }
            ret
        } catch (var7: Exception) {
            var7.printStackTrace()
            ret
        } finally {
        }
    }

    @TargetApi(17)
    override fun isCusShortVisible(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, "force_black", 0) == 1
    }

    override fun getCurrentCutShortHeight(view: View): Int {
        val context = view.context
        return if (!isCusShortVisible(context)) {
            0
        } else try {
            val resourceId = context.resources.getIdentifier("notch_height", "dimen", "android")
            var height = 0
            if (resourceId > 0) {
                height = context.resources.getDimensionPixelSize(resourceId)
            }
            if (height == 0) {
                val resourceIdForStatusHeight = context.resources.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceIdForStatusHeight > 0) {
                    height = context.resources.getDimensionPixelSize(resourceIdForStatusHeight)
                }
            }
            height
        } catch (var4: Exception) {
            var4.printStackTrace()
            0
        }
    }

    private fun getInt(key: String, context: Context): Int {
        var result = 0
        try {
            val classLoader = context.classLoader
            val systemProperties = classLoader.loadClass("android.os.SystemProperties")
            val paramTypes = arrayOf(String::class.java, Integer.TYPE)
            val getInt = systemProperties.getMethod("getInt", *paramTypes)
            val params = arrayOf<Any>(StringBuilder(key), 0)
            result = getInt.invoke(systemProperties, *params) as Int
        } catch (var8: Exception) {
            var8.printStackTrace()
        }
        return result
    }

    companion object {
        const val VENDOR = "XiaoMi"
    }
}