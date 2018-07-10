package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;

import com.effective.android.panel.interfaces.ViewAssertion;

/**
 *     --------------------
 *    | PanelSwitchLayout  |
 *    |  ----------------  |
 *    | |                | |
 *    | |ContentContainer| |
 *    | |                | |
 *    |  ----------------  |
 *    |  ----------------  |
 *    | | PanelContainer | |
 *    |  ----------------  |
 *     --------------------
 * Created by yummyLau on 18-7-10
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class PanelContainer extends LinearLayout implements ViewAssertion {

    private SparseArray<PanelView> mPanelViewSparseArray;

    public PanelContainer(Context context) {
        this(context, null);
    }

    public PanelContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public PanelContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        mPanelViewSparseArray = new SparseArray<>();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (!(view instanceof PanelView)) {
                throw new RuntimeException("PanelContainer -- PanelContainer's child should be PanelView");
            }
            PanelView panelView = (PanelView) view;
            mPanelViewSparseArray.put(panelView.getTriggerViewId(), panelView);
            panelView.setVisibility(GONE);
        }
    }

    public SparseArray<PanelView> getPanelSparseArray() {
        return mPanelViewSparseArray;
    }
}

