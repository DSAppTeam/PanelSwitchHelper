package com.effective.android.panel.interfaces.listener;

import android.view.View;

/**
 * preventing listeners that {@link com.effective.android.panel.PanelSwitchHelper} set these to view from being overwritten
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public interface OnViewClickListener {

    void onClickBefore(View view);
}
