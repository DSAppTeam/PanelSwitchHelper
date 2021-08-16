
### 场景目录
1. 支持动态更改输入法高度/隐藏显示导航栏时适配功能面板
2. 聊天场景（类微信）
3. 视频播放场景（类BiliBili）
4. 电脑直播场景（类虎牙直播）
5. 手机直播场景（类抖音直播）
6. 信息流评论场景（类微信朋友圈）

#### 支持动态更改输入法高度/隐藏显示导航栏时
市面上的很多IM场景，用户更改输入法高度时切换回功能面板，则发生高度不一致，微信也如此。同时还需要适配存在导航栏操作的设备，常见如华为，小米等。为了追求更平滑的切换效果，框架支持动态更改输入法高度时适配功能面板。

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/auto_adjust.gif" width = "270" height = "480"/><img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/compat_navigation.gif" width = "270" height = "480"/>

#### 聊天场景

聊天场景适用于任何 IM 场景，Demo 分别使用了 Activity/Fragment/DialogFragment/PopupWindow/Dialog 来实现，满足你可所用的窗口技术。可参考 Demo 中的聊天场景。

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/chat.gif" width = "289" height = "638"/>

#### 视频播放场景

视频播放效果参考 BiliBili 实现，左图为 BiliBili 效果，右图为 Demo 效果，效果优于 BiliBili 实现。

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/bilibili.gif" width = "289" height = "638"/>   <img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/bilibili_demo.gif" width = "289" height = "638"/>

#### 电脑直播场景

电脑直播效果参考 虎牙直播间 实现，左图为 虎牙直播间 效果，右图为 Demo 效果，效果优于 虎牙 实现。

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/huya.gif" width = "289" height = "638"/>   <img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/huya_demo.gif" width = "289" height = "638"/>


#### 手机直播场景

手机直播效果参考 抖音直播 实现，左图为 抖音直播 效果，右图为 Demo 效果，效果优于 抖音直播 实现。

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/douyin.gif" width = "289" height = "638"/>   <img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/douyin_demo.gif" width = "289" height = "638"/>


#### 信息流评论场景

信息流评论效果参考 微信朋友圈 实现，左图为 微信朋友圈 效果，右图为 Demo 效果，效果与微信朋友圈相同。

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/wechat_feed.gif" width = "289" height = "638"/>   <img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/scene/wechat_feed_demo.gif" width = "289" height = "638"/>

#### 复杂聊天场景

* 自定义各个子View滑动
* 支持模拟多个EditText拉起面板

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/api/cus_scroll_content.gif" width = "289" height = "638"/>


