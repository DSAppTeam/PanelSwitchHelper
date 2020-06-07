### API 目录

1. 框架视图结构
2. 内容区域
3. 面板区域
4. PanelSwitchHelper 构建及使用细节

#### 框架视图结构

框架定义了 PanelSwitchLayout 容器，该容器内容由内容区域和面板区域构成。

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/api/arch.jpg" width = "696" height = "703"/>

同时基于上述结构，框支持两种模式来更好服务复杂的场景。

<img src="https://github.com/YummyLau/PanelSwitchHelper/blob/master/source/api/mode.jpg" width = "662" height = "444" align=center />


#### 内容面板

框架提供了多种 Container 容器

* `LinearContentContainer` 可当作 `LinearLayout` 功能使用
* `RelativeContentContainer` 可当作 `RelativeLayout` 功能使用
* `FrameContentContainer` 可当作 `FrameLayout` 功能使用

所有提供的 Container 容器内部都是由实现了 `IContentContainer` 接口的 `ContentContainerImpl` 对象代理处理。开发者可使用 `ContentContainerImpl` 模仿框架提供的各种 Container 来实现自定义的 Container。 Demo 中 `CusContentContainer` 就是基于约束布局实现的一个例子。

例子可参考 Demo：

* **API-内容容器（线性布局）**
* **API-内容容器（相对布局）**
* **API-内容容器（帧布局）**
* **API-内容容器（自定义布局）**

开发中你可能会遇到：“在某种场景中，希望点击 Container 区域来隐藏已显示的面板，可能是输入法（输入法也是一种年版），也可能是业务面板。”
框架基于这些场景，各个容器提供了自动隐藏面板的API，详情可查看 Demo 中的 `ResetActivity`。

自动隐藏面板功能被定义在 container 的扩展属性内,包含

* `auto_reset_enable` 表示是否支持点击内容区域内隐藏面板，默认打开。打开时，当区域内子view没有消费事件时，则会默认消费该事件并自动隐藏。
* `auto_reset_area`, 当且仅当 auto_reset_enable 为 true 才有效，指定一个 view 的id，为 `auto_reset_enable` 的消费事件限定区域。

	1. 比如场景一，指定了空白透明 view  ，view 没有消费事件时，则才会自动隐藏面板；
	2. 比如场景二，指定了列表的 recyclerview ，则recyclerview 没有消费事件时，则才会自动隐藏面板；
	3. 比如场景三，场景二 recyclerview 时显然很难不消费事件，如果 holder 被点击（比如聊天项），则应该被正常消费如果点击 recyclerview 内的空白，recyclerview 也会默认消费，因为需要滑动.

    为了解决这种下层应该消费点击滑动事件，而上层容器应该获取点击并自动隐藏，`HookActionUpRecyclerView` 就是该场景的 Demo,需要把下层消费完之后的 ACTION_UP 返回 false 让上层有机会处理。 `ContentContainerImpl` 内的实现预留了这种可能，用于处理该复杂场景。

例子可参考 Demo：

* **API-点击内容容器收起面板（默认处理）**
* **API-点击空白 View 收起面板**
* **API-点击原生 Recyclerview 收起面板**
* **API-点击自定义 RecyclerView 收起面板**
* **API-关闭点击内容容器收起面板**


#### 功能区域

面板区域是一个 `FrameLayout` 容器，内部可存放各个功能面板。

```
<!-- 面板区域，仅能包含PanelView-->
<com.effective.android.panel.view.panel.PanelContainer
    android:id="@+id/panel_container"
    android:layout_width="match_parent"
    android:background="@color/common_page_bg_color"
    android:layout_height="wrap_content">

	<!-- 默认的 PanelView -->
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
```

框架默认提供了 `PanelView` 来定义一个功能面板,其定义了多个属性：

* `panel_trigger` 用户点击该 ID 对应的 View 时切换到该面板
* `panel_layout ` 用于扩展该面板的布局，功能和 <include> 标签相似
* `panel_trigger` 用于当该面板显示时 ，用户再次点击 panel_trigger 对应的 View 时是否回切输入法

同时嗨提供 `IPanelView` 接口用于自主实现 `PanelView`。例子可参考 Demo：**API-自定义PanelView 容器**

#### PanelSwitchHelper 构建及使用细节

可在 Activity/Fragment/Dialog/DialogFragment/PopupWindow 等使用 PanelSwitchHelper，如下代码

```
PanelSwitchHelper mHelper = new PanelSwitchHelper.Builder(this)
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
            .contentCanScrollOutside(true)    
            .logTrack(true)                  
            .build(true)			          
```

其中，builder 构建过程可指定的功能如下：

1. addKeyboardStateListener，用于监听输入法状态，可获取输入法的可见性及高度
2. addEditTextFocusChangeListener，监听指定的输入源焦点变化
3. addViewClickListener，监听 trigger 及输入源 的点击，比如点击表情切换按钮，输入源点击等
4. addPanelChangeListener，监听面板变化，包括输入法显示，面板显示，输入法高度变化引起面板高度变化回调，隐藏面板状态
5. contentCanScrollOutside 指定是否选择滑动模式，默认是开启
6. logTrack 是否输出 log 信息
7. build，返回 PanelSwitchHelper 对象，可传递参数指定第一次是否自动显示输入法，默认不显示。

除了上述构建过程中提供的功能，还提供以下重要方法使用：

1. scrollOutsideEnable，动态更改模式
2. toKeyboardState，切换成输入法面板
3. resetState，隐藏所有面板
4. hookSystemBackByPanelSwitcher 拦截返回，如果当前用户按下返回或者业务返回键，则优先隐藏面板



