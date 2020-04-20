package com.example.demo.chat;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CusRecyclerView extends RecyclerView {

    public boolean startScroll = false;
    private ResetPanel resetPanel;

    public CusRecyclerView(Context context) {
        super(context);
    }

    public CusRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CusRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setResetPanel(ResetPanel resetPanel){
        this.resetPanel = resetPanel;
        setItemAnimator(null);
    }

    public interface ResetPanel{
        void resetPanel();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean result = super.onTouchEvent(e);
        if(e.getAction() == MotionEvent.ACTION_DOWN && result){
            startScroll = false;
        }
        if(e.getAction() == MotionEvent.ACTION_SCROLL && result){
            startScroll = true;
        }
        if(e.getAction() == MotionEvent.ACTION_UP && result){
            if(resetPanel != null && !startScroll){
                resetPanel.resetPanel();
            }
        }
        return result;
    }
}
