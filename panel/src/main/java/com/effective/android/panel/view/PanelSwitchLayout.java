package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.effective.android.panel.ViewAssertion;

/**
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
