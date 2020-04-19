package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.effective.android.panel.Constants;
import com.effective.android.panel.R;
import com.effective.android.panel.interfaces.ViewAssertion;
import com.effective.android.panel.utils.PanelUtil;
import com.effective.android.panel.utils.ReflectionUtils;


/**
 * --------------------
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

    public int panelState = Constants.PANEL_NONE;
    public int lastState = Constants.PANEL_NONE;
    public boolean isModify = false;
    public int keyboardHeight = 0;

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
        setOrientation(VERTICAL);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutVertical(changed,l, t, r, b);
    }

    void layoutVertical(boolean changed,int left, int top, int right, int bottom) {
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();

        int childTop;
        int childLeft;

        final int width = right - left;
        int childRight = width - paddingRight;
        int childSpace = width - paddingLeft - paddingRight;

        final int count = getChildCount();

        int mGravity = Gravity.TOP | Gravity.LEFT;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            mGravity = (Integer) ReflectionUtils.getFieldValue(this, "mGravity");
        } else {
            mGravity = getGravity();
        }

        final int majorGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
        final int minorGravity = mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;

        switch (majorGravity) {
            case Gravity.BOTTOM: {
                int mTotalLength = (Integer) ReflectionUtils.getFieldValue(this, "mTotalLength");
                childTop = paddingTop + bottom - top - mTotalLength;
                break;
            }
            case Gravity.CENTER_VERTICAL: {
                int mTotalLength = (Integer) ReflectionUtils.getFieldValue(this, "mTotalLength");
                childTop = paddingTop + (bottom - top - mTotalLength) / 2;
                break;
            }
            case Gravity.TOP:
            default:
                childTop = paddingTop;
                break;
        }

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null) {
                childTop += 0;
            } else if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                final LinearLayout.LayoutParams lp =
                        (LinearLayout.LayoutParams) child.getLayoutParams();

                int gravity = lp.gravity;
                if (gravity < 0) {
                    gravity = minorGravity;
                }
                final int layoutDirection = getLayoutDirection();
                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = paddingLeft + ((childSpace - childWidth) / 2)
                                + lp.leftMargin - lp.rightMargin;
                        break;

                    case Gravity.RIGHT:
                        childLeft = childRight - childWidth - lp.rightMargin;
                        break;

                    case Gravity.LEFT:
                    default:
                        childLeft = paddingLeft + lp.leftMargin;
                        break;
                }

                try {
                    Boolean hasDividerBeforeChildAt = (Boolean) ReflectionUtils.invokeMethod(
                            this, "hasDividerBeforeChildAt", new Class[]{Integer.class}, new Object[]{i});
                    if (hasDividerBeforeChildAt != null && hasDividerBeforeChildAt) {
                        int mDividerHeight = (Integer) ReflectionUtils.getFieldValue(this, "mDividerHeight");
                        childTop += mDividerHeight;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(i == 0){
                    if(panelState != Constants.PANEL_NONE){
                        if(!isModify){
                            childTop += keyboardHeight;
                            childHeight -= keyboardHeight;
                        }
                    }else{
                        if(isModify){
                            isModify = false;
                        }
                    }
                }

                childTop += lp.topMargin;

                int locationOffset = 0;
                int nextLocationOffset = 0;
                try {
                    Integer location = (Integer) ReflectionUtils.invokeMethod(
                            this, "getLocationOffset", new Class[]{View.class}, new Object[]{child});
                    if (location != null) {
                        locationOffset = location;
                    }
                    Integer nextLocation = (Integer) ReflectionUtils.invokeMethod(
                            this, "getNextLocationOffset", new Class[]{View.class}, new Object[]{child});
                    if (nextLocation != null) {
                        nextLocationOffset = nextLocation;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                setChildFrame(child, childLeft, childTop + locationOffset, childWidth, childHeight);

                childTop += childHeight + lp.bottomMargin + nextLocationOffset;


                try {
                    Integer skipCount = (Integer) ReflectionUtils.invokeMethod(
                            this, "getChildrenSkipCount", new Class[]{View.class, Integer.class}, new Object[]{child, i});
                    if (skipCount != null) {
                        i += skipCount;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mEditText = findViewById(editTextId);
        mEmptyView = findViewById(emptyViewId);
        int imeOptions = mEditText.getImeOptions();
        imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;        //Prohibited all screens
        mEditText.setImeOptions(imeOptions);
        assertView();
    }


    @Override
    public void assertView() {
        if (mEditText == null) {
            throw new RuntimeException("ContentContainer should set edit_view to get the editText!");
        }
    }


    public void emptyViewVisible(boolean visible) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(visible ? VISIBLE : GONE);
        }
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

    public void preformClickForEditText() {
        mEditText.performClick();
    }

    public void toKeyboardState() {
        if (editTextHasFocus()) {
            preformClickForEditText();
        } else {
            requestFocusByEditText();
        }
    }
}
