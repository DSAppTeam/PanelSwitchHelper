### Update log

* 1.0.1 Support compatible AndroidQ+ focus conflict, support video mode
* 1.0.2 Support WeChat-style sliding list. Collapse the panel while the list responds to sliding events, improving the user experience
* 1.0.3 Fix [issue](https://github.com/YummyLau/PanelSwitchHelper/issues/10) scene issue
* 1.0.4 Added support for Dialog/Fragment/DialogFragment
* 1.0.5 Supports special models such as Huawei/Xiaomi that support dynamic navigation bar hiding
* 1.1.0 Pursuing the ultimate switching experience
    * Supports animation blessing in the switching process, the effect is synchronized with the "WeChat chat" scene, but the supported scenes are far more than these (see Demo), and support custom animation speed
    * Optimize the internal switching process of the framework, abandon the old logic implementation, and the new implementation uses custom drawing to switch the interface without worrying about memory leaks
    * Demo adds a custom title bar scene to optimize the video scene experience
* 1.1.1 Supports special scenes such as full screen/bang screen/drop screen
    * Optimize internal calculation of effective area height, compatible with special scenes
    * Eliminate the call to bindPanelSwitchLayout api, and customize the logic inside the framework
    * Demo adds complex IM interaction scenarios
* 1.1.2 Added content sliding mode/content fixed mode dynamic switching api
    * Optimize hidden panel logic to improve animation fluency
    * Added content sliding mode, the content area can dynamically slide to the outside of the interface, class adjustPan, enabled by default
    * Added content fixed mode, content area dynamically adjust drawing area, class adjustResize
    * Solve the problem of being slid outside due to too little content in IM scenarios, support dynamic switching mode, optimize experience
* 1.1.3 Compatible with the use requirements of Google channel non-public SDK-API, optimize the fixed mode drawing implementation
* 1.2.0 kotlin version/new content area container that supports multiple layout types
    * Panel is adjusted to kotlin language implementation, fully compatible with existing functions and Java, and supports DSL
    * Added content area container, default provides linear/relative/frame layout, supports custom content area container
    * Demo adds kotlin to use constraint layout to implement custom containers, and adds 4 different layout container scenarios
* 1.2.2 Fix known defects and optimize
    * Merged pr to fix emptyView reference error
    * Optimize the smoothness of switching in fixed mode
* 1.2.3 compatible with android pad models
* 1.3.0 Support auto hide panel, open custom panel, optimization adjustment
    * Added auto_reset_enable and auto_reset_area api to open auto hide panel and abandon EmptyView
    * Add IPanelView interface, externally can realize PanelView independently, more flexible
    * Optimized animation in sliding mode

    `1.3.0` is an important version, it is recommended to upgrade, and pay attention to the following Api changes

    * EmptyView removed, please refer to Demo How to realize hidden panel more elegantly
    * Panel class specification naming, with native Linear/Relative/Frame as a prefix, it is easier to distinguish
    * PanelView migrated to panel package

* 1.3.1 supports using the bottom system layout to capture a copy of the user's gesture. The extra system view at the bottom of the interface in this part of the layout may cause input method calculations to be biased. Rice and other samples.
* 1.3.2 supports xml layout preview, optimizes animation, and solves the problem that the background of the switch panel is visible when there is a background in the presentation
    * 1.3.2.1 Compatible with projects that use the autoSize library, to solve the problem that the height of the input method may be incorrectly calculated due to the height of the status bar
* 1.3.3 Optimize the experience and fix known problems
     * Fixed the problem that window may cause fragment memory leak in multiple fragment scenarios
     * Remove fixed mode, remove `contentCanScrollOutside` api, slide mode to achieve high performance sliding, remove bang api judgment
     * Added `toPanelState`api for externally pulling up the panel
     * Added `addDistanceMeasurer` for external autonomous control of content area sliding, compatible with the problem of data being swept away under a screen in IM scenarios
* 1.3.4 Fix known issues and enhance functions
    * Change api`contentCanScrollOutside`->`contentScrollOutsideEnable` to switch fixed/scroll mode
    * Change api`addDistanceMeasurer`->`addContentScrollMeasurer`, when in sliding mode, can control the content sliding distance independently
    * Add api`addPanelHeightMeasurer` to set the panel height, compatible with scenes without input method
    * Optimized animation and internal logic
* 1.3.5 Compatible with AndroidQ full-screen virtual navigation bar gesture mode, such as MiUI12 devices, etc.
* 1.3.6 extended api
    * Extended `animationSpeed`, supports setting the animation speed, `Standard`, `Slow`, `Slowest`, `Fast`, `Fastest`
    * PanelSwitchHelper added to judge the current panel state, such as `isPanelState()` etc.
    * Optimize the height calculation logic of the full-screen soft keyboard and fix known issues
* 1.3.7 Compatible with Samsung devices such as s8/note8 before OneUI 2 version
* 1.3.8 Extension api
    * Extend the `PanelHeightMeasurer` interface and add the `synchronizeKeyboardHeight` method to control whether the panel height is synchronized with the soft keyboard height
    * Container supports to control the internal multi-layer layout freely sliding
* 1.3.10 Supports full screen mode, supports additional setting of EditText to invoke the panel, and fixes known issues
* 1.3.11 Fixed some MiUI mobile phone horizontal screen status bar pull up the input method abnormal problem, optimize the Log display, unify the prefix of all custom panel container property names
* 1.3.12 compatible with floating keyboard scene
* 1.3.12 Compatible with some mobile phone lock screen input method issues; compatible with Huawei customized tablet issues; optimize the full screen display speed;
* Starting from 1.4.0, only Androidx is supported, non-Androidx is no longer maintained
     * Fix the compatibility issue of some Samsung devices
     * Added `setTriggerViewClickInterceptor` to support dynamic control of whether TriggerView automatically responds to the "click to trigger switch panel" behavior, the default is corresponding
     * Added `DisUtils#setCompatSizeProxy` to support dynamic setting of pixel conversion logic