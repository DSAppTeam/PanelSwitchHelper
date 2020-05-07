package com.effective.android.panel.view.content;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.EditText;


public interface IContentContainer {

    //容器行为
    View findTriggerView(@IdRes int id);
    void layoutGroup(int l,int t,int r,int b);
    void adjustHeight(int targetHeight);

    //empty相关
    void emptyViewVisible(boolean visible);
    void setEmptyViewClickListener(View.OnClickListener l);

    //editText相关
    EditText getEditText();
    void setEditTextClickListener(View.OnClickListener l);
    void setEditTextFocusChangeListener(View.OnFocusChangeListener l);
    void clearFocusByEditText();
    void requestFocusByEditText();
    boolean editTextHasFocus();
    void preformClickForEditText();
}
