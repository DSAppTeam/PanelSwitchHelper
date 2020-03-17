package com.effective.android.panel.view;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;

import com.effective.android.panel.Constants;
import com.effective.android.panel.LogTracker;
import com.effective.android.panel.PanelHelper;
import com.effective.android.panel.interfaces.ViewAssertion;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;

import java.util.ArrayList;
import java.util.List;

import static android.animation.LayoutTransition.APPEARING;
import static android.animation.LayoutTransition.DISAPPEARING;

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
public class PanelContainer extends LinearLayout implements ViewAssertion {

    private static final String TAG = PanelContainer.class.getSimpleName();
    private SparseArray<PanelView> mPanelViewSparseArray;
    private List<OnPanelChangeListener> mListeners = new ArrayList<>();
    private LayoutTransition mLayoutTransition;

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
//        mLayoutTransition = new LayoutTransition();
        mLayoutTransition = null;
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

    public void addPanelChangeListener(List<OnPanelChangeListener> listeners) {
        if (listeners != null && !listeners.isEmpty()) {
            mListeners.addAll(listeners);
        }
    }

    public void notifyPanelChange(int panelId) {
        for (OnPanelChangeListener listener : mListeners) {
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

    public void notifyPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
        LogTracker.Log(TAG + "#showPanel", "change panel's layout, " + oldWidth + " -> " + width + " " + oldHeight + " -> " + height);
        for (OnPanelChangeListener listener : mListeners) {
            listener.onPanelSizeChange(panelView, portrait, oldWidth, oldHeight, width, height);
        }
    }

    public Pair<Integer, Integer> showPanel(int panelId, int width, int height) {
        setLayoutTransition(mLayoutTransition);
        PanelView panelView = mPanelViewSparseArray.get(panelId);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) panelView.getLayoutParams();
        int oldWidth = params.width;
        int oldHeight = params.height;
        if (oldWidth != width || oldHeight != height) {
            params.width = width;
            params.height = height;
            panelView.requestLayout();
            boolean isPortrait = PanelHelper.isPortrait(getContext());
            notifyPanelSizeChange(panelView, isPortrait, oldWidth, oldHeight, width, height);
        }
        panelView.setVisibility(View.VISIBLE);
        return new Pair<>(oldWidth, oldHeight);
    }

    public void hidePanel(int panelId, int toPanelId) {
        setLayoutTransition(toPanelId == Constants.PANEL_NONE ? null : mLayoutTransition);
        PanelView panelView = mPanelViewSparseArray.get(panelId);
        panelView.setVisibility(View.GONE);
    }
}

