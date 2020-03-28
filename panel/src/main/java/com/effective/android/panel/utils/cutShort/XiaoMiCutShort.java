package com.effective.android.panel.utils.cutShort;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import java.lang.reflect.Method;

/**
 * 小米刘海计算
 * https://dev.mi.com/console/doc/detail?pId=1293
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class XiaoMiCutShort implements DeviceCutShort {

    public static final String VENDOR = "XiaoMi";

    @Override
    public boolean hasCutShort(Context context) {
        boolean ret = false;
        try {
            String manufacturer = Build.MANUFACTURER;
            boolean isXiaomi = false;
            if (!TextUtils.isEmpty(manufacturer) && manufacturer.equalsIgnoreCase("Xiaomi")) {
                isXiaomi = true;
            }
            if (isXiaomi && getInt("ro.miui.notch", context) == 1) {
                ret = true;
            }
            return ret;
        } catch (Exception var7) {
            var7.printStackTrace();
            return ret;
        } finally {
            ;
        }
    }

    @Override
    public int getCutShortHeight(View view) {
        try {
            Context context = view.getContext();
            int resourceId = context.getResources().getIdentifier("notch_height", "dimen", "android");
            int height = 0;
            if (resourceId > 0) {
                height = context.getResources().getDimensionPixelSize(resourceId);
            }

            if (height == 0) {
                int resourceIdForStatusHeight = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceIdForStatusHeight > 0) {
                    height = context.getResources().getDimensionPixelSize(resourceIdForStatusHeight);
                }
            }

            return height;
        } catch (Exception var4) {
            var4.printStackTrace();
            return 0;
        }
    }

    public int getInt(String key, Context context) {
        int result = 0;

        try {
            ClassLoader classLoader = context.getClassLoader();
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            Class[] paramTypes = new Class[]{String.class, Integer.TYPE};
            Method getInt = SystemProperties.getMethod("getInt", paramTypes);
            Object[] params = new Object[]{new String(key), new Integer(0)};
            result = (Integer) getInt.invoke(SystemProperties, params);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return result;
    }
}
