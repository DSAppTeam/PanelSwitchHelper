package com.example.demo.scene.api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.effective.R
import com.effective.android.panel.PanelSwitchHelper
import com.effective.android.panel.utils.PanelUtil
import com.effective.android.panel.view.panel.PanelView
import com.example.demo.scene.chat.emotion.EmotionPagerView
import com.example.demo.scene.chat.emotion.Emotions
import com.example.demo.util.DisplayUtils
import com.rd.PageIndicatorView

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/9/19
 * desc   :
 * version: 1.0
 */
class FixIssuesActivity2 : AppCompatActivity() {

    var mHelper: PanelSwitchHelper? = null

    private val editView: EditText
        get() = findViewById(R.id.edit_text)

    private val scrollView: ScrollView
        get() = findViewById(R.id.scrollView)

    private val emotionView: View
        get() = findViewById(R.id.emotion_btn)


    companion object {

        const val TAG = "FixIssuesActivity"


        @JvmStatic
        fun start(context: Context?) {
            if (context == null) {
                return
            }
            val intent = Intent(context, FixIssuesActivity2::class.java)
            context.startActivity(intent)
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fix_issues_layout2)
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
                    getScrollDistance { 0 }
                    getScrollViewId { R.id.scrollView }
                }
                .addPanelChangeListener {
                    onKeyboard {
                        Log.d(TAG, "唤起系统输入法")
                        emotionView.isSelected = false
                        scrollView.setPadding(0, 0, 0, PanelUtil.getKeyBoardHeight(this@FixIssuesActivity2))
                    }
                    onNone {
                        Log.d(TAG, "隐藏所有面板")
                        emotionView.isSelected = false
                        scrollView.setPadding(0, 0, 0, 0)
                    }
                    onPanel {
                        Log.d(TAG, "唤起面板 : $it")
                        if (it is PanelView) {
                            emotionView.isSelected = it.id == R.id.panel_emotion
                        }
                        scrollView.setPadding(0, 0, 0, PanelUtil.getKeyBoardHeight(this@FixIssuesActivity2))
                    }
                    onPanelSizeChange { panelView, _, _, _, width, height ->
                        if (panelView is PanelView) {
                            when (panelView.id) {
                                R.id.panel_emotion -> {
                                    val pagerView: EmotionPagerView = findViewById(R.id.view_pager)
                                    val viewPagerSize = height - DisplayUtils.dip2px(this@FixIssuesActivity2, 30f)
                                    pagerView.buildEmotionViews(
                                        findViewById<View>(R.id.pageIndicatorView) as PageIndicatorView,
                                        editView,
                                        Emotions.getEmotions(), width, viewPagerSize
                                    )
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