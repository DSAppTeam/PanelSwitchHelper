package com.example.demo.widget

import android.content.Context
import android.support.annotation.IdRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import com.effective.R
import com.effective.android.panel.view.content.ContentContainerImpl
import com.effective.android.panel.view.content.IContentContainer

/**
 * 实现IContentContainer接口，可参考
 * [com.effective.android.panel.view.content.ContentFrameContainer] 等库提供的模版实现基础的container容器
 * demo已约束布局为例子，使用者按需扩展就可以了
 * Created by yummyLau on 2020/05/07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class ContentCusContainer @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), IContentContainer {
    @IdRes
    var editTextId = 0
    @IdRes
    var emptyViewId = 0
    private var contentContainer: ContentContainerImpl? = null
    private fun initView(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ContentCusContainer, defStyleAttr, 0)
        editTextId = typedArray.getResourceId(R.styleable.ContentCusContainer_cus_edit_view, -1)
        emptyViewId = typedArray.getResourceId(R.styleable.ContentCusContainer_cus_empty_view, -1)
        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentContainer = ContentContainerImpl(this, editTextId, emptyViewId)
    }

    override fun layoutGroup(l: Int, t: Int, r: Int, b: Int) {
        contentContainer!!.layoutGroup(l, t, r, b)
    }

    override fun findTriggerView(id: Int): View? {
        return contentContainer!!.findTriggerView(id)
    }

    override fun adjustHeight(targetHeight: Int) {
        contentContainer!!.adjustHeight(targetHeight)
    }

    override fun emptyViewVisible(visible: Boolean) {
        contentContainer!!.emptyViewVisible(visible)
    }

    override fun setEmptyViewClickListener(l: OnClickListener) {
        contentContainer!!.setEmptyViewClickListener(l)
    }

    override fun getEditText(): EditText {
        return contentContainer!!.getEditText()
    }

    override fun setEditTextClickListener(l: OnClickListener) {
        contentContainer!!.setEditTextClickListener(l)
    }

    override fun setEditTextFocusChangeListener(l: OnFocusChangeListener) {
        contentContainer!!.setEditTextFocusChangeListener(l)
    }

    override fun clearFocusByEditText() {
        contentContainer!!.clearFocusByEditText()
    }

    override fun requestFocusByEditText() {
        contentContainer!!.requestFocusByEditText()
    }

    override fun editTextHasFocus(): Boolean {
        return contentContainer!!.editTextHasFocus()
    }

    override fun preformClickForEditText() {
        contentContainer!!.preformClickForEditText()
    }

    init {
        initView(attrs, defStyleAttr, 0)
    }
}