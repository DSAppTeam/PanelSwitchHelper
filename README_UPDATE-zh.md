### 更新日志

* 1.0.1 支持兼容AndroidQ+焦点冲突，支持视频模式
* 1.0.2 支持微信式滑动列表收起面板同时列表响应滑动事件，提升用户体验
* 1.0.3 修复 [issue](https://github.com/YummyLau/PanelSwitchHelper/issues/10) 场景问题
* 1.0.4 新增支持 Dialog/Fragment/DialogFragment
* 1.0.5 支持适配华为/小米等支持动态导航栏隐藏的特殊机型
* 1.1.0 追求极致的切换体验
	* 支持切换流程中动画加持，效果同步“微信聊天”场景，但支持的场景远远不止这些（见Demo），同时支持自定义动画速度
	* 优化框架内部切换流程，摈弃旧逻辑实现，新实现通过自定义绘制切换界面，无需担心内存泄漏
	* Demo新增自定义标题栏场景，优化视频场景体验
* 1.1.1 支持适配全面屏/刘海屏/水滴屏幕等特殊场景
	* 优化内部计算有效面积高度，兼容特殊场景
	* 免去 bindPanelSwitchLayout api 调用，框架内部自定完成逻辑
	* Demo新增复杂IM交互场景
* 1.1.2 新增内容滑动模式/内容固定模式动态切换api
	* 优化隐藏面板逻辑，提升动画流畅性
	* 新增内容滑动模式，内容区域可动态滑动到界面外部，类 adjustPan，默认开启
	* 新增内容固定模式，内容区域动态调整绘制区域，类 adjustResize
	* 解决 IM 场景下可能因为内容过少而被滑动外部的问题，支持动态切换模式，优化体验
* 1.1.3 兼容谷歌渠道非公开SDK-API的使用要求，优化固定模式的绘制实现
* 1.2.0 kotlin版本/新增支持多种布局类型的内容区域容器
	* panel 调整为 kotlin 语言实现，完全兼容现有功能及 Java，支持 DSL
	* 新增内容区域容器，默认提供线性/相对/帧布局，支持实现自定义内容区域容器
	* Demo 新增 kotlin 使用约束布局实现自定义容器，新增 4 种不同布局的容器场景
* 1.2.2 修复已知缺陷及优化
    * 合并 pr 修复 emptyView 引用错误问题
    * 优化固定模式下切换的流畅性
* 1.2.3 兼容 android pad 机型
* 1.3.0 支持自动隐藏面板，开放自定义面板，优化性调整
    * 新增 auto_reset_enable 及 auto_reset_area api 用于开放自动隐藏面板，摒弃 EmptyView
    * 新增 IPanelView 接口，外部可自主实现 PanelView，更灵活
    * 优化滑动模式下的动画实

    `1.3.0` 为重要版本，建议升级，同时注意以下 Api 更改
        * EmptyView 移除，可参考 Demo 如何更优雅实现隐藏面板
        * 面板类规范命名，已原生 Linear/Relative/Frame 为前缀，更容易区分
        * PanelView 迁移到 panel 包
* 1.3.1 支持适配采用底部系统布局来捕获用户手势的机型，这部分机型在界面底部多出的系统view可能导致输入法计算有偏差。比如 Findx，红米等机型。
* 1.3.2 支持xml布局预览，优化动画，解决Demo存在背景时切换面板背景可见的问题
    * 1.3.2.1 兼容使用autoSize库的项目，解决可能因为状态栏高度被修改导致输入法高度计算错误的问题
* 1.3.3 优化体验，修复已知问题
    * 修复多fragment场景下 window 可能引起 fragment 内存泄漏问题
    * 新增 `toPanelState`api 用于外部拉起面板
    * 新增 `addDistanceMeasurer` 用于外部自主控制内容区域滑动，兼容 IM 场景下未满一屏数据被滑走的问题
* 1.3.4 修复已知问题，增强功能
    * 更改 api `contentCanScrollOutside` -> `contentScrollOutsideEnable`，用于切换固定/滑动模式
    * 更改 api `addDistanceMeasurer` -> `addContentScrollMeasurer`, 当处于滑动模式时，可自主控制内容滑动距离
    * 新增 api `addPanelHeightMeasurer` 用于设置默认面板高度，兼容未获取输入法场景
    * 优化动画及内部逻辑
* 1.3.5 兼容 AndroidQ 全屏虚拟导航栏手势模式，如 MiUI12 设备等
* 1.3.6 扩展 api
    * 扩展 `animationSpeed`，支持设置动画速度， `standard` 标准速，`slow` 慢速,`slowest` 最慢速,`fast` 快速,`fastest`最快速
    * PanelSwitchHelper 新增判断当前面板状态，如 `isPanelState()` 等
    * 优化全面屏软键盘高度计算逻辑，修复已知问题
* 1.3.7 兼容OneUI 2版本前 s8/note8 等三星设备
* 1.3.8 扩展 api
    * 扩展 `PanelHeightMeasurer` 接口新增 `synchronizeKeyboardHeight` 方法控制面板高度是否同步软键盘高度
    * Container 容器支持控制内部多层嵌套布局自由滑动
* 1.3.10 支持全屏模式，支持额外设置EditText唤起面板，修复已知问题
* 1.3.11 修复部分 MiUI 手机横屏状态栏拉起输入法异常问题，优化 Log 显示，统一所有自定义面板容器属性命名的前缀
* 1.3.12 兼容悬浮键盘场景
* 1.3.13 兼容部分手机锁屏收起输入法问题；兼容华为定制平板问题；优化全屏显示速度；
* 1.4.0 该版本起仅支持 Androidx，非 Androidx 不再维护
    * 修复三星部分设备的兼容问题
    * 新增 `setTriggerViewClickInterceptor` 支持动态控制 TriggerView 是否自动响应"点击触发切换面板"行为，默认相应
    * 新增 `DisUtils#setCompatSizeProxy` 支持动态设置像素转化逻辑
* 1.5.0 支持Android 11键盘过渡动画特性
    * 添加了悬浮弹窗场景的用例。
    * `PanelSwitchLayout` 控件添加了 `enableKeyboardAnimator` 属性，用来控制是否启用Android 11 的键盘动画效果，默认开启。
    * Android 11 键盘过渡动画需要在 SOFT_INPUT_ADJUST_NOTHING 模式下才能生效。
    * 暂时不支持DialogFragment、PopupWindow、悬浮弹窗等场景下Android 11键盘过渡动画效果。
    * 修复已知Bug [issue](https://github.com/DSAppTeam/PanelSwitchHelper/issues)
* 1.5.1 修复已知Bug。
    * 修复ContentScrollMeasurer计算子View高度不准确问题。
    * IContentContainer#layoutContainer()添加了changed参数，用来判断是否需要重新布局。
* 1.5.2 修复已知Bug。
    * 修复Android 11 以上布局偏移量变化后，布局位置没有同步的问题。
* 1.5.3 修复已知Bug。
    * 修复快速滑动列表时，偏移量计算异常的问题。
    * 优化viewPosition位置变化的监听。
* 1.5.4 修复已知Bug，提供兼容方案。
    * 更改 api `enableKeyboardAnimator` -> `android11KeyboardFeature`，用来控制是否启用Android 11 的键盘动画效果，默认开启。
    * 新增 `PanelSwitchLayout`提供两个兼容方法（`softInputHeightCalculatorOnStart`，`softInputHeightCalculatorOnProgress`），针对Android 11以上开启键盘动画特性，高度获取失败时，对外提供兼容方案。
