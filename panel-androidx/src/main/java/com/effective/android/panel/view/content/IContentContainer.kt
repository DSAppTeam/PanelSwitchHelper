package com.effective.android.panel.view.content

import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import androidx.annotation.IdRes
import com.effective.android.panel.interfaces.ContentScrollMeasurer

interface IContentContainer {
    //容器行为
    fun findTriggerView(@IdRes id: Int): View?
    fun layoutContainer(l: Int, t: Int, r: Int, b: Int,
                        contentScrollMeasurer: MutableList<ContentScrollMeasurer>, defaultScrollHeight: Int, canScrollOutsize: Boolean,
                        reset: Boolean, changed: Boolean = false)

    fun translationContainer(contentScrollMeasurers: MutableList<ContentScrollMeasurer>, defaultScrollHeight: Int, contentTranslationY: Float)


    fun changeContainerHeight(targetHeight: Int)

    //输入相关
    fun getInputActionImpl(): IInputAction

    //隐藏相关
    fun getResetActionImpl(): IResetAction
}

interface IInputAction {
    fun addSecondaryInputView(editText: EditText)
    fun removeSecondaryInputView(editText: EditText)
    fun setEditTextClickListener(l: View.OnClickListener)
    fun setEditTextFocusChangeListener(l: OnFocusChangeListener)
    fun requestKeyboard()
    fun hideKeyboard(isKeyboardShowing : Boolean,clearFocus : Boolean)
    fun showKeyboard() : Boolean
    fun getFullScreenPixelInputView(): EditText
    fun updateFullScreenParams(isFullScreen : Boolean, panelId : Int, panelHeight : Int)
    fun recycler()
}

interface IResetAction {
    fun enableReset(enable: Boolean)
    fun setResetCallback(runnable: Runnable)
    fun hookDispatchTouchEvent(ev: MotionEvent?, consume: Boolean): Boolean
    fun hookOnTouchEvent(ev: MotionEvent?): Boolean
}