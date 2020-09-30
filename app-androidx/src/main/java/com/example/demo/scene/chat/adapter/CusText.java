package com.example.demo.scene.chat.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

public class CusText extends AppCompatTextView {

    public CusText(Context context) {
        super(context);
    }

    public CusText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CusText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
