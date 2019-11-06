### PanelSwitchHelper
[![](https://travis-ci.org/YummyLau/PanelSwitchHelper.svg?branch=master)](https://travis-ci.org/YummyLau/panelSwitchHelper)
![Language](https://img.shields.io/badge/language-java-orange.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Size](https://img.shields.io/badge/size-14K-brightgreen.svg)

README: [English](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README.md) | [中文](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README-zh.md)

#### 版本更新

* 1.0.1(2019-07-08) 支持兼容AndroidQ+焦点冲突，支持视频模式
* 1.0.2(2019-11-05) 支持微信式滑动列表收起面板同时列表响应滑动事件，提升用户体验
* 1.0.3(2019-11-06) 修复 [issue](https://github.com/YummyLau/PanelSwitchHelper/issues/10) 场景问题

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.0.1.gif" width = "453" height = "980" alt="activity layout" align=center />

#### 用于做什么

在开发聊天页面时，开发者希望用户在输入法与功能面板（比如表情面板/更多选项面板等）切换过程中保持平滑过渡不闪烁。 参考了市场上主流的社交app效果及实现，综合互联网上的多种实现思路，最总整合成一个模版框架，该模版框架已经过测试使用。

##### 效果展示

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch.gif" width = "540" height = "960" alt="activity layout" align=center />

##### 实现方法
通过监听 Window 窗口变化来获取输入法高度并动态调整布局来达到平滑过渡切换面板。

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_layout.jpg" width = "696" height = "703" alt="界面布局" align=center />

涉及的核心类有：

* *PanelSwitchLayout* ，即黄色区域 ，仅能包含 *PanelContainer*  和 *PanelSwitchLayout* 并实现一些辅助性功能。
* *ContentContainer* ，即蓝色区域 ，用于存放显示内容 ，比如列表内容等 。 并存放可触发切换的布局，比如输入框表情按钮等 。
* *PanelContainer* ， 即绿色区域 ， 仅用于存放可切换的面板 （*PanelView*），开发者自主定制 *PanelView* 面板。
* *EmptyView* ， 可选配置，一般建议使用，支持1.0.2更新的功能

以 activity_sample_layout.xml 为例子

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <com.effective.android.panel.view.PanelSwitchLayout
        android:id="@+id/panel_switch_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <!-- 内容区域 -->
        <!-- edit_view 指定一个 EditText 用于输入 ，必须项-->
        <!-- empty_view 指定用户点击该 ID 对应的 View 时实现面板或者输入法隐藏，非必须项 -->
        <com.effective.android.panel.view.ContentContainer
            android:id="@+id/content_view"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
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
implementation 'com.effective.android:panelSwitchHelper:1.0.2'
```

2. 在 activity#onStart 方法中初始化 PanelSwitchHelper 对象，在 activity#onBackPressed hook 返回键 。
```
   private PanelSwitchHelper mHelper;

   @Override
   protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .bindPanelSwitchLayout(R.id.panel_switch_layout)        //绑定PanelSwitchLayout 对象
                    .bindPanelContainerId(R.id.panel_container)             //绑定ContentContainer 对象
                    .bindContentContainerId(R.id.content_view)              //绑定PanelContainer 对象
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

```
//具体方法可在源码查看
PanelSwitchHelper 提供 隐藏输入法或面板 和 显示输入法方法
PanelHelper 提供隐藏输入法，显示输入法，判断全屏，获取状态栏高度，导航栏高度，是否是横竖屏等
```


#### 期望
编写该项目只是希望能提高日常开发的效率，专注于处理业务 。如果更好的做法或者意见建议，欢迎写信到 yummyl.lau@gmail.com 。