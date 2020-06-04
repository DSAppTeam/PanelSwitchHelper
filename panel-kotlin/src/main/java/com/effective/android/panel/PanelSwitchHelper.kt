package com.effective.android.panel

import android.R
import android.app.Activity
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.effective.android.panel.interfaces.OnScrollOutsideBorder
import com.effective.android.panel.interfaces.listener.*
import com.effective.android.panel.utils.PanelUtil.getKeyBoardHeight
import com.effective.android.panel.view.PanelSwitchLayout

/**
 * the helper of panel switching
 * Created by yummyLau on 2018-6-21.
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 *
 *
 * updated by yummyLau on 20/03/18
 * 重构整个输入法切换框架，移除旧版使用 weight+Runnable延迟切换，使用新版 layout+动画无缝衔接！
 */
class PanelSwitchHelper private constructor(builder: Builder,showKeyboard: Boolean) {

    private val mPanelSwitchLayout: PanelSwitchLayout
    private var canScrollOutside: Boolean

    init {
        Constants.DEBUG = builder.logTrack
        canScrollOutside = builder.contentCanScrollOutside
        if (builder.logTrack) {
            builder.viewClickListeners.add(LogTracker)
            builder.panelChangeListeners.add(LogTracker)
            builder.keyboardStatusListeners.add(LogTracker)
            builder.editFocusChangeListeners.add(LogTracker)
        }
        mPanelSwitchLayout = builder.panelSwitchLayout!!
        mPanelSwitchLayout.setScrollOutsideBorder(object : OnScrollOutsideBorder {
            override fun canLayoutOutsideBorder(): Boolean {
                return canScrollOutside
            }

            override fun getOutsideHeight(): Int = getKeyBoardHeight(mPanelSwitchLayout.context)
        })
        mPanelSwitchLayout.bindListener(builder.viewClickListeners, builder.panelChangeListeners, builder.keyboardStatusListeners, builder.editFocusChangeListeners)
        mPanelSwitchLayout.bindWindow(builder.window)
        if(showKeyboard){
            mPanelSwitchLayout.toKeyboardState()
        }
    }

    fun hookSystemBackByPanelSwitcher(): Boolean {
        return mPanelSwitchLayout.hookSystemBackByPanelSwitcher()
    }

    fun scrollOutsideEnable(enable: Boolean) {
        canScrollOutside = enable
    }

    /**
     * 外部显示输入框
     */
    fun toKeyboardState() {
        mPanelSwitchLayout.toKeyboardState()
    }

    /**
     * 隐藏输入法或者面板
     */
    fun resetState() {
        mPanelSwitchLayout.checkoutPanel(Constants.PANEL_NONE)
    }

    class Builder(window: Window?, root: View?) {
        var viewClickListeners: MutableList<OnViewClickListener> = mutableListOf()
        var panelChangeListeners: MutableList<OnPanelChangeListener> = mutableListOf()
        var keyboardStatusListeners: MutableList<OnKeyboardStateListener> = mutableListOf()
        var editFocusChangeListeners: MutableList<OnEditFocusChangeListener> = mutableListOf()
        var panelSwitchLayout: PanelSwitchLayout? = null
        var window: Window
        var rootView: View
        var logTrack = false
        var contentCanScrollOutside = true

        constructor(activity: Activity) : this(activity.window, activity.window.decorView.findViewById<View>(R.id.content))
        constructor(fragment: Fragment) : this(fragment.activity?.window, fragment.view)
        constructor(dialogFragment: DialogFragment) : this(dialogFragment.activity?.window, dialogFragment.view)

        init {
            requireNotNull(window) { "PanelSwitchHelper\$Builder#build : window can't be null!please set value by call #Builder" }
            this.window = window
            requireNotNull(root) { "PanelSwitchHelper\$Builder#build : rootView can't be null!please set value by call #Builder" }
            this.rootView = root
        }

        /**
         * note: helper will set view's onClickListener to View ,so you should add OnViewClickListener for your project.
         *
         * @param listener
         * @return
         */
        fun addViewClickListener(listener: OnViewClickListener): Builder {
            if (!viewClickListeners.contains(listener)) {
                viewClickListeners.add(listener)
            }
            return this
        }

        fun addViewClickListener(function: OnViewClickListenerBuilder.() -> Unit): Builder {
            viewClickListeners.add(OnViewClickListenerBuilder().also(function))
            return this
        }

        fun addPanelChangeListener(listener: OnPanelChangeListener): Builder {
            if (!panelChangeListeners.contains(listener)) {
                panelChangeListeners.add(listener)
            }
            return this
        }

        fun addPanelChangeListener(function: OnPanelChangeListenerBuilder.() -> Unit): Builder {
            panelChangeListeners.add(OnPanelChangeListenerBuilder().also(function))
            return this
        }

        fun addKeyboardStateListener(listener: OnKeyboardStateListener): Builder {
            if (!keyboardStatusListeners.contains(listener)) {
                keyboardStatusListeners.add(listener)
            }
            return this
        }

        fun addKeyboardStateListener(function: OnKeyboardStateListenerBuilder.() -> Unit): Builder {
            keyboardStatusListeners.add(OnKeyboardStateListenerBuilder().also(function))
            return this
        }

        fun addEditTextFocusChangeListener(listener: OnEditFocusChangeListener): Builder {
            if (!editFocusChangeListeners.contains(listener)) {
                editFocusChangeListeners.add(listener)
            }
            return this
        }

        fun addEditTextFocusChangeListener(function: OnEditFocusChangeListenerBuilder.() -> Unit): Builder {
            editFocusChangeListeners.add(OnEditFocusChangeListenerBuilder().also(function))
            return this
        }

        fun contentCanScrollOutside(canScrollOutside: Boolean): Builder {
            contentCanScrollOutside = canScrollOutside
            return this
        }

        fun logTrack(logTrack: Boolean): Builder {
            this.logTrack = logTrack
            return this
        }

        @JvmOverloads
        fun build(showKeyboard: Boolean = false): PanelSwitchHelper {
            findSwitchLayout(rootView)
            requireNotNull(panelSwitchLayout) { "PanelSwitchHelper\$Builder#build : not found PanelSwitchLayout!" }
            return PanelSwitchHelper(this, showKeyboard)
        }

        private fun findSwitchLayout(view: View) {
            if (view is PanelSwitchLayout) {
                require(panelSwitchLayout == null) { "PanelSwitchHelper\$Builder#build : rootView has one more panelSwitchLayout!" }
                panelSwitchLayout = view
                return
            }
            if (view is ViewGroup) {
                val childCount = view.childCount
                for (i in 0 until childCount) {
                    findSwitchLayout(view.getChildAt(i))
                }
            }
        }
    }
}