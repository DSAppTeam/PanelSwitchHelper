package com.effective.android.panel;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.effective.android.panel.interfaces.OnScrollOutsideBorder;
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.utils.PanelUtil;
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
    private boolean canScrollOutside;

    private PanelSwitchHelper(Builder builder) {
        Constants.DEBUG = builder.logTrack;
        this.canScrollOutside = builder.contentCanScrollOutside;
        if (builder.logTrack) {
            builder.viewClickListeners.add(LogTracker.getInstance());
            builder.panelChangeListeners.add(LogTracker.getInstance());
            builder.keyboardStatusListeners.add(LogTracker.getInstance());
            builder.editFocusChangeListeners.add(LogTracker.getInstance());
        }
        mPanelSwitchLayout = builder.panelSwitchLayout;
        mPanelSwitchLayout.setScrollOutsideBorder(new OnScrollOutsideBorder() {
            @Override
            public boolean canLayoutOutsideBorder() {
                return canScrollOutside;
            }

            @Override
            public int getOutsideHeight() {
                return PanelUtil.getKeyBoardHeight(mPanelSwitchLayout.getContext());
            }
        });
        mPanelSwitchLayout.bindListener(builder.viewClickListeners, builder.panelChangeListeners, builder.keyboardStatusListeners, builder.editFocusChangeListeners);
        mPanelSwitchLayout.bindWindow(builder.window);
    }

    public boolean hookSystemBackByPanelSwitcher() {
        return mPanelSwitchLayout.hookSystemBackByPanelSwitcher();
    }

    public void scrollOutsideEnable(boolean enable){
        this.canScrollOutside = enable;
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


    public static class Builder {

        List<OnViewClickListener> viewClickListeners;
        List<OnPanelChangeListener> panelChangeListeners;
        List<OnKeyboardStateListener> keyboardStatusListeners;
        List<OnEditFocusChangeListener> editFocusChangeListeners;
        PanelSwitchLayout panelSwitchLayout;
        Window window;
        View rootView;
        boolean logTrack;
        boolean contentCanScrollOutside = true;

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

        public Builder contentCanScrollOutside(boolean canScrollOutside){
            this.contentCanScrollOutside = canScrollOutside;
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

            findSwitchLayout(rootView);
            if (panelSwitchLayout == null) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : not found PanelSwitchLayout!");
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

        private void findSwitchLayout(View view) {
            if (view instanceof PanelSwitchLayout) {
                if(panelSwitchLayout != null){
                    throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : rootView has one more panelSwitchLayout!");
                }
                panelSwitchLayout = (PanelSwitchLayout) view;
                return;
            }

            if(view instanceof ViewGroup){
                int childCount = ((ViewGroup) view).getChildCount();
                for(int i = 0; i <  childCount; i++){
                    findSwitchLayout(((ViewGroup) view).getChildAt(i));
                }
            }
        }
    }
}
