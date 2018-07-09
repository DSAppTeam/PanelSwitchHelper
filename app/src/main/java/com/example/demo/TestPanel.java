package com.example.demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sample.android.panel.panel.IPanelView;


/**
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class TestPanel extends FrameLayout implements IPanelView {

    public TestPanel(@NonNull Context context) {
        super(context);
    }

    public TestPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean changeLayout() {
        return true;
    }

    @Override
    public void onChangeLayout(int width, int height) {
        if (height != getLayoutParams().height) {
            getLayoutParams().height = height;
            requestLayout();
        }
    }

    @Nullable
    @Override
    public String name() {
        return "TestPanel";
    }
}
