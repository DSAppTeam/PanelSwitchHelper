package com.effective.android.panel.interfaces.listener

import com.effective.android.panel.view.PanelView

/**
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 * update 2020/05/08 支持 dsl
 */
interface OnPanelChangeListener {
    fun onKeyboard()
    fun onNone()
    fun onPanel(view: PanelView?)
    fun onPanelSizeChange(panelView: PanelView?, portrait: Boolean, oldWidth: Int, oldHeight: Int, width: Int, height: Int)
}

private typealias OnKeyboard = () -> Unit
private typealias OnNone = () -> Unit
private typealias OnPanel = (view: PanelView?) -> Unit
private typealias OnPanelSizeChange = (panelView: PanelView?, portrait: Boolean, oldWidth: Int, oldHeight: Int, width: Int, height: Int) -> Unit

class OnPanelChangeListenerBuilder : OnPanelChangeListener {

    private var onKeyboard: OnKeyboard? = null
    private var onNone: OnNone? = null
    private var onPanel: OnPanel? = null
    private var onPanelSizeChange: OnPanelSizeChange? = null

    override fun onKeyboard() {
        onKeyboard?.invoke()
    }

    override fun onNone() {
        onNone?.invoke()
    }

    override fun onPanel(view: PanelView?) {
        onPanel?.invoke(view)
    }

    override fun onPanelSizeChange(panelView: PanelView?, portrait: Boolean, oldWidth: Int, oldHeight: Int, width: Int, height: Int) {
        onPanelSizeChange?.invoke(panelView, portrait, oldWidth, oldHeight, width, height)
    }

    fun onKeyboard(onKeyboard: OnKeyboard) {
        this.onKeyboard = onKeyboard
    }

    fun onNone(onNone: OnNone) {
        this.onNone = onNone
    }

    fun onPanel(onPanel: OnPanel) {
        this.onPanel = onPanel
    }

    fun onPanelSizeChange(onPanelSizeChange: OnPanelSizeChange) {
        this.onPanelSizeChange = onPanelSizeChange
    }
}