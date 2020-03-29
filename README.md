### PanelSwitchHelper
[![](https://travis-ci.org/YummyLau/PanelSwitchHelper.svg?branch=master)](https://travis-ci.org/YummyLau/panelSwitchHelper)
![Language](https://img.shields.io/badge/language-java-orange.svg)
![Version](https://img.shields.io/badge/version-1.1.1-blue.svg)
![Size](https://img.shields.io/badge/size-14K-brightgreen.svg)

README: [English](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README.md) | [中文](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README-zh.md)

#### Version Update

* 1.0.1(2019-07-08) Support compatible with AndroidQ+ focus conflict, support video mode
* 1.0.2(2019-11-05) Support WeChat sliding list to close the panel and list the response to the sliding event to enhance the user experience
* 1.0.3(2019-11-06) Fix [issue](https://github.com/YummyLau/PanelSwitchHelper/issues/10) Scene issues
* 1.0.4(2019-11-18) Added support for Dialog/Fragment/DialogFragment
* 1.0.5 (2019-11-26) Support for special models such as Huawei / Xiaomi that support the hiding of dynamic navigation bar
* 1.1.0 (2020-03-18) Pursuing the ultimate switching experience [See details](http://yummylau.com/2020/03/22/%E5%BC%80%E6%BA%90%E9%A1%B9%E7%9B%AE_20120-03-22_%E8%BE%93%E5%85%A5%E6%B3%95%E5%88%87%E6%8D%A2%E6%A1%86%E6%9E%B6(2)/)
	* Support animation blessing in the switching process, the effect is synchronized with "WeChat chat" scenes, but the supported scenes are far more than this (see Demo), and support custom animation speed
	* Optimize the internal switching process of the framework, abandon the old logic implementation, and the new implementation uses custom drawing to switch the interface without worrying about memory leaks
	* Demo adds custom title bar scene to optimize video scene experience
* 1.1.1 (2020-03-29) Support for special scenes such as full screen / bangs screen / water drop screen
	* Optimize internal calculation effective area height, compatible with special scene
	* Eliminate the call to bindPanelSwitchLayout api, complete the logic inside the framework
	* Demo adds complex IM interaction scenarios

#### What to do

When developing a chat page, the developer wants the user to keep a smooth transition without flickering during the keyboard and function panel (such as the emoticon panel/more options panel). Referring to the mainstream social app effect and implementation in the market, a variety of implementation ideas on the integrated Internet, the most integrated into a template framework, the template framework has been tested and used.

##### Show results

* Figure 1: Core function display
* Figure 2: 1.0.1 update support video function
* Figure 3: 1.0.5 update supports dialog/fragment/dialogFragment/popupwindow, various immersive scenes

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch.gif" width = "270" height = "480" alt="activity layout"/><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.0.1.gif" width = "270" height = "480" alt="activity layout" /><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.0.4.gif" width = "270" height = "480" alt="activity layout" />

* Figure 4: 1.1.0 Animation effect display and dynamically adjust the navigation bar

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.0.gif" width = "270" height = "480" alt="activity layout" /><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.0_2.gif" width = "270" height = "480" alt="activity layout" />

* Figures 5 to 10: 1.1.1 Suitable for scenes such as full screen / drop screen / bang screen
* Figure 5-Xiaomi full screen
* Figure 6-Huawei bangs screen, Figure 7-Huawei bangs screen hide top area
* Figure 8-Xiaomi water drop screen, Figure 9-Xiaomi water drop screen is hidden but the status bar is in cutou, Figure 10-Xiaomi water drop screen is hidden but the status bar is in cutou

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_全面屏.gif" width = "270" height = "480" alt="activity layout"/><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_刘海屏.gif" width = "270" height = "480" alt="activity layout" /><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_刘海屏_隐藏顶部区域.gif" width = "270" height = "480" alt="activity layout" />

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_水滴屏_不隐藏刘海.gif" width = "270" height = "480" alt="activity layout"/><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_水滴屏_隐藏刘海_状态栏在刘海内.gif" width = "270" height = "480" alt="activity layout" /><img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.1.1_水滴屏_隐藏刘海_状态栏在刘海外.gif" width = "270" height = "480" alt="activity layout" />

##### Implementation
Get the keyboard's height by listening to the window's changes and dynamically adjust the layout to achieve a smooth transition switch panel.

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_layout.jpg" width = "696" height = "703" alt="activity layout" align=center />

The core classes ：

* *PanelSwitchLayout* ，including the yellow area, can only contain *PanelContainer* and *PanelSwitchLayout* and implement some auxiliary functions. 1.1.0 Core implementation framework functions，Support to configure animation speed.
* *ContentContainer* ，including the blue area, can store display content such as list content. And store the layout that triggers the switch, such as input box emoticons, etc.
* *PanelContainer* ， including the green area, only for the switchable panel (*PanelView*), the developer customizes the *PanelView* panel.
* *EmptyView* ， Optional configuration, support 1.0.2 update function, complex scene can refer to Activity complex scene.

Take Demo as an example

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

	<!-- 1.1.0 and later versions support setting animation speed. There are 4 levels of speed: slow, standard, moderate, and fast -->
    <com.effective.android.panel.view.PanelSwitchLayout
        android:id="@+id/panel_switch_layout"
        app:animationSpeed="standard"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <!-- Content area -->
        <!-- edit_view, specify an EditText for input, required-->
        <!-- empty_view, specify the panel or keyboard to hide when the user clicks the View corresponding to the ID. -->
        <!-- 1.1.0 and later versions no longer need to set weight -->
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

                <!-- More entrance -->
                <ImageView
                    android:id="@+id/add_btn"
                    android:layout_width="@dimen/dp_35"
                    android:layout_height="@dimen/dp_35"
                    android:layout_marginRight="@dimen/dp_10"
                    android:src="@drawable/icon_add" />

                <!-- Input entrance -->
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

                    <!-- Emotion entrance -->
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


        <!-- Panel area, can only contain PanelView-->
        <com.effective.android.panel.view.PanelContainer
            android:id="@+id/panel_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <!-- Each panel -->
            <!-- panel_layout, used to specify the layout corresponding to the ID of the panel.-->
            <!-- panel_trigger, used to switch to the panel when the user clicks on the View corresponding to the ID -->
            <!-- panel_toggle, used to cut back the keyboard when the user clicks the view corresponding to panel_trigger again when the panel is displayed.-->
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


#### How to quote
1. Add dependencies in module build.gradle file。
```
implementation 'com.effective.android:panelSwitchHelper:1.1.1'
```

2. Initialize the PanelSwitchHelper object in the activity#onStart method, in the activity#onBackPressed hook return。
```
   private PanelSwitchHelper mHelper;

   @Override
   protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
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


3. The framework provides a variety of APIs to solve special situations, and you need to learn to use it flexibly (for ultra-complex requirements)

```
//The specific method is visible in the source code
PanelSwitchHelper, Provide hidden input method or panel and display input method
PanelHelper, Provide hidden input method, display input method, judge full screen, get status bar height, navigation bar height, whether it is horizontal and vertical screen, etc.
PanelSwitchLayout core implementation, dynamic adjustment of sub layout structure and animation support
```

> If the framework is helpful to you, Amway can give your partners around, every start is an affirmation of the framework.

#### Expect
The project was written only to improve the efficiency of day-to-day development and focus on the business. If you have a better practice or suggestions, please write to yummyl.lau@gmail.com.
