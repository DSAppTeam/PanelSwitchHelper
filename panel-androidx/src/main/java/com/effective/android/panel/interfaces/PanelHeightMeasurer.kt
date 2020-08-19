package com.effective.android.panel.interfaces

interface PanelHeightMeasurer{
    fun synchronizeKeyboardHeight() : Boolean
    fun getTargetPanelDefaultHeight(): Int
    fun getPanelTriggerId(): Int
}

private typealias GetTargetPanelDefaultHeight = () -> Int
private typealias GetPanelId = () -> Int
private typealias SynchronizeKeyboardHeight = () -> Boolean

class PanelHeightMeasurerBuilder : PanelHeightMeasurer {

    private var getPanelDefaultHeight: GetTargetPanelDefaultHeight? = null
    private var getPanelId: GetPanelId? = null
    private var synchronizeKeyboardHeight: SynchronizeKeyboardHeight? = null

    override fun getTargetPanelDefaultHeight(): Int = getPanelDefaultHeight?.invoke() ?: 0

    override fun getPanelTriggerId(): Int = getPanelId?.invoke() ?: -1

    override fun synchronizeKeyboardHeight(): Boolean = synchronizeKeyboardHeight?.invoke() ?: true

    fun getTargetPanelDefaultHeight(getPanelDefaultHeight: GetTargetPanelDefaultHeight) {
        this.getPanelDefaultHeight = getPanelDefaultHeight
    }

    fun getPanelTriggerId(getPanelId: GetPanelId) {
        this.getPanelId = getPanelId
    }

    fun synchronizeKeyboardHeight(synchronizeKeyboardHeight: SynchronizeKeyboardHeight) {
        this.synchronizeKeyboardHeight = synchronizeKeyboardHeight
    }
}