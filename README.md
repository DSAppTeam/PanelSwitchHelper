### PanelSwitchHelper
[![](https://travis-ci.org/YummyLau/PanelSwitchHelper.svg?branch=master)](https://travis-ci.org/YummyLau/panelSwitchHelper)
![Language](https://img.shields.io/badge/language-java-orange.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Size](https://img.shields.io/badge/size-14K-brightgreen.svg)

README: [English](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README.md) | [中文](https://github.com/YummyLau/PanelSwitchHelper/blob/master/README-zh.md)

#### Version Update

* 1.0.1(2019-07-08) Support compatible with AndroidQ+ focus conflict, support video mode

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_1.0.1.gif" width = "453" height = "980" alt="activity layout" align=center />

#### What to do

When developing a chat page, the developer wants the user to keep a smooth transition without flickering during the keyboard and function panel (such as the emoticon panel/more options panel). Referring to the mainstream social app effect and implementation in the market, a variety of implementation ideas on the integrated Internet, the most integrated into a template framework, the template framework has been tested and used.

##### Show results

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch.gif" width = "540" height = "960" alt="activity layout" align=center />

##### Implementation
Get the keyboard's height by listening to the window's changes and dynamically adjust the layout to achieve a smooth transition switch panel.

<img src="https://raw.githubusercontent.com/YummyLau/PanelSwitchHelper/master/source/panel_switch_layout.jpg" width = "696" height = "703" alt="activity layout" align=center />

The core classes ：

* *PanelSwitchLayout* ，including the yellow area, can only contain *PanelContainer* and *PanelSwitchLayout* and implement some auxiliary functions
* *ContentContainer* ，including the blue area, can store display content such as list content. And store the layout that triggers the switch, such as input box emoticons, etc.
* *PanelContainer* ， including the green area, only for the switchable panel (*PanelView*), the developer customizes the *PanelView* panel.

Take activity_sample_layout.xml as an example

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <com.effective.android.panel.view.PanelSwitchLayout
        android:id="@+id/panel_switch_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <!-- Content area -->
        <!-- edit_view, specify an EditText for input, required-->
        <!-- empty_view, specify the panel or keyboard to hide when the user clicks the View corresponding to the ID. -->
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

                <View
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
implementation 'com.effective.android:panelSwitchHelper:1.0.0'
```

2. Initialize the PanelSwitchHelper object in the activity#onStart method, in the activity#onBackPressed hook return。
```
   private PanelSwitchHelper mHelper;

   @Override
   protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .bindPanelSwitchLayout(R.id.panel_switch_layout)        //Binding a panelSwitchLayout
                    .bindPanelContainerId(R.id.panel_container)             //Binding a contentContainer
                    .bindContentContainerId(R.id.content_view)              //Binding a panelContainer
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


#### Expect
The project was written only to improve the efficiency of day-to-day development and focus on the business. If you have a better practice or suggestions, please write to yummyl.lau@gmail.com.