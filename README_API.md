### API Directory

1. Frame view structure
2. Content area
3. Panel area
4. PanelSwitchHelper construction and use details

#### Frame view structure

The framework defines the PanelSwitchLayout container, the content of which consists of the content area and the panel area.

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/api/arch.jpg" width = "696" height = "703"/>

Based on the above structure, the frame supports the following two modes

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/api/mode.jpg" width = "662" height = "444" align=center />


#### Content panel

The framework provides a variety of Container containers

* `LinearContentContainer` can be used as a function of `LinearLayout`
* `RelativeContentContainer` can be used as a function of `RelativeLayout`
* `FrameContentContainer` can be used as a function of `FrameLayout`

All the provided Containers are handled by the `ContentContainerImpl` object proxy that implements the `IContentContainer` interface. Developers can use `ContentContainerImpl` to imitate various Containers provided by the framework to implement custom Containers. `CusContentContainer` in Demo is an example of implementation based on constrained layout.

Examples can refer to Demo:

* **API-content container (linear layout)**
* **API-content container (relative layout)**
* **API-content container (frame layout)**
* **API-content container (custom layout)**

During development, you may encounter: "In a certain scenario, you want to click the Container area to hide the displayed panel. It may be an input method (the input method is also a kind of annual version), or it may be a business panel."
The framework is based on these scenarios, and each container provides an API to automatically hide the panel. For details, see `ResetActivity` in the Demo.

The function of automatically hiding the panel is defined in the extended attributes of the container, including

* `auto_reset_enable` indicates whether to support clicking the hidden panel in the content area, which is opened by default. When open, when there is no consumption event in the subview in the area, the event will be consumed by default and hidden automatically.
* `auto_reset_area`, if and only if auto_reset_enable is true, specify a view id, which is the consumption event limited area of ​​`auto_reset_enable`.

    1. For example, in scenario 1, a blank transparent view is specified, and the panel will be automatically hidden only when there is no consumption event in the view;
    2. For example, in scenario two, if the recyclerview of the list is specified, the panel will be hidden automatically when there is no consumption event in the recyclerview;
    3. For Scenario 3 and Scenario 2, it is obviously difficult to not consume events during recyclerview. If the holder is clicked (such as a chat item), it should be consumed normally. If you click the blank in recyclerview, the recyclerview will also consume by default because it needs to slide.

    In order to solve this kind of lower layer should consume the click sliding event, and the upper container should get the click and hide it automatically. `HookActionUpRecyclerView` is the demo of the scene. It is necessary to return ACTION_UP after the consumption of the lower layer to false to give the upper layer a chance to deal with it. The implementation in `ContentContainerImpl` reserves this possibility for handling this complex scenario.

Examples can refer to Demo:

* **API-click on the content container to collapse the panel (default processing)**
* **API-click on blank View Collapse panel**
* **API-click native Recyclerview to collapse the panel**
* **API-click to customize RecyclerView to collapse the panel**
* **API-Close the click content container to collapse the panel**


#### Functional Area

The panel area is a `FrameLayout` container, and each function panel can be stored inside.

```
<!-- Panel area, can only contain PanelView-->
<com.effective.android.panel.view.panel.PanelContainer
    android:id="@+id/panel_container"
    android:layout_width="match_parent"
    android:background="@color/common_page_bg_color"
    android:layout_height="wrap_content">

<!-- Default PanelView -->
    <com.effective.android.panel.view.panel.PanelView
        android:id="@+id/panel_emotion"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:panel_layout="@layout/panel_emotion_layout"
        app:panel_trigger="@id/emotion_btn" />

    <!-- In addition to using the PanelView provided by the framework, you can also use a custom Panel -->
    <com.example.demo.scene.api.CusPanelView
        android:id="@+id/panel_addition"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:cus_panel_trigger="@id/add_btn"
        app:cus_panel_toggle="true"/>

</com.effective.android.panel.view.panel.PanelContainer>
```

The framework provides `PanelView` by default to define a function panel, which defines multiple attributes:

