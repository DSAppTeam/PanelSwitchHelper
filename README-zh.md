[![](https://travis-ci.org/YummyLau/PanelSwitchHelper.svg?branch=master)](https://travis-ci.org/YummyLau/panelSwitchHelper)
![Language](https://img.shields.io/badge/language-java-orange.svg)
![Language](https://img.shields.io/badge/language-kotlin-orange.svg)
![Version](https://img.shields.io/badge/version-1.3.3-blue.svg)
![Size](https://img.shields.io/badge/size-14K-brightgreen.svg)

README: [English Doc](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README.md)

### 框架简介

在开发聊天/视频/直播/信息流界面时，希望用户在输入法与功能面板（比如表情面板/更多选项面板等）的切换过程中保持平滑过渡。调研了市场上主流的app效果及实现，实现了一套兼容多场景的输入面板切换框架。目前该框架已测试使用。

### 框架优势

* 改进传统使用 `Weight+LinearLayout` 动态更改布局高度适配面板的技术方案，支持多种原生 ViewGroup 容器
* 为了追求更平滑的适配效果，当输入法动态调整高度或动态隐藏导航栏时，功能面板能实时适配
* 为了追求更流畅的切换效果，支持滑动模式，滑动会更流畅，同时也支持固定模式
* 丰富的机型适配，适配 全面屏/刘海屏/挖孔屏/Pad 等非常规 Phone 机型
* 丰富的场景支持，支持 Activity/Fragment/Dialog/PopupWindow，应用到聊天/视频/直播/信息流评论等场景
* 丰富的 API 支持，可自定义内容容器，业务面板，灵活控制面板隐藏，灵活控制切换面板速度

更多细节可参考

 * [场景使用介绍](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README_SENCE-zh.md)
 * [API 使用指南](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README_API-zh.md)

Demo 内容如下

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/demo.png" width = "360" height = "790"/>

从二维码下载 Demo

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/v1.3.3_code.png" width = "256" height = "256"/>

默认运行 Androidx 版本，如果需要打开非 Androidx，则在 `Settings.gradle` 中打开 `app` 并在 `gradle.properties` 中关闭 Androidx 配置即可。

### 使用方法

1. 在模块脚本 `build.gradle` 添加库依赖

```
implementation 'com.effective.android:panelSwitchHelper:1.3.3'

//or for androidx
implementation 'com.effective.android:panelSwitchHelper-androidx:1.3.3'
```

2. 在布局文件 Xml 中使用框架提供的容器

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

		 <!-- 不需要被框架处理的布局，可自由布置 -->
        <RelativeLayout
            android:id="@+id/cus_title_bar"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@color/colorPrimary"
            android:visibility="gone">

            <TextView
                android:id="@+id/title"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:gravity="left|center_vertical"
                android:paddingLeft="20dp"
                android:text="自定义标题栏"
                android:textColor="@android:color/white"
                android:textSize="20sp"
                android:textStyle="bold" />
        </RelativeLayout>

        <com.effective.android.panel.view.PanelSwitchLayout
            android:id="@+id/panel_switch_layout"
            android:layout_width="match_parent"
            app:animationSpeed="standard"
            android:layout_height="match_parent"
            android:orientation="vertical">

            <!-- 内容区域 -->
            <!-- linear_edit_view 指定一个 EditText 用于输入 ，必须项-->
            <!-- linear_auto_reset_enable 指定是否 LinearContentContainer 是否能够接受 Touch 事件自动隐藏面板-->
            <!-- linear_auto_reset_area 指定是否 LinearContentContainer 只接受某个 View 区域的 Touch 事件来自定隐藏面板-->
            <com.effective.android.panel.view.content.LinearContentContainer
                android:id="@+id/content_view"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical"
                app:linear_edit_view="@id/edit_text">

                <com.example.demo.scene.chat.view.HookActionUpRecyclerView
                    android:id="@+id/recycler_view"
                    android:layout_width="match_parent"
                    android:layout_height="0dp"
                    android:layout_weight="1"/>

                <LinearLayout
                    android:id="@+id/bottom_action"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:background="@drawable/shape_input_layout"
                    android:gravity="bottom"
                    android:minHeight="@dimen/dp_50"
                    android:orientation="horizontal"
                    android:paddingLeft="@dimen/dp_10"
                    android:paddingRight="@dimen/dp_10"
                    android:paddingBottom="@dimen/dp_7.5">

                    <!-- 更多入口 -->
                    <ImageView
                        android:id="@+id/add_btn"
                        android:layout_width="@dimen/dp_35"
                        android:layout_height="@dimen/dp_35"
                        android:layout_marginRight="@dimen/dp_10"
                        android:src="@drawable/icon_add" />

                    <!-- 输入入口 -->
                    <EditText
                        android:id="@+id/edit_text"
                        android:layout_width="0dp"
                        android:layout_height="wrap_content"
                        android:layout_marginEnd="@dimen/dp_10"
                        android:layout_marginRight="@dimen/dp_10"
                        android:layout_weight="1"
                        android:background="@drawable/selector_edit_focus"
                        android:imeOptions="actionSearch"
                        android:maxLines="5"
                        android:minHeight="@dimen/dp_35"
                        android:paddingLeft="@dimen/dp_3"
                        android:paddingTop="@dimen/dp_7.5"
                        android:paddingRight="@dimen/dp_3"
                        android:paddingBottom="@dimen/dp_3"
                        android:textCursorDrawable="@drawable/shape_edit_cursor"
                        android:textSize="@dimen/sp_16" />

                    <LinearLayout
                        android:layout_width="wrap_content"
                        android:layout_height="@dimen/dp_35"
                        android:orientation="horizontal">

                        <!-- 表情入口 -->
                        <ImageView
                            android:id="@+id/emotion_btn"
                            android:layout_width="@dimen/dp_35"
                            android:layout_height="@dimen/dp_35"
                            android:layout_marginEnd="@dimen/dp_10"
                            android:layout_marginRight="@dimen/dp_10"
                            android:src="@drawable/selector_emotion_btn" />

                        <TextView
                            android:id="@+id/send"
                            android:layout_width="@dimen/dp_50"
                            android:layout_height="@dimen/dp_35"
                            android:background="@drawable/selector_send_btn"
                            android:gravity="center"
                            android:text="@string/send"
                            android:textColor="@color/color_send_btn"
                            android:textSize="@dimen/sp_15" />
                    </LinearLayout>

                </LinearLayout>

            </com.effective.android.panel.view.content.LinearContentContainer>


            <!-- 面板区域，仅能包含PanelView-->
            <com.effective.android.panel.view.panel.PanelContainer
                android:id="@+id/panel_container"
                android:layout_width="match_parent"
                android:background="@color/common_page_bg_color"
                android:layout_height="wrap_content">

                <!-- 每一项面板 -->
                <!-- panel_layout 用于指定面板该 ID 对应的布局 ，必须项-->
                <!-- panel_trigger 用于用户点击该 ID 对应的 View 时切换到该面板 -->
                <!-- panel_toggle  用于当该面板显示时 ，用户再次点击 panel_trigger 对应的 View 时是否回切输入法-->
                <com.effective.android.panel.view.panel.PanelView
                    android:id="@+id/panel_emotion"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    app:panel_layout="@layout/panel_emotion_layout"
                    app:panel_trigger="@id/emotion_btn" />

                <!-- 除了使用框架提供的 PanelView，也可以使用自定义 Panel -->
                <com.example.demo.scene.api.CusPanelView
                    android:id="@+id/panel_addition"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    app:cus_panel_trigger="@id/add_btn"
                    app:cus_panel_toggle="true"/>

            </com.effective.android.panel.view.panel.PanelContainer>
        </com.effective.android.panel.view.PanelSwitchLayout>

    </LinearLayout>
</layout>
```


3. 初始化 PanelSwitchHelper 对象，框架会自动收集布局信息。同时在返回键会调时拦截处理即可。

```
	//Activity 场景，在 onStart 方法初始化，其他如 Fragment/Dialog/PopupWindow 参考 Demo
   private PanelSwitchHelper mHelper;

   @Override
   protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                   .addKeyboardStateListener {
                        onKeyboardChange {
                            //可选实现，监听输入法变化
                        }
                    }
                    .addEditTextFocusChangeListener {
                        onFocusChange { _, hasFocus ->
								 //可选实现，监听输入框焦点变化
                        }
                    }
                    .addViewClickListener {
                        onClickBefore {
 								//可选实现，监听触发器的点击
                        }
                    }
                    .addPanelChangeListener {
                        onKeyboard {
 								//可选实现，输入法显示回调
                        }
                        onNone {
 								//可选实现，默认状态回调
                        }
                        onPanel {
 								//可选实现，面板显示回调
                        }
                        onPanelSizeChange { panelView, _, _, _, width, height ->
 								//可选实现，输入法动态调整时引起的面板高度变化动态回调
                        }
                    }
                    .addDistanceMeasurer {      //用于视频IM场景下如果数据还不到一屏，则动态控制滑动距离
                        getUnfilledHeight{
                            //业务可动态计算
                            0 
                        }
                        getViewTag{
                            "recyclerView"
                        }
                    }
                    .logTrack(true)                   //可选，默认false，是否开启log信息输出
                    .build(true)			          //可选，默认false，是否默认打开输入法
        }
    }


   @Override
   public void onBackPressed() {
   		 //用户按下返回键的时候，如果显示面板，则需要隐藏
        if (mHelper != null && mHelper.hookSystemBackForHindPanel()) {
            return;
        }
        super.onBackPressed();
   }

```

### 版本更新

* 1.0.1(2019-07-08) 支持兼容AndroidQ+焦点冲突，支持视频模式
* 1.0.2(2019-11-05) 支持微信式滑动列表收起面板同时列表响应滑动事件，提升用户体验
* 1.0.3(2019-11-06) 修复 [issue](https://github.com/YummyLau/PanelSwitchHelper/issues/10) 场景问题
* 1.0.4(2019-11-18) 新增支持 Dialog/Fragment/DialogFragment
* 1.0.5(2019-11-26) 支持适配华为/小米等支持动态导航栏隐藏的特殊机型
* 1.1.0(2020-03-18) 追求极致的切换体验
	* 支持切换流程中动画加持，效果同步“微信聊天”场景，但支持的场景远远不止这些（见Demo），同时支持自定义动画速度
	* 优化框架内部切换流程，摈弃旧逻辑实现，新实现通过自定义绘制切换界面，无需担心内存泄漏
	* Demo新增自定义标题栏场景，优化视频场景体验
* 1.1.1(2020-03-29) 支持适配全面屏/刘海屏/水滴屏幕等特殊场景
	* 优化内部计算有效面积高度，兼容特殊场景
	* 免去 bindPanelSwitchLayout api 调用，框架内部自定完成逻辑
	* Demo新增复杂IM交互场景
* 1.1.2(2020-04-20) 新增内容滑动模式/内容固定模式动态切换api
	* 优化隐藏面板逻辑，提升动画流畅性
	* 新增内容滑动模式，内容区域可动态滑动到界面外部，类 adjustPan，默认开启
	* 新增内容固定模式，内容区域动态调整绘制区域，类 adjustResize
	* 解决 IM 场景下可能因为内容过少而被滑动外部的问题，支持动态切换模式，优化体验
* 1.1.3(2020-04-27) 兼容谷歌渠道非公开SDK-API的使用要求，优化固定模式的绘制实现
* 1.2.0(2020-05-08) kotlin版本/新增支持多种布局类型的内容区域容器
	* panel 调整为 kotlin 语言实现，完全兼容现有功能及 Java，支持 DSL
	* 新增内容区域容器，默认提供线性/相对/帧布局，支持实现自定义内容区域容器
	* Demo 新增 kotlin 使用约束布局实现自定义容器，新增 4 种不同布局的容器场景
* 1.2.2(2020-05-17) 修复已知缺陷及优化
    * 合并 pr 修复 emptyView 引用错误问题
    * 优化固定模式下切换的流畅性
* 1.2.3(2020-05-24) 兼容 android pad 机型
* 1.3.0(2020-06-07) 支持自动隐藏面板，开放自定义面板，优化性调整
    * 新增 auto_reset_enable 及 auto_reset_area api 用于开放自动隐藏面板，摒弃 EmptyView
    * 新增 IPanelView 接口，外部可自主实现 PanelView，更灵活
    * 优化滑动模式下的动画实

    `1.3.0` 为重要版本，建议升级，同时注意以下 Api 更改
        * EmptyView 移除，可参考 Demo 如何更优雅实现隐藏面板
        * 面板类规范命名，已原生 Linear/Relative/Frame 为前缀，更容易区分
        * PanelView 迁移到 panel 包
* 1.3.1(2020-06-12) 支持适配采用底部系统布局来捕获用户手势的机型，这部分机型在界面底部多出的系统view可能导致输入法计算有偏差。比如 Findx，红米等机型。
* 1.3.2(2020-06-27) 支持xml布局预览，优化动画，解决Demo存在背景时切换面板背景可见的问题
    * 1.3.2.1(2020-06-30) 兼容使用autoSize库的项目，解决可能因为状态栏高度被修改导致输入法高度计算错误的问题
* 1.3.3(2020-07-09) 优化体验，修复已知问题
    * 修复多fragment场景下 window 可能引起 fragment 内存泄漏问题
    * 移除固定模式，移除 `contentCanScrollOutside` api，滑动模式实现高性能滑动，移除刘海api判断
    * 新增 `toPanelState`api 用于外部拉起面板
    * 新增 `addDistanceMeasurer` 用于外部自主控制内容区域滑动，兼容 IM 场景下未满一屏数据被滑走的问题


### 期望

编写该项目只是希望能提高日常开发的效率，专注于处理业务。如果更好的做法或者意见建议，欢迎写信到 yummyl.lau@gmail.com 。

也可以添加微信，进反馈群讨论反馈。

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/qr_code.jpg" width = "385" height = "385"/>

如果框架对你有帮助，可安利给身边的伙伴，每一个 star 都是对框架付出的肯定。




