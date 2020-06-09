package com.effective.android.panel.utils.cutShort

import android.content.Context
import android.view.View

/**
 * 华为刘海计算
 * https://devcenter-test.huawei.com/consumer/cn/devservice/doc/50114
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class HuaweiCutShort : DeviceCutShort {
    override fun hasCutShort(context: Context): Boolean {
        var ret = false
        return try {
            val cl = context.classLoader
            val hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = hwNotchSizeUtil.getMethod("hasNotchInScreen")
            ret = get.invoke(hwNotchSizeUtil) as Boolean
            ret
        } catch (var8: Exception) {
            var8.printStackTrace()
            ret
        } finally {
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
            val widthAndHeight = getHuaweiDisplayCutoutSize(view.context)
            if (widthAndHeight != null && widthAndHeight.size == 2) widthAndHeight[1] else 0
        } catch (var2: Exception) {
            var2.printStackTrace()
            0
        }
    }

    private fun getHuaweiDisplayCutoutSize(context: Context): IntArray? {
        var ret = intArrayOf(0, 0)
        return try {
            val cl = context.classLoader
            val hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = hwNotchSizeUtil.getMethod("getNotchSize")
            ret = get.invoke(hwNotchSizeUtil) as IntArray
            ret
        } catch (var8: Exception) {
            var8.printStackTrace()
            ret
        } finally {
        }
    }

    companion object {
        const val VENDOR = "Huawei"
    }
}