package com.effective.android.panel.utils

import android.view.View
import com.effective.android.panel.utils.cutShort.*

/**
 * 刘海计算
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
object CusShortUtil {

    private val HUAWEI: DeviceCutShort = HuaweiCutShort()
    private val OFFICIAL: DeviceCutShort = OfficialCutShort()
    private val OPPO: DeviceCutShort = OppoCutShort()
    private val VIVO: DeviceCutShort = ViVoCutShort()
    private val XIAOMI: DeviceCutShort = XiaoMiCutShort()
    private val SAMSUNG: DeviceCutShort = SamSungCutShort()

    @JvmStatic
    fun getDeviceCutShortHeight(view: View): Int {
        val context = view.context
        if (HUAWEI.hasCutShort(context)) {
            return HUAWEI.getCurrentCutShortHeight(view)
        }
        if (OPPO.hasCutShort(context)) {
            return OPPO.getCurrentCutShortHeight(view)
        }
        if (VIVO.hasCutShort(context)) {
            return VIVO.getCurrentCutShortHeight(view)
        }
        if (XIAOMI.hasCutShort(context)) {
            return XIAOMI.getCurrentCutShortHeight(view)
        }
        return if (SAMSUNG.hasCutShort(context)) {
            SAMSUNG.getCurrentCutShortHeight(view)
        } else OFFICIAL.getCurrentCutShortHeight(view)
    }
}