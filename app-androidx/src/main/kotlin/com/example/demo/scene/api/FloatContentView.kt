package com.example.demo.scene.api

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.effective.R
import com.effective.android.panel.PanelSwitchHelper
import com.effective.databinding.ViewFloatContentLayoutBinding
import com.example.demo.scene.chat.adapter.ChatAdapter

/**
 * author : linzheng
 * time   : 2022/6/17
 * desc   :
 * version: 1.0
 */
class FloatContentView : FrameLayout {

    companion object {

        private const val TAG = "FloatContentView"

    }

    private lateinit var dataBinding: ViewFloatContentLayoutBinding
    var backPressedListener: (() -> Boolean)? = null


    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context!!, attrs, defStyleAttr) {
        initView()
    }

    @TargetApi(21)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context!!, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private fun initView() {
        val layoutInflater = LayoutInflater.from(context)
        dataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.view_float_content_layout, this, true)
        dataBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        dataBinding.recyclerView.adapter = ChatAdapter(context, 30)
        dataBinding.ivBack.setOnClickListener { FloatWindowManager.removeViewWithoutPermission(context) }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(TAG, "onKeyDown: ")
        return super.onKeyDown(keyCode, event)
    }
    
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val activity = context as? Activity
        val helper = PanelSwitchHelper.Builder(activity?.window, this)
            .setWindowInsetsRootView(this)
            .addKeyboardStateListener {
                onKeyboardChange { visible, height ->
                    Log.d(TAG, "系统键盘是否可见 : $visible ,高度为：$height")
                }
            }
            .addPanelChangeListener {
                onKeyboard {
                    Log.d(TAG, "唤起系统输入法")
                }
                onNone {
                    Log.d(TAG, "隐藏所有面板")
                }
                onPanel {
                    Log.d(TAG, "唤起面板 : $it")
                }
            }
            .build()
    }

    private var downTime = 0L

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_UP) {
                if (event.downTime == downTime && !event.isCanceled && backPressedListener?.invoke() == true) {
                    downTime = 0L
                    return true
                }
                downTime = 0L
            } else if (event.action == KeyEvent.ACTION_DOWN) {
                downTime = event.eventTime
            }
        }
        return super.dispatchKeyEvent(event)
    }


    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        return super.dispatchKeyEventPreIme(event)
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean {
        return super.dispatchKeyShortcutEvent(event)
    }


}
