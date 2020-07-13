package com.effective.android.panel.interfaces

interface PanelHeightMeasurer{
    fun getTargetPanelDefaultHeight(): Int
    fun getPanelTriggerId(): Int
}

private typealias GetTargetPanelDefaultHeight = () -> Int
private typealias GetPanelId = () -> Int

class PanelHeightMeasurerBuilder : PanelHeightMeasurer {

    private var getPanelDefaultHeight: GetTargetPanelDefaultHeight? = null
    private var getPanelId: GetPanelId? = null

    override fun getTargetPanelDefaultHeight(): Int = getPanelDefaultHeight?.invoke() ?: 0

    override fun getPanelTriggerId(): Int = getPanelId?.invoke() ?: -1

    fun getTargetPanelDefaultHeight(getPanelDefaultHeight: GetTargetPanelDefaultHeight) {
        this.getPanelDefaultHeight = getPanelDefaultHeight
    }

    fun getPanelTriggerId(getPanelId: GetPanelId) {
        this.getPanelId = getPanelId
    }
}