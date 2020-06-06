package com.example.demo.scene.api

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.Gravity
import com.effective.R
import com.effective.android.panel.view.panel.IPanelView

class CusPanelView : IPanelView, AppCompatTextView {

    private var triggerViewId = 0
    private var isToggle = true

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr, 0)
    }

    private fun initView(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusPanelView, defStyleAttr, defStyleRes)
        triggerViewId = typedArray.getResourceId(R.styleable.CusPanelView_cus_panel_trigger, -1)
        isToggle = typedArray.getBoolean(R.styleable.CusPanelView_cus_panel_toggle, isToggle)
        typedArray.recycle()
        setBackgroundColor(Color.BLACK)
        text = "自定义面板"
        gravity = Gravity.CENTER
        setTextColor(Color.WHITE)
        textSize = 20f
    }

    override fun getBindingTriggerViewId(): Int = triggerViewId

    override fun isTriggerViewCanToggle(): Boolean = isToggle

    override fun isShowing(): Boolean = isShown

    override fun assertView() {
        if (triggerViewId == -1) {
            throw RuntimeException("PanelView -- you must set 'panel_layout' and panel_trigger by Integer id")
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        assertView()
    }
}