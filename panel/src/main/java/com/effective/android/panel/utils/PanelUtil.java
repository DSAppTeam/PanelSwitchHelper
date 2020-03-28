package com.effective.android.panel.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.effective.android.panel.Constants;
import com.effective.android.panel.LogTracker;


/**
 * panel helper
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public final class PanelUtil {

    private static final String TAG = PanelUtil.class.getSimpleName();


    public static void showKeyboard(Context context, View view) {
        view.requestFocus();
        InputMethodManager mInputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputManager.showSoftInput(view, 0);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager mInputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getDeviceVendor(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(Constants.DEVICE_VENDOR, "");
    }

    public static boolean setDeviceVendor(Context context, String vendor) {
        SharedPreferences sp = context.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.edit().putString(Constants.DEVICE_VENDOR, vendor).commit();
    }


    public static int getKeyBoardHeight(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isPortrait = DisplayUtil.isPortrait(context);
        String key = isPortrait ?
                Constants.KEYBOARD_HEIGHT_FOR_P : Constants.KEYBOARD_HEIGHT_FOR_L;
        float defaultHeight = isPortrait ?
                Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_P : Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_L;
        return sp.getInt(key, DisplayUtil.dip2px(context, defaultHeight));
    }


    public static boolean setKeyBoardHeight(Context context, int height) {
        SharedPreferences sp = context.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isPortrait = DisplayUtil.isPortrait(context);
        //filter wrong data
        //mActivity.getWindow().getDecorView().getHeight() may be right when onGlobalLayout listener
        if (!isPortrait) {
            int portraitHeight = sp.getInt(Constants.KEYBOARD_HEIGHT_FOR_P, DisplayUtil.dip2px(context, Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_P));
            if (height >= portraitHeight) {
                LogTracker.getInstance().log(TAG + "#setKeyBoardHeight", "filter wrong data : " + portraitHeight + " -> " + height);
                return false;
            }
        }
        String key = DisplayUtil.isPortrait(context) ?
                Constants.KEYBOARD_HEIGHT_FOR_P : Constants.KEYBOARD_HEIGHT_FOR_L;
        return sp.edit().putInt(key, height).commit();
    }
}
