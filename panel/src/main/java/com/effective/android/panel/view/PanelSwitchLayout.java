package com.effective.android.panel.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
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
import com.effective.android.panel.interfaces.OnScrollOutsideBorder;
import com.effective.android.panel.utils.DisplayUtil;
import com.effective.android.panel.utils.PanelUtil;
import com.effective.android.panel.R;
import com.effective.android.panel.interfaces.ViewAssertion;
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.view.content.IContentContainer;

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

    private IContentContainer contentContainer;
    private PanelContainer panelContainer;

    private Window window;
    private boolean isKeyboardShowing;
    private int panelId = Constants.PANEL_NONE;
    private int animationSpeed = 200;  //standard
    private OnScrollOutsideBorder scrollOutsideBorder;

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
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PanelSwitchLayout, defStyleAttr, 0);
        animationSpeed = typedArray.getInteger(R.styleable.PanelSwitchLayout_animationSpeed, animationSpeed);
        typedArray.recycle();
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
                    PanelUtil.hideKeyboard(getContext(), v);
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
                        PanelUtil.hideKeyboard(getContext(), v);
                    }
                }
            }
        });

        contentContainer.setEmptyViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (panelId != Constants.PANEL_NONE) {
                    notifyViewClick(v);
                    if (panelId == Constants.PANEL_KEYBOARD) {
                        PanelUtil.hideKeyboard(getContext(), contentContainer.getEditText());
                    } else {
                        checkoutPanel(Constants.PANEL_NONE);
                    }
                }
            }
        });

        /**
         * save panel that you want to use these to checkout
         */
        SparseArray<PanelView> array = panelContainer.getPanelSparseArray();
        for (int i = 0; i < array.size(); i++) {
            final PanelView panelView = array.get(array.keyAt(i));
            final View keyView = contentContainer.findTriggerView(panelView.getTriggerViewId());
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

    public void setScrollOutsideBorder(@NonNull OnScrollOutsideBorder scrollOutsideBorder) {
        this.scrollOutsideBorder = scrollOutsideBorder;
    }

    public int getPanedId() {
        return panelId;
    }

    public void bindWindow(final Window window) {
        this.window = window;
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.getDecorView().getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int contentHeight = DisplayUtil.getScreenHeightWithoutSystemUI(window);
                int screenHeight = DisplayUtil.getScreenHeightWithSystemUI(window);
                int systemUIHeight = DisplayUtil.getSystemUI(getContext(), window);
                int keyboardHeight = screenHeight - contentHeight - systemUIHeight;
                LogTracker.Log(TAG + "#onGlobalLayout", "keyboardHeight is : " + keyboardHeight);
                if (isKeyboardShowing) {
                    if (keyboardHeight <= 0) {
                        isKeyboardShowing = false;
                        if (panelId == Constants.PANEL_KEYBOARD) {
                            panelId = Constants.PANEL_NONE;
                            contentContainer.clearFocusByEditText();
                            contentContainer.emptyViewVisible(false);
                            PanelSwitchLayout.this.requestLayout();
                        }
                        notifyKeyboardState(false);
                    } else {
                        if (PanelUtil.getKeyBoardHeight(getContext()) != keyboardHeight) {
                            PanelSwitchLayout.this.requestLayout();
                            PanelUtil.setKeyBoardHeight(getContext(), keyboardHeight);
                            LogTracker.Log(TAG + "#onGlobalLayout", "setKeyBoardHeight is : " + keyboardHeight);
                        }
                    }
                } else {
                    if (keyboardHeight > 0) {
                        if (PanelUtil.getKeyBoardHeight(getContext()) != keyboardHeight) {
                            PanelSwitchLayout.this.requestLayout();
                            PanelUtil.setKeyBoardHeight(getContext(), keyboardHeight);
                            LogTracker.Log(TAG + "#onGlobalLayout", "setKeyBoardHeight is : " + keyboardHeight);
                        }
                        isKeyboardShowing = true;
                        notifyKeyboardState(true);
                    }
                }
            }
        });
    }

    private void notifyViewClick(View view) {
        for (OnViewClickListener listener : viewClickListeners) {
            listener.onClickBefore(view);
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

        if (!(firstView instanceof IContentContainer)) {
            throw new RuntimeException("PanelSwitchLayout -- the first view isn't a IContentContainer");
        }
        this.contentContainer = (IContentContainer) firstView;

        if (!(secondView instanceof PanelContainer)) {
            throw new RuntimeException("PanelSwitchLayout -- the second view is a ContentContainer, but the other isn't a PanelContainer！");
        }
        this.panelContainer = (PanelContainer) secondView;
    }

    private int getContentContainerTop(int scrollOutsideHeight) {
        if (scrollOutsideBorder.canLayoutOutsideBorder()) {
            return (panelId == Constants.PANEL_NONE) ? 0 : -scrollOutsideHeight;
        }
        return 0;
    }

    private int getContentContainerHeight(int allHeight, int paddingTop, int scrollOutsideHeight) {
        return allHeight - paddingTop -
                (!scrollOutsideBorder.canLayoutOutsideBorder() && panelId != Constants.PANEL_NONE ? scrollOutsideHeight : 0);
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
        LogTracker.Log(TAG + "#onLayout", "onLayout");

        int visibility = getVisibility();
        if (visibility != VISIBLE) {
            return;
        }

        int screenHeight = DisplayUtil.getScreenHeightWithSystemUI(window);
        int navigationBarHeight = DisplayUtil.getNavigationBarHeight(getContext());
        boolean navigationBarShow = DisplayUtil.isNavigationBarShow(getContext(), window);

//
//        int screenWithoutSystemUIHeight = DisplayUtil.getScreenHeightWithoutSystemUI(window);
//        int screenWithoutNavigationHeight = DisplayUtil.getScreenHeightWithoutNavigationBar(getContext());
//        int systemUIHeight = DisplayUtil.getSystemUI(getContext(), window);
//        int statusBarHeight = DisplayUtil.getStatusBarHeight(getContext());
////        以这种方式计算出来的toolbar，如果和statusBarHeight一样，则实际上就是statusBar的高度，大于statusBar的才是toolBar的高度。
//        int toolbarHeight = DisplayUtil.getToolbarHeight(window);
//        if (toolbarHeight == statusBarHeight) {
//            toolbarHeight = 0;
//        }
//        int contentViewHeight = DisplayUtil.getContentViewHeight(window);


        int scrollOutsideHeight = scrollOutsideBorder.getOutsideHeight();
        int paddingTop = getPaddingTop();
        int allHeight = screenHeight;
        if (DisplayUtil.isPortrait(getContext())) {

            /**
             * 1.1.0 使用 screenWithoutNavigationHeight + navigationBarHeight ，结合 navigationBarShow 来动态计算高度，但是部分特殊机型
             * 比如水滴屏，刘海屏，等存在刘海区域，甚至华为，小米支持动态切换刘海模式（不隐藏刘海，隐藏后状态栏在刘海内，隐藏后状态栏在刘海外）
             * 同时还存在全面屏，挖孔屏，这套方案存在兼容问题。
             * CusShortUtil 支持计算绝大部分机型的刘海高度，但是考虑到动态切换的模式计算太过于复杂，且不能完全兼容所有场景。
             * 1.1.1 使用 screenHeight - navigationBarHeight，结合 navigationBarShow 来动态计算告诉，原因是：
             *  无论现不现实刘海区域，只需要记住应用的绘制区域以 getDecorView 的绘制区域为准，我们只需要关注一个关系：
             *  刘海区域与状态栏区域的是否重叠。
             *  如果状态栏与刘海不重叠，则 screenHeight 不包含刘海
             *  如果状态栏与刘海重叠，则 screenHeight 包含刘海
             *  这样抽象逻辑变得更加简单。
             */
            if (navigationBarShow) {
                allHeight -= navigationBarHeight;
            }

        }
        int[] localLocation = DisplayUtil.getLocationOnScreen(this);
        allHeight -= localLocation[1];

        int contentContainerTop = getContentContainerTop(scrollOutsideHeight);
        contentContainerTop += paddingTop;


        int contentContainerHeight = getContentContainerHeight(allHeight, paddingTop, scrollOutsideHeight);
        int panelContainerTop = contentContainerTop + contentContainerHeight;
        int panelContainerHeight = scrollOutsideHeight;

        setTransition(animationSpeed, panelId);

//        Log.d(TAG, "   ");
//        Log.d(TAG, " onLayout  =======> 被回调 ");
//        Log.d(TAG, " layout参数 changed : " + changed + " l : " + l + " t : " + t + " r : " + r + " b : " + b);
//        Log.d(TAG, " panel场景  : " + (panelId == Constants.PANEL_NONE ? "收起" : (panelId == Constants.PANEL_KEYBOARD ? "键盘" : "面板")));
//        Log.d(TAG, " 界面高度（包含系统UI）  ：" + screenHeight);
//        Log.d(TAG, " 界面高度（不包含导航栏）  ：" + screenWithoutNavigationHeight);
//        Log.d(TAG, " 内容高度（不包含系统UI）  ：" + screenWithoutSystemUIHeight);
//        Log.d(TAG, " 刘海高度  ：" + CusShortUtil.getDeviceCutShortHeight(window.getDecorView()));
//        Log.d(TAG, " 系统UI高度  ：" + systemUIHeight);
//        Log.d(TAG, " 系统状态栏高度  ：" + statusBarHeight);
//        Log.d(TAG, " 系统导航栏高度  ：" + navigationBarHeight);
//        Log.d(TAG, " 系统导航栏是否显示  ：" + navigationBarShow);
//        Log.d(TAG, " contentView高度  ：" + contentViewHeight);
//        Log.d(TAG, " switchLayout 绘制起点  ：（" + localLocation[0] + "，" + localLocation[1] + "）");
//        Log.d(TAG, " toolbar高度  ：" + toolbarHeight);
//        Log.d(TAG, " paddingTop  ：" + paddingTop);
//        Log.d(TAG, " 输入法高度  ：" + scrollOutsideHeight);
//        Log.d(TAG, " 内容 top  ：" + contentContainerTop);
//        Log.d(TAG, " 内容 高度 ：" + contentContainerHeight);
//        Log.d(TAG, " 面板 top ：" + panelContainerTop);
//        Log.d(TAG, " 面板 高度 " + panelContainerHeight);

        //处理第一个view contentContainer
        {
            contentContainer.layoutGroup(l, contentContainerTop, r, contentContainerTop + contentContainerHeight);
            Log.d(TAG, " layout参数 contentContainer : height - " + contentContainerHeight);
            Log.d(TAG, " layout参数 contentContainer : " + " l : " + l + " t : " + contentContainerTop + " r : " + r + " b : " + (contentContainerTop + contentContainerHeight));
            contentContainer.adjustHeight(contentContainerHeight);
        }

        //处理第二个view panelContainer
        {
            panelContainer.layout(l, panelContainerTop, r, panelContainerTop + panelContainerHeight);
            Log.d(TAG, " layout参数 panelContainerTop : height - " + panelContainerHeight);
            Log.d(TAG, " layout参数 panelContainer : " + " l : " + l + "  : " + panelContainerTop + " r : " + r + " b : " + (panelContainerTop + panelContainerHeight));
            ViewGroup.LayoutParams layoutParams = panelContainer.getLayoutParams();
            if (layoutParams.height != panelContainerHeight) {
                layoutParams.height = panelContainerHeight;
                panelContainer.setLayoutParams(layoutParams);
            }
        }
    }

    @TargetApi(19)
    private void setTransition(long duration, int panelId) {
        //如果禁止了内容区域滑出边界且当当前是收起面板，则取消动画。
        //因为禁止滑出边界使用动态更改高度，动画过程中界面已绘制内容会有极其短暂的重叠，故禁止动画。
        if (scrollOutsideBorder.canLayoutOutsideBorder()
                || (!scrollOutsideBorder.canLayoutOutsideBorder() && panelId != Constants.PANEL_NONE)) {
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(duration);
            TransitionManager.beginDelayedTransition(this, changeBounds);
        }
    }

    /**
     * This will be called when User press System Back Button.
     * 1. if keyboard is showing, should be hide;
     * 2. if you want to hide panel(exclude keyboard),you should call it before {@link android.support.v7.app.AppCompatActivity#onBackPressed()} to hook it.
     *
     * @return if need hook
     */
    public boolean hookSystemBackByPanelSwitcher() {
        if (panelId != Constants.PANEL_NONE) {
            if (panelId == Constants.PANEL_KEYBOARD) {
                PanelUtil.hideKeyboard(getContext(), contentContainer.getEditText());
            } else {
                checkoutPanel(Constants.PANEL_NONE);
            }
            return true;
        }
        return false;
    }

    public void toKeyboardState() {
        if (contentContainer.editTextHasFocus()) {
            contentContainer.preformClickForEditText();
        } else {
            contentContainer.requestFocusByEditText();
        }
    }

    /**
     * @param panelId
     * @return
     */
    public boolean checkoutPanel(int panelId) {
        panelContainer.hidePanels();
        switch (panelId) {
            case Constants.PANEL_NONE: {
                PanelUtil.hideKeyboard(getContext(), contentContainer.getEditText());
                contentContainer.clearFocusByEditText();
                contentContainer.emptyViewVisible(false);
                break;
            }
            case Constants.PANEL_KEYBOARD: {
                PanelUtil.showKeyboard(getContext(), contentContainer.getEditText());
                contentContainer.emptyViewVisible(true);
                break;
            }
            default: {
                PanelUtil.hideKeyboard(getContext(), contentContainer.getEditText());
                Pair<Integer, Integer> size = new Pair<>(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), PanelUtil.getKeyBoardHeight(getContext()));
                Pair<Integer, Integer> oldSize = panelContainer.showPanel(panelId, size);
                if (size.first != oldSize.first || size.second != oldSize.second) {
                    notifyPanelSizeChange(panelContainer.getPanelView(panelId), DisplayUtil.isPortrait(getContext()), oldSize.first, oldSize.second, size.first, size.second);
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
