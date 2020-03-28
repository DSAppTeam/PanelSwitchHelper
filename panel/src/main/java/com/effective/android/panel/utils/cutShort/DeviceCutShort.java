package com.effective.android.panel.utils.cutShort;

import android.content.Context;
import android.view.View;

public interface DeviceCutShort {

    boolean hasCutShort(Context context);

    int getCutShortHeight(View view);
}