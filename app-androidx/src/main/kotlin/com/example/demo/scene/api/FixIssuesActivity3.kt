package com.example.demo.scene.api

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.effective.R
import com.effective.android.panel.PanelSwitchHelper
import com.effective.android.panel.view.panel.PanelView
import com.example.demo.TestSecondActivity
import com.example.demo.scene.chat.emotion.EmotionPagerView
import com.example.demo.scene.chat.emotion.Emotions
import com.example.demo.util.DisplayUtils
import com.rd.PageIndicatorView
import kotlinx.android.synthetic.main.activity_fix_issues_layout.*

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/12/05
 * desc   :
 * version: 1.0
 */
class FixIssuesActivity3 : AppCompatActivity() {

    var mHelper: PanelSwitchHelper? = null

    private val editView: EditText
        get() = findViewById(R.id.edit_text)

    private val emotionView: View
        get() = findViewById(R.id.emotion_btn)


    private val bottomAction: View
        get() = findViewById(R.id.bottom_action)

    companion object {

        const val TAG = "FixIssuesActivity3"


        @JvmStatic
        fun start(context: Context?) {
            if (context == null) {
                return
            }
            val intent = Intent(context, FixIssuesActivity3::class.java)
            context.startActivity(intent)
        }


    }

    private var spaceHeight = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fix_issues_layout3)


        editView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            spaceHeight = bottomAction.top - bottom
            Log.d(TAG, "onCreate: spaceHeight = $spaceHeight")
            Log.d(TAG, "onCreate: bottom = $bottom")
        }


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
                .addContentScrollMeasurer {
                    getScrollDistance {
                        if (spaceHeight <= 0) {
                            spaceHeight = bottomAction.top - editView.bottom
                        }
                        it - spaceHeight
                    }
                    getScrollViewId { R.id.edit_text }
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
                                    val viewPagerSize = height - DisplayUtils.dip2px(this@FixIssuesActivity3, 30f)
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

    }


}