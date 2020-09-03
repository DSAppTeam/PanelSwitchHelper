package com.effective.android.panel.view.panel

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.effective.android.panel.Constants

/**
 * 用于过渡全屏第一次弹出输入法的场景
 * 由于fullScreen 下 resize 无法正常调整，所以需要确保输入焦点在拉起输入法前已经不被阻挡
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class FullScreenTransitionView : FrameLayout, IPanelView {

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context!!, attrs, defStyleAttr) {
        initView(attrs, defStyleAttr, 0)
    }

    @TargetApi(21)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context!!, attrs, defStyleAttr, defStyleRes) {
        initView(attrs, defStyleAttr, defStyleRes)
    }

    private fun initView(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {

    }

    override fun getBindingTriggerViewId(): Int = Constants.PANEL_FULLSCREEN_TRANSITION

    override fun isTriggerViewCanToggle(): Boolean = false

    override fun isShowing(): Boolean = isShown

    override fun assertView() {
        //不需要处理
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        assertView()
    }

}