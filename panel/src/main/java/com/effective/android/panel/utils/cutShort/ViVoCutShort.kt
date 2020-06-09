package com.effective.android.panel.utils.cutShort

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.View
import com.effective.android.panel.utils.DisplayUtil

/**
 * vivo刘海计算
 * https://dev.vivo.com.cn/documentCenter/doc/103
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class ViVoCutShort : DeviceCutShort {

    override fun hasCutShort(context: Context): Boolean {
        return try {
            val manufacturer = Build.MANUFACTURER
            var isVivo = false
            var hasDisplayCutout = false
            if (!TextUtils.isEmpty(manufacturer) && manufacturer.equals("vivo", ignoreCase = true)) {
                isVivo = true
            }
            val cl = context.classLoader
            val ftFeature = cl.loadClass("android.util.FtFeature")
            val get = ftFeature.getMethod("isFeatureSupport", Integer.TYPE)
            hasDisplayCutout = get.invoke(ftFeature, 32) as Boolean
            isVivo && hasDisplayCutout
        } catch (var7: Exception) {
            var7.printStackTrace()
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
        } else DisplayUtil.dip2px(view.context, 27.0f)
    }

    companion object {
        const val VENDOR = "ViVo"
    }
}