package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 用于提供emptyView帮助用户把触摸事件传递到容器外，提升用户体验 https://github.com/YummyLau/PanelSwitchHelper/issues/6
 * Created by yummyLau on 2019-11-05
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class EmptyView extends View {

    private OnClickListener clickListener;

    public EmptyView(Context context) {
        super(context);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (clickListener != null) {
                clickListener.onClick(this);
                return false;
            }
        }
        return super.onTouchEvent(event);
    }


    @CallSuper
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if (l != null) {
            this.clickListener = l;
        }
        super.setOnClickListener(null);
    }
}
