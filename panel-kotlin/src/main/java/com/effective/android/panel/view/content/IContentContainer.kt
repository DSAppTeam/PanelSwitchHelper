package com.effective.android.panel.view.content

import android.support.annotation.IdRes
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText

interface IContentContainer {
    //容器行为
    fun findTriggerView(@IdRes id: Int): View?

    fun layoutGroup(l: Int, t: Int, r: Int, b: Int)
    fun adjustHeight(targetHeight: Int)
    //empty相关
    fun emptyViewVisible(visible: Boolean)

    fun setEmptyViewClickListener(l: View.OnClickListener)

    //editText相关
    fun getInputText():EditText
    fun setEditTextClickListener(l: View.OnClickListener)
    fun setEditTextFocusChangeListener(l: OnFocusChangeListener)
    fun clearFocusByEditText()
    fun requestFocusByEditText()
    fun editTextHasFocus(): Boolean
    fun preformClickForEditText()
}