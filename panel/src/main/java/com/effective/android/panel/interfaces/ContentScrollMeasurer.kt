package com.effective.android.panel.interfaces

/**
 * 当内容允许滑动时，即[com.effective.android.panel.view.PanelSwitchLayout.isContentScrollOutsizeEnable] 返回true,则默认内容区域会向上 layout 一个面板距离
 * 默认 getScrollDistance 为软键盘/面板的距离
 * Created by yummyLau on 2020-07-08
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
interface ContentScrollMeasurer {
    fun getScrollDistance(defaultDistance: Int): Int
    fun getScrollViewId(): Int
}

private typealias GetScrollDistance = (defaultDistance: Int) -> Int
private typealias GetScrollViewId = () -> Int

class ContentScrollMeasurerBuilder : ContentScrollMeasurer {

    private var getScrollDistance: GetScrollDistance? = null
    private var getScrollViewId: GetScrollViewId? = null

    override fun getScrollDistance(defaultDistance: Int): Int = getScrollDistance?.invoke(defaultDistance) ?: 0
    override fun getScrollViewId(): Int = getScrollViewId?.invoke() ?: -1

    fun getScrollDistance(getScrollDistance: GetScrollDistance) {
        this.getScrollDistance = getScrollDistance
    }

    fun getScrollViewId(getScrollViewId: GetScrollViewId) {
        this.getScrollViewId = getScrollViewId
    }
}