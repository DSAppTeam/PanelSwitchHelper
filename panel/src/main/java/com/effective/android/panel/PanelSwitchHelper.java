package com.effective.android.panel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.view.PanelSwitchLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * the helper of panel switching
 * Created by yummyLau on 2018-6-21.
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 *
 * updated by yummyLau on 20/03/18
 * 重构整个输入法切换框架，移除旧版使用 weight+Runnable延迟切换，使用新版 layout+动画无缝衔接！
 */

public final class PanelSwitchHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = PanelSwitchHelper.class.getSimpleName();
    public static boolean isKeyboardShowing;
    private final List<OnKeyboardStateListener> keyboardStatusListeners;

    private Context context;
    public static Window window;
    private PanelSwitchLayout mPanelSwitchLayout;

    private PanelSwitchHelper(Builder builder) {
        window = builder.window;
        context = builder.context;

        Constants.DEBUG = builder.logTrack;
        if (builder.logTrack) {
            builder.viewClickListeners.add(LogTracker.getInstance());
            builder.panelChangeListeners.add(LogTracker.getInstance());
            builder.keyboardStatusListeners.add(LogTracker.getInstance());
            builder.editFocusChangeListeners.add(LogTracker.getInstance());
        }
        keyboardStatusListeners = builder.keyboardStatusListeners;
        mPanelSwitchLayout = builder.panelSwitchLayout;
        mPanelSwitchLayout.bindListener(builder.viewClickListeners, builder.panelChangeListeners, builder.keyboardStatusListeners, builder.editFocusChangeListeners);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(this);
    }


    @Override
    public void onGlobalLayout() {

        //get window height exclude SystemUi(statusBar and navigationBar)
        Rect r = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(r);
        int contentHeight = r.bottom - r.top;

        //get window height include SystemUi
        int screenHeight = window.getDecorView().getHeight();
        int calKeyboardHeight = screenHeight - contentHeight;

        boolean isFullScreen = PanelHelper.isFullScreen(window);
        if (!isFullScreen) {
            int systemUIHeight = 0;

            //get statusBar 和 navigationBar height
            int statusBarHeight = PanelHelper.getStatusBarHeight(context);
            int navigationBatHeight = PanelHelper.getNavigationBarHeight(context);
            if (PanelHelper.isPortrait(context)) {
                systemUIHeight = PanelHelper.isNavigationBarShow(context, window) ? statusBarHeight + navigationBatHeight : statusBarHeight;
            } else {
                systemUIHeight = statusBarHeight;
            }
            calKeyboardHeight -= systemUIHeight;
        }

        int keyboardHeight = 0;
        if (keyboardHeight == 0 && calKeyboardHeight > 0) {
            keyboardHeight = calKeyboardHeight;
        }
        LogTracker.Log(TAG + "#onGlobalLayout", "setKeyBoardHeight is : " + keyboardHeight);

        if (isKeyboardShowing) {
            //meet Hinding keyboard
            if (keyboardHeight <= 0) {
                isKeyboardShowing = false;
                notifyKeyboardState(false);
            } else {
                //if user adjust keyboard
                LogTracker.Log(TAG + "#onGlobalLayout", "setKeyBoardHeight is : " + keyboardHeight);
                PanelHelper.setKeyBoardHeight(context, keyboardHeight);
            }
        } else {
            //meet Showing keyboard,
            if (keyboardHeight > 0) {
                LogTracker.Log(TAG + "#onGlobalLayout", "setKeyBoardHeight is : " + keyboardHeight);
                PanelHelper.setKeyBoardHeight(context, keyboardHeight);
                isKeyboardShowing = true;
                notifyKeyboardState(true);
            }
        }
    }

    public boolean hookSystemBackByPanelSwitcher() {
        return mPanelSwitchLayout.hookSystemBackByPanelSwitcher();
    }

    /**
     * 外部显示输入框
     */
    public void toKeyboardState() {
        mPanelSwitchLayout.toKeyboardState();
    }

    /**
     * 隐藏输入法或者面板
     */
    public void resetState() {
        mPanelSwitchLayout.checkoutPanel(Constants.PANEL_NONE);
    }


    private void notifyKeyboardState(boolean visible) {
        for (OnKeyboardStateListener listener : keyboardStatusListeners) {
            listener.onKeyboardChange(visible);
        }
    }

    /**
     * recycle resource!
     */
    public void onDestroy() {

    }

    public static class Builder {

        List<OnViewClickListener> viewClickListeners;
        List<OnPanelChangeListener> panelChangeListeners;
        List<OnKeyboardStateListener> keyboardStatusListeners;
        List<OnEditFocusChangeListener> editFocusChangeListeners;

        PanelSwitchLayout panelSwitchLayout;
        Context context;
        Window window;
        View rootView;
        boolean logTrack;

        @IdRes
        private int panelSwitchLayoutId;

        public Builder(Activity activity) {
            this(activity, activity.getWindow(), activity.getWindow().getDecorView().findViewById(android.R.id.content));
        }

        public Builder(Fragment fragment) {
            this(fragment.getActivity(), fragment.getActivity().getWindow(), fragment.getView());
        }

        public Builder(DialogFragment dialogFragment) {
            this(dialogFragment.getActivity(), dialogFragment.getActivity().getWindow(), dialogFragment.getView());
        }

        public Builder(Context context, Window window, View root) {
            this.context = context;
            this.window = window;
            this.rootView = root;
            viewClickListeners = new ArrayList<>();
            panelChangeListeners = new ArrayList<>();
            keyboardStatusListeners = new ArrayList<>();
            editFocusChangeListeners = new ArrayList<>();
        }

        public Builder bindPanelSwitchLayout(@IdRes int panelSwitchLayoutId) {
            this.panelSwitchLayoutId = panelSwitchLayoutId;
            return this;
        }


        /**
         * note: helper will set view's onClickListener to View ,so you should add OnViewClickListener for your project.
         *
         * @param listener
         * @return
         */
        public Builder addViewClickListener(OnViewClickListener listener) {
            if (listener != null && !viewClickListeners.contains(listener)) {
                viewClickListeners.add(listener);
            }
            return this;
        }

        public Builder addPanelChangeListener(OnPanelChangeListener listener) {
            if (listener != null && !panelChangeListeners.contains(listener)) {
                panelChangeListeners.add(listener);
            }
            return this;
        }

        public Builder addKeyboardStateListener(OnKeyboardStateListener listener) {
            if (listener != null && !keyboardStatusListeners.contains(listener)) {
                keyboardStatusListeners.add(listener);
            }
            return this;
        }

        public Builder addEditTextFocusChangeListener(OnEditFocusChangeListener listener) {
            if (listener != null && !editFocusChangeListeners.contains(listener)) {
                editFocusChangeListeners.add(listener);
            }
            return this;
        }

        public Builder logTrack(boolean logTrack) {
            this.logTrack = logTrack;
            return this;
        }

        public PanelSwitchHelper build(boolean showKeyboard) {

            if (window == null) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : window can't be null!please set value by call #Builder");
            }

            if (context == null) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : context can't be null!please set value by call #Builder");
            }

            if (rootView == null) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : rootView can't be null!please set value by call #Builder");
            }

            panelSwitchLayout = rootView.findViewById(panelSwitchLayoutId);
            if (panelSwitchLayout == null || !(panelSwitchLayout instanceof PanelSwitchLayout)) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : not found PanelSwitchLayout by id(" + panelSwitchLayoutId + ")");
            }

            final PanelSwitchHelper panelSwitchHelper = new PanelSwitchHelper(this);
            if (showKeyboard) {
                panelSwitchLayout.toKeyboardState();
            }
            return panelSwitchHelper;
        }

        public PanelSwitchHelper build() {
            return build(false);
        }
    }

}
