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
import com.effective.android.panel.LogTracker;
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
 *
 * updated by yummylau on 20/03/18
 * 重构整个输入法切换框架，移除旧版使用 weight+Runnable延迟切换，使用新版 layout+动画无缝衔接！
 */
public class PanelSwitchLayout extends LinearLayout implements ViewAssertion {

    private static final String TAG = PanelSwitchLayout.class.getSimpleName();
    private ContentContainer contentContainer;
    private PanelContainer panelContainer;
    private int panelId = Constants.PANEL_NONE;


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

    public void checkoutPanel(int panelId) {
        switch (panelId) {
            case Constants.PANEL_NONE: {
                panelContainer.hidePanel();
                PanelHelper.hideKeyboard(getContext(), contentContainer.getEditText());
                contentContainer.clearFocusByEditText();
                contentContainer.emptyViewVisible(false);
                break;
            }
            case Constants.PANEL_KEYBOARD: {
                panelContainer.hidePanel();
                PanelHelper.showKeyboard(getContext(), contentContainer.getEditText());
                contentContainer.emptyViewVisible(true);
                break;
            }
            default: {
                PanelHelper.hideKeyboard(getContext(), contentContainer.getEditText());
                panelContainer.showPanel(panelId);
                contentContainer.emptyViewVisible(true);
            }
        }
        this.panelId = panelId;
        LogTracker.Log(TAG + "#checkoutPanel", "panel' id :" + panelId);
        panelContainer.notifyPanelChange(this.panelId);
        requestLayout();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int visibility = getVisibility();
        if (visibility != VISIBLE) {
            return;
        }
        int keyboardHeight = 691;
        int allHeight = b - t;
        int contentTop = (panelId == Constants.PANEL_NONE) ? t : t - keyboardHeight;
        int contentHeight = allHeight;
        int panelTop = t + contentHeight;
        int panelHeight = keyboardHeight;
        setTransition(200);


        Log.d("xxxx", "");
        Log.d("xxxx", " onLayout  =======> 被回调 ");
        Log.d("xxxx", " 屏幕高度（不包含虚拟键高度,包含状态栏和ToolBar高度） : " + getTotalHeight());
        Log.d("xxxx", " 界面高度（包含状态栏和ToolBar高度） " + getVisibleHeight());
        Log.d("xxxx", " 获取ToolBar高度（包含状态栏） " + getTitleBarHeight());
        Log.d("xxxx", " layout参数 changed : " + changed + " l : " + l + " t : " + t + " r : " + r + " b : " + b);
        Log.d("xxxx", " panel场景  : " + (panelId == Constants.PANEL_NONE ? "收起" : (panelId == Constants.PANEL_KEYBOARD ? "键盘" : "面板")));
        Log.d("xxxx", " 内容 top  ：" + contentTop);
        Log.d("xxxx", " 内容 高度 ：" + contentHeight);
        Log.d("xxxx", " 面板 top ：" + panelTop);
        Log.d("xxxx", " 面板 高度 " + panelHeight);

        //处理第一个view contentContainer
        {
            contentContainer.layout(l, contentTop, r, contentTop + contentHeight);
            ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
            if (layoutParams.height != contentHeight) {
                layoutParams.height = contentHeight;
                contentContainer.setLayoutParams(layoutParams);
            }
            contentContainer.setBackgroundColor(Color.RED);
        }

        //处理第二个view panelContainer
        {
            panelContainer.layout(l, panelTop, r, panelTop + panelHeight);
            ViewGroup.LayoutParams layoutParams = panelContainer.getLayoutParams();
            if (layoutParams.height != panelHeight) {
                layoutParams.height = panelHeight;
                panelContainer.setLayoutParams(layoutParams);
            }
            panelContainer.setBackgroundColor(Color.GREEN);
        }
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
        this.contentContainer = (ContentContainer) firstView;

        if (!(secondView instanceof PanelContainer)) {
            throw new RuntimeException("PanelSwitchLayout -- the second view is ContentContainer,the other is PanelContainer！");
        }
        this.panelContainer = (PanelContainer) secondView;
    }
}
