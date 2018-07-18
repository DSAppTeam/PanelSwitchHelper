### panelSwitchHelper
[![](https://travis-ci.org/YummyLau/panelSwitchHelper.svg?branch=master)](https://travis-ci.org/YummyLau/panelSwitchHelper)
![Language](https://img.shields.io/badge/language-java-orange.svg)
![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Size](https://img.shields.io/badge/size-14K-brightgreen.svg)

README: [English](https://github.com/YummyLau/SharedPreferencesInjector/blob/master/README.md) | [中文](https://github.com/YummyLau/SharedPreferencesInjector/blob/master/README-zh.md)

#### 用于做什么

在开发聊天页面时，开发者希望用户在输入法与功能面板（比如表情面板/更多选项面板等）切换过程中保持平滑过渡不闪烁。 参考了市场上主流的社交app效果及实现，综合互联网上的多种实现思路，最总整合成一个模版框架，该模版框架已经过大量测试使用。

##### 效果展示


##### 实现原理
1. 


#### 如何引用
1. 在对应模块下 `build.gradle` 添加依赖。
```
implementation 'com.effective.android:panelSwitchHelper:1.0.0'
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



#### 期望
编写该项目只是希望能提高日常开发的效率，专注于处理业务 。 javapoet 是个好东西，也可以参考 apt 文件下的三个模块，自行利用注解扩展满足自身项目需求 。

如果更好的做法或者意见建议，欢迎写信到 yummyl.lau@gmail.com 。