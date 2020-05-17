package com.effective.android.panel.view.content

import android.support.annotation.IdRes
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
class ContentContainerImpl(private val mViewGroup: ViewGroup, @IdRes editTextId: Int, @IdRes emptyId: Int) : IContentContainer, ViewAssertion {
    private val mEditText: EditText? = mViewGroup.findViewById(editTextId)
    private val mEmptyView: View? = mViewGroup.findViewById(emptyId)

    init {
        assertView()
        var imeOptions = mEditText?.imeOptions
        if (imeOptions != null) {
            imeOptions = imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI
            mEditText?.imeOptions = imeOptions
        }
    }

    override fun findTriggerView(id: Int): View? {
        return mViewGroup.findViewById(id)
    }

    override fun layoutGroup(l: Int, t: Int, r: Int, b: Int) {
        mViewGroup.layout(l, t, r, b)
    }

    override fun adjustHeight(targetHeight: Int) {
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

    override fun emptyViewVisible(visible: Boolean) {
        if (mEmptyView != null) {
            mEmptyView.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    override fun setEmptyViewClickListener(l: View.OnClickListener) {
        mEmptyView?.setOnClickListener(l)
    }

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