package com.effective.android.panel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
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
 * <p>
 * updated by yummyLau on 20/03/18
 * 重构整个输入法切换框架，移除旧版使用 weight+Runnable延迟切换，使用新版 layout+动画无缝衔接！
 */

public final class PanelSwitchHelper {

    private PanelSwitchLayout mPanelSwitchLayout;

    private PanelSwitchHelper(Builder builder) {
        Constants.DEBUG = builder.logTrack;
        if (builder.logTrack) {
            builder.viewClickListeners.add(LogTracker.getInstance());
            builder.panelChangeListeners.add(LogTracker.getInstance());
            builder.keyboardStatusListeners.add(LogTracker.getInstance());
            builder.editFocusChangeListeners.add(LogTracker.getInstance());
        }
        mPanelSwitchLayout = builder.panelSwitchLayout;
        mPanelSwitchLayout.bindListener(builder.viewClickListeners, builder.panelChangeListeners, builder.keyboardStatusListeners, builder.editFocusChangeListeners);
        mPanelSwitchLayout.bindWindow(builder.window);
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
        Window window;
        View rootView;
        boolean logTrack;

        @IdRes
        private int panelSwitchLayoutId;

        public Builder(@NonNull Activity activity) {
            this(activity.getWindow(), activity.getWindow().getDecorView().findViewById(android.R.id.content));
        }

        public Builder(@NonNull Fragment fragment) {
            this(fragment.getActivity().getWindow(), fragment.getView());
        }

        public Builder(@NonNull DialogFragment dialogFragment) {
            this(dialogFragment.getActivity().getWindow(), dialogFragment.getView());
        }

        public Builder(@NonNull Window window, @NonNull View root) {
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
