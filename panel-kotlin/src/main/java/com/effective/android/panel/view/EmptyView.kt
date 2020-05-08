package com.effective.android.panel.view

import android.annotation.TargetApi
import android.content.Context
import android.support.annotation.CallSuper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * 用于提供emptyView帮助用户把触摸事件传递到容器外，提升用户体验 https://github.com/YummyLau/PanelSwitchHelper/issues/6
 * Created by yummyLau on 2019-11-05
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class EmptyView : View {
    private var clickListener: OnClickListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (clickListener != null) {
                clickListener!!.onClick(this)
                return false
            }
        }
        return super.onTouchEvent(event)
    }

    @CallSuper
    override fun setOnClickListener(l: OnClickListener?) {
        if (l != null) {
            clickListener = l
        }
        super.setOnClickListener(null)
    }
}