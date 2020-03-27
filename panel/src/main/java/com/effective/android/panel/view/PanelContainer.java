package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.effective.android.panel.Constants;
import com.effective.android.panel.LogTracker;
import com.effective.android.panel.interfaces.ViewAssertion;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;

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
public class PanelContainer extends FrameLayout implements ViewAssertion {

    private SparseArray<PanelView> mPanelViewSparseArray = new SparseArray<>();

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

    @NonNull
    public SparseArray<PanelView> getPanelSparseArray() {
        return mPanelViewSparseArray;
    }

    @Nullable
    public PanelView getPanelView(int panelId) {
        return mPanelViewSparseArray.get(panelId);
    }

    public int getPanelId(PanelView view) {
        if (view == null) {
            return Constants.PANEL_KEYBOARD;
        } else {
            return view.getTriggerViewId();
        }
    }

    public void hidePanels() {
        for (int i = 0; i < mPanelViewSparseArray.size(); i++) {
            PanelView panelView = mPanelViewSparseArray.get(mPanelViewSparseArray.keyAt(i));
            panelView.setVisibility(GONE);
        }
    }

    public Pair<Integer, Integer> showPanel(int panelId, @NonNull Pair<Integer, Integer> size) {
        PanelView panelView = mPanelViewSparseArray.get(panelId);
        ViewGroup.LayoutParams layoutParams = panelView.getLayoutParams();
        Pair<Integer, Integer> curSize = new Pair<>(layoutParams.width, layoutParams.height);
        if (curSize.first != size.first || curSize.second != size.second) {
            layoutParams.width = size.first;
            layoutParams.height = size.second;
            panelView.setLayoutParams(layoutParams);
        }
        panelView.setVisibility(View.VISIBLE);
        return curSize;
    }
}

