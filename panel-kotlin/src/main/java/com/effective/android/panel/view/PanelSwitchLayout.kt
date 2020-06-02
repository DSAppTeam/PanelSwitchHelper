package com.effective.android.panel.view

import android.annotation.TargetApi
import android.content.Context
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Pair
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.effective.android.panel.Constants
import com.effective.android.panel.LogTracker
import com.effective.android.panel.R
import com.effective.android.panel.device.DeviceRuntime
import com.effective.android.panel.interfaces.OnScrollOutsideBorder
import com.effective.android.panel.interfaces.ViewAssertion
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener
import com.effective.android.panel.interfaces.listener.OnViewClickListener
import com.effective.android.panel.utils.DisplayUtil.getLocationOnScreen
import com.effective.android.panel.utils.DisplayUtil.getScreenHeightWithSystemUI
import com.effective.android.panel.utils.DisplayUtil.getScreenHeightWithoutSystemUI
import com.effective.android.panel.utils.DisplayUtil.isPortrait
import com.effective.android.panel.utils.PanelUtil
import com.effective.android.panel.utils.PanelUtil.getKeyBoardHeight
import com.effective.android.panel.utils.PanelUtil.hideKeyboard
import com.effective.android.panel.utils.PanelUtil.showKeyboard
import com.effective.android.panel.view.content.IContentContainer
import com.effective.android.panel.view.panel.IPanelView
import com.effective.android.panel.view.panel.PanelContainer

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
 *
 *
 * updated by yummyLau on 20/03/18
 * 重构整个输入法切换框架，移除旧版使用 weight+Runnable延迟切换，使用新版 layout+动画无缝衔接！
 */
class PanelSwitchLayout : LinearLayout, ViewAssertion {
    //must init
    private lateinit var viewClickListeners: MutableList<OnViewClickListener>
    private lateinit var panelChangeListeners: MutableList<OnPanelChangeListener>
    private lateinit var keyboardStatusListeners: MutableList<OnKeyboardStateListener>
    private lateinit var editFocusChangeListeners: MutableList<OnEditFocusChangeListener>
    private lateinit var contentContainer: IContentContainer
    private lateinit var panelContainer: PanelContainer
    private lateinit var window: Window
    private lateinit var scrollOutsideBorder: OnScrollOutsideBorder

    private var isKeyboardShowing = false
    var panelId = Constants.PANEL_NONE
        private set
    var lastPanelId = Constants.PANEL_NONE
        private set
    private var animationSpeed = 200 //standard

