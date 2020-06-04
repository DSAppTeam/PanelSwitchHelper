package com.example.demo.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.Size;

import java.lang.reflect.Field;

/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class DisplayUtils {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static Pair<Integer,Integer> getScreenSize(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return new Pair<Integer,Integer>(dm.widthPixels, dm.heightPixels);
    }

    public static boolean isPortrait(Context context){
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

    }

    public static void  checkoutOrientation(Activity activity) {
        activity.setRequestedOrientation(isPortrait(activity) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer)field.get(o);
            result = context.getResources().getDimensionPixelSize(x);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        if (result == 0) {
            result = dip2px(context, 25.0F);
        }

        return result;
    }
}
