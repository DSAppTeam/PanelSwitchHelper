package com.effective.android.panel.utils.cutShort;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 三星刘海计算
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class SamSungCutShort implements DeviceCutShort {

    public static final String VENDOR = "Samsung";

    @Override
    public boolean hasCutShort(Context context) {
        try {
            Resources res = context.getResources();
            int resId = res.getIdentifier("config_mainBuiltInDisplayCutout", "string", "android");
            String spec = resId > 0 ? res.getString(resId) : null;
            boolean hasDisplayCutout = spec != null && !TextUtils.isEmpty(spec);
            return hasDisplayCutout;
        } catch (Exception var5) {
            var5.printStackTrace();
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
        try {
            if (Build.VERSION.SDK_INT >= 23 && view != null && view.getRootWindowInsets() != null) {
                WindowInsets windowInsets = view.getRootWindowInsets();
                Method method = WindowInsets.class.getDeclaredMethod("getDisplayCutout");
                Object displayCutoutInstance = method.invoke(windowInsets);
                Rect safeInsets = new Rect();
                List<Rect> boundingRects = new ArrayList();
                Class cls = displayCutoutInstance.getClass();
                int top = (Integer)cls.getDeclaredMethod("getSafeInsetTop").invoke(displayCutoutInstance);
                int bottom = (Integer)cls.getDeclaredMethod("getSafeInsetBottom").invoke(displayCutoutInstance);
                int left = (Integer)cls.getDeclaredMethod("getSafeInsetLeft").invoke(displayCutoutInstance);
                int right = (Integer)cls.getDeclaredMethod("getSafeInsetRight").invoke(displayCutoutInstance);
                safeInsets.set(left, top, right, bottom);
                boundingRects.addAll((List)cls.getDeclaredMethod("getBoundingRects").invoke(displayCutoutInstance));
                if (top != 0) {
                    return top;
                }

                if (bottom != 0) {
                    return bottom;
                }

                if (left != 0) {
                    return left;
                }

                if (right != 0) {
                    return right;
                }
            }

            return 0;
        } catch (Exception var11) {
            var11.printStackTrace();
            return 0;
        }
    }
}
