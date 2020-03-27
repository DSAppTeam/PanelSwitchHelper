package com.effective.android.panel.helper.cutShort;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.effective.android.panel.helper.PanelHelper;

import java.lang.reflect.Method;

/**
 * 官方刘海计算
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class OfficialCutShort implements DeviceCutShort {

    @Override
    public boolean hasCutShort(Context context) {
        return true;
    }

    @Override
    public int getCutShortHeight(View view) {
        int displayCutoutHeight = 0;
        try {
            if (Build.VERSION.SDK_INT >= 28 && view != null && view.getRootWindowInsets() != null && view.getRootWindowInsets().getDisplayCutout() != null) {
                displayCutoutHeight = view.getRootWindowInsets().getDisplayCutout().getSafeInsetTop();
            }
            return displayCutoutHeight;
        } catch (Exception var3) {
            return 0;
        }
    }
}
