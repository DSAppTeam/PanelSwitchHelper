package com.effective.android.panel;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.view.PanelView;

/**
 * single logTracker
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class LogTracker implements OnEditFocusChangeListener, OnKeyboardStateListener, OnPanelChangeListener, OnViewClickListener {

    private static final String TAG = LogTracker.class.getSimpleName();
    private static volatile LogTracker sInstance = null;
    private boolean openLog;

    private LogTracker(boolean openLog) {
        this.openLog = openLog;
    }

    public static LogTracker getInstance() {
        if (sInstance == null) {
            synchronized (LogTracker.class) {
                if (sInstance == null) {
                    sInstance = new LogTracker(Constants.DEBUG);
                }
            }
        }
        return sInstance;
    }

    public void log(String methodName, @NonNull String message) {
        if (TextUtils.isEmpty(methodName) || TextUtils.isEmpty(message)) {
            return;
        }
        if (openLog) {
            Log.d(Constants.LOG_TAG, methodName + " -- " + message);
        }
    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        log(TAG + "#onFocusChange", "EditText has focus ( " + hasFocus + " )");
    }

    @Override
    public void onKeyboardChange(boolean show) {
        log(TAG + "#onKeyboardChange", "Keyboard is showing ( " + show + " )");
    }


    @Override
    public void onKeyboard() {
        log(TAG + "#onKeyboard", "panel： keyboard");
    }

    @Override
    public void onNone() {
        log(TAG + "#onNone", "panel： none");
    }

    @Override
    public void onPanel(PanelView view) {
        log(TAG + "#onPanel", "panel：" + (view != null ? view.toString() : "null"));
    }

    @Override
    public void onPanelSizeChange(PanelView panelView, int oldWidth, int oldHeight, int width, int height) {
        log(TAG + "#onPanelSizeChange", "panelView is " + (panelView != null ? panelView.toString() : "null" +
                " oldWidth : " + oldWidth + " oldHeight : " + oldHeight +
                " width : " + width + " height : " + height));
    }


    @Override
    public void onViewClick(View view) {
        log(TAG + "#onViewClick", "view is " + (view != null ? view.toString() : " null "));
    }
}
