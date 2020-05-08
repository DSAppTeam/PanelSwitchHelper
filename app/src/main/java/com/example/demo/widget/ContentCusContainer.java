package com.example.demo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.effective.R;
import com.effective.android.panel.view.content.ContentContainerImpl;
import com.effective.android.panel.view.content.IContentContainer;

/**
 * 实现IContentContainer接口，可参考
 * {@link com.effective.android.panel.view.content.ContentFrameContainer} 等库提供的模版实现基础的container容器
 * demo已约束布局为例子，使用者按需扩展就可以了
 * Created by yummyLau on 2020/05/07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ContentCusContainer extends ConstraintLayout implements IContentContainer {

    @IdRes
    int editTextId;
    @IdRes
    int emptyViewId;

    private ContentContainerImpl contentContainer;

    public ContentCusContainer(Context context) {
        this(context, null);
    }

    public ContentCusContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentCusContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    private void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ContentCusContainer, defStyleAttr, 0);
        if (typedArray != null) {
            editTextId = typedArray.getResourceId(R.styleable.ContentCusContainer_cus_edit_view, -1);
            emptyViewId = typedArray.getResourceId(R.styleable.ContentCusContainer_cus_empty_view, -1);
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentContainer = new ContentContainerImpl(this, editTextId, emptyViewId);
    }

    @Override
    public void layoutGroup(int l, int t, int r, int b) {
        contentContainer.layoutGroup(l, t, r, b);
    }

    public View findTriggerView(int id) {
        return contentContainer.findTriggerView(id);
    }

    @Override
    public void adjustHeight(int targetHeight) {
        contentContainer.adjustHeight(targetHeight);
    }


    @Override
    public void emptyViewVisible(boolean visible) {
        contentContainer.emptyViewVisible(visible);
    }

    @Override
    public void setEmptyViewClickListener(OnClickListener l) {
        contentContainer.setEmptyViewClickListener(l);
    }

    @Override
    public EditText getEditText() {
        return contentContainer.getEditText();
    }

    @Override
    public void setEditTextClickListener(OnClickListener l) {
        contentContainer.setEditTextClickListener(l);
    }

    @Override
    public void setEditTextFocusChangeListener(OnFocusChangeListener l) {
        contentContainer.setEditTextFocusChangeListener(l);
    }

    @Override
    public void clearFocusByEditText() {
        contentContainer.clearFocusByEditText();
    }

    @Override
    public void requestFocusByEditText() {
        contentContainer.requestFocusByEditText();
    }

    @Override
    public boolean editTextHasFocus() {
        return contentContainer.editTextHasFocus();
    }

    @Override
    public void preformClickForEditText() {
        contentContainer.preformClickForEditText();
    }
}
