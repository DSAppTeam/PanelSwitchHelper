package com.effective.android.panel.interfaces.listener;

import android.view.View;

/**
 * listen to  {@link android.widget.EditText} focus change
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public interface OnEditFocusChangeListener {

    void onFocusChange(View view, boolean hasFocus);
}
