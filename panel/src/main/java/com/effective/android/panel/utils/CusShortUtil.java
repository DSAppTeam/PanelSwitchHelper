package com.effective.android.panel.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.effective.android.panel.utils.cutShort.DeviceCutShort;
import com.effective.android.panel.utils.cutShort.HuaweiCutShort;
import com.effective.android.panel.utils.cutShort.OfficialCutShort;
import com.effective.android.panel.utils.cutShort.OppoCutShort;
import com.effective.android.panel.utils.cutShort.SamsungCutShort;
import com.effective.android.panel.utils.cutShort.ViVoCutShort;
import com.effective.android.panel.utils.cutShort.XiaoMiCutShort;

/**
 * 刘海计算
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class CusShortUtil {

    private static final DeviceCutShort HUAWEI = new HuaweiCutShort();
    private static final DeviceCutShort OFFICIAL = new OfficialCutShort();
    private static final DeviceCutShort OPPO = new OppoCutShort();
    private static final DeviceCutShort VIVO = new ViVoCutShort();
    private static final DeviceCutShort XIAOMI = new XiaoMiCutShort();
    private static final DeviceCutShort SAMSUNG = new SamsungCutShort();

    private CusShortUtil() {
    }

    public static int getDeviceCutShortHeight(@NonNull View view) {
        Context context = view.getContext();
        if (HUAWEI.hasCutShort(context)) {
            return HUAWEI.getCurrentCutShortHeight(view);
        }
        if (OPPO.hasCutShort(context)) {
            return OPPO.getCurrentCutShortHeight(view);
        }
        if (VIVO.hasCutShort(context)) {
            return VIVO.getCurrentCutShortHeight(view);
        }
        if (XIAOMI.hasCutShort(context)) {
            return XIAOMI.getCurrentCutShortHeight(view);
        }
        if (SAMSUNG.hasCutShort(context)) {
            return SAMSUNG.getCurrentCutShortHeight(view);
        }
        return OFFICIAL.getCurrentCutShortHeight(view);
    }
}
