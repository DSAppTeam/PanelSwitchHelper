package com.example.demo.systemui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.effective.R;

import java.lang.reflect.Field;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * 状态栏view
 * Created by yummylau on 2017/9/09.
 */

public class StatusBarView extends LinearLayout {

    @ColorRes
    private int mStatusBarColor;

    public StatusBarView(Context context) {
        this(context, null);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr);
    }

    private void initView(AttributeSet attrs, int defStyle) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_status_bar_layout, this, true);
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StatusBarView, defStyle, 0);
        if (typedArray != null) {
            mStatusBarColor = typedArray.getResourceId(R.styleable.StatusBarView_status_bar_color, R.color.colorPrimary);
        } else {
            mStatusBarColor = R.color.colorPrimary;
        }
        setBackgroundColor(ContextCompat.getColor(getContext(), mStatusBarColor));
        view.findViewById(R.id.status_bar).setMinimumHeight(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? getStatusBarHeight(getContext()) : 0);
    }

    public void setColor(@ColorRes int statusBarColor) {
        mStatusBarColor = statusBarColor;
        setBackgroundColor(ContextCompat.getColor(getContext(), mStatusBarColor));
    }


    public static int getStatusBarHeight(Context context) {
        int sbar = 0;
        try {
            Class c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception var7) {
//            var7.printStackTrace();
        }
        return sbar;
    }
}
