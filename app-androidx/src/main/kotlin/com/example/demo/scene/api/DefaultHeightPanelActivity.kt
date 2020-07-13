package com.example.demo.scene.api

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.effective.R
import com.effective.android.panel.PanelSwitchHelper
import com.effective.android.panel.utils.PanelUtil.clearData
import com.effective.android.panel.view.panel.PanelView
import com.example.demo.scene.chat.emotion.EmotionPagerView
import com.example.demo.scene.chat.emotion.Emotions
import com.example.demo.util.DisplayUtils
import com.rd.PageIndicatorView

/**
 * 设置面板默认高度
 * created by yummylau on 2020/07/13
 */
class DefaultHeightPanelActivity : AppCompatActivity() {
    private var mHelper: PanelSwitchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_api_default_panel_height_layout)
        findViewById<TextView>(R.id.title).text = "设置面板默认高度，点击切换下面面板进行体验，如果曾拉起过输入法，则需要点击此标题清除高度缓存。"
        findViewById<TextView>(R.id.title).setOnClickListener {
            clearData(this@DefaultHeightPanelActivity)
            Toast.makeText(this@DefaultHeightPanelActivity, "已清除面板高度缓存，可拉起功能面板测试默认高度", Toast.LENGTH_SHORT).show()
        }
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
                Toast.makeText(this@DefaultHeightPanelActivity, "当前没有输入", Toast.LENGTH_SHORT).show()
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
                                        val viewPagerSize = height - DisplayUtils.dip2px(this@DefaultHeightPanelActivity, 30f)
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
                    .addContentScrollMeasurer {
                        getScrollDistance { defaultDistance -> defaultDistance - 200 }
                        getScrollViewId { R.id.recycler_view }
                    }
                    .addPanelHeightMeasurer {
                        getTargetPanelDefaultHeight { DisplayUtils.dip2px(this@DefaultHeightPanelActivity, 400f) }
                        getPanelTriggerId { R.id.add_btn }
                    }
                    .addPanelHeightMeasurer {
                        getTargetPanelDefaultHeight { DisplayUtils.dip2px(this@DefaultHeightPanelActivity, 200f) }
                        getPanelTriggerId { R.id.emotion_btn }
                    }
                    .logTrack(true) //output log
                    .build()
        }
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