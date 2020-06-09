package com.effective.android.panel.utils.cutShort

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.View

/**
 * oppo 刘海计算
 * https://id.heytap.com/index.html?callback=https%3A//open.oppomobile.com/service/message/detail%3Fid%3D61876
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class OppoCutShort : DeviceCutShort {

    override fun hasCutShort(context: Context): Boolean {
        var manufacturer = ""
        try {
            manufacturer = Build.MANUFACTURER
        } catch (var3: Exception) {
            var3.printStackTrace()
        }
        var isOppo = false
        if (!TextUtils.isEmpty(manufacturer) && manufacturer.equals("OPPO", ignoreCase = true)) {
            isOppo = true
        }
        return isOppo && context.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
    }

    override fun isCusShortVisible(context: Context): Boolean {
        return true
    }

    override fun getCurrentCutShortHeight(view: View): Int {
        val context = view.context
        return if (!isCusShortVisible(context)) {
            0
        } else 80
    }

    companion object {
        const val VENDOR = "Oppo"
    }
}