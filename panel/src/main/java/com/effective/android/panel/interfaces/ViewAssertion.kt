package com.effective.android.panel.interfaces

/**
 * --------------------
 * | PanelSwitchLayout  |
 * |  ----------------  |
 * | |                | |
 * | |ContentContainer| |
 * | |                | |
 * |  ----------------  |
 * |  ----------------  |
 * | | PanelContainer | |
 * |  ----------------  |
 * --------------------
 * There are some rules that must be processed:
 *
 * 1. [com.effective.android.panel.view.PanelSwitchLayout] must have only two children
 * [com.effective.android.panel.view.content.IContentContainer] and [com.effective.android.panel.view.PanelContainer]
 *
 * 2. [com.effective.android.panel.view.content.IContentContainer] must set "edit_view" value to provide [android.widget.EditText]
 *
 * 3. [com.effective.android.panel.view.PanelContainer] has some Children that are [com.effective.android.panel.view.PanelView]
 * [com.effective.android.panel.view.PanelView] must set "panel_layout" value to provide panelView and set "panel_trigger"  value to
 * specify layout for click to checkout panelView
 *
 * Created by yummyLau on 18-7-10
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
interface ViewAssertion {
    fun assertView()
}