package com.effective.android.panel.utils.cutShort;

import android.content.Context;
import android.os.Build;
import android.view.View;

/**
 * 官方刘海计算
 * https://developer.android.com/guide/topics/display-cutout
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class OfficialCutShort implements DeviceCutShort {

    public static final String VENDOR = "Officail";

    @Override
    public boolean hasCutShort(Context context) {
        return true;
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
        int displayCutoutHeight = 0;
        try {
            if (Build.VERSION.SDK_INT >= 28 && view.getRootWindowInsets() != null && view.getRootWindowInsets().getDisplayCutout() != null) {
                displayCutoutHeight = view.getRootWindowInsets().getDisplayCutout().getSafeInsetTop();
            }
            return displayCutoutHeight;
        } catch (Exception var3) {
            return 0;
        }
    }
}
