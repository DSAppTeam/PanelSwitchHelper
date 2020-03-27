package com.effective.android.panel.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.effective.android.panel.helper.cutShort.DeviceCutShort;
import com.effective.android.panel.helper.cutShort.HuaweiCutShort;
import com.effective.android.panel.helper.cutShort.OfficialCutShort;
import com.effective.android.panel.helper.cutShort.SamsungCutShort;
import com.effective.android.panel.helper.cutShort.ViVoCutShort;
import com.effective.android.panel.helper.cutShort.XiaoMiCutShort;

/**
 * 刘海计算
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class CusShortHelper {

    private static final DeviceCutShort HUAWEI = new HuaweiCutShort();
    private static final DeviceCutShort OFFICIAL = new OfficialCutShort();
    private static final DeviceCutShort OPPO = new OfficialCutShort();
    private static final DeviceCutShort VIVO = new ViVoCutShort();
    private static final DeviceCutShort XIAOMI = new XiaoMiCutShort();
    private static final DeviceCutShort SAMSUNG = new SamsungCutShort();

    private CusShortHelper() {

    }

    public int getDeviceCutShortHeight(@NonNull View view) {
        Context context = view.getContext();
        if (HUAWEI.hasCutShort(context)) {
            return HUAWEI.getCutShortHeight(view);
        } else if (OPPO.hasCutShort(context)) {
            return OPPO.getCutShortHeight(view);
        } else if (VIVO.hasCutShort(context)) {
            return VIVO.getCutShortHeight(view);
        } else if (XIAOMI.hasCutShort(context)) {
            return XIAOMI.getCutShortHeight(view);
        } else if (SAMSUNG.hasCutShort(context)) {
            return SAMSUNG.getCutShortHeight(view);
        } else {
            return OFFICIAL.getCutShortHeight(view);
        }
    }
}
