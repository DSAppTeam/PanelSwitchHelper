package com.effective.android.panel.interfaces

/**
 * 业务层返回未填充数据，默认为 0
 * Created by yummyLau on 2020-07-08
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
interface ViewDistanceMeasurer {
    fun getUnfilledHeight(): Int
    fun getViewTag(): String
}

private typealias GetUnfilledHeight = () -> Int
private typealias GetViewTag = () -> String

class ViewDistanceMeasurerBuilder : ViewDistanceMeasurer {

    private var getUnfilledHeight: GetUnfilledHeight? = null
    private var getViewTag: GetViewTag? = null

    override fun getUnfilledHeight(): Int = getUnfilledHeight?.invoke() ?: 0

    override fun getViewTag(): String = getViewTag?.invoke() ?: ""

    fun getUnfilledHeight(getUnfilledHeight: GetUnfilledHeight) {
        this.getUnfilledHeight = getUnfilledHeight
    }

    fun getViewTag(getViewTag: GetViewTag) {
        this.getViewTag = getViewTag
    }
}