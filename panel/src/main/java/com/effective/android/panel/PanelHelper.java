package com.effective.android.panel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * panel helper
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public final class PanelHelper {

    private static final String TAG = PanelHelper.class.getSimpleName();

    public static void showKeyboard(Context context, View view) {
        view.requestFocus();
        InputMethodManager mInputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputManager.showSoftInput(view, 0);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager mInputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isFullScreen(Activity activity) {
        return (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static boolean isFullScreen(Window window) {
        return (window.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
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


    public static boolean isPortrait(@NonNull Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT: {
                return true;
            }
            case Configuration.ORIENTATION_LANDSCAPE: {
                return false;
            }
            default: {
                Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                if (point.x <= point.y) {
                    return true;
                } else {
                    return false;
                }
            }
        }
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

    @TargetApi(14)
    public static boolean isNavigationBarShow(Context context,Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = window.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    public static int getKeyBoardHeight(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isPortrait = PanelHelper.isPortrait(context);
        String key = isPortrait ?
                Constants.KEYBOARD_HEIGHT_FOR_P : Constants.KEYBOARD_HEIGHT_FOR_L;
        float defaultHeight = isPortrait ?
                Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_P : Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_L;
        return sp.getInt(key, dip2px(context, defaultHeight));
    }


    public static boolean setKeyBoardHeight(Context context, int height) {
        SharedPreferences sp = context.getSharedPreferences(Constants.KB_PANEL_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isPortrait = PanelHelper.isPortrait(context);
        //filter wrong data
        //mActivity.getWindow().getDecorView().getHeight() may be right when onGlobalLayout listener
        if (!isPortrait) {
            int portraitHeight = sp.getInt(Constants.KEYBOARD_HEIGHT_FOR_P, dip2px(context, Constants.DEFAULT_KEYBOARD_HEIGHT_FOR_P));
            if (height >= portraitHeight) {
                LogTracker.getInstance().log(TAG + "#setKeyBoardHeight", "filter wrong data : " + portraitHeight + " -> " + height);
                return false;
            }
        }
        String key = PanelHelper.isPortrait(context) ?
                Constants.KEYBOARD_HEIGHT_FOR_P : Constants.KEYBOARD_HEIGHT_FOR_L;
        return sp.edit().putInt(key, height).commit();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
