[![](https://travis-ci.org/YummyLau/PanelSwitchHelper.svg?branch=master)](https://travis-ci.org/YummyLau/panelSwitchHelper)
![Language](https://img.shields.io/badge/language-java-orange.svg)
![Language](https://img.shields.io/badge/language-kotlin-orange.svg)
![Version](https://img.shields.io/badge/version-1.4.0-blue.svg)
![Size](https://img.shields.io/badge/size-14K-brightgreen.svg)

README: [English Doc](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README.md)

### 框架简介

在开发聊天/视频/直播/信息流界面时，希望用户在输入法与功能面板（比如表情面板/更多选项面板等）的切换过程中保持平滑过渡。调研了市场上主流的app效果及实现，实现了一套兼容多场景的输入面板切换框架。目前该框架已测试使用。

### 原理介绍

* 方案1：在 setSoftInputMode = SOFT_INPUT_ADJUST_RESIZE 的场景下，通过ViewTreeObserver.OnGlobalLayoutListener或者ViewCompat.setOnApplyWindowInsetsListener来获取键盘高度，通过修改onLayout的方式调整输入面板的高度。
* 方案2：在 setSoftInputMode = SOFT_INPUT_ADJUST_NOTHING 的场景下，通过ViewCompat.setWindowInsetsAnimationCallback监听键盘过渡动画，获取实时的键盘高度后，通过修改控件translationY的方式实现。

其中方案2是在1.5.0版本中引入的，可以通过android11KeyboardFeature属性控制是否开启Android 11键盘特性（默认是开启的）。在部分应用场景中Android 11键盘特性无法生效，我们会降级成方案1的方式。

备注：由于Android手机设备碎片化严重，可能会出现部分手机的兼容问题，我们为PanelSwitchLayout提供了两个兼容方法，当你的设备无法正常获取到键盘高度时，你可以尝试实现这两个方法来做兼容。

```kotlin

// 针对Android 11以上开启键盘动画特性，高度获取失败时，对外提供兼容方案
var softInputHeightCalculatorOnStart: ((animation: WindowInsetsAnimationCompat, bounds: WindowInsetsAnimationCompat.BoundsCompat) -> Int)? = null
var softInputHeightCalculatorOnProgress: ((insets: WindowInsetsCompat, runningAnimations: MutableList<WindowInsetsAnimationCompat>) -> Int)? = null


```

### 框架优势

* 改进传统使用 `Weight+LinearLayout` 动态更改布局高度适配面板的技术方案，支持多种原生 ViewGroup 容器
* 为了追求更平滑的适配效果，当输入法动态调整高度或动态隐藏导航栏时，功能面板能实时适配
* 为了追求更流畅的切换效果，支持滑动模式，滑动会更流畅，同时也支持固定模式
* 丰富的机型适配，适配 全面屏/刘海屏/挖孔屏/Pad 等非常规 Phone 机型
* 丰富的场景支持，支持 Activity/Fragment/Dialog/PopupWindow，应用到聊天/视频/直播/信息流评论等场景
* 丰富的 API 支持，可自定义内容容器，业务面板，灵活控制面板隐藏，灵活控制切换面板速度
* 支持全屏模式，FullScreen 模式下也能处理面板切换

更多细节可参考

* [场景使用介绍](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README_SENCE-zh.md)
* [API 使用指南](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README_API-zh.md)
* [库版本更新日志](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README_UPDATE-zh.md)

Demo 内容如下

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/demo.png" width = "360" height = "790"/>

从二维码下载 Demo

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/qr_code_apk.png" width = "256" height = "256"/>

默认运行 Androidx 版本，如果需要打开非 Androidx，则在 `Settings.gradle` 中打开 `app` 并在 `gradle.properties` 中关闭 Androidx 配置即可。

### 使用方法

1. 在模块脚本 `build.gradle` 添加库依赖

1.1、Add it in your root build.gradle at the end of repositories:

```groovy

allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

1.2、Add the dependency
```groovy
//1.4.0 版本及后续，仅支持 Androidx
dependencies {
    implementation 'com.github.DSAppTeam:PanelSwitchHelper:v1.5.4'
}
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
                app:edit_view="@id/edit_text">

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
                    .addContentScrollMeasurer { //可选，滑动模式下，可以针对内容面板内的view，定制滑动距离，默认滑动距离为 defaultDistance
                        getScrollDistance { defaultDistance -> defaultDistance - 200 }
                        getScrollViewId { R.id.recycler_view }
                    }
                    .addPanelHeightMeasurer {   //可选 用于设置未获取输入法高度前面板的高度，如果不设置则默认以框架内高度为主
                        getTargetPanelDefaultHeight { DisplayUtils.dip2px(this@DefaultHeightPanelActivity,400f)}
                        getPanelTriggerId { R.id.add_btn }
                    }   
                    .contentScrollOutsideEnable(true)  //可选，默认为true    
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

### 期望

编写该项目只是希望能提高日常开发的效率，专注于处理业务。如果更好的做法或者意见建议，欢迎写信到 yummyl.lau@gmail.com 。
也可以微信“sun_f_life” 添加微信进群反馈。

如果框架对你有帮助，可安利给身边的伙伴，每一个 star 都是对框架付出的肯定。
