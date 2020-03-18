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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
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

    private Window window;
    private boolean isKeyboardShowing;
    private int panelId = Constants.PANEL_NONE;
    private int prePanelId = Constants.PANEL_NONE;

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

    public void bindWindow(final Window window) {
        this.window = window;
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int contentHeight = PanelHelper.getScreenHeightWithoutSystemUI(window);
                int screenHeight = PanelHelper.getScreenHeightWithSystemUI(window);
                int systemUIHeight = PanelHelper.getSystemUI(getContext(), window);
                int keyboardHeight = screenHeight - contentHeight - systemUIHeight;
                if (isKeyboardShowing) {
                    if (keyboardHeight <= 0) {
                        isKeyboardShowing = false;
                        if(panelId == Constants.PANEL_KEYBOARD){
                            panelId = Constants.PANEL_NONE;
                            PanelSwitchLayout.this.requestLayout();
                        }
                        notifyKeyboardState(false);
                        LogTracker.Log(TAG + "#onGlobalLayout", "keyboardHeight is : " + 0);
                    } else {
                        LogTracker.Log(TAG + "#onGlobalLayout", "setKeyBoardHeight is : " + keyboardHeight);
                        PanelHelper.setKeyBoardHeight(getContext(), keyboardHeight);
                    }
                } else {
                    if (keyboardHeight > 0) {
                        LogTracker.Log(TAG + "#onGlobalLayout", "setKeyBoardHeight is : " + keyboardHeight);
                        PanelHelper.setKeyBoardHeight(getContext(), keyboardHeight);
                        isKeyboardShowing = true;
                        notifyKeyboardState(true);
                    }
                }
            }
        });
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

    private void notifyPanelChange(int panelId) {
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

    private void notifyPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, " onSizeChanged  =======> 被回调 ");
        Log.d(TAG, " size :  w : " + w + " h : " + h + " oldw : " + oldw + " oldh : " + oldh);
    }

    /**
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int visibility = getVisibility();
        if (visibility != VISIBLE) {
            return;
        }

        int screenHeight = PanelHelper.getScreenHeightWithSystemUI(window);
        int screenWithoutSystemUIHeight = PanelHelper.getScreenHeightWithoutSystemUI(window);
        int screenWithoutNavigationIHeight = PanelHelper.getScreenHeightWithoutNavigationBar(getContext());
        int systemUIHeight = PanelHelper.getSystemUI(getContext(), window);
        int statusBarHeight = PanelHelper.getStatusBarHeight(getContext());
        int navigationBarHeight = PanelHelper.isNavigationBarShow(getContext(), window) ? PanelHelper.getNavigationBarHeight(getContext()) : 0;
        //以这种方式计算出来的toolbar，如果和statusBarHeight一样，则实际上就是statusBar的高度，大于statusBar的才是toolBar的高度。
        int toolbarHeight = PanelHelper.getToolbarHeight(window);
        if(toolbarHeight == statusBarHeight){
            toolbarHeight = 0;
        }
        int contentViewHeight = PanelHelper.getContentViewHeight(window);
        int keyboardHeight = PanelHelper.getKeyBoardHeight(getContext());
        int paddingTop = getPaddingTop();


        //screenWithoutNavigationIHeight - toolbarHeight - statusBarHeight 表示不包含导航栏，状态栏和标题栏的可见界面高度
        //(PanelHelper.isNavigationBarShow(getContext(), window) ? 0 : PanelHelper.getNavigationBarHeight(getContext())) 表示当前界面动态显示的导航栏，比如华为手机等可以随时隐藏和显示
        int allHeight = screenWithoutNavigationIHeight - toolbarHeight - statusBarHeight + (PanelHelper.isNavigationBarShow(getContext(), window) ? 0 : PanelHelper.getNavigationBarHeight(getContext()));
        //如果该可见界面允许绘制到状态栏位置，则需要再加上状态栏
        //t 表示 panelSwitchLayout 被绘制的位置，如果 t == 0，则表示绘制在根部局左上角
        if (PanelHelper.contentViewCanDrawStatusBarArea(window)) {

            //常见于 activity中，如果 t > 0,则意味着布局可能在fragment中。
            if(t == 0){
                allHeight += statusBarHeight;
            }
        }
        //t 则意味着 PanelSwitchLayout 顶部在父容器顶部为 t 高度，所以有效高度需要减去 t，常见于fragment布局中，被activity xml 所读取
        allHeight -= t;

        int contentContainerTop = (panelId == Constants.PANEL_NONE) ? 0 : - keyboardHeight;
        contentContainerTop += paddingTop;


        int contentContainerHeight = allHeight - paddingTop;
        int panelContainerTop = contentContainerTop + contentContainerHeight;
        int panelContainerHeight = keyboardHeight;

        setTransition(200);

        Log.d(TAG, "   ");
        Log.d(TAG, " onLayout  =======> 被回调 ");
        Log.d(TAG, " layout参数 changed : " + changed + " l : " + l + " t : " + t + " r : " + r + " b : " + b);
        Log.d(TAG, " panel场景  : " + (panelId == Constants.PANEL_NONE ? "收起" : (panelId == Constants.PANEL_KEYBOARD ? "键盘" : "面板")));

        Log.d(TAG, " 界面高度（包含系统UI）  ：" + screenHeight);
        Log.d(TAG, " 界面高度（不包含导航栏）  ：" + screenWithoutNavigationIHeight);
        Log.d(TAG, " 内容高度（不包含系统UI）  ：" + screenWithoutSystemUIHeight);
        Log.d(TAG, " 系统UI高度  ：" + systemUIHeight);
        Log.d(TAG, " 系统状态栏高度  ：" + statusBarHeight);
        Log.d(TAG, " 系统导航栏高度  ：" + navigationBarHeight);
        Log.d(TAG, " contentView高度  ：" + contentViewHeight);
        Log.d(TAG, " toolbar高度  ：" + toolbarHeight);
        Log.d(TAG, " paddingTop  ：" + paddingTop);
        Log.d(TAG, " 输入法高度  ：" + keyboardHeight);

        Log.d(TAG, " 内容 top  ：" + contentContainerTop);
        Log.d(TAG, " 内容 高度 ：" + contentContainerHeight);
        Log.d(TAG, " 面板 top ：" + panelContainerTop);
        Log.d(TAG, " 面板 高度 " + panelContainerHeight);

        //处理第一个view contentContainer
        {
            contentContainer.layout(l, contentContainerTop, r, contentContainerTop + contentContainerHeight);
            Log.d(TAG, " layout参数 contentContainer : height - " +contentContainerHeight);
            Log.d(TAG, " layout参数 contentContainer : " + " l : " + l + " t : " + contentContainerTop + " r : " + r + " b : " + (contentContainerTop + contentContainerHeight));
            ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
            if (layoutParams.height != contentContainerHeight) {
                layoutParams.height = contentContainerHeight;
                contentContainer.setLayoutParams(layoutParams);
            }
        }

        //处理第二个view panelContainer
        {
            panelContainer.layout(l, panelContainerTop, r, panelContainerTop + panelContainerHeight);
            Log.d(TAG, " layout参数 panelContainerTop : height - " +panelContainerHeight);
            Log.d(TAG, " layout参数 panelContainer : " +  " l : " + l + "  : " + panelContainerTop + " r : " + r + " b : " + (panelContainerTop + panelContainerHeight));
            ViewGroup.LayoutParams layoutParams = panelContainer.getLayoutParams();
            if (layoutParams.height != panelContainerHeight) {
                layoutParams.height = panelContainerHeight;
                panelContainer.setLayoutParams(layoutParams);
            }
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
     * @param panelId
     * @return
     */
    public boolean checkoutPanel(int panelId) {
        panelContainer.hidePanels();
        switch (panelId) {
            case Constants.PANEL_NONE: {
                PanelHelper.hideKeyboard(getContext(), contentContainer.getEditText());
                contentContainer.clearFocusByEditText();
                contentContainer.emptyViewVisible(false);
                break;
            }
            case Constants.PANEL_KEYBOARD: {
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
        this.prePanelId = this.panelId;
        this.panelId = panelId;
        LogTracker.Log(TAG + "#checkoutPanel", "panel' id :" + panelId);
        notifyPanelChange(this.panelId);
        requestLayout();
        return true;
    }
}
