package com.effective.android.panel.interfaces;

/**
 *     --------------------
 *    | PanelSwitchLayout  |
 *    |  ----------------  |
 *    | |                | |
 *    | |ContentContainer| |
 *    | |                | |
 *    |  ----------------  |
 *    |  ----------------  |
 *    | | PanelContainer | |
 *    |  ----------------  |
 *     --------------------
 * There are some rules that must be processed:
 *
 * 1. {@link com.effective.android.panel.view.PanelSwitchLayout} must have only two children
 * {@link com.effective.android.panel.view.content.IContentContainer} and {@link com.effective.android.panel.view.PanelContainer}
 *
 * 2. {@link com.effective.android.panel.view.content.IContentContainer} must set "edit_view" value to provide {@link android.widget.EditText}
 *
 * 3. {@link com.effective.android.panel.view.PanelContainer} has some Children that are {@link com.effective.android.panel.view.PanelView}
 * {@link com.effective.android.panel.view.PanelView} must set "panel_layout" value to provide panelView and set "panel_trigger"  value to
 * specify layout for click to checkout panelView
 *
 * Created by yummyLau on 18-7-10
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public interface ViewAssertion {

    void assertView();
}
