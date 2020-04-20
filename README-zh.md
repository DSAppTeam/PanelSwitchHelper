### PanelSwitchHelper
[![](https://travis-ci.org/YummyLau/PanelSwitchHelper.svg?branch=master)](https://travis-ci.org/YummyLau/panelSwitchHelper)
![Language](https://img.shields.io/badge/language-java-orange.svg)
![Version](https://img.shields.io/badge/version-1.1.2-blue.svg)
![Size](https://img.shields.io/badge/size-14K-brightgreen.svg)

README: [English](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README.md) | [中文](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README-zh.md)

#### 版本更新

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

#### 用于做什么

在开发聊天页面时，开发者希望用户在输入法与功能面板（比如表情面板/更多选项面板等）切换过程中保持平滑过渡不闪烁。 参考了市场上主流的社交app效果及实现，综合互联网上的多种实现思路，最总整合成一个模版框架，该模版框架已经过测试使用。

##### 效果展示
* 图一： 核心功能展示
* 图二： 1.0.1 更新支持视频功能
* 图三： 1.0.5 更新支持dialog/fragment/dialogFragment/popupwindow，各种沉浸场景展示

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch.gif" width = "270" height = "480" alt="activity layout"/><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.0.1.gif" width = "270" height = "480" alt="activity layout" /><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.0.4.gif" width = "270" height = "480" alt="activity layout" />

* 图四： 1.1.0 动画效果展示及动态调整导航栏

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.0.gif" width = "270" height = "480" alt="activity layout" />

* 图五至十： 1.1.1 适配全面屏/水滴屏/刘海屏等场景
	* 图五-小米全面屏
	* 图六-华为刘海屏幕，图七-华为刘海屏隐藏顶部区域
	* 图八-小米水滴屏，图九-小米水滴屏隐藏但状态栏在刘海内，图十--小米水滴屏隐藏但状态栏在刘海外

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_全面屏.gif" width = "270" height = "480" alt="activity layout"/><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_刘海屏.gif" width = "270" height = "480" alt="activity layout" /><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_刘海屏_隐藏顶部区域.gif" width = "270" height = "480" alt="activity layout" />

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_水滴屏_不隐藏刘海.gif" width = "270" height = "480" alt="activity layout"/><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_水滴屏_隐藏刘海_状态栏在刘海内.gif" width = "270" height = "480" alt="activity layout" /><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/panel_switch_1.1.1_水滴屏_隐藏刘海_状态栏在刘海外.gif" width = "270" height = "480" alt="activity layout" />

* 图十一：支持 1.1.2 版本动态切换滑动模式

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/panel_switch_1.1.2.jpg" width = "795" height = "532" alt="activity layout" />

##### 实现方法
通过监听 Window 窗口变化来获取输入法高度并动态调整布局来达到平滑过渡切换面板。

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.0.gif" width = "270" height = "480" alt="activity layout" /><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.0_2.gif" width = "270" height = "480" alt="activity layout" />

涉及的核心类有：

* *PanelSwitchLayout* ，即黄色区域 ，仅能包含 *PanelContainer*  和 *PanelSwitchLayout* 并实现一些辅助性功能，1.1.0   核心实现框架功能，支持配置动画速度。
* *ContentContainer* ，即蓝色区域 ，用于存放显示内容 ，比如列表内容等 。 并存放可触发切换的布局，比如输入框表情按钮等 。
* *PanelContainer* ， 即绿色区域 ， 仅用于存放可切换的面板 （*PanelView*），开发者自主定制 *PanelView* 面板。
* *EmptyView* ， 可选配置，支持1.0.2更新的功能,复杂场景可参考 Activity 复杂场景。

以 Demo 为例子

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <!-- 1.1.0及以后版本支持设置动画速度,速度共4级：慢，标准，适中，快 -->
    <com.effective.android.panel.view.PanelSwitchLayout
        android:id="@+id/panel_switch_layout"
        app:animationSpeed="standard"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <!-- 内容区域 -->
        <!-- edit_view 指定一个 EditText 用于输入 ，必须项-->
        <!-- empty_view 指定用户点击该 ID 对应的 View 时实现面板或者输入法隐藏，非必须项 -->
        <!-- 1.1.0及以后版本不再需要设置 weight -->
        <com.effective.android.panel.view.ContentContainer
            android:id="@+id/content_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            app:edit_view="@id/edit_text"
            app:empty_view="@id/empty_view">

            <FrameLayout
                android:layout_width="match_parent"
                android:layout_height="0dp"
                android:layout_weight="1"
                android:background="#ebebeb">

                <android.support.v7.widget.RecyclerView
                    android:id="@+id/recycler_view"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent" />

                <com.effective.android.panel.view.EmptyView
                    android:id="@+id/empty_view"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent" />
            </FrameLayout>


            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@drawable/shape_input_layout"
                android:gravity="bottom"
                android:minHeight="@dimen/dp_50"
                android:orientation="horizontal"
                android:paddingBottom="@dimen/dp_7.5"
                android:paddingLeft="@dimen/dp_10"
                android:paddingRight="@dimen/dp_10">

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
                    android:maxLines="5"
                    android:minHeight="@dimen/dp_35"
                    android:paddingLeft="@dimen/dp_3"
                    android:paddingRight="@dimen/dp_3"
                    android:imeOptions="actionSearch"
                    android:paddingBottom="@dimen/dp_3"
                    android:paddingTop="@dimen/dp_7.5"
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

        </com.effective.android.panel.view.ContentContainer>


        <!-- 面板区域，仅能包含PanelView-->
        <com.effective.android.panel.view.PanelContainer
            android:id="@+id/panel_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <!-- 每一项面板 -->
            <!-- panel_layout 用于指定面板该 ID 对应的布局 ，必须项-->
            <!-- panel_trigger 用于用户点击该 ID 对应的 View 时切换到该面板 -->
            <!-- panel_toggle  用于当该面板显示时 ，用户再次点击 panel_trigger 对应的 View 时是否回切输入法-->
            <com.effective.android.panel.view.PanelView
                android:id="@+id/panel_emotion"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:panel_layout="@layout/panel_emotion_layout"
                app:panel_trigger="@id/emotion_btn" />

            <com.effective.android.panel.view.PanelView
                android:id="@+id/panel_addition"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                app:panel_layout="@layout/panel_add_layout"
                app:panel_trigger="@id/add_btn" />

        </com.effective.android.panel.view.PanelContainer>
    </com.effective.android.panel.view.PanelSwitchLayout>
</layout>
```


#### 如何引用
1. 在对应模块下 `build.gradle` 添加依赖。
```
implementation 'com.effective.android:panelSwitchHelper:1.1.2'
```

2. 在 activity#onStart 方法中初始化 PanelSwitchHelper 对象，在 activity#onBackPressed hook 返回键 。  

```
   private PanelSwitchHelper mHelper;

   @Override
   protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
            			//可选模式，默认true，scrollOutsideEnable() 可动态设置
            			.contentCanScrollOutside(false) 
                    .build();
        }
    }
    
   @Override
   public void onBackPressed() {
        if (mHelper != null && mHelper.hookSystemBackForHindPanel()) {
                return;
        }
        super.onBackPressed();
   }
    
```

3. 框架提供了多个可解决特殊场景的api，要学会灵活使用（针对超级复杂的需求场景）

**具体方法可在源码查看**

* PanelSwitchHelper 提供 隐藏输入法或面板 和 显示输入法方法
* PanelHelper 提供隐藏输入法，显示输入法，判断全屏，获取状态栏高度，导航栏高度，是否是横竖屏等
* PanelSwitchLayout 核心实现，动态调整子布局结构及动画支持

> 如果框架对你有帮助，可安利给身边的伙伴，每一个 start 都是对框架付出的肯定

#### 期望
编写该项目只是希望能提高日常开发的效率，专注于处理业务 。如果更好的做法或者意见建议，欢迎写信到 yummyl.lau@gmail.com 。