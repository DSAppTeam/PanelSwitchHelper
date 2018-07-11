package com.example.demo.bean;

import android.support.annotation.DrawableRes;

/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class Emotion {
    
    public String text;

    @DrawableRes
    public int drawableRes;

    public Emotion(String text, @DrawableRes int drawableRes) {
        this.text = text;
        this.drawableRes = drawableRes;
    }
}
