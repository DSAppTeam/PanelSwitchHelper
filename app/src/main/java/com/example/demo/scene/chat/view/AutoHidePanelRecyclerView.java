package com.example.demo.scene.chat.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.example.demo.util.DisplayUtils;

public class AutoHidePanelRecyclerView extends RecyclerView {

    PanelSwitchHelper panelSwitchHelper;

    public void setPanelSwitchHelper(PanelSwitchHelper panelSwitchHelper) {
        this.panelSwitchHelper = panelSwitchHelper;
    }

    public AutoHidePanelRecyclerView(Context context) {
        this(context, null);
    }

    public AutoHidePanelRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoHidePanelRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(panelSwitchHelper != null){
            panelSwitchHelper.hookSystemBackByPanelSwitcher();
        }
        return super.onTouchEvent(e);
    }
}
