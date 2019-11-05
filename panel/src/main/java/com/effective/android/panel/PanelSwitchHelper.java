package com.effective.android.panel;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.effective.android.panel.interfaces.IPopupSupport;
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
    private static long preClickTime = 0;
    private int currentPanelId = Constants.PANEL_NONE;
    private boolean isCheckoutDoing;
    private boolean isKeyboardShowing;
    private boolean preventOpeningKeyboard;

    private final List<OnViewClickListener> viewClickListeners;
    private final List<OnPanelChangeListener> panelChangeListeners;
    private final List<OnKeyboardStateListener> keyboardStatusListeners;
    private final List<OnEditFocusChangeListener> editFocusChangeListeners;

    private CheckoutPanelRunnable checkoutPanelRunnable;
    private UnlockContentHeightRunnable unlockContentHeightRunnable;

    private Activity mActivity;
    private PanelSwitchLayout mPanelSwitchLayout;
    private ContentContainer mContentContainer;
    private PanelContainer mPanelContainer;
    private SparseArray<PanelView> mPanelViewSparseArray;

    private PanelSwitchHelper(Builder builder) {
        mActivity = builder.activity;
        mPanelSwitchLayout = builder.panelSwitchLayout;
        mContentContainer = builder.contentContainer;
        mPanelContainer = builder.panelContainer;

        viewClickListeners = builder.viewClickListeners;
        panelChangeListeners = builder.panelChangeListeners;
        keyboardStatusListeners = builder.keyboardStatusListeners;
        editFocusChangeListeners = builder.editFocusChangeListeners;

        initLogTracker(builder);
        initWindow(mActivity);
        initListener();
    }

    private void initWindow(Activity activity) {
        Window window = activity.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    private void initLogTracker(Builder builder) {
        Constants.DEBUG = builder.logTrack;
        if (builder.logTrack) {
            viewClickListeners.add(LogTracker.getInstance());
            editFocusChangeListeners.add(LogTracker.getInstance());
            keyboardStatusListeners.add(LogTracker.getInstance());
            panelChangeListeners.add(LogTracker.getInstance());
        }
    }

    private void initListener() {
        /**
         * 1. if current currentPanelId is None,should show keyboard
         * 2. current currentPanelId is not None or KeyBoard that means some panel is showing,hide it and show keyboard
         */
        mContentContainer.setEditTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkout currentFlag to keyboard
                boolean result = checkoutPanel(Constants.PANEL_KEYBOARD);
                //when is checkout doing, unlockContentlength unfinished
                //editText click will make keyboard visible by system,so if checkoutPanel fail,should hide keyboard.
                if (!result && currentPanelId != Constants.PANEL_KEYBOARD) {
                    PanelHelper.hideKeyboard(mActivity, v);
                }
                notifyViewClick(v);
            }
        });

        mContentContainer.setEditTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !preventOpeningKeyboard) {
                    // checkout currentFlag to keyboard
                    boolean result = checkoutPanel(Constants.PANEL_KEYBOARD);
                    //when is checkout doing, unlockContentlength unfinished
                    //editText click will make keyboard visible by system,so if checkoutPanel fail,should hide keyboard.
                    if (!result && currentPanelId != Constants.PANEL_KEYBOARD) {
                        PanelHelper.hideKeyboard(mActivity, v);
                    }
                }
                preventOpeningKeyboard = false;
                notifyEditFocusChange(v, hasFocus);
            }
        });

        mContentContainer.setEmptyViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSamePaneId(Constants.PANEL_NONE)) {
                    checkoutPanel(Constants.PANEL_NONE);
                    notifyViewClick(v);
                }
            }
        });


        /**
         * save panel that you want to use these to checkout
         */
        mPanelViewSparseArray = mPanelContainer.getPanelSparseArray();
        for (int i = 0; i < mPanelViewSparseArray.size(); i++) {
            final PanelView panelView = mPanelViewSparseArray.get(mPanelViewSparseArray.keyAt(i));
            final View keyView = mContentContainer.findViewById(panelView.getTriggerViewId());
            if (keyView != null) {
                keyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        long currentTime = System.currentTimeMillis();
                        if (currentTime - preClickTime <= Constants.PROTECT_KEY_CLICK_DURATION) {
                            LogTracker.Log(TAG + "#initListener", "panelItem invalid click! preClickTime: " + preClickTime + " currentClickTime: " + currentTime);
                            return;
                        }

                        int panelId = getPanelId(panelView);
                        if (currentPanelId == panelId && panelView.isToggle() && panelView.isShown()) {
                            checkoutPanel(Constants.PANEL_KEYBOARD);
                        } else {
                            checkoutPanel(panelId);
                        }

                        preClickTime = currentTime;
                        notifyViewClick(v);
                    }
                });
            }
        }
    }

    private int getPanelId(@NonNull PanelView view) {
        if (view == null) {
            return Constants.PANEL_KEYBOARD;
        } else {
            return view.getTriggerViewId();
        }
    }

    private boolean checkoutPanel(int toPanelId) {

        if (isCheckoutDoing) {
            LogTracker.Log(TAG + "#checkoutPanel", "is doing checkout, skip!");
            return false;
        }

        isCheckoutDoing = true;

        if (currentPanelId == toPanelId) {
            LogTracker.Log(TAG + "#checkoutPanel", "currentPanelId is the same as toPanelId, it doesn't need to be checkout!");
            isCheckoutDoing = false;
            return true;
        }

        if (toPanelId == Constants.PANEL_NONE) {
            hidePanel(currentPanelId);
            showPanel(Constants.PANEL_NONE);
            isCheckoutDoing = false;
            return true;
        }

        switch (currentPanelId) {
            case Constants.PANEL_NONE: {
                hidePanel(Constants.PANEL_NONE);
                showPanel(toPanelId);
                isCheckoutDoing = false;
                break;
            }
            case Constants.PANEL_KEYBOARD: {
                lockContentHeight(mContentContainer);
                hidePanel(Constants.PANEL_KEYBOARD);
                showPanelWithUnlockContentHeight(toPanelId);
                break;
            }

            default: {
                if (toPanelId == Constants.PANEL_KEYBOARD) {
                    lockContentHeight(mContentContainer);
                    hidePanel(currentPanelId);
                    showPanelWithUnlockContentHeight(Constants.PANEL_KEYBOARD);
                } else {
                    hidePanel(currentPanelId);
                    showPanel(toPanelId);
                    isCheckoutDoing = false;
                }
            }
        }
        return true;
    }


    /**
     * except Constants.PANEL_NONE，others should
     *
     * @param panelId
     */
    private void showPanelWithUnlockContentHeight(int panelId) {
        if (checkoutPanelRunnable != null) {
            mPanelSwitchLayout.removeCallbacks(checkoutPanelRunnable);
        }
        if (unlockContentHeightRunnable != null) {
            mPanelSwitchLayout.removeCallbacks(unlockContentHeightRunnable);
        }
        long delayMillis = 0;
        if (panelId == Constants.PANEL_KEYBOARD) {
            delayMillis = Constants.DELAY_SHOW_KEYBOARD_TIME;
        }
        checkoutPanelRunnable = new CheckoutPanelRunnable(panelId);
        mPanelSwitchLayout.postDelayed(checkoutPanelRunnable, delayMillis);
    }

    private void showPanel(int panelId) {
        switch (panelId) {
            case Constants.PANEL_NONE: {
                mContentContainer.clearFocusByEditText();
                break;
            }
            case Constants.PANEL_KEYBOARD: {
                PanelHelper.showKeyboard(mActivity, mContentContainer.getEditText());
                setEmptyViewVisible(true);
                break;
            }
            default: {
                PanelView panelView = mPanelViewSparseArray.get(panelId);
                int newWidth = mPanelSwitchLayout.getMeasuredWidth() - mPanelSwitchLayout.getPaddingLeft() - mPanelSwitchLayout.getPaddingRight();
                int newHeight = PanelHelper.getKeyBoardHeight(mActivity);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) panelView.getLayoutParams();
                int oldWidth = params.width;
                int oldHeight = params.height;
                if (oldWidth != newWidth || oldHeight != newHeight) {
                    params.width = newWidth;
                    params.height = newHeight;
                    panelView.requestLayout();
                    LogTracker.Log(TAG + "#showPanel", "change panel's layout, " + oldWidth + " -> " + newWidth + " " + oldHeight + " -> " + newHeight);
                    notifyPanelSizeChange(panelView, oldWidth, oldHeight, newWidth, newHeight);
                }
                panelView.setVisibility(View.VISIBLE);
                setEmptyViewVisible(true);
            }
        }
        setPanelId(panelId);
        notifyPanelChange(panelId);
    }

    private void setPanelId(int panelId) {
        this.currentPanelId = panelId;
        LogTracker.Log(TAG + "#setPanelId", "panel' id :" + currentPanelId);
    }

    private boolean isSamePaneId(int panelId) {
        return this.currentPanelId == panelId;
    }

    private void hidePanel(int panelId) {
        switch (panelId) {
            case Constants.PANEL_NONE: {
                break;
            }
            case Constants.PANEL_KEYBOARD: {
                PanelHelper.hideKeyboard(mActivity, mContentContainer.getEditText());
                setEmptyViewVisible(false);
                break;
            }
            default: {
                PanelView panelView = mPanelViewSparseArray.get(panelId);
                panelView.setVisibility(View.GONE);
                setEmptyViewVisible(false);
            }
        }
    }

    private void lockContentHeight(@NonNull View contentView) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
        params.height = contentView.getHeight();
        params.weight = 0.0F;
        contentView.requestLayout();
    }

    private void setEmptyViewVisible(boolean isVisible) {
        if (mContentContainer.hasEmptyView()) {
            mContentContainer.getEmptyView().setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onGlobalLayout() {

        //get window height exclude SystemUi(statusBar and navigationBar)
        Rect r = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int contentHeight = r.bottom - r.top;

        //get window height include SystemUi
        int screenHeight = mActivity.getWindow().getDecorView().getHeight();
        int calKeyboardHeight = screenHeight - contentHeight;

        boolean isFullScreen = PanelHelper.isFullScreen(mActivity);
        if (!isFullScreen) {
            int systemUIHeight = 0;

            //get statusBar 和 navigationBar height
            int statusBarHeight = PanelHelper.getStatusBarHeight(mActivity);
            int navigationBatHeight = PanelHelper.getNavigationBarHeight(mActivity);
            if (PanelHelper.isPortrait(mActivity)) {
                systemUIHeight = PanelHelper.isNavigationBarShow(mActivity) ? statusBarHeight + navigationBatHeight : statusBarHeight;
            } else {
                systemUIHeight = statusBarHeight;
            }
            calKeyboardHeight -= systemUIHeight;
        }

        int keyboardHeight = 0;
        if (keyboardHeight == 0 && calKeyboardHeight > 0) {
            keyboardHeight = calKeyboardHeight;
        }

        if (isKeyboardShowing) {
            //meet Hinding keyboard
            if (keyboardHeight <= 0) {
                isKeyboardShowing = false;
                notifyKeyboardState(false);
            } else {
                //if user adjust keyboard
                PanelHelper.setKeyBoardHeight(mActivity, keyboardHeight);
            }
        } else {
            //meet Showing keyboard,
            if (keyboardHeight > 0) {
                LogTracker.Log(TAG + "#onGlobalLayout", "setKeyBoardHeight is : " + keyboardHeight);
                PanelHelper.setKeyBoardHeight(mActivity, keyboardHeight);
                isKeyboardShowing = true;
                notifyKeyboardState(true);
            }
        }
    }

    /**
     * This will be called when User press System Back Button.
     * 1. if keyboard is showing, should be hide;
     * 2. if you want to hide panel(exclude keyboard),you should call it before {@link Activity#onBackPressed()} to hook it.
     *
     * @return if need hook
     */
    public boolean hookSystemBackForHindPanel() {
        if (currentPanelId != Constants.PANEL_NONE) {
            if (currentPanelId != Constants.PANEL_KEYBOARD) {
                checkoutPanel(Constants.PANEL_NONE);
            }
            return true;
        }
        return false;
    }

    /**
     * 外部显示输入框
     */
    public void showKeyboard() {
        if (mContentContainer.editTextHasFocus()) {
            mContentContainer.preformClickForEditText();
        } else {
            mContentContainer.requestFocusByEditText();
        }
    }

    /**
     * 隐藏输入法或者面板
     */
    public void resetState() {
        checkoutPanel(Constants.PANEL_NONE);
    }


    private void notifyViewClick(View view) {
        for (OnViewClickListener listener : viewClickListeners) {
            listener.onViewClick(view);
        }
    }

    private void notifyPanelChange(int panelId) {
        for (OnPanelChangeListener listener : panelChangeListeners) {
            switch (panelId) {
                case Constants.PANEL_NONE: {
                    listener.onNone();
                    break;
                }
                case Constants.PANEL_KEYBOARD: {
                    listener.onKeyboard();
                    break;
                }
                default: {
                    listener.onPanel(mPanelViewSparseArray.get(panelId));
                }
            }
        }
    }

    private void notifyPanelSizeChange(PanelView panelView, int oldWidth, int oldHeight, int width, int height) {
        for (OnPanelChangeListener listener : panelChangeListeners) {
            listener.onPanelSizeChange(panelView, PanelHelper.isPortrait(mActivity), oldWidth, oldHeight, width, height);
        }
    }

    private void notifyKeyboardState(boolean visible) {
        for (OnKeyboardStateListener listener : keyboardStatusListeners) {
            listener.onKeyboardChange(visible);
        }
    }

    private void notifyEditFocusChange(View view, boolean hasFocus) {
        for (OnEditFocusChangeListener listener : editFocusChangeListeners) {
            listener.onFocusChange(view, hasFocus);
        }
    }

    private class CheckoutPanelRunnable implements Runnable {

        int panelId;

        public CheckoutPanelRunnable(int panelId) {
            this.panelId = panelId;
        }

        @Override
        public void run() {
            showPanel(panelId);
            checkoutPanelRunnable = null;
            //need to unlock
            if (panelId != Constants.PANEL_NONE) {
                unlockContentHeightRunnable = new UnlockContentHeightRunnable();
                mPanelSwitchLayout.postDelayed(unlockContentHeightRunnable, Constants.DELAY_UNLOCK_CONTENT_TIME);
            } else {
                isCheckoutDoing = false;
            }
        }
    }

    private class UnlockContentHeightRunnable implements Runnable {

        @Override
        public void run() {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentContainer.getLayoutParams();
            params.weight = 1.0F;
            mContentContainer.requestLayout();
            unlockContentHeightRunnable = null;
            isCheckoutDoing = false;
        }
    }

    /**
     * recycle resource!
     * Note: call in {@link Activity#onDestroy()}
     */
    public void onDestroy() {
        mPanelSwitchLayout.removeCallbacks(checkoutPanelRunnable);
        mPanelSwitchLayout.removeCallbacks(unlockContentHeightRunnable);
    }

    public static class Builder {

        List<OnViewClickListener> viewClickListeners;
        List<OnPanelChangeListener> panelChangeListeners;
        List<OnKeyboardStateListener> keyboardStatusListeners;
        List<OnEditFocusChangeListener> editFocusChangeListeners;

        Activity activity;
        PopupWindow popupWindow;
        PanelSwitchLayout panelSwitchLayout;
        ContentContainer contentContainer;
        PanelContainer panelContainer;
        boolean logTrack;

        @IdRes
        private int panelSwitchLayoutId, contentContainerId, panelContainerId;

        public Builder(IPopupSupport popupSupport) {
            this(popupSupport.getActivity(), popupSupport.getPopupWindow());
        }

        public Builder(Activity activity) {
            this(activity, null);
        }

        public Builder(Activity activity, PopupWindow popupWindow) {
            this.popupWindow = popupWindow;
            this.activity = activity;
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

            if (activity == null) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : innerActivity can't be null!please set value by call #Builder");
            }

            View root = null;
            if (popupWindow != null) {
                root = popupWindow.getContentView();
            } else {
                root = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            }

            panelSwitchLayout = root.findViewById(panelSwitchLayoutId);
            if (panelSwitchLayout == null || !(panelSwitchLayout instanceof PanelSwitchLayout)) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : not found PanelSwitchLayout by id(" + panelSwitchLayoutId + ")");
            }

            contentContainer = root.findViewById(contentContainerId);
            if (contentContainer == null || !(contentContainer instanceof ContentContainer)) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : not found contentContainer by id(" + contentContainerId + ")");
            }

            panelContainer = root.findViewById(panelContainerId);
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
