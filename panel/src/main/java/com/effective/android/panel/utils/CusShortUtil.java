package com.effective.android.panel.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
    private static String vendor = "";

    private CusShortUtil() {}

    public static int getDeviceCutShortHeight(@NonNull View view) {
        switch (vendor) {
            case HuaweiCutShort.VENDOR: {
                return HUAWEI.getCutShortHeight(view);
            }
            case OfficialCutShort.VENDOR: {
                return OFFICIAL.getCutShortHeight(view);
            }
            case OppoCutShort.VENDOR: {
                return OPPO.getCutShortHeight(view);
            }
            case ViVoCutShort.VENDOR: {
                return VIVO.getCutShortHeight(view);
            }
            case XiaoMiCutShort.VENDOR: {
                return XIAOMI.getCutShortHeight(view);
            }
            case SamsungCutShort.VENDOR: {
                return SAMSUNG.getCutShortHeight(view);
            }
            default: {
                Context context = view.getContext();
                if (HUAWEI.hasCutShort(context)) {
                    vendor = HuaweiCutShort.VENDOR;
                    PanelUtil.setDeviceVendor(context,vendor);
                    return HUAWEI.getCutShortHeight(view);
                }
                if (OPPO.hasCutShort(context)) {
                    vendor = OppoCutShort.VENDOR;
                    PanelUtil.setDeviceVendor(context,vendor);
                    return OPPO.getCutShortHeight(view);
                }
                if (VIVO.hasCutShort(context)) {
                    vendor = ViVoCutShort.VENDOR;
                    PanelUtil.setDeviceVendor(context,vendor);
                    return VIVO.getCutShortHeight(view);
                }
                if (XIAOMI.hasCutShort(context)) {
                    vendor = XiaoMiCutShort.VENDOR;
                    PanelUtil.setDeviceVendor(context,vendor);
                    return XIAOMI.getCutShortHeight(view);
                }
                if (SAMSUNG.hasCutShort(context)) {
                    vendor = SamsungCutShort.VENDOR;
                    PanelUtil.setDeviceVendor(context,vendor);
                    return SAMSUNG.getCutShortHeight(view);
                }
                vendor = OfficialCutShort.VENDOR;
                PanelUtil.setDeviceVendor(context,vendor);
                return OFFICIAL.getCutShortHeight(view);
            }
        }
    }
}