* `panel_trigger` users switch to this panel when they click the View corresponding to this ID
* `panel_layout `is used to extend the layout of the panel, the function is similar to the <include> tag
* `panel_trigger` is used to switch back to the input method when the user clicks the View corresponding to panel_trigger again when the panel is displayed

At the same time, he provides the `IPanelView` interface for autonomous implementation of `PanelView`. For an example, please refer to Demo: **API-Custom PanelView Container**

#### PanelSwitchHelper construction and use details

You can use PanelSwitchHelper in Activity/Fragment/Dialog/DialogFragment/PopupWindow, etc., the following code

```
PanelSwitchHelper mHelper = new PanelSwitchHelper.Builder(this)
           .addKeyboardStateListener {
                onKeyboardChange {
                    //Optional implementation, monitor input method changes
                }
            }
            .addEditTextFocusChangeListener {
                onFocusChange {_, hasFocus ->
                    //Optional implementation, monitor input box focus changes
                }
            }
            .addViewClickListener {
                onClickBefore {
                   //Optional implementation, monitor the click of trigger
                }
            }
            .addPanelChangeListener {
                onKeyboard {
                  //Optional implementation, input method display callback
                }
                onNone {
                    //Optional implementation, default state callback
                }
                onPanel {
                   //Optional implementation, panel display callback
                }
                onPanelSizeChange {panelView, _, _, _, width, height ->
                    //Optional implementation, dynamic callback of panel height change caused by dynamic adjustment of input method
                }
            }
            .addDistanceMeasurer {      //IM scene is more important
                getUnfilledHeight{
                    //can be calculated dynamically
                    0 
                }
                getViewTag{
                    "recyclerView"
                }
            }
            .logTrack(true)
            .build(true)
```

Among them, the functions that the builder can specify during the build process are as follows:

1. addKeyboardStateListener, used to monitor the state of the input method, you can get the visibility and height of the input method
2. addEditTextFocusChangeListener, monitor the focus change of the specified input source
3. addViewClickListener, monitor trigger and input source clicks, such as clicking the emoticon switch button, input source click, etc.
4. addPanelChangeListener, monitor panel changes, including input method display, panel display, the height change of the input method causes the panel height change callback, hide the panel status
5. addContentScrollMeasurer, used to interfere with the sliding of the frame, such as the child View inside the ContentContainer does not slide along with the parent layout
    * getScrollDistance parameter defaultDistance is the default distance of the frame, the outside can return other distances autonomously
    * getScrollViewId, the id of the subview to be intervened
6. addPanelHeightMeasurer, used to set the default height of the panel, when the frame does not get the height of the input method, the set height is read first, if not, the default height in the frame is used
    * getPanelTriggerId corresponds to the trigger id of the panel
    * getTargetPanelDefaultHeight triggers the default height of the panel
7. Does logTrack output log information
8. Build, return the PanelSwitchHelper object, you can pass the parameter to specify whether to display the input method automatically for the first time, not by default.

The following figure shows the behavior of multiple views in the content area after the soft keyboard/panel is pulled up after intervention through addContentScrollMeasurer

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/api/cus_scroll_content.gif" width = "289" height = "638"/>

The list and the two Views on the right have intervened, and the View on the left has not intervened, see the Demo class `ChatCusContentScrollActivity`

In addition to the functions provided in the above construction process, the following important methods are also provided:

1. setContentScrollOutsideEnable dynamic change mode
2. isContentScrollOutsizeEnable acquisition mode
3. toKeyboardState, switch to input method panel
4. toPanelState, switch to the corresponding function panel
5. resetState, hide all panels
6. hookSystemBackByPanelSwitcher intercepts the return, if the current user presses the return or business return button, the panel is preferentially hidden
7. addSecondaryInputView/removeSecondaryInputView to add an additional input source to drive the input method display
8. setTriggerViewClickInterceptor supports dynamic control of whether TriggerView automatically responds to the "click to trigger the switch panel" behavior, the default is corresponding