package com.effective.android.panel.utils.cutShort;

import android.content.Context;
import android.view.View;

public interface DeviceCutShort {

    boolean hasCutShort(Context context);

    boolean isCusShortVisible(Context context);

    int getCurrentCutShortHeight(View view);
}