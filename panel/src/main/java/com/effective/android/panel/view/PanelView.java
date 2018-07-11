package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.effective.android.panel.LogTracker;
import com.effective.android.panel.R;
import com.effective.android.panel.interfaces.ViewAssertion;

/**
 * interface, everyPanel should implements
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class PanelView extends FrameLayout implements ViewAssertion {

    private static final String TAG = PanelView.class.getSimpleName();

    private int panelLayoutId;
    private int triggerViewId;
    private View panelContentView;
    private boolean toggle;

    public PanelView(Context context) {
        this(context, null);
    }

    public PanelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public PanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr, defStyleRes);
    }

    private void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PanelView, defStyleAttr, 0);
        if (typedArray != null) {
            panelLayoutId = typedArray.getResourceId(R.styleable.PanelView_panel_layout, -1);
            triggerViewId = typedArray.getResourceId(R.styleable.PanelView_panel_trigger, -1);
            toggle = typedArray.getBoolean(R.styleable.PanelView_panel_toggle, true);
            typedArray.recycle();
        }
    }

    @Override
    public void assertView() {
        if (panelLayoutId == -1 || triggerViewId == -1) {
            throw new RuntimeException("PanelView -- you must set 'panel_layout' and panel_trigger by Integer id");
        }
        if (getChildCount() > 0) {
            throw new RuntimeException("PanelView -- you can't have any child!");
        }
        panelContentView = LayoutInflater.from(getContext()).inflate(panelLayoutId, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        assertView();
    }


    public void onChangeLayout(int width, int height) {
        LogTracker.getInstance().log(TAG + "#onChangeLayout", "width : " + width + " height: " + height);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.width = width;
        params.height = height;
        requestLayout();
    }


    @NonNull
    public int getTriggerViewId() {
        return triggerViewId;
    }

    public boolean isToggle() {
        return toggle;
    }
}
