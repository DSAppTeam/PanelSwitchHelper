package com.effective.android.panel.interfaces.listener


/**
 * Created by yummyLau on 18-7-07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 * update 2020/05/08 支持 dsl
 */
interface OnKeyboardStateListener {
    fun onKeyboardChange(visible: Boolean)
}
private typealias OnKeyboardChange = (visible: Boolean) -> Unit

class OnKeyboardStateListenerBuilder : OnKeyboardStateListener {

    private var onKeyboardChange: OnKeyboardChange? = null

    override fun onKeyboardChange(visible: Boolean) {
        onKeyboardChange?.invoke(visible)
    }

    fun onKeyboardChange(onKeyboardChange: OnKeyboardChange) {
        this.onKeyboardChange = onKeyboardChange
    }
}