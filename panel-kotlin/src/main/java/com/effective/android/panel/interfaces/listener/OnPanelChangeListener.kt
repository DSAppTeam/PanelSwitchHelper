package com.effective.android.panel.interfaces.listener
import com.effective.android.panel.view.panel.IPanelView

/**
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 * update 2020/05/08 支持 dsl
 * update 2020/06/01 不再直接回调可空的panelView，IPanelView 提供更开放的能力
 */
interface OnPanelChangeListener {
    fun onKeyboard()
    fun onNone()
    fun onPanel(panel: IPanelView?)
    fun onPanelSizeChange(panel: IPanelView?, portrait: Boolean, oldWidth: Int, oldHeight: Int, width: Int, height: Int)
}

private typealias OnKeyboard = () -> Unit
private typealias OnNone = () -> Unit
private typealias OnPanel = (view: IPanelView?) -> Unit
private typealias OnPanelSizeChange = (panelView: IPanelView?, portrait: Boolean, oldWidth: Int, oldHeight: Int, width: Int, height: Int) -> Unit

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

    override fun onPanel(panel: IPanelView?) {
        onPanel?.invoke(panel)
    }

    override fun onPanelSizeChange(panel: IPanelView?, portrait: Boolean, oldWidth: Int, oldHeight: Int, width: Int, height: Int) {
        onPanelSizeChange?.invoke(panel, portrait, oldWidth, oldHeight, width, height)
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