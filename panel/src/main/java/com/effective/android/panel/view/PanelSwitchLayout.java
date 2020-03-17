package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.effective.android.panel.Constants;
import com.effective.android.panel.PanelHelper;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.ViewAssertion;

/**
 * --------------------
 * | PanelSwitchLayout  |
 * |  ----------------  |
 * | |                | |
 * | |ContentContainer| |
 * | |                | |
 * |  ----------------  |
 * |  ----------------  |
 * | | PanelContainer | |
 * |  ----------------  |
 * --------------------
 * Created by yummyLau on 18-7-10
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class PanelSwitchLayout extends LinearLayout implements ViewAssertion {


    public PanelSwitchLayout(Context context) {
        this(context, null);
    }

    public PanelSwitchLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelSwitchLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public PanelSwitchLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr, defStyleRes);
    }

    private void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //nothing to do
    }

    /**
     * 获取窗口可见区域高度（包含状态栏和ToolBar高度）
     *
     * @return
     */
    private int getVisibleHeight() {
        Rect outRect = new Rect();
        getWindowVisibleDisplayFrame(outRect);
        return outRect.bottom;
    }

    /**
     * 获取屏幕高度（不包含虚拟键高度,包含状态栏和ToolBar高度）
     *
     * @return
     */
    private int getTotalHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    public boolean isSoftShowing() {
        return getTotalHeight() - getVisibleHeight() != 0;
    }

    private int getStatusBarHeight() {
        Rect outRect = new Rect();
        getWindowVisibleDisplayFrame(outRect);
        return outRect.top;
    }

    /**
     * 获取ToolBar高度（包含状态栏高度）
     *
     * @return
     */
    private int getTitleBarHeight() {
        View rootView = getRootView();
        if (rootView != null) {
            View window = rootView.findViewById(Window.ID_ANDROID_CONTENT);
            if (window != null) {
                return window.getTop() + (mHasStatusBar ? getStatusBarHeight() : 0);
            }
        }
        return mHasStatusBar ? getStatusBarHeight() : 0;
    }

    private int firstChildHeight;
    private boolean mHasStatusBar = true;
    private boolean secondChildState = false;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        firstChildHeight = getTotalHeight() - getTitleBarHeight();          //固定高度
    }

    public void toggle() {
        toggle(!secondChildState);
    }

    /**
     * 设置当前状态
     *
     * @param state
     */
    public void toggle(boolean state) {
        secondChildState = state;
        if (state) {
            if (isSoftShowing()) {
                hideSoft();
            } else {
                requestLayout();
            }
        } else {
            requestLayout();
        }
    }

    private void hideSoft() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("xxxx","l : " + l + " t : " + t + " r : " + r + " b : " + b);
        int visibility = this.getVisibility();
        int divTopHeight = 0;
        int divBotHeight = 0;
        if (visibility == VISIBLE) {
            int topHeight = getVisibleHeight() - getTitleBarHeight();
            int bottomHeight = getTotalHeight() - getVisibleHeight();

            Log.d("xxxx","");
            Log.d("xxxx","secondChildState " + secondChildState);
            if (isSoftShowing()) {
                secondChildState = false;
                divBotHeight = topHeight;
                divTopHeight = firstChildHeight - topHeight;
                setSecondChildHeight(bottomHeight);
                setTransition(200);
            } else {
                if (secondChildState) {
                    divBotHeight = topHeight - secondChildHeight;
                    divTopHeight = secondChildHeight;
                } else {
                    divBotHeight = topHeight;
                    divTopHeight = 0;
                }
                setTransition(200);
            }

            Log.d("xxxx","getTotalHeight " + getTotalHeight());
            Log.d("xxxx","getVisibleHeight " + getVisibleHeight());
            Log.d("xxxx","getTitleBarHeight " + getTitleBarHeight());
            Log.d("xxxx","topHeight " + topHeight);
            Log.d("xxxx","bottomHeight " + bottomHeight);
            Log.d("xxxx","firstChildHeight " + firstChildHeight);
            Log.d("xxxx","secondChildHeight " + secondChildHeight);
            Log.d("xxxx","isSoftShowing() " + isSoftShowing());
            Log.d("xxxx","divTopHeight " + divTopHeight);
            Log.d("xxxx","divBotHeight " + divBotHeight);
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            switch (i) {
                case 0:
                    view.layout(l, t - divTopHeight, r, t - divTopHeight + firstChildHeight);
                    Log.d("xxxx","v1 height  : " + layoutParams.height);
                    if (layoutParams.height != firstChildHeight) {
                        layoutParams.height = firstChildHeight;
                        view.setLayoutParams(layoutParams);
                    }
                    view.setBackgroundColor(Color.RED);
                    break;
                case 1:
                    view.layout(l, t + divBotHeight, r, t + divBotHeight + secondChildHeight);
                    Log.d("xxxx","v2 height  : " + layoutParams.height);
                    if (layoutParams.height != secondChildHeight) {
                        layoutParams.height = secondChildHeight;
                        view.setLayoutParams(layoutParams);
                    }
                    view.setBackgroundColor(Color.GREEN);
                    break;
            }
        }
    }

    private int secondChildHeight = DEFAULT_SECOND_CHILD_HEIGHT;
    private static final int DEFAULT_SECOND_CHILD_HEIGHT = 740;
    private static final int DEFAULT_SECOND_CHILD_MIN_HEIGHT = 400;
    private static final int DEFAULT_SECOND_CHILD_MAX_HEIGHT = 800;
    private static final int DEFAULT_SECOND_CHILD_MID_HEIGHT = 600;
    private int secondChildMinHeight = DEFAULT_SECOND_CHILD_MIN_HEIGHT;
    private int secondChildMaxHeight = DEFAULT_SECOND_CHILD_MAX_HEIGHT;

    private void setSecondChildHeight(int height) {
        secondChildHeight = height < secondChildMinHeight ? secondChildMinHeight : (height > secondChildMaxHeight ? secondChildMaxHeight : height);
    }


    @TargetApi(19)
    private void setTransition(long duration) {
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(duration);
        TransitionManager.beginDelayedTransition(this, changeBounds);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        assertView();
    }

    @Override
    public void assertView() {
        if (getChildCount() != 2) {
            throw new RuntimeException("PanelSwitchLayout -- PanelSwitchLayout should has two children,the first is ContentContainer,the other is PanelContainer！");
        }
        View firstView = getChildAt(0);
        View secondView = getChildAt(1);

        if (!(firstView instanceof ContentContainer)) {
            throw new RuntimeException("PanelSwitchLayout -- the first view is ContentContainer,the other is ContentContainer！");
        }
        if (!(secondView instanceof PanelContainer)) {
            throw new RuntimeException("PanelSwitchLayout -- the second view is ContentContainer,the other is PanelContainer！");
        }
    }
}
