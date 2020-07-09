package com.effective.android.panel.view.content

import android.support.annotation.IdRes
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import com.effective.android.panel.interfaces.ViewDistanceMeasurer

interface IContentContainer {
    //容器行为
    fun findTriggerView(@IdRes id: Int): View?
    fun layoutContainer(l: Int, t: Int, r: Int, b: Int,viewDistanceMeasurers: MutableList<ViewDistanceMeasurer>)
    fun changeContainerHeight(targetHeight: Int)

    //输入相关
    fun getInputActionImpl(): IInputAction

    //隐藏相关
    fun getResetActionImpl(): IResetAction
}

interface IInputAction {
    fun getInputText(): EditText
    fun setEditTextClickListener(l: View.OnClickListener)
    fun setEditTextFocusChangeListener(l: OnFocusChangeListener)
    fun clearFocusByEditText()
    fun requestFocusByEditText()
    fun editTextHasFocus(): Boolean
    fun preformClickForEditText()
}

interface IResetAction {
    fun enableReset(enable: Boolean)
    fun setResetCallback(runnable: Runnable)
    fun hookDispatchTouchEvent(ev: MotionEvent?, consume: Boolean): Boolean
    fun hookOnTouchEvent(ev: MotionEvent?): Boolean
}