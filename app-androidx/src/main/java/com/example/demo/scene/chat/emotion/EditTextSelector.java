package com.example.demo.scene.chat.emotion;

import android.widget.EditText;

import androidx.annotation.NonNull;

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/11/3
 * desc   : 当页面含有多个EditText时，根据业务选中当前获得焦点的EditText
 * version: 1.0
 */
public interface EditTextSelector {

    @NonNull
    EditText getEditText();

}
