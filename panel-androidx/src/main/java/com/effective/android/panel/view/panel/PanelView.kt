package com.effective.android.panel.view.panel

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.effective.android.panel.R

/**
 * interface, everyPanel should implements
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class PanelView : FrameLayout, IPanelView {
    private var panelLayoutId = 0
    private var triggerViewId = 0
    private var isToggle = true

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
        isToggle = typedArray.getBoolean(R.styleable.PanelView_panel_toggle, isToggle)
        typedArray.recycle()
    }

    override fun getBindingTriggerViewId(): Int = triggerViewId

    override fun isTriggerViewCanToggle(): Boolean = isToggle

    override fun isShowing(): Boolean = isShown

    override fun assertView() {
        if (panelLayoutId == -1 || triggerViewId == -1) {
            throw RuntimeException("PanelView -- you must set 'panel_layout' and panel_trigger by Integer id")
        }
        if (childCount > 0) {
            throw RuntimeException("PanelView -- you can't have any child!")
        }
        //默认实现 FrameLayout 恒为false，这里只是强调申明而已，可以不写。
        if (this !is View) {
            throw RuntimeException("PanelView -- should be a view!")
        }
        LayoutInflater.from(context).inflate(panelLayoutId, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        assertView()
    }

}