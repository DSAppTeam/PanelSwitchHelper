package com.effective.android.panel.utils.cutShort;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

/**
 * oppo 刘海计算
 * https://id.heytap.com/index.html?callback=https%3A//open.oppomobile.com/service/message/detail%3Fid%3D61876
 * Created by yummyLau on 20-03-27
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class OppoCutShort implements DeviceCutShort {

    public static final String VENDOR = "Oppo";

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
    public boolean isCusShortVisible(Context context) {
        return true;
    }

    @Override
    public int getCurrentCutShortHeight(View view) {
        Context context = view.getContext();
        if(!isCusShortVisible(context)){
            return 0;
        }
        return 80;
    }

}
