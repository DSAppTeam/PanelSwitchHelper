package com.effective.android.panel.utils.cutShort;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Method;

/**
 * 华为刘海计算
 * https://devcenter-test.huawei.com/consumer/cn/devservice/doc/50114
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class HuaweiCutShort implements DeviceCutShort {

    public static final String VENDOR = "Huawei";

    @Override
    public boolean hasCutShort(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (Boolean)get.invoke(HwNotchSizeUtil);
            return ret;
        } catch (Exception var8) {
            var8.printStackTrace();
            return ret;
        } finally {
            ;
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
        try {
            int[] widthAndHeight = getHuaweiDisplayCutoutSize(view.getContext());
            return widthAndHeight != null && widthAndHeight.length == 2 ? widthAndHeight[1] : 0;
        } catch (Exception var2) {
            var2.printStackTrace();
            return 0;
        }
    }

    public int[] getHuaweiDisplayCutoutSize(Context context) {
        int[] ret = new int[]{0, 0};

        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[])((int[])get.invoke(HwNotchSizeUtil));
            return ret;
        } catch (Exception var8) {
            var8.printStackTrace();
            return ret;
        } finally {
            ;
        }
    }

}
