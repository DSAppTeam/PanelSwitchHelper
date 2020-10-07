package com.example.demo.scene.api

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.effective.R
import com.effective.android.panel.PanelSwitchHelper
import com.effective.android.panel.view.panel.PanelView
import com.example.demo.scene.chat.emotion.EmotionPagerView
import com.example.demo.scene.chat.emotion.Emotions
import com.example.demo.scene.chat.view.AutoHidePanelRecyclerView
import com.example.demo.util.DisplayUtils
import com.rd.PageIndicatorView

/**
 * 处理可以使用默认的 PanelView，也可以通过继承 IPanelView 来实现自己的 PanelView。
 * 使用自定义面板
 * created by yummylau on 2020/06/06
 */
class CusPanelActivity : AppCompatActivity() {
    private var mHelper: PanelSwitchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_api_cus_panel_layout)
        findViewById<TextView>(R.id.title).text = "继承 IPanelView 实现 PanelView，点击左下角 + 试试吧"
        initView()
    }

    private val sendView: View
        get() = findViewById(R.id.send)

    private val editView: EditText
        get() = findViewById(R.id.edit_text)

    private val emotionView: View
        get() = findViewById(R.id.emotion_btn)

    private fun initView() {
        sendView.setOnClickListener(View.OnClickListener {
            val content = editView.text.toString()
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this@CusPanelActivity, "当前没有输入", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            editView.text = null
        })
    }

    override fun onStart() {
        super.onStart()
        if (mHelper == null) {
            mHelper = PanelSwitchHelper.Builder(this) //可选
                    .addKeyboardStateListener {
                        onKeyboardChange { visible, height ->
                            Log.d(TAG, "系统键盘是否可见 : $visible ,高度为：$height")
                        }
                    }
                    .addEditTextFocusChangeListener {
                        onFocusChange { _, hasFocus ->
                            Log.d(TAG, "输入框是否获得焦点 : $hasFocus")
                        }
                    }
                    .addViewClickListener {
                        onClickBefore {
                            Log.d(TAG, "点击了View : $it")
                        }
                    }
                    .addPanelChangeListener {
                        onKeyboard {
                            Log.d(TAG, "唤起系统输入法")
                            emotionView.isSelected = false
                        }
                        onNone {
                            Log.d(TAG, "隐藏所有面板")
                            emotionView.isSelected = false
                        }
                        onPanel {
                            Log.d(TAG, "唤起面板 : $it")
                            if (it is PanelView) {
                                emotionView.isSelected = it.id == R.id.panel_emotion
                            }
                        }
                        onPanelSizeChange { panelView, _, _, _, width, height ->
                            if (panelView is PanelView) {
                                when (panelView.id) {
                                    R.id.panel_emotion -> {
                                        val pagerView: EmotionPagerView = findViewById(R.id.view_pager)
                                        val viewPagerSize = height - DisplayUtils.dip2px(this@CusPanelActivity, 30f)
                                        pagerView.buildEmotionViews(
                                                findViewById<View>(R.id.pageIndicatorView) as PageIndicatorView,
                                                editView,
                                                Emotions.getEmotions(), width, viewPagerSize)
                                    }
                                    R.id.panel_addition -> {
                                    }
                                }
                            }
                        }
                    }
                    .logTrack(true) //output log
                    .build()
        }
        findViewById<AutoHidePanelRecyclerView>(R.id.recycler_view).setPanelSwitchHelper(mHelper)
    }

    override fun onBackPressed() {
        if (mHelper != null && mHelper!!.hookSystemBackByPanelSwitcher()) {
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val TAG = "PanelActivity"
    }

}