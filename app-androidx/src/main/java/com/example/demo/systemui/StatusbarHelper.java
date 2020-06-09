package com.example.demo.systemui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;


/**
 * 状态栏 统一处理入口
 * 第三方库持续更新，作为与应用层交互类
 * 沉浸式策略：支持 4.4 以上版本的 MIUI 和 Flyme，以及 5.0 以上版本的其他 Android
 * 设置状态栏黑色字体图标:支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
 * 设置状态栏白色字体图标:支持 4.4 以上版本 MIUI 和 Flyme，以及 6.0 以上版本的其他 Android
 * <p>
 * 分离主题色和状态栏颜色，主题色主要用于控制状态栏字体是否发生变化，比如说状态栏透明情况下，主题色由其页面底色决定，可以通过场景理解。
 *
 * 业务场景场景设置：
 * 1. 状态栏和标题栏都为纯色，技术上实现可分为两种，一种是状态栏透明，标题栏的高度增加状态栏高度，第二种是状态栏着色，标题栏与状态栏同色，高度为业务高度，此时 themeColor 和 statusBarColor一样
 * 2. 状态栏和标题栏不为纯色（比如是一张背景图），技术上实现为状态栏透明，底色用一张图处理，是否改变状态栏字体由图片的颜色来决定themeColor
 *
 * Email yummyl.lau@gmail.com
 * Created by yummylau on 2018/01/25.
 */

public class StatusbarHelper {

    public static void translucentStatusBar(Activity activity) {
        QMUIStatusBarHelper.translucent(activity);
    }

    public static void setStatusBarColor(Activity activity,
            int statusBarColor, @ColorInt int themeColor) {
        colorStatusBar(activity, statusBarColor);
        if (isNearWhiteColor(themeColor)) {
            QMUIStatusBarHelper.setStatusBarLightMode(activity);
        } else {
            QMUIStatusBarHelper.setStatusBarDarkMode(activity);
        }
    }

    public static void setStatusBarColor(Activity activity, @ColorInt int statusBarColor, boolean isNearWhiteColor) {
        colorStatusBar(activity, statusBarColor);
        if (isNearWhiteColor) {
            QMUIStatusBarHelper.setStatusBarLightMode(activity);
        } else {
            QMUIStatusBarHelper.setStatusBarDarkMode(activity);
        }
    }

    public static void setStatusBarColor(Activity activity, @ColorInt int statusBarColor) {
        if (isNearWhiteColor(statusBarColor)) {
            if (QMUIStatusBarHelper.setStatusBarLightMode(activity)) {
                colorStatusBar(activity, statusBarColor);
            }
        } else {
            if (QMUIStatusBarHelper.setStatusBarDarkMode(activity)) {
                colorStatusBar(activity, statusBarColor);
            }
        }
    }

    @TargetApi(19)
    private static void colorStatusBar(Activity activity, @ColorInt int statusBarColor) {
        // 部分三星机型在window.getDecorView() NPE 崩溃，可能是此时Activity onCreate，部分drawable为空
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                // 版本小于4.4，绝对不考虑沉浸式
                return;
            }
            // 小米和魅族4.4 以上版本支持沉浸式
            if (QMUIDeviceHelper.isMeizu() || QMUIDeviceHelper.isMIUI()) {
                Window window = activity.getWindow();
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && QMUIStatusBarHelper.supportTransclentStatusBar6()) {
                    // android 6以后可以改状态栏字体颜色，因此可以自行设置为透明
                    // ZUK Z1是个另类，自家应用可以实现字体颜色变色，但没开放接口
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(statusBarColor);
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(statusBarColor);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window mWindow = activity.getWindow();
                mWindow.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
                mTintManager.setStatusBarTintEnabled(true);
                mTintManager.setStatusBarTintColor(statusBarColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isNearWhiteColor(@ColorInt int color) {
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return alpha >= 255 * 0.7
                && red >= 255 * 0.7
                && green >= 255 * 0.7
                && blue >= 255 * 0.7;
    }

}
