package com.sample.android.panel;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sample.android.panel.listener.OnEditFocusChangeListener;
import com.sample.android.panel.listener.OnKeyboardStateListener;
import com.sample.android.panel.listener.OnPanelChangeListener;
import com.sample.android.panel.listener.OnViewClickListener;
import com.sample.android.panel.panel.IPanelView;
import com.sample.android.panel.panel.PanelItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理输入法切换
 * Created by yummyLau on 2018/6/821.
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public final class PanelSwitchHelper implements ViewTreeObserver.OnGlobalLayoutListener {

    private static long preClickTime = 0;
    private int flag = Constants.FLAG_NONE;

    private Activity activity;
    private View contentView;                                       //required,include editView and emptyView
    private EditText editView;                                      //required
    private View emptyView;
    private final SparseArray<PanelItem> panelItemSparseArray;

    private boolean isCheckoutDoing;
    private boolean isKeyboardShowing;
    private boolean isHidingKeyboardByUser;
    private boolean preventOpeningKeyboard;

    private final List<OnViewClickListener> viewClickListeners;
    private final List<OnPanelChangeListener> panelChangeListeners;
    private final List<OnKeyboardStateListener> keyboardStatusListeners;
    private final List<OnEditFocusChangeListener> editFocusChangeListeners;

    private CheckoutPanelRunnable checkoutPanelRunnable;
    private CheckoutKeyboardRunnable checkoutKeyboardRunnable;
    private UnlockContentHeightRunnable unlockContentHeightRunnable;

    private LogTracker logTracker;

    private PanelSwitchHelper(Builder builder) {

        activity = builder.innerActivity;
        contentView = builder.innerContentView;
        editView = builder.innerEditText;
        emptyView = builder.innerEmptyView;
        panelItemSparseArray = builder.innerPanelArray;

        viewClickListeners = builder.innerViewClickListeners;
        panelChangeListeners = builder.innerPanelChangeListeners;
        keyboardStatusListeners = builder.innerKeyboardStatusListeners;
        editFocusChangeListeners = builder.innerEditFocusChangeListeners;

        logTracker = new LogTracker(builder.logTrack);
        if (builder.logTrack) {
            viewClickListeners.add(logTracker);
            editFocusChangeListeners.add(logTracker);
            keyboardStatusListeners.add(logTracker);
            panelChangeListeners.add(logTracker);
        }


        this.activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.activity.getWindow().getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(this);


        /**
         * 1. if current flag is None,should show keyboard
         * 2. current flag is not None or KeyBoard that means some panel is showing,hide it and show keyboard
         */
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkout currentFlag to keyboard
                boolean result = checkoutFlag(Constants.FLAG_KEYBOARD);
                //editText click will make keyboard visible by system,so if checkoutFlag fail,should hide keyboard.
                if (!result && flag != Constants.FLAG_KEYBOARD) {
                    KbPanelHelper.hideKeyboard(activity, v);
                }
                notifyViewClick(v);
            }
        });


        editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !preventOpeningKeyboard) {
                    checkoutFlag(Constants.FLAG_KEYBOARD);
                }
                preventOpeningKeyboard = false;
                notifyEditFocusChange(v, hasFocus);
            }
        });


        /**
         * the EmptyView will help you to hide contentView when click it
         * for example，when you chatting in pager and keyboard is showing， you click the EmptyView to hide keyboard
         */
        if (emptyView != null) {
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkoutFlag(Constants.FLAG_NONE);
                    notifyViewClick(v);
                }
            });
        }

        /**
         * save panel that you want to use these to checkout
         */
        for (int i = 0; i < panelItemSparseArray.size(); i++) {
            final PanelItem panelItem = panelItemSparseArray.get(panelItemSparseArray.keyAt(i));
            panelItem.getKeyView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (System.currentTimeMillis() - preClickTime <= Constants.PROTECT_KEY_CLICK_DURATION) {
                        logTracker.log("PanelSwitchHelper#PanelSwitchHelper panelItem invalid click! preClickTime: " + preClickTime + " currentClickTime: " + System.currentTimeMillis());
                        return;
                    }

                    if (flag == panelItem.getFlag() && panelItem.isToggle() && ((View) panelItem.getPanelView()).isShown()) {
                        checkoutFlag(Constants.FLAG_KEYBOARD);
                    } else {
                        checkoutFlag(panelItem.getFlag());
                    }

                    preClickTime = System.currentTimeMillis();
                    notifyViewClick(v);
                }
            });
        }
    }

    private boolean checkoutFlag(int endFlag) {

        if (isCheckoutDoing()) {
            logTracker.log("PanelSwitchHelper#checkoutFlag -- is doing checkout,skip!");
            return false;
        }

        isCheckoutDoing = true;

        if (flag == endFlag) {
            logTracker.log("PanelSwitchHelper#checkoutFlag -- flag is the same as enfFlag, it doesn't need to be handled!");
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
                lockContentHeight(contentView);
                hidePanelByFlag(Constants.FLAG_KEYBOARD);
                //make sure when panel is showing，the keyboard was gone.
                postShowPanelRunnable(endFlag);
                postUnlockHeightContentRunnable(contentView);
                break;
            }

            default: {
                if (endFlag == Constants.FLAG_KEYBOARD) {
                    lockContentHeight(contentView);
                    hidePanelByFlag(flag);
                    showPanelByFlag(Constants.FLAG_KEYBOARD);
                    postUnlockHeightContentRunnable(contentView);
                } else {
                    hidePanelByFlag(flag);
                    showPanelByFlag(endFlag);
                }
            }
        }

        //通知更改
        if (flag == endFlag) {
            notifyPanelChange(flag);
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
        editView.postDelayed(unlockContentHeightRunnable, Constants.DELAY_UNLOCK_CONTENT_TIME);
    }

    private void postShowKeyboardRunnable() {
        checkoutKeyboardRunnable = new CheckoutKeyboardRunnable();
        editView.postDelayed(checkoutKeyboardRunnable, Constants.DELAY_SHOW_KEYBOARD_TIME);
    }

    private void postShowPanelRunnable(int endFlag) {
        checkoutPanelRunnable = new CheckoutPanelRunnable(endFlag);
        editView.postDelayed(checkoutPanelRunnable, Constants.DELAY_SHOW_KEYBOARD_TIME);
    }

    private void showPanelByFlag(int flag) {
        switch (flag) {
            case Constants.FLAG_NONE: {
                break;
            }
            case Constants.FLAG_KEYBOARD: {
                editView.requestFocus();
                postShowKeyboardRunnable();
                break;
            }
            default: {
                int keyboardHeight = KbPanelHelper.getKeyBoardHeight(activity);
                logTracker.log("PanelSwitchHelper#showPanelByFlag -- keyboard get height is : " + keyboardHeight);
                IPanelView panelView = panelItemSparseArray.get(flag).getPanelView();
                ((View) panelView).getLayoutParams().height = keyboardHeight;
                ((View) panelView).requestLayout();
                if (panelView.changeLayout()) {
                    panelView.onChangeLayout(-1, KbPanelHelper.getKeyBoardHeight(activity));
                }
                ((View) panelView).setVisibility(View.VISIBLE);
                setEmptyViewVisible(true);
            }
        }
        this.flag = flag;
    }

    private void hidePanelByFlag(int flag) {
        switch (flag) {
            case Constants.FLAG_NONE: {
                break;
            }
            case Constants.FLAG_KEYBOARD: {
                isHidingKeyboardByUser = true;
                KbPanelHelper.hideKeyboard(activity, editView);
                setEmptyViewVisible(false);
                break;
            }
            default: {
                IPanelView panelView = panelItemSparseArray.get(flag).getPanelView();
                if (((View) panelView).isShown()) {
                    ((View) panelView).setVisibility(View.GONE);
                }
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
        if (emptyView != null) {
            emptyView.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onGlobalLayout() {

        //get window height exclude SystemUi(statusBar and navigationBar)
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int contentHeight = r.bottom - r.top;

        //get statusBar 和 navigationBar height
        int systemUIHeight;
        int statusBarHeight = KbPanelHelper.getStatusBarHeight(activity);
        int navigationBatHeight = KbPanelHelper.getNavigationBarHeight(activity);
        if (KbPanelHelper.isPortrait(activity)) {
            systemUIHeight = KbPanelHelper.isNavigationBarShow(activity) ? statusBarHeight + navigationBatHeight : statusBarHeight;
        } else {
            systemUIHeight = statusBarHeight;
        }

        //get window height include SystemUi
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        int heightDiff = screenHeight - (contentHeight);

        //get keyboard height
        int keyboardHeight = 0;
        if (keyboardHeight == 0 && heightDiff > systemUIHeight) {
            keyboardHeight = heightDiff - systemUIHeight;
        }

        if (isKeyboardShowing) {
            //meet Hinding keyboard
            if (keyboardHeight <= 0) {
                if (isHidingKeyboardByUser) {
                    isHidingKeyboardByUser = false;
                } else {
                    hookSystemBackForHindPanel();
                }
                isKeyboardShowing = false;
                notifyKeyboardState(false);
            }
        } else {
            //meet Showing keyboard,
            if (keyboardHeight > 0) {
                Log.d(Constants.LOG_TAG, "keyboard set height is : " + keyboardHeight);
                KbPanelHelper.setKeyBoardHeight(activity, keyboardHeight);
                isKeyboardShowing = true;
                notifyKeyboardState(true);
            }
        }
    }

    /**
     * This will be called when User press System Back Button.
     * 1. if keyboard is showing, should be hide;
     * 2. if you want to hide panel(exclude keyboard),you should call it before {@link Activity#onBackPressed()} to hook it.
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
        editView.requestFocus();
    }

    public void showKeyboard() {
        if (editView.hasFocus()) {
            editView.performClick();
        } else {
            requestFocus(false);
        }
    }

    public void resetState() {
        checkoutFlag(Constants.FLAG_NONE);
    }

    @Nullable
    private PanelItem getPanelItem(int flag) {
        return panelItemSparseArray.get(flag);
    }

    private void notifyViewClick(View view) {
        for (OnViewClickListener listener : viewClickListeners) {
            listener.onViewClick(view);
        }
    }

    private void notifyPanelChange(int flag) {
        switch (flag) {
            case Constants.FLAG_NONE: {
                notifyPanelChange(false, null);
                break;
            }
            case Constants.FLAG_KEYBOARD: {
                notifyPanelChange(true, null);
                break;
            }
            default: {
                notifyPanelChange(false, getPanelItem(flag));
            }
        }
    }

    private void notifyPanelChange(boolean keyboardVisible, PanelItem panelView) {
        for (OnPanelChangeListener listener : panelChangeListeners) {
            listener.onPanelChange(keyboardVisible, panelView);
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
            KbPanelHelper.showKeyboard(activity, editView);
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

        private List<OnViewClickListener> innerViewClickListeners;
        private List<OnPanelChangeListener> innerPanelChangeListeners;
        private List<OnKeyboardStateListener> innerKeyboardStatusListeners;
        private List<OnEditFocusChangeListener> innerEditFocusChangeListeners;

        private Activity innerActivity;
        private View innerContentView;
        private EditText innerEditText;
        private View innerEmptyView;
        private SparseArray<PanelItem> innerPanelArray;

        private boolean logTrack;

        public Builder(Activity activity) {
            this.innerActivity = activity;
            innerPanelArray = new SparseArray<>();
            innerViewClickListeners = new ArrayList<>();
            innerPanelChangeListeners = new ArrayList<>();
            innerKeyboardStatusListeners = new ArrayList<>();
            innerEditFocusChangeListeners = new ArrayList<>();
        }

        public Builder bindContentView(View contentView) {
            this.innerContentView = contentView;
            return this;
        }

        public Builder bindEditText(EditText editText) {
            this.innerEditText = editText;
            return this;
        }

        public Builder bindEmptyView(View emptyView) {
            this.innerEmptyView = emptyView;
            return this;
        }

        /**
         * 绑定切换布局
         *
         * @param keyView        触发切换panelView的布局
         * @param panelView      面板布局
         * @param toggleKeyboard true，则当面板为panelView时，再次点击keyView则切换回输入法
         */
        public Builder bindPanelItem(@NonNull View keyView, @NonNull IPanelView panelView, final boolean toggleKeyboard) {
            if (keyView == null) {
                throw new IllegalArgumentException("keyView is not a view!");
            }
            if (panelView == null || !(panelView instanceof IPanelView)) {
                throw new IllegalArgumentException("panelView is not a view!");
            }

            innerPanelArray.append(keyView.getId(), new PanelItem(keyView, panelView, toggleKeyboard));
            return this;
        }

        public Builder bindPanelItem(@NonNull View keyView, @NonNull IPanelView panelView) {
            return bindPanelItem(keyView, panelView, false);
        }

        public Builder addViewClickListener(OnViewClickListener listener) {
            if (listener != null) {
                innerViewClickListeners.add(listener);
            }
            return this;
        }

        public Builder addPanelChangeListener(OnPanelChangeListener listener) {
            if (listener != null) {
                innerPanelChangeListeners.add(listener);
            }
            return this;
        }

        public Builder addKeyboardStateListener(OnKeyboardStateListener listener) {
            if (listener != null) {
                innerKeyboardStatusListeners.add(listener);
            }
            return this;
        }

        public Builder addEdittextFocesChangeListener(OnEditFocusChangeListener listener) {
            if (listener != null) {
                innerEditFocusChangeListeners.add(listener);
            }
            return this;
        }

        public Builder logTrack(boolean logTrack) {
            this.logTrack = logTrack;
            return this;
        }

        public PanelSwitchHelper build(boolean showKeyboard) {
            if (innerActivity == null) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : innerActivity can't be null!please set value by call #Builder");
            }

            if (innerContentView == null) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : contentView can't be null,please set value by call #bindContentView");
            }

            if (innerEditText == null) {
                throw new IllegalArgumentException("PanelSwitchHelper$Builder#build : innerEditText can't be null!please set value by call #bindEditText");
            }

            final PanelSwitchHelper panelSwitchHelper = new PanelSwitchHelper(this);
            if (showKeyboard && panelSwitchHelper.editView != null) {
                panelSwitchHelper.editView.requestFocus();
            }
            return panelSwitchHelper;
        }

        public PanelSwitchHelper build() {
            return build(false);
        }
    }

}
