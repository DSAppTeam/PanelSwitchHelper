package com.effective.android.panel.helper.cutShort;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import java.lang.reflect.Method;
/**
 * oppo 刘海计算
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class OppoCutShort implements DeviceCutShort {

    @Override
    public boolean hasCutShort(Context context) {
        String manufacturer = "";

        try {
            manufacturer = Build.MANUFACTURER;
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        boolean isOppo = false;
        if (!TextUtils.isEmpty(manufacturer) && manufacturer.equalsIgnoreCase("OPPO")) {
            isOppo = true;
        }

        return isOppo && context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    @Override
    public int getCutShortHeight(View view) {
        return 80;
    }

}
