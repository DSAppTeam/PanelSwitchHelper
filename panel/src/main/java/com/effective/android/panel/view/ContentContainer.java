package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.effective.android.panel.R;
import com.effective.android.panel.interfaces.ViewAssertion;


/**
 *  --------------------
 * | PanelSwitchLayout  |
 * |  ----------------  |
 * | |                | |
 * | |ContentContainer| |
 * | |                | |
 * |  ----------------  |
 * |  ----------------  |
 * | | PanelContainer | |
 * |  ----------------  |
 * --------------------
 * Created by yummyLau on 18-7-10
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ContentContainer extends LinearLayout implements ViewAssertion {

    private EditText mEditText;
    private View mEmptyView;

    @IdRes
    int editTextId;
    @IdRes
    int emptyViewId;

    public ContentContainer(Context context) {
        this(context, null);
    }

    public ContentContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ContentContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr, defStyleRes);
    }

    private void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ContentContainer, defStyleAttr, 0);
        if (typedArray != null) {
            editTextId = typedArray.getResourceId(R.styleable.ContentContainer_edit_view, -1);
            emptyViewId = typedArray.getResourceId(R.styleable.ContentContainer_empty_view, -1);
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mEditText = findViewById(editTextId);
        mEmptyView = findViewById(emptyViewId);
        assertView();
    }

    @Override
    public void assertView() {
        if (mEditText == null) {
            throw new RuntimeException("ContentContainer should set edit_view to get the editText!");
        }
    }

    public boolean hasEmptyView() {
        return mEmptyView != null;
    }

    @Nullable
    public View getEmptyView() {
        return mEmptyView;
    }

    public EditText getEditText() {
        return mEditText;
    }

    public void setEmptyViewClickListener(OnClickListener l) {
        if (mEmptyView != null) {
            mEmptyView.setOnClickListener(l);
        }
    }

    public void setEditTextClickListener(OnClickListener l) {
        mEditText.setOnClickListener(l);
    }

    public void setEditTextFocusChangeListener(OnFocusChangeListener l) {
        mEditText.setOnFocusChangeListener(l);
    }

    public void clearFocusByEditText() {
        mEditText.clearFocus();
    }

    public void requestFocusByEditText() {
        mEditText.requestFocus();
    }

    public boolean editTextHasFocus() {
        return mEditText.hasFocus();
    }

    public void preformClickByEditText() {
        mEditText.performClick();
    }
}
