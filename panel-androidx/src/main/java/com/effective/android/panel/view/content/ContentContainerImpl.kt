package com.effective.android.panel.view.content

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.IdRes
import com.effective.android.panel.LogTracker
import com.effective.android.panel.interfaces.ViewAssertion

/**
 * 内容区域代理
 * --------------------
 * Created by yummyLau on 2020/05/07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class ContentContainerImpl(private val mViewGroup: ViewGroup, private val autoReset: Boolean, @IdRes private val editTextId: Int, @IdRes private val resetId: Int) : IContentContainer, ViewAssertion {
    private val mEditText: EditText? = mViewGroup.findViewById(editTextId)
    private val mResetView: View? = mViewGroup.findViewById(resetId)
    private val mInputAction: IInputAction
    private val mResetAction: IResetAction
    private val tag = ContentContainerImpl::class.java.simpleName

    init {
        assertView()
        var imeOptions = mEditText?.imeOptions
        if (imeOptions != null) {
            imeOptions = imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI
            mEditText?.imeOptions = imeOptions
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

            override fun getInputText(): EditText = mEditText!!

            override fun setEditTextClickListener(l: View.OnClickListener) {
                mEditText!!.setOnClickListener(l)
            }

            override fun setEditTextFocusChangeListener(l: OnFocusChangeListener) {
                mEditText!!.onFocusChangeListener = l
            }

            override fun clearFocusByEditText() {
                mEditText!!.clearFocus()
            }

            override fun requestFocusByEditText() {
                mEditText!!.requestFocus()
            }

            override fun editTextHasFocus(): Boolean {
                return mEditText!!.hasFocus()
            }

            override fun preformClickForEditText() {
                mEditText!!.performClick()
            }
        }
    }


    override fun getInputActionImpl(): IInputAction = mInputAction

    override fun getResetActionImpl(): IResetAction = mResetAction

    override fun findTriggerView(id: Int): View? {
        return mViewGroup.findViewById(id)
    }

    override fun layoutContainer(l: Int, t: Int, r: Int, b: Int) {
        mViewGroup.layout(l, t, r, b)
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