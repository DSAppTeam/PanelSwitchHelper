package com.effective.android.panel.view.content

import android.graphics.Rect
import android.support.annotation.IdRes
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.effective.android.panel.interfaces.ViewAssertion

/**
 * 内容区域代理
 * --------------------
 * Created by yummyLau on 2020/05/07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class ContentContainerImpl(private val mViewGroup: ViewGroup, private val canAutoReset: Boolean, @IdRes private val editTextId: Int, @IdRes private val resetId: Int) : IContentContainer, ViewAssertion {
    private val mEditText: EditText? = mViewGroup.findViewById(editTextId)
    private val mResetView: View? = mViewGroup.findViewById(resetId)
    private val mInputAction: IInputAction
    private val mResetAction: IResetAction

    init {
        assertView()
        var imeOptions = mEditText?.imeOptions
        if (imeOptions != null) {
            imeOptions = imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI
            mEditText?.imeOptions = imeOptions
        }
        mResetAction = object : IResetAction {

            private var canReset: Boolean = false
            private var action: Runnable? = null

            override fun hookDispatchTouchEvent(ev: MotionEvent?, consume: Boolean) {
                if (canAutoReset && canReset && mResetView != null && eventInViewArea(mResetView, ev) && !consume) {
                    action?.run()
                }
            }

            override fun hookOnTouchEvent(ev: MotionEvent?) {
                if (canAutoReset && canReset && mResetView == null) {
                    action?.run()
                }
            }

            override fun enableReset(enable: Boolean) {
                canReset = enable
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