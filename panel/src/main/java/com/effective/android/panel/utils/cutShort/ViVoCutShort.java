package com.effective.android.panel.utils.cutShort;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.effective.android.panel.utils.DisplayUtil;
import com.effective.android.panel.utils.PanelUtil;

import java.lang.reflect.Method;

/**
 * vivo刘海计算
 * https://dev.vivo.com.cn/documentCenter/doc/103
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ViVoCutShort implements DeviceCutShort {

    public static final String VENDOR = "ViVo";

    @Override
    public boolean hasCutShort(Context context) {
        try {
            String manufacturer = Build.MANUFACTURER;
            boolean isVivo = false;
            boolean hasDisplayCutout = false;
            if (!TextUtils.isEmpty(manufacturer) && manufacturer.equalsIgnoreCase("vivo")) {
                isVivo = true;
            }

            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("android.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", Integer.TYPE);
            hasDisplayCutout = (Boolean)get.invoke(FtFeature, 32);
            return isVivo && hasDisplayCutout;
        } catch (Exception var7) {
            var7.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isCusShortVisible(Context context) {
        return true;
    }

    @Override
    public int getCurrentCutShortHeight(View view) {
        Context context = view.getContext();
        if(!isCusShortVisible(context)){
            return 0;
        }
        return DisplayUtil.dip2px(view.getContext(),27.0F);
    }

}
