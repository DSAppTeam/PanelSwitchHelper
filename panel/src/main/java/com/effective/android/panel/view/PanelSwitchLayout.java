package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.effective.android.panel.Constants;
import com.effective.android.panel.LogTracker;
import com.effective.android.panel.PanelHelper;
import com.effective.android.panel.interfaces.ViewAssertion;
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;

import java.util.List;

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
 * <p>
 * updated by yummyLau on 20/03/18
 * 重构整个输入法切换框架，移除旧版使用 weight+Runnable延迟切换，使用新版 layout+动画无缝衔接！
 */
public class PanelSwitchLayout extends LinearLayout implements ViewAssertion {

    private static final String TAG = PanelSwitchLayout.class.getSimpleName();

    private static long preClickTime = 0;

    private List<OnViewClickListener> viewClickListeners;
    private List<OnPanelChangeListener> panelChangeListeners;
    private List<OnKeyboardStateListener> keyboardStatusListeners;
    private List<OnEditFocusChangeListener> editFocusChangeListeners;

    private ContentContainer contentContainer;
    private PanelContainer panelContainer;
    private int panelId = Constants.PANEL_NONE;


    public PanelSwitchLayout(Context context) {
        this(context, null);
    }

    public PanelSwitchLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelSwitchLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public PanelSwitchLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr, defStyleRes);
    }

    private void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //nothing to do
    }

    private void initListener() {
        /**
         * 1. if current currentPanelId is None,should show keyboard
         * 2. current currentPanelId is not None or KeyBoard that means some panel is showing,hide it and show keyboard
         */
        contentContainer.setEditTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyViewClick(v);
                //checkout currentFlag to keyboard
                boolean result = checkoutPanel(Constants.PANEL_KEYBOARD);
                //when is checkout doing, unlockContentlength unfinished
                //editText click will make keyboard visible by system,so if checkoutPanel fail,should hide keyboard.
                if (!result && panelId != Constants.PANEL_KEYBOARD) {
                    PanelHelper.hideKeyboard(getContext(), v);
                }
            }
        });

        contentContainer.setEditTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                notifyEditFocusChange(v, hasFocus);
                if (hasFocus) {
                    // checkout currentFlag to keyboard
                    boolean result = checkoutPanel(Constants.PANEL_KEYBOARD);
                    //when is checkout doing, unlockContentlength unfinished
                    //editText click will make keyboard visible by system,so if checkoutPanel fail,should hide keyboard.
                    if (!result && panelId != Constants.PANEL_KEYBOARD) {
                        PanelHelper.hideKeyboard(getContext(), v);
                    }
                }
            }
        });

        contentContainer.setEmptyViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (panelId != Constants.PANEL_NONE) {
                    notifyViewClick(v);
                    checkoutPanel(Constants.PANEL_NONE);
                }
            }
        });

        /**
         * save panel that you want to use these to checkout
         */
        SparseArray<PanelView> array = panelContainer.getPanelSparseArray();
        for (int i = 0; i < array.size(); i++) {
            final PanelView panelView = array.get(array.keyAt(i));
            final View keyView = contentContainer.findViewById(panelView.getTriggerViewId());
            if (keyView != null) {
                keyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        long currentTime = System.currentTimeMillis();
                        if (currentTime - preClickTime <= Constants.PROTECT_KEY_CLICK_DURATION) {
                            LogTracker.Log(TAG + "#initListener", "panelItem invalid click! preClickTime: " + preClickTime + " currentClickTime: " + currentTime);
                            return;
                        }
                        notifyViewClick(v);
                        int targetId = panelContainer.getPanelId(panelView);
                        if (panelId == targetId && panelView.isToggle() && panelView.isShown()) {
                            checkoutPanel(Constants.PANEL_KEYBOARD);
                        } else {
                            checkoutPanel(targetId);
                        }

                        preClickTime = currentTime;
                    }
                });
            }
        }
    }

    public void bindListener(List<OnViewClickListener> viewClickListeners, List<OnPanelChangeListener> panelChangeListeners,
                             List<OnKeyboardStateListener> keyboardStatusListeners, List<OnEditFocusChangeListener> editFocusChangeListeners) {
        this.viewClickListeners = viewClickListeners;
        this.panelChangeListeners = panelChangeListeners;
        this.keyboardStatusListeners = keyboardStatusListeners;
        this.editFocusChangeListeners = editFocusChangeListeners;
    }

    private void notifyViewClick(View view) {
        for (OnViewClickListener listener : viewClickListeners) {
            listener.onViewClick(view);
        }
    }

    private void notifyKeyboardState(boolean visible) {
        for (OnKeyboardStateListener listener : keyboardStatusListeners) {
            listener.onKeyboardChange(visible);
        }
    }

    private void notifyEditFocusChange(View view, boolean hasFocus) {
        for (OnEditFocusChangeListener listener : editFocusChangeListeners) {
            listener.onFocusChange(view, hasFocus);
        }
    }


    public void notifyPanelChange(int panelId) {
        for (OnPanelChangeListener listener : panelChangeListeners) {
            switch (panelId) {
                case Constants.PANEL_NONE: {
                    listener.onNone();
                    break;
                }
                case Constants.PANEL_KEYBOARD: {
                    listener.onKeyboard();
                    break;
                }
                default: {
                    listener.onPanel(panelContainer.getPanelView(panelId));
                }
            }
        }
    }

    public void notifyPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
        for (OnPanelChangeListener listener : panelChangeListeners) {
            listener.onPanelSizeChange(panelView, portrait, oldWidth, oldHeight, width, height);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        assertView();
        initListener();
    }

    @Override
    public void assertView() {
        if (getChildCount() != 2) {
            throw new RuntimeException("PanelSwitchLayout -- PanelSwitchLayout should has two children,the first is ContentContainer,the other is PanelContainer！");
        }
        View firstView = getChildAt(0);
        View secondView = getChildAt(1);

        if (!(firstView instanceof ContentContainer)) {
            throw new RuntimeException("PanelSwitchLayout -- the first view is ContentContainer,the other is ContentContainer！");
        }
        this.contentContainer = (ContentContainer) firstView;

        if (!(secondView instanceof PanelContainer)) {
            throw new RuntimeException("PanelSwitchLayout -- the second view is ContentContainer,the other is PanelContainer！");
        }
        this.panelContainer = (PanelContainer) secondView;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int visibility = getVisibility();
        if (visibility != VISIBLE) {
            return;
        }
        int keyboardHeight = 691;
        int allHeight = b - t;
        int contentTop = (panelId == Constants.PANEL_NONE) ? t : t - keyboardHeight;
        int contentHeight = allHeight;
        int panelTop = contentTop + contentHeight;
        int panelHeight = keyboardHeight;
        setTransition(200);

        Log.d("xxxx", "");
        Log.d("xxxx", " onLayout  =======> 被回调 ");
        Log.d("xxxx", " layout参数 changed : " + changed + " l : " + l + " t : " + t + " r : " + r + " b : " + b);
        Log.d("xxxx", " panel场景  : " + (panelId == Constants.PANEL_NONE ? "收起" : (panelId == Constants.PANEL_KEYBOARD ? "键盘" : "面板")));
        Log.d("xxxx", " 内容 top  ：" + contentTop);
        Log.d("xxxx", " 内容 高度 ：" + contentHeight);
        Log.d("xxxx", " 面板 top ：" + panelTop);
        Log.d("xxxx", " 面板 高度 " + panelHeight);

        //处理第一个view contentContainer
        {
            contentContainer.layout(l, contentTop, r, contentTop + contentHeight);
            ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
            if (layoutParams.height != contentHeight) {
                layoutParams.height = contentHeight;
                contentContainer.setLayoutParams(layoutParams);
            }
            contentContainer.setBackgroundColor(Color.RED);
        }

        //处理第二个view panelContainer
        {
            panelContainer.layout(l, panelTop, r, panelTop + panelHeight);
            ViewGroup.LayoutParams layoutParams = panelContainer.getLayoutParams();
            if (layoutParams.height != panelHeight) {
                layoutParams.height = panelHeight;
                panelContainer.setLayoutParams(layoutParams);
            }
            panelContainer.setBackgroundColor(Color.GREEN);
        }
    }

    @TargetApi(19)
    private void setTransition(long duration) {
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(duration);
        TransitionManager.beginDelayedTransition(this, changeBounds);
    }

    /**
     * This will be called when User press System Back Button.
     * 1. if keyboard is showing, should be hide;
     * 2. if you want to hide panel(exclude keyboard),you should call it before {@link android.support.v7.app.AppCompatActivity#onBackPressed()} to hook it.
     *
     * @return if need hook
     */
    public boolean hookSystemBackByPanelSwitcher() {
        if (panelId != Constants.PANEL_NONE && panelId != Constants.PANEL_KEYBOARD) {
            checkoutPanel(Constants.PANEL_NONE);
            return true;
        }
        return false;
    }

    public void toKeyboardState() {
        contentContainer.toKeyboardState();
    }

    /**
     * todo 需要处理点击切换
     *
     * @param panelId
     * @return
     */
    public boolean checkoutPanel(int panelId) {
        switch (panelId) {
            case Constants.PANEL_NONE: {
                panelContainer.hidePanels();
                PanelHelper.hideKeyboard(getContext(), contentContainer.getEditText());
                contentContainer.clearFocusByEditText();
                contentContainer.emptyViewVisible(false);
                break;
            }
            case Constants.PANEL_KEYBOARD: {
                panelContainer.hidePanels();
                PanelHelper.showKeyboard(getContext(), contentContainer.getEditText());
                contentContainer.emptyViewVisible(true);
                break;
            }
            default: {
                PanelHelper.hideKeyboard(getContext(), contentContainer.getEditText());
                Pair<Integer, Integer> size = new Pair<>(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), PanelHelper.getKeyBoardHeight(getContext()));
                Pair<Integer, Integer> oldSize = panelContainer.showPanel(panelId, size);
                if (size.first != oldSize.first || size.second != oldSize.second) {
                    notifyPanelSizeChange(panelContainer.getPanelView(panelId), PanelHelper.isPortrait(getContext()), oldSize.first, oldSize.second, size.first, size.second);
                }
                contentContainer.emptyViewVisible(true);
            }
        }
        this.panelId = panelId;
        LogTracker.Log(TAG + "#checkoutPanel", "panel' id :" + panelId);
        notifyPanelChange(this.panelId);
        requestLayout();
        return true;
    }
}
