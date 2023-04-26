package com.effective.android.panel.view

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.util.Pair
import android.view.*
import android.view.View.OnClickListener
import android.view.View.OnFocusChangeListener
import android.widget.LinearLayout
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import com.effective.android.panel.Constants
import com.effective.android.panel.R
import com.effective.android.panel.compat.KeyboardHeightCompat
import com.effective.android.panel.device.DeviceInfo
import com.effective.android.panel.device.DeviceRuntime
import com.effective.android.panel.interfaces.ContentScrollMeasurer
import com.effective.android.panel.interfaces.PanelHeightMeasurer
import com.effective.android.panel.interfaces.TriggerViewClickInterceptor
import com.effective.android.panel.interfaces.ViewAssertion
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener
import com.effective.android.panel.interfaces.listener.OnViewClickListener
import com.effective.android.panel.log.LogFormatter
import com.effective.android.panel.log.LogTracker
import com.effective.android.panel.utils.DisplayUtil
import com.effective.android.panel.utils.DisplayUtil.getLocationOnScreen
import com.effective.android.panel.utils.DisplayUtil.getScreenHeightWithoutSystemUI
import com.effective.android.panel.utils.DisplayUtil.getScreenRealHeight
import com.effective.android.panel.utils.DisplayUtil.isPortrait
import com.effective.android.panel.utils.PanelUtil
import com.effective.android.panel.utils.PanelUtil.getKeyBoardHeight
import com.effective.android.panel.utils.isSystemInsetsAnimationSupport
import com.effective.android.panel.view.content.IContentContainer
import com.effective.android.panel.view.panel.IPanelView
import com.effective.android.panel.view.panel.PanelContainer
import kotlin.math.min


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

    private var viewClickListeners: MutableList<OnViewClickListener>? = null
    private var panelChangeListeners: MutableList<OnPanelChangeListener>? = null
    private var keyboardStatusListeners: MutableList<OnKeyboardStateListener>? = null
    private var editFocusChangeListeners: MutableList<OnEditFocusChangeListener>? = null

    private lateinit var contentContainer: IContentContainer
    private lateinit var panelContainer: PanelContainer
    private lateinit var window: Window

    private var windowInsetsRootView: View? = null // 用于Android 11以上，通过OnApplyWindowInsetsListener获取键盘高度
    private var triggerViewClickInterceptor: TriggerViewClickInterceptor? = null
    private val contentScrollMeasurers = mutableListOf<ContentScrollMeasurer>()
    private val panelHeightMeasurers = HashMap<Int, PanelHeightMeasurer>()

    private var isKeyboardShowing = false
    private var panelId = Constants.PANEL_NONE
    private var lastPanelId = Constants.PANEL_NONE
    private var lastPanelHeight = -1
    private var animationSpeed = 200 //standard
    private var enableAndroid11KeyboardFeature = true   // 是否启用 Android 11 键盘动画方案，目前发现 dialog，popupWindow等子窗口场景不支持键盘动画
    private var contentScrollOutsizeEnable = true

    private var deviceRuntime: DeviceRuntime? = null
    private var realBounds: Rect? = null
    private var keyboardStateRunnable = Runnable { toKeyboardState(false) }

    private var doingCheckout = false

    private val retryCheckoutKbRunnable = CheckoutKbRunnable()

    internal fun getContentContainer() = contentContainer

    // 针对Android 11以上开启键盘动画特性，高度获取失败时，对外提供兼容方案
    var softInputHeightCalculatorOnStart: ((animation: WindowInsetsAnimationCompat, bounds: WindowInsetsAnimationCompat.BoundsCompat) -> Int)? = null
    var softInputHeightCalculatorOnProgress: ((insets: WindowInsetsCompat, runningAnimations: MutableList<WindowInsetsAnimationCompat>) -> Int)? = null

    inner class CheckoutKbRunnable : Runnable {
        var retry = false
        var delay: Long = 0L
        override fun run() {
            val result = checkoutPanel(Constants.PANEL_KEYBOARD)
            if (!result && panelId != Constants.PANEL_KEYBOARD && retry) {
                this@PanelSwitchLayout.postDelayed(this, delay)
            }
            retry = false
        }
    }


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
        enableAndroid11KeyboardFeature = typedArray.getBoolean(R.styleable.PanelSwitchLayout_android11KeyboardFeature, true)
        typedArray.recycle()
    }

    internal fun setTriggerViewClickInterceptor(interceptor: TriggerViewClickInterceptor?) {
        this.triggerViewClickInterceptor = interceptor
    }

    internal fun setContentScrollOutsizeEnable(enable: Boolean) {
        this.contentScrollOutsizeEnable = enable
    }

    internal fun isContentScrollOutsizeEnable() = contentScrollOutsizeEnable

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!hasAttachLister) {
            tryBindKeyboardChangedListener()
        }
    }

    private fun recycle() {
        removeCallbacks(retryCheckoutKbRunnable)
        removeCallbacks(keyboardStateRunnable)
        contentContainer.getInputActionImpl().recycler()
        if (hasAttachLister) {
            releaseKeyboardChangedListener()
        }
    }

    private fun checkoutKeyboard(retry: Boolean = true, delay: Long = 200L) {
        this@PanelSwitchLayout.removeCallbacks(retryCheckoutKbRunnable)
        retryCheckoutKbRunnable.retry = retry
        retryCheckoutKbRunnable.delay = delay
        retryCheckoutKbRunnable.run()
    }

    private fun initListener() {
        /**
         * 1. if current currentPanelId is None,should show keyboard
         * 2. current currentPanelId is not None or KeyBoard that means some panel is showing,hide it and show keyboard
         */
        contentContainer.getInputActionImpl().setEditTextClickListener(OnClickListener { v ->
            notifyViewClick(v)
            checkoutKeyboard()
        })
        contentContainer.getInputActionImpl().setEditTextFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            notifyEditFocusChange(v, hasFocus)
            checkoutKeyboard()
        })
        contentContainer.getResetActionImpl().setResetCallback(Runnable {
            hookSystemBackByPanelSwitcher()
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
                    triggerViewClickInterceptor?.let {
                        if (it.intercept(v.id)) {
                            return
                        }
                    }
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - preClickTime <= Constants.PROTECT_KEY_CLICK_DURATION) {
                        LogTracker.log("$TAG#initListener", "panelItem invalid click! preClickTime: $preClickTime currentClickTime: $currentTime")
                        return
                    }
                    notifyViewClick(v)
                    val targetId = panelContainer.getPanelId(panelView)
                    if (panelId == targetId && panelView.isTriggerViewCanToggle() && panelView.isShowing()) {
                        checkoutKeyboard(false)
                    } else {
                        checkoutPanel(targetId)
                    }
                    preClickTime = currentTime
                }
            })
        }
    }

    internal fun bindListener(
        viewClickListeners: MutableList<OnViewClickListener>, panelChangeListeners: MutableList<OnPanelChangeListener>,
        keyboardStatusListeners: MutableList<OnKeyboardStateListener>, editFocusChangeListeners: MutableList<OnEditFocusChangeListener>
    ) {
        this.viewClickListeners = viewClickListeners
        this.panelChangeListeners = panelChangeListeners
        this.keyboardStatusListeners = keyboardStatusListeners
        this.editFocusChangeListeners = editFocusChangeListeners
    }

    internal fun setScrollMeasurers(mutableList: MutableList<ContentScrollMeasurer>) {
        contentScrollMeasurers.addAll(mutableList)
    }

    internal fun setPanelHeightMeasurers(mutableList: MutableList<PanelHeightMeasurer>) {
        for (panelHeightMeasurer in mutableList) {
            panelHeightMeasurers[panelHeightMeasurer.getPanelTriggerId()] = panelHeightMeasurer
        }
    }

    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var hasAttachLister = false


    /**
     * 针对 Android Q 场景判断，
     * 设备开启虚拟手势导航栏如 MIUI12时，正常情况下使用 navigationBarBackground 判断可见时是正确的
     * 如果同时采用 SYSTEM_UL_FLAG_LAYOUT_HIDE_NAVIGATION 绘制到导航栏下面，那么在 Layout Inspector 上 navigationBarBackground 布局是不存在的，但是 getWindowVisibleDisplayFrame 并没有包含这部分高度。
     * 所以针对 AndroidQ 采用 rootWindowInsets 来获取这部分可视导航栏的高度并在计算软键盘高度的时候需要加回去。
     */
    private fun getAndroidQNavHIfNavIsInvisible(runtime: DeviceRuntime, window: Window): Int {
        return if (!runtime.isNavigationBarShow && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && DisplayUtil.hasSystemUIFlag(window, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)) {
            val inset = window.decorView.rootView.rootWindowInsets
            LogTracker.log("$TAG#onGlobalLayout", " -> Android Q takes windowInset into calculation When nav is not shown and SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION flag is existed <-")
            LogTracker.log("$TAG#onGlobalLayout", "stableInsetTop is : ${inset.stableInsetTop}")
            LogTracker.log("$TAG#onGlobalLayout", "stableInsetBottom is : ${inset.stableInsetBottom}")
            LogTracker.log("$TAG#onGlobalLayout", "androidQCompatNavH is  ${inset.stableInsetBottom}")
            inset.stableInsetBottom
        } else 0
    }

    /**
     * 常规导航栏显示或者全屏手势虚拟导航栏显示且不通过 SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 让界面绘制到全屏手势导航栏下时有值
     */
    private fun getCurrentNavigationHeight(deviceRuntime: DeviceRuntime, deviceInfo: DeviceInfo): Int {
        return if (deviceRuntime.isNavigationBarShow)
            deviceInfo.getCurrentNavigationBarHeightWhenVisible(deviceRuntime.isPortrait, deviceRuntime.isPad)
        else 0
    }

    /**
     * 历史从 fullScreen api 控制上看，该属性决定状态栏行为。
     * 对于有导航栏的机型，使用该属性进行全屏显示不可取。
     */
    private fun getCurrentStatusBarHeight(deviceInfo: DeviceInfo): Int {
        return deviceInfo.statusBarH
    }

    private var lastContentHeight: Int? = null
    private var lastNavigationBarShow: Boolean? = null
    private var lastKeyboardHeight: Int = 0
    private val minLimitOpenKeyboardHeight by lazy { KeyboardHeightCompat.getMinLimitHeight() }
    private var minLimitCloseKeyboardHeight: Int = 0
    private var keyboardAnimationFeature = false     // 是否使用Android 11键盘动画特性

    internal fun bindWindow(window: Window, windowInsetsRootView: View?) {
        this.window = window
        this.windowInsetsRootView = windowInsetsRootView
        keyboardAnimationFeature = enableAndroid11KeyboardFeature && supportKeyboardAnimation()
        if (keyboardAnimationFeature) {
            // 通过监听键盘动画，修改translationY线上面板
            keyboardChangedAnimation()
        } else {
            // 通过获取键盘高度，触发onLayout修改面板高度
            deviceRuntime = DeviceRuntime(context, window)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            deviceRuntime?.let {
                contentContainer.getInputActionImpl().updateFullScreenParams(it.isFullScreen, panelId, getCompatPanelHeight(panelId))
                if (supportKeyboardFeature()) {
                    keyboardChangedListener30Impl()
                } else {
                    keyboardChangedListener(window, it)
                }
                hasAttachLister = true
            }
        }
    }


    /**
     * Android 11 ViewCompat.setWindowInsetsAnimationCallback
     */
    private fun keyboardChangedAnimation() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        var hasSoftInput = false
        var transitionY = 0f
        val callback = object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {

            override fun onStart(animation: WindowInsetsAnimationCompat, bounds: WindowInsetsAnimationCompat.BoundsCompat): WindowInsetsAnimationCompat.BoundsCompat {
                val typeFlag = animation.typeMask.and(WindowInsetsCompat.Type.ime())
                val insetsCompat = ViewCompat.getRootWindowInsets(window.decorView)
                hasSoftInput = insetsCompat?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
                LogTracker.log("onStart", "hasSoftInput = $hasSoftInput")
                if (hasSoftInput && typeFlag != 0) {
                    val navigationBarH = insetsCompat?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
                    val imeH = insetsCompat?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0
                    var keyboardH = if (imeH != 0) imeH else bounds.upperBound.bottom
                    if (keyboardH == 0) {
                        LogTracker.log("onStart", "键盘高度获取失败，请实现softInputHeightCalculatorOnStart兼容正确的键盘高度")
                        keyboardH = softInputHeightCalculatorOnStart?.invoke(animation, bounds)?: 0
                    }
                    val realKeyboardH = keyboardH - navigationBarH
                    LogTracker.log("onStart", "keyboard height = $keyboardH")
                    LogTracker.log("onStart", "realKeyboardH height = $realKeyboardH")
                    val panelHeight = panelContainer.layoutParams.height
                    if (realKeyboardH > 0 && panelHeight != realKeyboardH) {
                        panelContainer.layoutParams.height = realKeyboardH
                        lastKeyboardHeight = realKeyboardH
                        PanelUtil.setKeyBoardHeight(context, realKeyboardH)
                    }
                    // 当键盘高度小于已偏移的高度时，调整回键盘高度
                    if (keyboardH > 0 && hasSoftInput) {
                        val maxSoftInputTop = window.decorView.bottom - keyboardH
                        val location = DisplayUtil.getLocationOnWindow(this@PanelSwitchLayout)
                        val floatInitialBottom = location[1] + height
                        val maxOffset = (maxSoftInputTop - floatInitialBottom).toFloat()
                        if (panelContainer.translationY < maxOffset) {
                            updatePanelStateByAnimation(-maxOffset.toInt())
                        }
                    }
                }
                return bounds
            }

            override fun onProgress(insets: WindowInsetsCompat, runningAnimations: MutableList<WindowInsetsAnimationCompat>): WindowInsetsCompat {
                if (isPanelState(panelId)) {
                    LogTracker.log("onProgress", "isPanelState: ture")
                } else {
                    val logFormatter = LogFormatter.setUp()
                    logFormatter.addContent(value = "keyboard animation progress")
                    // 找到键盘（IME）动画
                    val imeAnimation = runningAnimations.find { it.typeMask.and(WindowInsetsCompat.Type.ime()) != 0 }
                    val fraction = imeAnimation?.fraction ?: return insets                          // 动画进度 【0,1】
                    var softInputHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom    // 键盘高度，这个高度包含了导航栏
                    if (hasSoftInput && fraction != 0f && softInputHeight == 0) { // 部分手机在键盘动画执行过程中无法获取到键盘高度
                        // 这里提供一个键盘计算的方法，业务方可以自行兼容
                        LogTracker.log("onProgress", "键盘高度获取失败，请实现softInputHeightCalculatorOnProgress兼容正确的键盘高度，当前动画进度fraction = $fraction")
                        softInputHeight = softInputHeightCalculatorOnProgress?.invoke(insets, runningAnimations) ?: 0
                    }
                    val softInputTop = window.decorView.bottom - softInputHeight            // 键盘top位于当前window的位置
                    logFormatter.addContent("fraction", "$fraction")
                    logFormatter.addContent("softInputHeight", "$softInputHeight")
                    logFormatter.addContent("decorView.bottom", "${window.decorView.bottom}")
                    val location = DisplayUtil.getLocationOnWindow(this@PanelSwitchLayout)
                    val floatInitialBottom = height + location[1]                           // PanelSwitchLayout控件位于当前window的Bottom
                    val compatPanelHeight = getCompatPanelHeight(panelId)
                    if (hasSoftInput) { // 键盘显示时
                        if (softInputTop < floatInitialBottom) {
                            val offset = (softInputTop - floatInitialBottom).toFloat()
                            if (panelContainer.translationY > offset) {
                                panelContainer.translationY = offset
                                contentContainer.translationContainer(contentScrollMeasurers, compatPanelHeight, offset)
                                logFormatter.addContent("translationY", "$offset")
                                transitionY = offset
                            }
                        }
                    } else {// 键盘关闭时
                        // 有些设备隐藏键盘时，softInputHeight的值一直等于0，此时用 fraction 来计算偏移量
                        if (softInputHeight > 0) {
                            val offset = min(softInputTop - floatInitialBottom, 0).toFloat()
                            panelContainer.translationY = offset
                            contentContainer.translationContainer(contentScrollMeasurers, compatPanelHeight, offset)
                            logFormatter.addContent("translationY", "$offset")
                        } else {
                            val offset = min(transitionY - transitionY * (fraction + 0.5f), 0f)
                            panelContainer.translationY = offset
                            contentContainer.translationContainer(contentScrollMeasurers, compatPanelHeight, offset)
                            logFormatter.addContent("translationY", "$offset")
                        }
                    }
                    logFormatter.log("onProgress")
                }
                return insets
            }
        }
        ViewCompat.setWindowInsetsAnimationCallback(window.decorView, callback)
    }


    /**
     * 是否支持Android 11 方案获取键盘高度
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    private fun supportKeyboardFeature(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    /**
     * 是否支持键盘过渡动画
     */
    private fun supportKeyboardAnimation(): Boolean {
        return window.decorView.isSystemInsetsAnimationSupport()
    }


    /**
     * Android 11 监听键盘变化
     */
    private fun keyboardChangedListener30Impl() {
        if (!this::window.isInitialized) {
            return
        }
        val rootView = windowInsetsRootView ?: window.decorView.rootView
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            // 键盘
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            // 导航
            val hasNavigation = insets.isVisible(WindowInsetsCompat.Type.navigationBars())
            val navigationH = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            var keyboardH = if (imeVisible && hasNavigation) imeHeight - navigationH else imeHeight
            // 发现部分机型键盘可见时，键盘高度返回0，因此这里用已保存的键盘高度
            if (imeVisible && keyboardH == 0) {
                keyboardH = getKeyBoardHeight(context)
            }
            LogTracker.log("$TAG#WindowInsetsListener", "KeyBoardHeight : $keyboardH，isShow $imeVisible")

            if (keyboardH != lastKeyboardHeight) {
                val contentHeight = getScreenHeightWithoutSystemUI(window)
                val androidQCompatNavH = deviceRuntime?.run { getAndroidQNavHIfNavIsInvisible(this, window) } ?: 0
                val realHeight = keyboardH + androidQCompatNavH
                handleKeyboardStateChanged(keyboardH, realHeight, contentHeight)
                LogTracker.log("$TAG#WindowInsetsListener", "requestLayout")
            }
            ViewCompat.onApplyWindowInsets(view, insets)
        }
    }

    /**
     * 监听键盘变化
     */
    private fun keyboardChangedListener(window: Window, it: DeviceRuntime) {
        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val logFormatter = LogFormatter.setUp()
            logFormatter.addContent(value = "界面每一次变化的信息回调")
            logFormatter.addContent("windowSoftInputMode", "${window.attributes.softInputMode}")
            logFormatter.addContent("currentPanelSwitchLayoutVisible", "${this@PanelSwitchLayout.visibility == VISIBLE}")
            if (this@PanelSwitchLayout.visibility != VISIBLE) {
                logFormatter.addContent(value = "skip cal keyboard Height When window is invisible!")
            }
            val screenHeight = getScreenRealHeight(window)
            val contentHeight = getScreenHeightWithoutSystemUI(window)
            val info = it.getDeviceInfoByOrientation(true)
            val curStatusHeight = getCurrentStatusBarHeight(info)
            val cusNavigationHeight = getCurrentNavigationHeight(it, info)
            val androidQCompatNavH = getAndroidQNavHIfNavIsInvisible(it, window)
            val systemUIHeight = curStatusHeight + cusNavigationHeight + androidQCompatNavH
            logFormatter.addContent("screenHeight", "$screenHeight")
            logFormatter.addContent("contentHeight", "$contentHeight")
            logFormatter.addContent("isFullScreen", "${it.isFullScreen}")
            logFormatter.addContent("isNavigationBarShown", "${it.isNavigationBarShow}")
            logFormatter.addContent("deviceStatusBarH", "${info.statusBarH}")
            logFormatter.addContent("deviceNavigationBarH", "${info.navigationBarH}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val inset = window.decorView.rootWindowInsets
                logFormatter.addContent(
                    "systemInset",
                    "left(${inset.systemWindowInsetTop}) top(${inset.systemWindowInsetLeft}) right(${inset.systemWindowInsetRight}) bottom(${inset.systemWindowInsetBottom})"
                )
                logFormatter.addContent("inset", "left(${inset.stableInsetLeft}) top(${inset.stableInsetTop}) right(${inset.stableInsetRight}) bottom(${inset.stableInsetBottom})")
            }
            logFormatter.addContent("currentSystemInfo", "statusBarH : $curStatusHeight, navigationBarH : $cusNavigationHeight 全面屏手势虚拟栏H : $androidQCompatNavH")
            logFormatter.addContent("currentSystemH", "$systemUIHeight")

            lastNavigationBarShow = it.isNavigationBarShow
            val keyboardHeight = screenHeight - contentHeight - systemUIHeight
            //输入法拉起时，需要追加 "悬浮在界面之上的全屏手势虚拟导航栏" 高度
            val realHeight = keyboardHeight + androidQCompatNavH
            minLimitCloseKeyboardHeight = if (info.navigationBarH > androidQCompatNavH) info.navigationBarH else androidQCompatNavH
            logFormatter.addContent("minLimitCloseKeyboardH", "$minLimitCloseKeyboardHeight")
            logFormatter.addContent("minLimitOpenKeyboardH", "$minLimitOpenKeyboardHeight")
            logFormatter.addContent("lastKeyboardH", "$lastKeyboardHeight")
            logFormatter.addContent("currentKeyboardInfo", "keyboardH : $keyboardHeight, realKeyboardH : $realHeight, isShown : $isKeyboardShowing")
            handleKeyboardStateChanged(keyboardHeight, realHeight, contentHeight)
            logFormatter.log("$TAG#onGlobalLayout")

        }
        window.decorView.rootView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    /**
     * 键盘高度变化时，状态更新
     */
    private fun handleKeyboardStateChanged(keyboardHeight: Int, realHeight: Int, contentHeight: Int) {
        if (isKeyboardShowing) {
            if (keyboardHeight <= minLimitOpenKeyboardHeight) {
                isKeyboardShowing = false
                if (isKeyboardState()) {
                    checkoutPanel(Constants.PANEL_NONE)
                }
                notifyKeyboardState(false)
            } else {
                /**
                 * 拉起输入法的时候递增，隐藏输入法的时候递减，机型较差的手机需要 requestLayout() 动态更新布局
                 */
                if (keyboardHeight != lastKeyboardHeight) {
                    LogTracker.log("$TAG#KeyboardStateChanged", "try to set KeyBoardHeight : $realHeight，isShow $isKeyboardShowing")
                    PanelUtil.setKeyBoardHeight(context, realHeight)
                    requestLayout()
                }
            }
        } else {
            if (keyboardHeight > minLimitOpenKeyboardHeight) {
                isKeyboardShowing = true
                if (keyboardHeight > lastKeyboardHeight) {
                    LogTracker.log("$TAG#KeyboardStateChanged", "try to set KeyBoardHeight : $realHeight，isShow $isKeyboardShowing")
                    PanelUtil.setKeyBoardHeight(context, realHeight)
                    requestLayout()
                }
                if (!isKeyboardState()) {
                    checkoutPanel(Constants.PANEL_KEYBOARD, false)
                }
                notifyKeyboardState(true)
            } else {
                //1.3.5 实时兼容导航栏动态隐藏调整布局
                lastContentHeight?.let { lastHeight ->
                    lastNavigationBarShow?.let { lastShow ->
                        if (lastHeight != contentHeight && lastShow != deviceRuntime?.isNavigationBarShow) {
                            requestLayout()
                            LogTracker.log("$TAG#KeyboardStateChanged", "update layout by navigation visibility State change")
                        }
                    }
                }
            }
        }
        // 这里为了兼容华为手机隐藏导航栏时会回调两次键盘高度，并且第一回调的高度不准确
        if (lastContentHeight == contentHeight && lastKeyboardHeight != keyboardHeight) {
            trySyncKeyboardHeight(keyboardHeight)
        }
        lastKeyboardHeight = keyboardHeight
        lastContentHeight = contentHeight
    }

    /**
     * 键盘高度发生变化时，同步键盘高度
     */
    private fun trySyncKeyboardHeight(keyboardHeight: Int) {
        Log.d(TAG, "trySyncKeyboardHeight: $keyboardHeight")
        if (lastKeyboardHeight > 0 && keyboardHeight > 0) {
            // 采用键盘过渡动画的方案需要同步高度，采用 onLayout 方案的不需要通过这个方法进行同步
            if (keyboardAnimationFeature && panelContainer.translationY != 0F) {
                updatePanelStateByAnimation(keyboardHeight)
            }
        }
    }


    private fun tryBindKeyboardChangedListener() {
        if (keyboardAnimationFeature || supportKeyboardFeature()) {
            keyboardChangedListener30Impl()
        } else {
            globalLayoutListener?.let {
                window.decorView.rootView.viewTreeObserver.addOnGlobalLayoutListener(it)
            }
        }
        hasAttachLister = true
    }


    private fun releaseKeyboardChangedListener() {
        if (keyboardAnimationFeature || supportKeyboardFeature()) {
            val rootView = windowInsetsRootView ?: window.decorView.rootView
            ViewCompat.setOnApplyWindowInsetsListener(rootView, null)
        } else {
            globalLayoutListener?.let {
                window.decorView.rootView.viewTreeObserver.removeOnGlobalLayoutListener(it)
            }
        }
        hasAttachLister = false
    }


    private fun notifyViewClick(view: View) {
        viewClickListeners?.let {
            for (listener in it) {
                listener.onClickBefore(view)
            }
        }
    }

    private fun notifyKeyboardState(visible: Boolean) {
        keyboardStatusListeners?.let {
            for (listener in it) {
                listener.onKeyboardChange(visible, if (visible) getKeyBoardHeight(context) else 0)
            }
        }
    }

    private fun notifyEditFocusChange(view: View, hasFocus: Boolean) {
        editFocusChangeListeners?.let {
            for (listener in it) {
                listener.onFocusChange(view, hasFocus)
            }
        }
    }

    private fun notifyPanelChange(panelId: Int) {
        panelChangeListeners?.let {
            for (listener in it) {
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
    }

    private fun notifyPanelSizeChange(panelView: IPanelView?, portrait: Boolean, oldWidth: Int, oldHeight: Int, width: Int, height: Int) {
        panelChangeListeners?.let {
            for (listener in it) {
                listener.onPanelSizeChange(panelView, portrait, oldWidth, oldHeight, width, height)
            }
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
        val result = if (contentScrollOutsizeEnable) {
            if (isResetState()) 0 else -scrollOutsideHeight
        } else 0
        LogTracker.log("$TAG#onLayout", " getContentContainerTop  :$result")
        return result;
    }

    private fun getContentContainerHeight(allHeight: Int, paddingTop: Int, scrollOutsideHeight: Int): Int {
        return allHeight - paddingTop -
                if (!contentScrollOutsizeEnable && !isResetState()) scrollOutsideHeight else 0
    }

    private fun getCompatPanelHeight(panelId: Int): Int {
        if (isPanelState(panelId)) {
            val panelHeightMeasurer = panelHeightMeasurers[panelId]
            panelHeightMeasurer?.let {
                //如果输入法还没有测量或者不同步输入法高度，则是有默认高度
                if (!PanelUtil.hasMeasuredKeyboard(context) || !it.synchronizeKeyboardHeight()) {
                    val result = it.getTargetPanelDefaultHeight()
                    LogTracker.log("$TAG#onLayout", " getCompatPanelHeight by default panel  :$result")
                    return result
                }
            }
        }
        val result = getKeyBoardHeight(context);
        LogTracker.log("$TAG#onLayout", " getCompatPanelHeight  :$result")
        return result
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
            LogTracker.log("$TAG#onLayout", "isGone，skip")
            return
        }
        // 这里是使用键盘过渡动画方案
        if (keyboardAnimationFeature) {
            super.onLayout(changed, l, t, r, b)
            val compatPanelHeight = getCompatPanelHeight(panelId)
            if (panelId != Constants.PANEL_NONE && compatPanelHeight != 0) {
                val translationY = panelContainer.translationY
                contentContainer.translationContainer(contentScrollMeasurers, compatPanelHeight, translationY)
            }
            return
        }
        // 以下是使用layout方案
        deviceRuntime?.let {
            val logFormatter = LogFormatter.setUp()
            val deviceInfo = it.getDeviceInfoByOrientation()

            /**
             * 当还没有进行输入法高度获取时，由于兼容性测试之后设置的默认高度无法兼容所有机型
             * 为了业务能100%兼容，开放设置每个面板的默认高度，待输入法高度获取之后统一高度。
             */
            val compatPanelHeight = getCompatPanelHeight(panelId);
            val paddingTop = paddingTop
            var allHeight = deviceInfo.screenH

            if (it.isNavigationBarShow) {
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
                allHeight -= deviceInfo.getCurrentNavigationBarHeightWhenVisible(it.isPortrait, it.isPad)
            }

            val localLocation = getLocationOnScreen(this)
            allHeight -= localLocation[1]
            var contentContainerTop = getContentContainerTop(compatPanelHeight)
            contentContainerTop += paddingTop
            val contentContainerHeight = getContentContainerHeight(allHeight, paddingTop, compatPanelHeight)
            val panelContainerTop = contentContainerTop + contentContainerHeight

            if (Constants.DEBUG) {
                logFormatter.addContent(value = "界面每一次 layout 的信息回调")
                logFormatter.addContent("layoutInfo", "onLayout(changed : $changed , l : $l  , t : $t , r : $r , b : $b)")
                val state = when (panelId) {
                    Constants.PANEL_NONE -> "收起所有输入源"
                    Constants.PANEL_KEYBOARD -> "显示键盘输入"
                    else -> "显示面板输入"
                }
                logFormatter.addContent("currentPanelState", "$state")
                logFormatter.addContent("isPad", "${it.isPad}")
                logFormatter.addContent("isFullScreen", "${it.isFullScreen}")
                logFormatter.addContent("isPortrait", "${it.isPortrait}")
                logFormatter.addContent("isNavigationShown", "${it.isNavigationBarShow}")
                logFormatter.addContent("screenH (static,include SystemUI)", "${deviceInfo.screenH}")
                logFormatter.addContent("screenH (static,exclude SystemUI)", "${deviceInfo.screenWithoutNavigationH}")
                logFormatter.addContent("screenH (dynamic,exclude SystemUI)", "${deviceInfo.screenWithoutSystemUiH}")
                logFormatter.addContent("localLocation[y]", "${localLocation[1]}")
                logFormatter.addContent("toolbarH", "${deviceInfo.toolbarH}")
                logFormatter.addContent("StatusBarH", "${deviceInfo.statusBarH}")
                logFormatter.addContent("NavigationBarH", "${deviceInfo.navigationBarH}")
                logFormatter.addContent("layout Location", "(${localLocation[0]},${localLocation[1]})")
                logFormatter.addContent("paddingTop", "$paddingTop")
                logFormatter.addContent("keyboardH", "${getKeyBoardHeight(context)}")
                logFormatter.addContent("ContentContainerTop", "$contentContainerTop")
                logFormatter.addContent("ContentContainerH", "$contentContainerHeight")
                logFormatter.addContent("PanelContainerTop", "$panelContainerTop")
                logFormatter.addContent("PanelContainerH", "$compatPanelHeight")
            }

            //计算实际bounds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val changeBounds = isBoundChange(l, contentContainerTop, r, panelContainerTop + compatPanelHeight)
                logFormatter.addContent("changeBounds", "$changeBounds")
                if (changeBounds) {
                    val reverseResetState = reverseResetState()
                    logFormatter.addContent("reverseResetState", "$reverseResetState")
                    if (reverseResetState) {
                        setTransition(animationSpeed.toLong(), panelId)
                    }
                } else {
                    //如果功能面板的互相切换，则需要判断是否存在高度不一致，如果不一致则需要过渡
                    if (lastPanelHeight != -1 && lastPanelHeight != compatPanelHeight) {
                        setTransition(animationSpeed.toLong(), panelId)
                    }
                }
            }

            //处理第一个view contentContainer
            run {
                contentContainer.layoutContainer(
                    l, contentContainerTop, r, contentContainerTop + contentContainerHeight,
                    contentScrollMeasurers, compatPanelHeight, contentScrollOutsizeEnable, isResetState(), changed
                )
                logFormatter.addContent("contentContainer Layout", "($l,$contentContainerTop,$r,${contentContainerTop + contentContainerHeight})")
                contentContainer.changeContainerHeight(contentContainerHeight)
            }

            //处理第二个view panelContainer
            run {
                panelContainer.layout(l, panelContainerTop, r, panelContainerTop + compatPanelHeight)
                logFormatter.addContent("panelContainer Layout", "($l,$panelContainerTop,$r,${panelContainerTop + compatPanelHeight})")
                panelContainer.changeContainerHeight(compatPanelHeight)
            }
            this.lastPanelHeight = compatPanelHeight;
            contentContainer.getInputActionImpl().updateFullScreenParams(it.isFullScreen, panelId, compatPanelHeight)
            logFormatter.log("$TAG#onLayout")
            return
        }

        //预览的时候由于 helper 还没有初始化导致可能为 null
        super.onLayout(changed, l, t, r, b)
    }


    private fun isBoundChange(l: Int, t: Int, r: Int, b: Int): Boolean {
        val change = realBounds == null || realBounds!!.run {
            this.left != l || this.top != top || this.right != r || this.bottom != b
        }
        realBounds = Rect(l, t, r, b)
        return change
    }

    internal fun isPanelState() = isPanelState(panelId)

    internal fun isKeyboardState() = isKeyboardState(panelId)

    internal fun isResetState() = isResetState(panelId)

    private fun isPanelState(panelId: Int) = !isResetState(panelId) && !isKeyboardState(panelId)

    private fun isKeyboardState(panelId: Int) = panelId == Constants.PANEL_KEYBOARD

    private fun isResetState(panelId: Int) = panelId == Constants.PANEL_NONE

    private fun reverseResetState(): Boolean = (isResetState(lastPanelId) && !isResetState(panelId))
            || (!isResetState(lastPanelId) && isResetState(panelId))


    @TargetApi(19)
    private fun setTransition(duration: Long, panelId: Int) {
        val changeBounds = ChangeBounds()
        changeBounds.duration = duration
        TransitionManager.beginDelayedTransition(this, changeBounds)
    }


    /**
     * This will be called when User press System Back Button.
     * 1. if keyboard is showing, should be hide;
     * 2. if you want to hide panel(exclude keyboard),you should call it before [android.support.v7.app.AppCompatActivity.onBackPressed] to hook it.
     *
     * @return if need hook
     */
    internal fun hookSystemBackByPanelSwitcher(): Boolean {
        if (!isResetState()) {
            //模仿系统输入法隐藏，如果直接掉  checkoutPanel(Constants.PANEL_NONE)，可能导致隐藏时上层 recyclerview 因为 layout 导致界面出现短暂卡顿。
            if (isKeyboardState()) {
                if (isKeyboardShowing) {
                    contentContainer.getInputActionImpl().hideKeyboard(isKeyboardShowing, true)
                } else {
                    checkoutPanel(Constants.PANEL_NONE)
                    return false
                }
            } else {
                checkoutPanel(Constants.PANEL_NONE)
            }
            return true
        }
        return false
    }

    @JvmOverloads
    internal fun toKeyboardState(async: Boolean = false) {
        if (async) {
            post(keyboardStateRunnable)
        } else {
            contentContainer.getInputActionImpl().requestKeyboard()
        }
    }


    /**
     * @param panelId
     * @return
     */
    internal fun checkoutPanel(panelId: Int, checkoutKeyboard: Boolean = true): Boolean {
        if (doingCheckout) {
            LogTracker.log("$TAG#checkoutPanel", "is checkouting,just ignore!")
            return false
        }
        doingCheckout = true

        if (panelId == this.panelId) {
            LogTracker.log("$TAG#checkoutPanel", "current panelId is $panelId ,just ignore!")
            doingCheckout = false
            return false
        }

        when (panelId) {
            Constants.PANEL_NONE -> {
                contentContainer.getInputActionImpl().hideKeyboard(isKeyboardShowing, true)
                contentContainer.getResetActionImpl().enableReset(false)
                if (keyboardAnimationFeature) {
                    updatePanelStateByAnimation(0)
                }
            }

            Constants.PANEL_KEYBOARD -> {
                if (checkoutKeyboard) {
                    if (!contentContainer.getInputActionImpl().showKeyboard()) {
                        LogTracker.log("$TAG#checkoutPanel", "system show keyboard fail, just ignore!")
                        doingCheckout = false
                        return false
                    }
                }
                contentContainer.getResetActionImpl().enableReset(true)
            }
            else -> {
                val size = Pair(measuredWidth - paddingLeft - paddingRight, getCompatPanelHeight(panelId))
                val oldSize = panelContainer.showPanel(panelId, size)
                if (size.first != oldSize.first || size.second != oldSize.second) {
                    notifyPanelSizeChange(panelContainer.getPanelView(panelId), isPortrait(context), oldSize.first, oldSize.second, size.first, size.second)
                }
                contentContainer.getInputActionImpl().hideKeyboard(isKeyboardShowing, false)
                contentContainer.getResetActionImpl().enableReset(true)
                // Android 11 修改偏移量来显示面板
                if (keyboardAnimationFeature) {
                    val compatPanelHeight = getCompatPanelHeight(panelId)
                    updatePanelStateByAnimation(compatPanelHeight)
                }
            }
        }
        this.lastPanelId = this.panelId
        this.panelId = panelId
        LogTracker.log("$TAG#checkoutPanel", "checkout success ! lastPanel's id : $lastPanelId , panel's id :$panelId")
        requestLayout()
        notifyPanelChange(this.panelId)
        doingCheckout = false
        return true
    }

    /**
     * 更新面板状态
     * @param expectHeight 期望高度
     */
    private fun updatePanelStateByAnimation(expectHeight: Int) {
        Log.d(TAG, "updatePanelStateByAnimation: $expectHeight")
        val translationY = panelContainer.translationY
        val targetY = -expectHeight.toFloat()
        if (translationY != targetY) {
            val compatPanelHeight = getCompatPanelHeight(panelId)
            val animation = ValueAnimator.ofFloat(translationY, targetY)
                .setDuration(animationSpeed.toLong())
            animation.addUpdateListener {
                val y = it.animatedValue as? Float ?: 0F
                panelContainer.translationY = y
                contentContainer.translationContainer(contentScrollMeasurers, compatPanelHeight, y)
            }
            animation.start()
        }
        // 尝试对其键盘高度
        val panelHeight = panelContainer.layoutParams.height
        if (expectHeight > 0 && panelHeight != expectHeight) {
            panelContainer.layoutParams.height = expectHeight
        }
    }


    companion object {
        const val TAG = "PanelSwitchLayout"
        private var preClickTime: Long = 0
    }
}