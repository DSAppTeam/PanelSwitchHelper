package com.example.demo.scene.api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.effective.R
import com.effective.android.panel.PanelSwitchHelper
import com.effective.android.panel.view.panel.PanelView
import com.example.demo.scene.chat.adapter.ChatAdapter
import com.example.demo.scene.chat.adapter.ChatInfo
import com.example.demo.scene.chat.emotion.EmotionPagerView
import com.example.demo.scene.chat.emotion.Emotions
import com.example.demo.scene.chat.view.AutoHidePanelRecyclerView
import com.example.demo.util.DisplayUtils
import com.rd.PageIndicatorView


class FixIssuesActivity4 : AppCompatActivity(), ChatAdapter.ItemLongClickListener {
    private var mHelper: PanelSwitchHelper? = null
    private lateinit var mAdapter: ChatAdapter
    private var mLinearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_fix_issues_layout4)
        findViewById<TextView>(R.id.title).text = "自定义布局"
        initView()
    }

    private val recyclerView: AutoHidePanelRecyclerView
        get() = findViewById<View>(R.id.recycler_view) as AutoHidePanelRecyclerView

    private val sendView: View
        get() = findViewById(R.id.send)

    private val editView: EditText
        get() = findViewById(R.id.edit_text)

    private val emotionView: View
        get() = findViewById(R.id.emotion_btn)

    private fun initView() {
        mLinearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLinearLayoutManager
        mAdapter = ChatAdapter(this, 50)
        mAdapter.setItemLongClickListener(this)
        recyclerView.adapter = mAdapter
        sendView.setOnClickListener(View.OnClickListener {
            val content = editView.text.toString()
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this@FixIssuesActivity4, "当前没有输入", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            mAdapter.insertInfo(ChatInfo.CREATE(content))
            editView.text = null
            scrollToBottom()
        })
    }

    private fun scrollToBottom() {
        editView.post { mLinearLayoutManager?.scrollToPosition(mAdapter.itemCount - 1) }
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
                        if (hasFocus) {
                            scrollToBottom()
                        }
                    }
                }
                .addViewClickListener {
                    onClickBefore {
                        when (it!!.id) {
                            R.id.edit_text, R.id.add_btn, R.id.emotion_btn -> {
                                scrollToBottom()
                            }
                        }
                        Log.d(TAG, "点击了View : $it")
                    }
                }
                .addPanelChangeListener {
                    onKeyboard {
                        Log.d(TAG, "唤起系统输入法")
                        emotionView.isSelected = false
                        scrollToBottom()
                    }
                    onNone {
                        Log.d(TAG, "隐藏所有面板")
                        emotionView.isSelected = false
                    }
                    onPanel {
                        Log.d(TAG, "唤起面板 : $it")
                        if (it is PanelView) {
                            emotionView.isSelected = it.id == R.id.panel_emotion
                            scrollToBottom()
                        }
                    }
                    onPanelSizeChange { panelView, _, _, _, width, height ->
                        if (panelView is PanelView) {
                            when (panelView.id) {
                                R.id.panel_emotion -> {
                                    val pagerView: EmotionPagerView = findViewById(R.id.view_pager)
                                    val viewPagerSize =
                                        height - DisplayUtils.dip2px(this@FixIssuesActivity4, 30f)
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
        findViewById<AutoHidePanelRecyclerView>(R.id.recycler_view).setPanelSwitchHelper(mHelper)
    }

    override fun onBackPressed() {
        if (mHelper != null && mHelper!!.hookSystemBackByPanelSwitcher()) {
            return
        }
        super.onBackPressed()
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, FixIssuesActivity4::class.java)
            context.startActivity(intent)
        }

        private const val TAG = "ContentActivity"
    }

    override fun onItemLongClickListener(view: View?, position: Int) {
        var textView = findViewById<AppCompatTextView>(R.id.tv_reply)
        textView.visibility = View.VISIBLE
    }
}