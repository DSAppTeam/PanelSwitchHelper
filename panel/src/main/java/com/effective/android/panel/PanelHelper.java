package com.effective.android.panel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;

/**
 * panel helper
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public final class PanelHelper {

    public static void showKeyboard(Context context, View view) {
        InputMethodManager mInputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        mInputManager.showSoftInput(view, 0);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager mInputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context.getResources(), Constants.STATUS_BAR_HEIGHT_RES_NAME);
    }

    public static int getNavigationBarHeight(Context context) {
        return getInternalDimensionSize(context.getResources(), Constants.NAVIGATION_BAR_HEIGHT_RES_NAME);
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, Constants.DIMEN, Constants.ANDROID);
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @TargetApi(14)
    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    public static int getKeyBoardHeight(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isPortrait = PanelHelper.isPortrait(activity);
        String key = isPortrait ?
                Constants.KEYBOARD_HEIGHT_FOR_P : Constants.KEYBOARD_HEIGHT_FOR_L;
        float defaultHeight = isPortrait ?
                Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_P : Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_L;
        return sp.getInt(key, dip2px(activity, defaultHeight));
    }

    public static boolean setKeyBoardHeight(Activity activity, int height) {
        SharedPreferences sp = activity.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String key = PanelHelper.isPortrait(activity) ?
                Constants.KEYBOARD_HEIGHT_FOR_P : Constants.KEYBOARD_HEIGHT_FOR_L;
        return sp.edit().putInt(key, height).commit();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