    lateinit var deviceRuntime: DeviceRuntime

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr, 0)
    }

    @TargetApi(21)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(attrs, defStyleAttr, defStyleRes)
    }

    private fun initView(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PanelSwitchLayout, defStyleAttr, 0)
        animationSpeed = typedArray.getInteger(R.styleable.PanelSwitchLayout_animationSpeed, animationSpeed)
        typedArray.recycle()
    }

    private fun initListener() {
        /**
         * 1. if current currentPanelId is None,should show keyboard
         * 2. current currentPanelId is not None or KeyBoard that means some panel is showing,hide it and show keyboard
         */
        contentContainer.getInputActionImpl().setEditTextClickListener(OnClickListener { v ->
            notifyViewClick(v)
            //checkout currentFlag to keyboard
            val result = checkoutPanel(Constants.PANEL_KEYBOARD)
            //when is checkout doing, unlockContentlength unfinished
            //editText click will make keyboard visible by system,so if checkoutPanel fail,should hide keyboard.
            if (!result && panelId != Constants.PANEL_KEYBOARD) {
                hideKeyboard(context, v)
            }
        })
        contentContainer.getInputActionImpl().setEditTextFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            notifyEditFocusChange(v, hasFocus)
            if (hasFocus) { // checkout currentFlag to keyboard
                val result = checkoutPanel(Constants.PANEL_KEYBOARD)
                //when is checkout doing, unlockContentlength unfinished
                //editText click will make keyboard visible by system,so if checkoutPanel fail,should hide keyboard.
                if (!result && panelId != Constants.PANEL_KEYBOARD) {
                    hideKeyboard(context, v)
                }
            }
        })
        contentContainer.getResetActionImpl().setResetCallback(Runnable {
            if (panelId != Constants.PANEL_NONE) {
                checkoutPanel(Constants.PANEL_NONE)
            }
        })

        /**
         * save panel that you want to use these to checkout
         */
        val array = panelContainer.panelSparseArray
        for (i in 0 until array.size()) {
            val panelView = array[array.keyAt(i)]
            val keyView = contentContainer.findTriggerView(panelView.getBindingTriggerViewId())
            keyView?.setOnClickListener(object : OnClickListener {
                override fun onClick(v: View) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - preClickTime <= Constants.PROTECT_KEY_CLICK_DURATION) {
                        LogTracker.log("$TAG#initListener", "panelItem invalid click! preClickTime: $preClickTime currentClickTime: $currentTime")
                        return
                    }
                    notifyViewClick(v)
                    val targetId = panelContainer.getPanelId(panelView)
                    if (panelId == targetId && panelView.isTriggerViewCanToggle() && panelView.isShowing()) {
                        checkoutPanel(Constants.PANEL_KEYBOARD)
                    } else {
                        checkoutPanel(targetId)
                    }
                    preClickTime = currentTime
                }
            })
        }
    }

    fun bindListener(viewClickListeners: MutableList<OnViewClickListener>, panelChangeListeners: MutableList<OnPanelChangeListener>,
                     keyboardStatusListeners: MutableList<OnKeyboardStateListener>, editFocusChangeListeners: MutableList<OnEditFocusChangeListener>) {
        this.viewClickListeners = viewClickListeners
        this.panelChangeListeners = panelChangeListeners
        this.keyboardStatusListeners = keyboardStatusListeners
        this.editFocusChangeListeners = editFocusChangeListeners
    }

    fun setScrollOutsideBorder(scrollOutsideBorder: OnScrollOutsideBorder) {
        this.scrollOutsideBorder = scrollOutsideBorder
    }

    fun bindWindow(window: Window) {
        this.window = window
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        deviceRuntime = DeviceRuntime(context, window);
        window.decorView.rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val screenHeight = getScreenHeightWithSystemUI(window)
            val contentHeight = getScreenHeightWithoutSystemUI(window)
            val info = deviceRuntime.getDeviceInfoByOrientation(true)
            val systemUIHeight = if (deviceRuntime.isFullScreen) {
                0
            } else {
                info.statusBarH + (if (deviceRuntime.isNavigationBarShow) info.getCurrentNavigationBarHeightWhenVisible(deviceRuntime.isPortrait, deviceRuntime.isPad) else 0)
            }
            val keyboardHeight = screenHeight - contentHeight - systemUIHeight
            LogTracker.log("$TAG#onGlobalLayout", "screenHeight : $screenHeight, contentHeight : $contentHeight，systemUIHeight : $systemUIHeight")
            LogTracker.log("$TAG#onGlobalLayout", "keyboardHeight is : $keyboardHeight, isShow : $isKeyboardShowing")
            if (isKeyboardShowing) {
                if (keyboardHeight <= 0) {
                    isKeyboardShowing = false
                    if (panelId == Constants.PANEL_KEYBOARD) {
                        panelId = Constants.PANEL_NONE
                        contentContainer.getInputActionImpl().clearFocusByEditText()
                        contentContainer.getResetActionImpl().enableReset(false)
                        requestLayout()
                    }
                    notifyKeyboardState(false)
                } else {
                    if (getKeyBoardHeight(context) != keyboardHeight) {
                        requestLayout()
                        PanelUtil.setKeyBoardHeight(context, keyboardHeight)
                        LogTracker.log("$TAG#onGlobalLayout", "setKeyBoardHeight is : $keyboardHeight")
                    }
                }
            } else {
                if (keyboardHeight > 0) {
                    if (getKeyBoardHeight(context) != keyboardHeight) {
                        requestLayout()
                        PanelUtil.setKeyBoardHeight(context, keyboardHeight)
                        LogTracker.log("$TAG#onGlobalLayout", "setKeyBoardHeight is : $keyboardHeight")
                    }
                    isKeyboardShowing = true
                    notifyKeyboardState(true)
                }
            }
        }
    }

    private fun notifyViewClick(view: View) {
        for (listener in viewClickListeners) {
            listener.onClickBefore(view)
        }
    }

    private fun notifyKeyboardState(visible: Boolean) {
        for (listener in keyboardStatusListeners) {
            listener.onKeyboardChange(visible,if(visible) getKeyBoardHeight(context) else 0)
        }
    }

    private fun notifyEditFocusChange(view: View, hasFocus: Boolean) {
        for (listener in editFocusChangeListeners) {
            listener.onFocusChange(view, hasFocus)
        }
    }

    private fun notifyPanelChange(panelId: Int) {
        for (listener in panelChangeListeners) {
            when (panelId) {
                Constants.PANEL_NONE -> {
                    listener.onNone()
                }
                Constants.PANEL_KEYBOARD -> {
                    listener.onKeyboard()
                }
                else -> {
                    listener.onPanel(panelContainer.getPanelView(panelId))
                }
            }
        }
    }

    private fun notifyPanelSizeChange(panelView: IPanelView?, portrait: Boolean, oldWidth: Int, oldHeight: Int, width: Int, height: Int) {
        for (listener in panelChangeListeners) {
            listener.onPanelSizeChange(panelView, portrait, oldWidth, oldHeight, width, height)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        assertView()
        initListener()
    }

    override fun assertView() {
        if (childCount != 2) {
            throw RuntimeException("PanelSwitchLayout -- PanelSwitchLayout should has two children,the first is ContentContainer,the other is PanelContainer！")
        }
        val firstView = getChildAt(0)
        val secondView = getChildAt(1)
        if (firstView !is IContentContainer) {
            throw RuntimeException("PanelSwitchLayout -- the first view isn't a IContentContainer")
        }
        contentContainer = firstView
        if (secondView !is PanelContainer) {
            throw RuntimeException("PanelSwitchLayout -- the second view is a ContentContainer, but the other isn't a PanelContainer！")
        }
        panelContainer = secondView
    }

    private fun getContentContainerTop(scrollOutsideHeight: Int): Int {
        return if (scrollOutsideBorder.canLayoutOutsideBorder()) {
            if (panelId == Constants.PANEL_NONE) 0 else -scrollOutsideHeight
        } else 0
    }

    private fun getContentContainerHeight(allHeight: Int, paddingTop: Int, scrollOutsideHeight: Int): Int {
        return allHeight - paddingTop -
                if (!scrollOutsideBorder.canLayoutOutsideBorder() && panelId != Constants.PANEL_NONE) scrollOutsideHeight else 0
    }

    /**
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val visibility = visibility
        if (visibility != View.VISIBLE) {
            return
        }

        val deviceInfo = deviceRuntime.getDeviceInfoByOrientation()
        val scrollOutsideHeight = scrollOutsideBorder.getOutsideHeight()
        val paddingTop = paddingTop
        var allHeight = deviceInfo.screenH
        if (deviceRuntime.isNavigationBarShow) {
            /**
             * 1.1.0 使用 screenWithoutNavigationHeight + navigationBarHeight ，结合 navigationBarShow 来动态计算高度，但是部分特殊机型
             * 比如水滴屏，刘海屏，等存在刘海区域，甚至华为，小米支持动态切换刘海模式（不隐藏刘海，隐藏后状态栏在刘海内，隐藏后状态栏在刘海外）
             * 同时还存在全面屏，挖孔屏，这套方案存在兼容问题。
             * CusShortUtil 支持计算绝大部分机型的刘海高度，但是考虑到动态切换的模式计算太过于复杂，且不能完全兼容所有场景。
             * 1.1.1 使用 screenHeight - navigationBarHeight，结合 navigationBarShow 来动态计算告诉，原因是：
             * 无论现不现实刘海区域，只需要记住应用的绘制区域以 getDecorView 的绘制区域为准，我们只需要关注一个关系：
             * 刘海区域与状态栏区域的是否重叠。
             * 如果状态栏与刘海不重叠，则 screenHeight 不包含刘海
             * 如果状态栏与刘海重叠，则 screenHeight 包含刘海
             * 这样抽象逻辑变得更加简单。
             */
            allHeight -= deviceInfo.getCurrentNavigationBarHeightWhenVisible(deviceRuntime.isPortrait, deviceRuntime.isPad)
        }

        val localLocation = getLocationOnScreen(this)
        allHeight -= localLocation[1]
        var contentContainerTop = getContentContainerTop(scrollOutsideHeight)
        contentContainerTop += paddingTop
        val contentContainerHeight = getContentContainerHeight(allHeight, paddingTop, scrollOutsideHeight)
        val panelContainerTop = contentContainerTop + contentContainerHeight
        setTransition(animationSpeed.toLong(), panelId)

        if (Constants.DEBUG) {
            LogTracker.log("$TAG#onLayout", " onLayout(changed : $changed , l : $l  , t : $t , r : $r , b : $b")
            val state = when (panelId) {
                Constants.PANEL_NONE -> "收起所有输入源"
                Constants.PANEL_KEYBOARD -> "显示键盘输入"
                else -> "显示面板输入"
            }
            val mode = if (scrollOutsideBorder.canLayoutOutsideBorder()) "滑动模式" else "固定模式"
            LogTracker.log("$TAG#onLayout", " 切换模式  :$mode")
            LogTracker.log("$TAG#onLayout", " 当前状态  :$state")
            LogTracker.log("$TAG#onLayout", " 是否是全屏  ：${deviceRuntime.isFullScreen}");
            LogTracker.log("$TAG#onLayout", " 是否是pad机型  ：${deviceRuntime.isPad}");
            LogTracker.log("$TAG#onLayout", " 是否显示导航栏  ：${deviceRuntime.isNavigationBarShow}");
            LogTracker.log("$TAG#onLayout", " 是否是竖屏  ：${deviceRuntime.isPortrait}");
            LogTracker.log("$TAG#onLayout", " 界面高度（包含系统UI）  ：${deviceInfo.screenH}");
            LogTracker.log("$TAG#onLayout", " 界面高度（不包含系统UI，无论导航栏显示与否）  ：${deviceInfo.screenWithoutNavigationH}");
            LogTracker.log("$TAG#onLayout", " 界面高度（不包含系统UI，动态计算）  ：${deviceInfo.screenWithoutSystemUiH}");
            LogTracker.log("$TAG#onLayout", " 刘海高度  ： + ${deviceInfo.cutShortH}");
            LogTracker.log("$TAG#onLayout", " toolbar高度  ：${deviceInfo.toolbarH}");
            LogTracker.log("$TAG#onLayout", " StatusBar高度  ：${deviceInfo.statusBarH}");
            LogTracker.log("$TAG#onLayout", " NavigationBar高度  ：${deviceInfo.navigationBarH}");
            LogTracker.log("$TAG#onLayout", " PanelSwitchLayout 绘制起点  ：（" + localLocation[0] + "，" + localLocation[1] + "）")
            LogTracker.log("$TAG#onLayout", " PanelSwitchLayout paddingTop  ：$paddingTop");
            LogTracker.log("$TAG#onLayout", " 输入法高度  ：$scrollOutsideHeight");
            LogTracker.log("$TAG#onLayout", " 内容容器 top  ：$contentContainerTop");
            LogTracker.log("$TAG#onLayout", " 内容容器 高度 ：$contentContainerHeight");
            LogTracker.log("$TAG#onLayout", " 面板容器 top ：$panelContainerTop");
            LogTracker.log("$TAG#onLayout", " 面板容器 高度 $scrollOutsideHeight");
        }

        //处理第一个view contentContainer
        run {
            contentContainer.layoutContainer(l, contentContainerTop, r, contentContainerTop + contentContainerHeight)
            LogTracker.log("$TAG#onLayout", " layout参数 contentContainer : height - $contentContainerHeight")
            LogTracker.log("$TAG#onLayout", " layout参数 contentContainer : " + " l : " + l + " t : " + contentContainerTop + " r : " + r + " b : " + (contentContainerTop + contentContainerHeight))
            contentContainer.changeContainerHeight(contentContainerHeight)
        }
        //处理第二个view panelContainer
        run {
            panelContainer.layout(l, panelContainerTop, r, panelContainerTop + scrollOutsideHeight)
            LogTracker.log("$TAG#onLayout", " layout参数 panelContainerTop : height - $scrollOutsideHeight")
            LogTracker.log("$TAG#onLayout", " layout参数 panelContainer : " + " l : " + l + "  : " + panelContainerTop + " r : " + r + " b : " + (panelContainerTop + scrollOutsideHeight))
            val layoutParams = panelContainer.layoutParams
            if (layoutParams.height != scrollOutsideHeight) {
                layoutParams.height = scrollOutsideHeight
                panelContainer.layoutParams = layoutParams
            }
        }
    }

    @TargetApi(19)
    private fun setTransition(duration: Long, panelId: Int) { //如果禁止了内容区域滑出边界且当当前是收起面板，则取消动画。
        //因为禁止滑出边界使用动态更改高度，动画过程中界面已绘制内容会有极其短暂的重叠，故禁止动画。
        if (scrollOutsideBorder.canLayoutOutsideBorder()
                || (!scrollOutsideBorder.canLayoutOutsideBorder()
                        && (lastPanelId == Constants.PANEL_NONE || (lastPanelId != Constants.PANEL_KEYBOARD && panelId == Constants.PANEL_NONE)))) {
            val changeBounds = ChangeBounds()
            changeBounds.duration = duration
            TransitionManager.beginDelayedTransition(this, changeBounds)
        }
    }

    /**
     * This will be called when User press System Back Button.
     * 1. if keyboard is showing, should be hide;
     * 2. if you want to hide panel(exclude keyboard),you should call it before [android.support.v7.app.AppCompatActivity.onBackPressed] to hook it.
     *
     * @return if need hook
     */
    fun hookSystemBackByPanelSwitcher(): Boolean {
        if (panelId != Constants.PANEL_NONE) {
            if (panelId == Constants.PANEL_KEYBOARD) {
                hideKeyboard(context, contentContainer.getInputActionImpl().getInputText())
            } else {
                checkoutPanel(Constants.PANEL_NONE)
            }
            return true
        }
        return false
    }

    fun toKeyboardState() {
        if (contentContainer.getInputActionImpl().editTextHasFocus()) {
            contentContainer.getInputActionImpl().preformClickForEditText()
        } else {
            contentContainer.getInputActionImpl().requestFocusByEditText()
        }
    }

    /**
     * @param panelId
     * @return
     */
    fun checkoutPanel(panelId: Int): Boolean {
        if (panelId == this.panelId) {
            return false
        }
        panelContainer.hidePanels()
        when (panelId) {
            Constants.PANEL_NONE -> {
                hideKeyboard(context, contentContainer.getInputActionImpl().getInputText())
                contentContainer.getInputActionImpl().clearFocusByEditText()
                contentContainer.getResetActionImpl().enableReset(false)
            }
            Constants.PANEL_KEYBOARD -> {
                showKeyboard(context, contentContainer.getInputActionImpl().getInputText())
                contentContainer.getResetActionImpl().enableReset(true)
            }
            else -> {
                hideKeyboard(context, contentContainer.getInputActionImpl().getInputText())
                val size = Pair(measuredWidth - paddingLeft - paddingRight, getKeyBoardHeight(context))
                val oldSize = panelContainer.showPanel(panelId, size)
                if (size.first != oldSize.first || size.second != oldSize.second) {
                    notifyPanelSizeChange(panelContainer.getPanelView(panelId), isPortrait(context), oldSize.first, oldSize.second, size.first, size.second)
                }
                contentContainer.getResetActionImpl().enableReset(true)
            }
        }
        this.lastPanelId = this.panelId
        this.panelId = panelId
        LogTracker.log("$TAG#checkoutPanel", "panel' id :$panelId")
        notifyPanelChange(this.panelId)
        requestLayout()
        return true
    }

    companion object {
        private val TAG = PanelSwitchLayout::class.java.simpleName
        private var preClickTime: Long = 0
    }
}