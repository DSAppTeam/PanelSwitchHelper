package com.effective.android.panel.view

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.effective.android.panel.R
import com.effective.android.panel.interfaces.ViewAssertion

/**
 * interface, everyPanel should implements
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class PanelView : FrameLayout, ViewAssertion {
    private var panelLayoutId = 0
    var triggerViewId = 0
        private set
    var isToggle = false
        private set

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context!!, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr, 0)
    }

    @TargetApi(21)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context!!, attrs, defStyleAttr, defStyleRes) {
        initView(attrs, defStyleAttr, defStyleRes)
    }

    private fun initView(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PanelView, defStyleAttr, 0)
        panelLayoutId = typedArray.getResourceId(R.styleable.PanelView_panel_layout, -1)
        triggerViewId = typedArray.getResourceId(R.styleable.PanelView_panel_trigger, -1)
        isToggle = typedArray.getBoolean(R.styleable.PanelView_panel_toggle, true)
        typedArray.recycle()
    }

    override fun assertView() {
        if (panelLayoutId == -1 || triggerViewId == -1) {
            throw RuntimeException("PanelView -- you must set 'panel_layout' and panel_trigger by Integer id")
        }
        if (childCount > 0) {
            throw RuntimeException("PanelView -- you can't have any child!")
        }
        LayoutInflater.from(context).inflate(panelLayoutId, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        assertView()
    }

}