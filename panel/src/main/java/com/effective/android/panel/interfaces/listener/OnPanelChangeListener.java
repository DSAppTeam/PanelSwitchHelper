package com.effective.android.panel.interfaces.listener;


import com.effective.android.panel.view.PanelView;

/**
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public interface OnPanelChangeListener {

    void onKeyboard();

    void onNone();

    void onPanel(PanelView view);

    void onPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height);
}
