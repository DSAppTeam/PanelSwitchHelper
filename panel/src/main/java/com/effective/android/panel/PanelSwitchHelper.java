package com.effective.android.panel;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
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
    private int flag = Constants.FLAG_NONE;

    private boolean isCheckoutDoing;
    private boolean isKeyboardShowing;
    private boolean preventOpeningKeyboard;

    private final List<OnViewClickListener> viewClickListeners;
    private final List<OnPanelChangeListener> panelChangeListeners;
    private final List<OnKeyboardStateListener> keyboardStatusListeners;
    private final List<OnEditFocusChangeListener> editFocusChangeListeners;

    private CheckoutPanelRunnable checkoutPanelRunnable;
    private CheckoutKeyboardRunnable checkoutKeyboardRunnable;
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

        Constants.DEBUG = builder.logTrack;
        if (builder.logTrack) {
            viewClickListeners.add(LogTracker.getInstance());
            editFocusChangeListeners.add(LogTracker.getInstance());
            keyboardStatusListeners.add(LogTracker.getInstance());
            panelChangeListeners.add(LogTracker.getInstance());
        }

        this.mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.mActivity.getWindow().getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        initListener();
    }

    private void initListener() {
        /**
         * 1. if current flag is None,should show keyboard
         * 2. current flag is not None or KeyBoard that means some panel is showing,hide it and show keyboard
         */
        mContentContainer.setEditTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkout currentFlag to keyboard
                boolean result = checkoutFlag(Constants.FLAG_KEYBOARD);
                //editText click will make keyboard visible by system,so if checkoutFlag fail,should hide keyboard.
                if (!result && flag != Constants.FLAG_KEYBOARD) {
                    mContentContainer.clearFocusByEditText();
                    PanelHelper.hideKeyboard(mActivity, v);
                }
                notifyViewClick(v);
            }
        });

        mContentContainer.setEditTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !preventOpeningKeyboard) {
                    //checkout currentFlag to keyboard
                    boolean result = checkoutFlag(Constants.FLAG_KEYBOARD);
                    //editText click will make keyboard visible by system,so if checkoutFlag fail,should hide keyboard.
                    if (!result && flag != Constants.FLAG_KEYBOARD) {
                        mContentContainer.clearFocusByEditText();
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
                checkoutFlag(Constants.FLAG_NONE);
                notifyViewClick(v);
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

                        if (System.currentTimeMillis() - preClickTime <= Constants.PROTECT_KEY_CLICK_DURATION) {
                            LogTracker.getInstance().log(TAG + "#initListener", "panelItem invalid click! preClickTime: " + preClickTime + " currentClickTime: " + System.currentTimeMillis());
                            return;
                        }

                        if (flag == panelView.getTriggerViewId() && panelView.isToggle() && panelView.isShown()) {
                            checkoutFlag(Constants.FLAG_KEYBOARD);
                        } else {
                            checkoutFlag(panelView.getTriggerViewId());
                        }

                        preClickTime = System.currentTimeMillis();
                        notifyViewClick(v);
                    }
                });
            }
        }
    }

    private boolean checkoutFlag(int endFlag) {

        if (isCheckoutDoing()) {
            LogTracker.getInstance().log(TAG + "#checkoutFlag", "checkout doing : skip!");
            return false;
        }

        isCheckoutDoing = true;

        if (flag == endFlag) {
            LogTracker.getInstance().log(TAG + "#checkoutFlag", "flag is the same as enfFlag, it doesn't need to be handled!");
            isCheckoutDoing = false;
            return true;
        }

        if (endFlag == Constants.FLAG_NONE) {
            hidePanelByFlag(flag);
            showPanelByFlag(Constants.FLAG_NONE);
            isCheckoutDoing = false;
            return true;
        }

        switch (flag) {
            case Constants.FLAG_NONE: {
                hidePanelByFlag(Constants.FLAG_NONE);
                showPanelByFlag(endFlag);
                break;
            }
            case Constants.FLAG_KEYBOARD: {
                lockContentHeight(mContentContainer);
                hidePanelByFlag(Constants.FLAG_KEYBOARD);
                //make sure when panel is showing，the keyboard was gone.
                postShowPanelRunnable(endFlag);
                postUnlockHeightContentRunnable(mContentContainer);
                break;
            }

            default: {
                if (endFlag == Constants.FLAG_KEYBOARD) {
                    lockContentHeight(mContentContainer);
                    hidePanelByFlag(flag);
                    showPanelByFlag(Constants.FLAG_KEYBOARD);
                    postUnlockHeightContentRunnable(mContentContainer);
                } else {
                    hidePanelByFlag(flag);
                    showPanelByFlag(endFlag);
                }
            }
        }
        isCheckoutDoing = false;
        return true;
    }

    private boolean isCheckoutDoing() {
        if (checkoutPanelRunnable != null
                || unlockContentHeightRunnable != null
                || checkoutKeyboardRunnable != null) {
            return true;
        }
        return isCheckoutDoing;
    }

    private void postUnlockHeightContentRunnable(View contentView) {
        unlockContentHeightRunnable = new UnlockContentHeightRunnable(contentView);
        mPanelSwitchLayout.postDelayed(unlockContentHeightRunnable, Constants.DELAY_UNLOCK_CONTENT_TIME);
    }

    private void postShowKeyboardRunnable() {
        checkoutKeyboardRunnable = new CheckoutKeyboardRunnable();
        mPanelSwitchLayout.postDelayed(checkoutKeyboardRunnable, Constants.DELAY_SHOW_KEYBOARD_TIME);
    }

    private void postShowPanelRunnable(int endFlag) {
        checkoutPanelRunnable = new CheckoutPanelRunnable(endFlag);
        mPanelSwitchLayout.postDelayed(checkoutPanelRunnable, Constants.DELAY_SHOW_KEYBOARD_TIME);
    }

    private void showPanelByFlag(int flag) {
        switch (flag) {
            case Constants.FLAG_NONE: {
                mContentContainer.clearFocusByEditText();
                break;
            }
            case Constants.FLAG_KEYBOARD: {
                mContentContainer.requestFocusByEditText();
                postShowKeyboardRunnable();
                break;
            }
            default: {
                PanelView panelView = mPanelViewSparseArray.get(flag);
                int newWidth = mPanelSwitchLayout.getMeasuredWidth() - mPanelSwitchLayout.getPaddingLeft() - mPanelSwitchLayout.getPaddingRight();
                int newHeight = PanelHelper.getKeyBoardHeight(mActivity);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) panelView.getLayoutParams();
                int oldWidth = params.width;
                int oldHeight = params.height;
                if (oldWidth != newWidth || oldHeight != newHeight) {
                    params.width = newWidth;
                    params.height = newHeight;
                    panelView.requestLayout();
                    LogTracker.getInstance().log(TAG + "#showPanelByFlag", "change panel's layout, " + oldWidth + " -> " + newWidth + " " + oldHeight + " -> " + newHeight);
                    notifyPanelSizeChange(panelView, oldWidth, oldHeight, newWidth, newHeight);
                }
                panelView.setVisibility(View.VISIBLE);
                setEmptyViewVisible(true);
                mContentContainer.clearFocusByEditText();
            }
        }
        this.flag = flag;
        notifyPanelChange(flag);
    }

    private void hidePanelByFlag(int flag) {
        switch (flag) {
            case Constants.FLAG_NONE: {
                break;
            }
            case Constants.FLAG_KEYBOARD: {
                PanelHelper.hideKeyboard(mActivity, mContentContainer.getEditText());
                setEmptyViewVisible(false);
                break;
            }
            default: {
                PanelView panelView = mPanelViewSparseArray.get(flag);
                panelView.setVisibility(View.GONE);
                setEmptyViewVisible(false);
            }
        }
        this.flag = Constants.FLAG_NONE;
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

        //get statusBar 和 navigationBar height
        int systemUIHeight;
        int statusBarHeight = PanelHelper.getStatusBarHeight(mActivity);
        int navigationBatHeight = PanelHelper.getNavigationBarHeight(mActivity);
        if (PanelHelper.isPortrait(mActivity)) {
            systemUIHeight = PanelHelper.isNavigationBarShow(mActivity) ? statusBarHeight + navigationBatHeight : statusBarHeight;
        } else {
            systemUIHeight = statusBarHeight;
        }

        //get window height include SystemUi
        int screenHeight = mActivity.getWindow().getDecorView().getHeight();
        int heightDiff = screenHeight - (contentHeight);

        //get keyboard height
        int keyboardHeight = 0;

        //compat popupwindow or activity window is fullScreen
        if (keyboardHeight == 0 && heightDiff > systemUIHeight) {
            if ((mActivity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    != WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                keyboardHeight = heightDiff - systemUIHeight;
            }else{
                keyboardHeight = heightDiff;
            }
        }

        if (isKeyboardShowing) {
            //meet Hinding keyboard
            if (keyboardHeight <= 0) {
                flag = Constants.FLAG_NONE;
                isKeyboardShowing = false;
                notifyKeyboardState(false);
            } else {
                //if user adjust keyboard
                PanelHelper.setKeyBoardHeight(mActivity, keyboardHeight);
            }
        } else {
            //meet Showing keyboard,
            if (keyboardHeight > 0) {
                LogTracker.getInstance().log(TAG + "#onGlobalLayout", "setKeyBoardHeight is : " + keyboardHeight);
                PanelHelper.setKeyBoardHeight(mActivity, keyboardHeight);
                flag = Constants.FLAG_KEYBOARD;
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
        if (flag != Constants.FLAG_NONE) {
            if (flag != Constants.FLAG_KEYBOARD) {
                checkoutFlag(Constants.FLAG_NONE);
            }
            return true;
        }
        return false;
    }

    public void requestFocus(boolean preventOpeningKeyboard) {
        this.preventOpeningKeyboard = preventOpeningKeyboard;
        mContentContainer.requestFocusByEditText();
    }

    public void showKeyboard() {
        if (mContentContainer.editTextHasFocus()) {
            mContentContainer.preformClickByEditText();
        } else {
            requestFocus(false);
        }
    }

    public void resetState() {
        checkoutFlag(Constants.FLAG_NONE);
    }

    @Nullable
    private PanelView getPanelView(int flag) {
        return mPanelViewSparseArray.get(flag);
    }

    private void notifyViewClick(View view) {
        for (OnViewClickListener listener : viewClickListeners) {
            listener.onViewClick(view);
        }
    }

    private void notifyPanelChange(int flag) {
        for (OnPanelChangeListener listener : panelChangeListeners) {
            switch (flag) {
                case Constants.FLAG_NONE: {
                    listener.onNone();
                    break;
                }
                case Constants.FLAG_KEYBOARD: {
                    listener.onKeyboard();
                    break;
                }
                default: {
                    listener.onPanel(mPanelViewSparseArray.get(flag));
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

        int flag;

        public CheckoutPanelRunnable(int flag) {
            this.flag = flag;
        }

        @Override
        public void run() {
            showPanelByFlag(flag);
            checkoutPanelRunnable = null;
        }
    }

    private class CheckoutKeyboardRunnable implements Runnable {
        @Override
        public void run() {
            PanelHelper.showKeyboard(mActivity, mContentContainer.getEditText());
            setEmptyViewVisible(true);
            checkoutKeyboardRunnable = null;
        }
    }

    private class UnlockContentHeightRunnable implements Runnable {

        private View contentView;

        public UnlockContentHeightRunnable(View contentView) {
            this.contentView = contentView;
        }

        @Override
        public void run() {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.weight = 1.0F;
            contentView.requestLayout();
            unlockContentHeightRunnable = null;
        }
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
            this.popupWindow = popupSupport.getPopupWindow();
            this.activity = popupSupport.getActivity();
            viewClickListeners = new ArrayList<>();
            panelChangeListeners = new ArrayList<>();
            keyboardStatusListeners = new ArrayList<>();
            editFocusChangeListeners = new ArrayList<>();
        }

        public Builder(Activity activity) {
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
