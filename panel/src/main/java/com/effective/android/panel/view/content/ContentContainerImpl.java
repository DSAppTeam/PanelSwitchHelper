package com.effective.android.panel.view.content;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.effective.android.panel.interfaces.ViewAssertion;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * 内容区域代理
 * --------------------
 * Created by yummyLau on 2020/05/07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ContentContainerImpl implements IContentContainer, ViewAssertion {

    private ViewGroup mViewGroup;
    private EditText mEditText;
    private View mEmptyView;

    public ContentContainerImpl(ViewGroup realViewGroup, @IdRes int editTextId, @IdRes int emptyId) {
        this.mViewGroup = realViewGroup;
        this.mEditText = realViewGroup.findViewById(editTextId);
        this.mEmptyView = realViewGroup.findViewById(emptyId);
        assertView();
        int imeOptions = mEditText.getImeOptions();
        imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;        //Prohibited all screens
        mEditText.setImeOptions(imeOptions);
    }

    @Override
    public View findTriggerView(int id) {
        return mViewGroup.findViewById(id);
    }

    @Override
    public void layoutGroup(int l, int t, int r, int b) {
        mViewGroup.layout(l,t,r,b);
    }

    @Override
    public void adjustHeight(int targetHeight) {
        ViewGroup.LayoutParams layoutParams = mViewGroup.getLayoutParams();
        if (layoutParams != null && layoutParams.height != targetHeight) {
            layoutParams.height = targetHeight;
            mViewGroup.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void assertView() {
        if (mEditText == null) {
            throw new RuntimeException("ContentContainer should set edit_view to get the editText!");
        }
    }

    @Override
    public void emptyViewVisible(boolean visible) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(visible ? VISIBLE : GONE);
        }
    }

    @Override
    public void setEmptyViewClickListener(View.OnClickListener l) {
        if (mEmptyView != null) {
            mEmptyView.setOnClickListener(l);
        }
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }

    @Override
    public void setEditTextClickListener(View.OnClickListener l) {
        mEditText.setOnClickListener(l);
    }

    @Override
    public void setEditTextFocusChangeListener(View.OnFocusChangeListener l) {
        mEditText.setOnFocusChangeListener(l);
    }

    @Override
    public void clearFocusByEditText() {
        mEditText.clearFocus();
    }

    @Override
    public void requestFocusByEditText() {
        mEditText.requestFocus();
    }

    @Override
    public boolean editTextHasFocus() {
        return mEditText.hasFocus();
    }

    @Override
    public void preformClickForEditText() {
        mEditText.performClick();
    }
}
