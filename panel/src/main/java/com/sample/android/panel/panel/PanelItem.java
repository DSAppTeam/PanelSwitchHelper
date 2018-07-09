package com.sample.android.panel.panel;

import android.view.View;

/**
 * include keyView and {@link IPanelView}
 * for example, if you has a viewpager(IPanelView) that contains Emoji Expressions,you need key view(may be a button)
 * that will be clicked to checkout viewpager.
 * <p>
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public class PanelItem {

    private View mKeyView;
    private IPanelView mPanelView;
    private boolean toggle;

    public PanelItem(View keyView, IPanelView panelView, boolean toggle) {
        mKeyView = keyView;
        mPanelView = panelView;
        this.toggle = toggle;
    }

    /**
     * @return unique flag
     */
    public int getFlag() {
        return mKeyView.getId();
    }

    public View getKeyView() {
        return mKeyView;
    }

    public IPanelView getPanelView() {
        return mPanelView;
    }

    public boolean isToggle() {
        return toggle;
    }

    public String getPanelName() {
        return mPanelView.name();
    }
}
