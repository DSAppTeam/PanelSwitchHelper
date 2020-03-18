package com.effective.android.panel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.view.ContentContainer;
import com.effective.android.panel.view.PanelSwitchLayout;
import com.effective.android.panel.view.PanelContainer;
import com.effective.android.panel.view.PanelView;

import java.util.ArrayList;
import java.util.List;

/**
 * the helper of panel switching
 * Created by yummyLau on 2018-6-21.
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public final class PanelSwitchHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = PanelSwitchHelper.class.getSimpleName();
    public static boolean isKeyboardShowing;
    private final List<OnViewClickListener> viewClickListeners;
    private final List<OnPanelChangeListener> panelChangeListeners;
    private final List<OnKeyboardStateListener> keyboardStatusListeners;
    private final List<OnEditFocusChangeListener> editFocusChangeListeners;

    private Context context;
    public static Window window;
    private PanelSwitchLayout mPanelSwitchLayout;

    private PanelSwitchHelper(Builder builder) {
        window = builder.window;
        context = builder.context;

        mPanelSwitchLayout = builder.panelSwitchLayout;

        viewClickListeners = builder.viewClickListeners;
        panelChangeListeners = builder.panelChangeListeners;
        keyboardStatusListeners = builder.keyboardStatusListeners;
        editFocusChangeListeners = builder.editFocusChangeListeners;

        Constants.DEBUG = builder.logTrack;
        if (builder.logTrack) {
            viewClickListeners.add(LogTracker.getInstance());
            editFocusChangeListeners.add(LogTracker.getInstance());
            keyboardStatusListeners.add(LogTracker.getInstance());
            panelChangeListeners.add(LogTracker.getInstance());
        }

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
        return mPanelSwitchLayout.hookSystemBackForHindPanel();
    }

    /**
     * 外部显示输入框
     */
    public void toKeyboardState() {
        mPanelSwitchLayout.checkoutPanel(Constants.PANEL_KEYBOARD);
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
        ContentContainer contentContainer;
        PanelContainer panelContainer;
        Context context;
        Window window;
        View rootView;
        boolean logTrack;

        @IdRes
        private int panelSwitchLayoutId, contentContainerId, panelContainerId;

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

        public Builder bindContentContainerId(@IdRes int contentContainerId) {
            this.contentContainerId = contentContainerId;
            return this;
        }

        public Builder bindPanelContainerId(@IdRes int bindPanelContainerId) {
            this.panelContainerId = bindPanelContainerId;
            return this;
        }

        /**
         * note: helper will set view's onClickListener to View ,so you should add OnViewClickListener for your project.
         *
         * @param listener
         * @return
         */
        public Builder addViewClickListener(OnViewClickListener listener) {
            if (listener != null) {
                viewClickListeners.add(listener);
            }
            return this;
        }

        public Builder addPanelChangeListener(OnPanelChangeListener listener) {
            if (listener != null) {
                panelChangeListeners.add(listener);
            }
            return this;
        }

        public Builder addKeyboardStateListener(OnKeyboardStateListener listener) {
            if (listener != null) {
                keyboardStatusListeners.add(listener);
            }
            return this;
        }

        public Builder addEdittextFocesChangeListener(OnEditFocusChangeListener listener) {
            if (listener != null) {
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

            contentContainer = rootView.findViewById(contentContainerId);
            if (contentContainer == null || !(contentContainer instanceof ContentContainer)) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : not found contentContainer by id(" + contentContainerId + ")");
            }

            panelContainer = rootView.findViewById(panelContainerId);
            if (panelContainer == null || !(panelContainer instanceof PanelContainer)) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : not found panelContainer by id(" + panelContainerId + ")");
            }


            final PanelSwitchHelper panelSwitchHelper = new PanelSwitchHelper(this);
            if (showKeyboard) {
                contentContainer.requestFocus();
            }
            return panelSwitchHelper;
        }

        public PanelSwitchHelper build() {
            return build(false);
        }
    }

}
