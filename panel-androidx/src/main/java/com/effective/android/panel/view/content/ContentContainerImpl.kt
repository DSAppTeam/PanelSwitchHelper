package com.effective.android.panel.view.content

import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.IdRes
import com.effective.android.panel.Constants
import com.effective.android.panel.log.LogTracker
import com.effective.android.panel.interfaces.ViewAssertion
import com.effective.android.panel.interfaces.ContentScrollMeasurer
import com.effective.android.panel.utils.PanelUtil
import com.effective.android.panel.view.PanelSwitchLayout
import java.util.*
import kotlin.collections.HashMap

/**
 * 内容区域代理
 * --------------------
 * Created by yummyLau on 2020/05/07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class ContentContainerImpl(private val mViewGroup: ViewGroup, private val autoReset: Boolean, @IdRes private val editTextId: Int, @IdRes private val resetId: Int) : IContentContainer, ViewAssertion {
    private val mEditText: EditText? = mViewGroup.findViewById(editTextId)
    private val context = mViewGroup.context;
    private val mResetView: View? = mViewGroup.findViewById(resetId)
    private val mInputAction: IInputAction
    private val mResetAction: IResetAction
    private val tag = ContentContainerImpl::class.java.simpleName
    private val mPixelInputView = EditText(mEditText?.context)

    init {
        assertView()
        var imeOptions = mEditText?.imeOptions
        if (imeOptions != null) {
            imeOptions = imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI
            mEditText?.imeOptions = imeOptions
            mPixelInputView.imeOptions = imeOptions
        }
        mResetAction = object : IResetAction {

            private var enableReset: Boolean = false
            private var action: Runnable? = null

            /**
             * 当子类不处理事件时，则 hookOnTouchEvent 会尝试消费 DOWN 。
             * 当子类处理事件时，则没有机会 hookOnTouchEvent。 这是时候有两种做法
             *  1. 寻找一个在子 View 的 hook 点，在不影响可滑动的场景先，拦截 ACTION_UP 就可以了.(Demo中)
             *  2. 如果不想在子 View 处理，则需要在点击的区域构建一个透明view盖住，监听点击之后调用 PanelSwitchHelper#resetState 手动隐藏。
             *  hookDispatchTouchEvent 为第一种方案预留可能
             */
            override fun hookDispatchTouchEvent(ev: MotionEvent?, consume: Boolean): Boolean {
                ev?.let { event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        action?.let {
                            if (autoReset && enableReset && !consume) {
                                if (mResetView == null || eventInViewArea(mResetView, event)) {
                                    it.run()
                                    LogTracker.log("$tag#hookDispatchTouchEvent", "hook ACTION_UP")
                                    return true
                                }
                            }
                        }

                    }
                }
                return false
            }

            /**
             * 子view不消费事件时，则默认会自己处理。
             * 当不需要指定reset区域时，捕获 ACTION_DOWN 进行消费。
             * 当指定reset区域时，则需要匹配事件发生的位置是否在区域内
             */
            override fun hookOnTouchEvent(ev: MotionEvent?): Boolean {
                ev?.let { event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        action?.let {
                            if (autoReset && enableReset) {
                                if (mResetView == null || eventInViewArea(mResetView, event)) {
                                    it.run()
                                    LogTracker.log("$tag#hookOnTouchEvent", "hook ACTION_DOWN")
                                }
                            }
                        }
                    }
                }
                return true
            }

            override fun enableReset(enable: Boolean) {
                enableReset = enable
            }

            override fun setResetCallback(runnable: Runnable) {
                action = runnable
            }

            fun eventInViewArea(view: View, ev: MotionEvent?): Boolean {
                ev?.let {
                    val x: Float = ev.rawX
                    val y: Float = ev.rawY
                    val rect = Rect()
                    view.getGlobalVisibleRect(rect)
                    return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom
                }
                return false
            }
        }
        mInputAction = object : IInputAction {

            private val mainInputView: EditText = mEditText!!
            private var mainFocusIndex = -1

            private val secondaryViews = WeakHashMap<Int, EditText>()
            private var secondaryViewRequestFocus = false
            private var onClickListener: OnClickListener? = null
            private var realEditViewAttach: Boolean = true
            private var curPanelId = Int.MAX_VALUE
            private var checkoutInputRight = true
            private val requestFocusRunnable = RequestFocusRunnable()
            private val resetSelectionRunnable = ResetSelectionRunnable()

            inner class RequestFocusRunnable : Runnable {
                var resetSelection = false
                override fun run() {
                    mainInputView.requestFocus()
                    if (resetSelection) {
                        mainInputView.postDelayed(resetSelectionRunnable, 100)
                    } else {
                        checkoutInputRight = false
                    }
                }
            }

            inner class ResetSelectionRunnable : Runnable {
                override fun run() {
                    if (mainFocusIndex != -1 && mainFocusIndex <= mainInputView.text.length) {
                        mainInputView.setSelection(mainFocusIndex)
                    } else {
                        mainInputView.setSelection(mainInputView.text.length)
                    }
                    checkoutInputRight = false
                }
            }

            init {
                mainInputView.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (realEditViewAttach && mainInputView.hasFocus() && !checkoutInputRight) {
                            mainFocusIndex = mainInputView.selectionStart
                        }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
                mainInputView.accessibilityDelegate = object : View.AccessibilityDelegate() {
                    override fun sendAccessibilityEvent(host: View?, eventType: Int) {
                        super.sendAccessibilityEvent(host, eventType)
                        if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                            if (realEditViewAttach && mainInputView.hasFocus() && !checkoutInputRight) {
                                mainFocusIndex = mainInputView.selectionStart
                            }
                        }
                    }
                }
            }

            private fun giveUpFocusRight() {
                checkoutInputRight = true
                realEditViewAttach = false
                if (mPixelInputView.hasFocus()) {
                    mPixelInputView.clearFocus()
                }
                checkoutInputRight = false
            }

            override fun getFullScreenPixelInputView(): EditText {
                mPixelInputView.background = null
                return mPixelInputView
            }

            override fun recycler() {
                mainInputView.removeCallbacks(requestFocusRunnable)
                mainInputView.removeCallbacks(resetSelectionRunnable)
            }

            /**
             * 对于全屏模式：
             * 1. 如果拉起的是输入法，焦点权利归属到 realEditView，重新获取焦点及重置光标
             * 2. 如果拉起的面板且面板高度大于输入法，焦点权利也归属到 realEditView，
             * 3. 其他比如隐藏面板或者面板比输入法低，焦点权利让给 mPixelInputView
             * 非全屏模式下：
             * 所有焦点权利都在 realEditView
             */
            override fun updateFullScreenParams(isFullScreen: Boolean, panelId: Int, panelHeight: Int) {
                if (panelId == curPanelId) {
                    return
                }
                curPanelId = panelId
                if (secondaryViewRequestFocus) {
                    secondaryViewRequestFocus = false
                    return
                }
                //解决部分手机抢占焦点，可能出现重复显示/隐藏输入法
                mPixelInputView.visibility = if (isFullScreen) VISIBLE else GONE
                if (mPixelInputView.parent is ViewGroup) {
                    (mPixelInputView.parent as ViewGroup).isFocusableInTouchMode = true
                    (mPixelInputView.parent as ViewGroup).isFocusable = true
                }
                if (isFullScreen) {
                    if (panelId == Constants.PANEL_KEYBOARD) {
                        retrieveFocusRight(requestFocus = true, resetSelection = true)
                    } else if (panelId != Constants.PANEL_NONE && !PanelUtil.isPanelHeightBelowKeyboardHeight(context, panelHeight)) {
                        retrieveFocusRight(requestFocus = false, resetSelection = true)
                    } else {
                        giveUpFocusRight()
                    }
                } else {
                    retrieveFocusRight()
                }
            }

            override fun addSecondaryInputView(editText: EditText) {
                val key = editText.hashCode()
                if (!secondaryViews.containsKey(key)) {
                    editText.setOnFocusChangeListener { v, hasFocus ->
                        secondaryViewRequestFocus = hasFocus
                    }
                    secondaryViews[key] = editText
                }
            }

            override fun removeSecondaryInputView(editText: EditText) {
                val key = editText.hashCode()
                if (secondaryViews.containsKey(key)) {
                    secondaryViews.remove(key)
                }
            }

            private fun retrieveFocusRight(requestFocus: Boolean = false, resetSelection: Boolean = false) {
                checkoutInputRight = true
                realEditViewAttach = true
                if (mPixelInputView.hasFocus()) {
                    mPixelInputView.clearFocus()
                }
                recycler()
                if (requestFocus) {
                    requestFocusRunnable.resetSelection = resetSelection
                    mainInputView.postDelayed(requestFocusRunnable, 200L)
                } else {
                    if (resetSelection) {
                        resetSelectionRunnable.run()
                    } else {
                        checkoutInputRight = false
                    }
                }
            }

            override fun setEditTextClickListener(l: OnClickListener) {
                onClickListener = l
                mainInputView.setOnClickListener { v ->
                    if (realEditViewAttach) {
                        onClickListener?.onClick(v)
                    } else {
                        mPixelInputView.requestFocus()
                    }
                }
            }

            override fun setEditTextFocusChangeListener(l: OnFocusChangeListener) {
                mainInputView.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        if (realEditViewAttach) {
                            l.onFocusChange(v, hasFocus)
                        } else {
                            mPixelInputView.requestFocus()
                        }
                    }
                }
                mPixelInputView.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        l.onFocusChange(v, hasFocus)
                    }
                }
            }

            override fun hideKeyboard(isKeyboardShowing: Boolean, clearFocus: Boolean) {
                val targetView = if (realEditViewAttach) mainInputView else mPixelInputView
                if (isKeyboardShowing) {
                    PanelUtil.hideKeyboard(context, targetView)
                }
                if (clearFocus) {
                    targetView.clearFocus()
                }
            }

            override fun showKeyboard(): Boolean {
                val targetView = if (realEditViewAttach) mainInputView else mPixelInputView
                return PanelUtil.showKeyboard(context, targetView)
            }

            override fun requestKeyboard() {
                val targetView = if (realEditViewAttach) mainInputView else mPixelInputView
                if (targetView.hasFocus()) {
                    targetView.performClick()
                } else {
                    targetView.requestFocus()
                }
            }
        }
    }


    override fun getInputActionImpl(): IInputAction = mInputAction

    override fun getResetActionImpl(): IResetAction = mResetAction

    override fun findTriggerView(id: Int): View? {
        return mViewGroup.findViewById(id)
    }

    private data class ViewPosition(val id: Int, val l: Int, val t: Int, val r: Int, val b: Int) {
        var changeL: Int = l
        var changeT: Int = t
        var changeR: Int = r
        var changeB: Int = b

        fun hasChange() = changeL != l || changeT != t || changeR != r || changeB != b

        fun change(newL: Int, newT: Int, newR: Int, newB: Int) {
            changeL = newL
            changeT = newT
            changeR = newR
            changeB = newB
        }

        fun reset() {
            changeL = l
            changeT = t
            changeR = r
            changeB = b
        }
    }

    private val map = HashMap<Int, ViewPosition>()

    override fun layoutContainer(l: Int, t: Int, r: Int, b: Int,
                                 contentScrollMeasurers: MutableList<ContentScrollMeasurer>, defaultScrollHeight: Int, canScrollOutsize: Boolean,
                                 reset: Boolean) {
        mViewGroup.layout(l, t, r, b)
        if (!canScrollOutsize) {
            return
        }
        for (contentScrollMeasurer in contentScrollMeasurers) {
            val viewId = contentScrollMeasurer.getScrollViewId()
            if (viewId != -1) {
                val view = (mViewGroup).findViewById<View>(viewId)
                view.let {
                    var viewPosition = map[viewId]
                    if (viewPosition == null) {
                        viewPosition = ViewPosition(viewId, view.left, view.top, view.right, view.bottom)
                        map[viewId] = viewPosition
                    }

                    var willScrollDistance = 0;
                    if (reset) {
                        if (viewPosition.hasChange()) {
                            val viewLeft = viewPosition.l
                            val viewTop = viewPosition.t
                            val viewRight = viewPosition.r
                            var viewBottom = viewPosition.b
                            view.layout(viewLeft, viewTop, viewRight, viewBottom)
                            viewPosition.reset();
                        }
                    } else {
                        willScrollDistance = contentScrollMeasurer.getScrollDistance(defaultScrollHeight)
                        if (willScrollDistance > defaultScrollHeight) {
                            return
                        }
                        if (willScrollDistance < 0) {
                            willScrollDistance = 0
                        }
                        val diffY = defaultScrollHeight - willScrollDistance;
                        viewPosition.change(viewPosition.l, viewPosition.t + diffY, viewPosition.r, viewPosition.b + diffY);
                        view.layout(viewPosition.changeL, viewPosition.changeT, viewPosition.changeR, viewPosition.changeB)
                    }
                    LogTracker.log("${PanelSwitchLayout.TAG}#onLayout", "ContentScrollMeasurer(id $viewId , defaultScrollHeight $defaultScrollHeight , scrollDistance $willScrollDistance reset $reset) origin (l ${viewPosition.l},t ${viewPosition.t},r ${viewPosition.l}, b ${viewPosition.b})")
                    LogTracker.log("${PanelSwitchLayout.TAG}#onLayout", "ContentScrollMeasurer(id $viewId , defaultScrollHeight $defaultScrollHeight , scrollDistance $willScrollDistance reset $reset) layout parent(l $l,t $t,r $r,b $b) self(l ${viewPosition.changeL},t ${viewPosition.changeT},r ${viewPosition.changeR}, b${viewPosition.changeB})")
                }
            }
        }
    }

    override fun changeContainerHeight(targetHeight: Int) {
        val layoutParams = mViewGroup.layoutParams
        if (layoutParams != null && layoutParams.height != targetHeight) {
            layoutParams.height = targetHeight
            mViewGroup.layoutParams = layoutParams
        }
    }

    override fun assertView() {
        if (mEditText == null) {
            throw RuntimeException("ContentContainer should set edit_view to get the editText!")
        }
    }
}