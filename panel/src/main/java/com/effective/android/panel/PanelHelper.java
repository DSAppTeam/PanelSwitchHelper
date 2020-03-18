package com.effective.android.panel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.effective.android.panel.view.ContentContainer;

/**
 * panel helper
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public final class PanelHelper {

    private static final String TAG = PanelHelper.class.getSimpleName();

    /**
     * 获取toolar的高度，但是这个方法仅仅在非沉浸下才有用。
     *
     * @param window
     * @return
     */
    public static int getToolbarHeight(Window window) {
        return window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    public static boolean contentViewCanDrawStatusBarArea(Window window) {
        int[] contentViewLocationInScreen = new int[2];
        window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT).getLocationOnScreen(contentViewLocationInScreen);
        return contentViewLocationInScreen[1] == 0;
    }

    /**
     * 对应 id 为 @Android：id/content 的 FrameLayout 所加载的布局。
     * 也就是我们 setContentView 的布局高度
     *
     * @param window
     * @return
     */
    public static int getContentViewHeight(Window window) {
        return window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
    }


    /**
     * 实际上获取的是DecorView的布局高度，是一个 FrameLayout，其内置布局 id 为 com.android.internal.R.layout.screen_simple 的 LinearLayout
     * 包含 id为 @+id/action_mode_bar_stub_ViewStub 的 ViewStub 还有 id 为 @Android：id/content 的 FrameLayout。
     *
     * @param window
     * @return
     */
    public static int getScreenHeightWithSystemUI(Window window) {
        return window.getDecorView().getHeight();
    }

    public static int getScreenHeightWithoutNavigationBar(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenHeightWithoutSystemUI(Window window) {
        Rect r = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(r);
        return r.bottom - r.top;
    }

    /**
     * 获取当前界面系统UI：包含状态栏+盗汗栏
     *
     * @param context
     * @param window
     * @return
     */
    public static int getSystemUI(Context context, Window window) {
        int systemUIHeight = 0;
        if (!isFullScreen(window)) {
            //get statusBar 和 navigationBar height
            int statusBarHeight = PanelHelper.getStatusBarHeight(context);
            int navigationBatHeight = PanelHelper.getNavigationBarHeight(context);
            if (PanelHelper.isPortrait(context)) {
                systemUIHeight = PanelHelper.isNavigationBarShow(context, window) ? statusBarHeight + navigationBatHeight : statusBarHeight;
            } else {
                systemUIHeight = statusBarHeight;
            }
        }
        return systemUIHeight;
    }

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
    public static boolean isNavigationBarShow(Context context, Window window) {
        return isNavBarVisible(context, window);
    }

    /**
     * Decorview 源码
     * public static final ColorViewAttributes NAVIGATION_BAR_COLOR_VIEW_ATTRIBUTES =
     * new ColorViewAttributes(
     * SYSTEM_UI_FLAG_HIDE_NAVIGATION, FLAG_TRANSLUCENT_NAVIGATION,
     * Gravity.BOTTOM, Gravity.RIGHT, Gravity.LEFT,
     * Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME,
     * com.android.internal.R.id.navigationBarBackground,
     * 0 /* hideWindowFlag
     *
     * @param context
     * @param window
     * @return
     */
    public static boolean isNavBarVisible(Context context, @NonNull final Window window) {
        ViewGroup viewGroup = (ViewGroup) window.getDecorView();
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                int id = viewGroup.getChildAt(i).getId();
                if (id != View.NO_ID) {
                    String resourceEntryName = context.getResources()
                            .getResourceEntryName(id);
                    if ("navigationBarBackground".equals(resourceEntryName)
                            && viewGroup.getChildAt(i).getVisibility() == View.VISIBLE) {
                        return true;
                    }
                }
            }
        }
        return false;
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
