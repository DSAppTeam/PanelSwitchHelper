package com.sample.android.panel.panel;

import android.support.annotation.Nullable;

/**
 * interface, everyPanel should implements
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public interface IPanelView {

    /**
     * @return true,#onChangeLayout will be called when the keyboard'height changes
     */
    boolean changeLayout();

    /**
     * will be call when the keyboard'height changes and #changeLayout return true
     * @param width
     * @param height
     */
    void onChangeLayout(int width, int height);

    /**
     * @return the value will be used by {@link com.sample.android.panel.LogTracker}
     */
    @Nullable
    String name();
}
