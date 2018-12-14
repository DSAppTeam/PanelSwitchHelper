package com.effective.android.panel.interfaces;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.PopupWindow;

public interface IPopupSupport {
    @NonNull
    Activity getActivity();

    @NonNull
    PopupWindow getPopupWindow();
}
