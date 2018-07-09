package com.sample.android.panel.listener;

import android.view.View;

/**
 * preventing listeners that {@link com.sample.android.panel.PanelSwitchHelper} set these to view from being overwritten
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public interface OnViewClickListener {

    void onViewClick(View view);
}
