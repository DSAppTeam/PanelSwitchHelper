package com.example.demo.chat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CusRecyclerView extends RecyclerView {

    public boolean startScroll = false;

    public CusRecyclerView(Context context) {
        super(context);
    }

    public CusRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CusRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * 当recyclerview 内的holder消费事件是不会对调此方法。
     * 当没有任何子view消费的时候，默认交于上层处理。
     * 如果放在 Container 内部并打开 auto_reset，可实现自动隐藏
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean result = super.onTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_DOWN && result) {
            startScroll = false;
            return false;
        }
        if (e.getAction() == MotionEvent.ACTION_SCROLL && result) {
            startScroll = true;
            return false;
        }
        if (e.getAction() == MotionEvent.ACTION_UP && result) {
            if (!startScroll) {
                return false;
            }
        }
        return result;
    }
}
