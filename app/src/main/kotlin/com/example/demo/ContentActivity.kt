package com.example.demo

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.effective.R
import com.effective.android.panel.PanelSwitchHelper
import com.effective.android.panel.view.panel.PanelView
import com.effective.databinding.ChatContentLinearLayoutBinding
import com.example.demo.anno.ContentType
import com.example.demo.chat.ChatAdapter
import com.example.demo.chat.ChatInfo
import com.example.demo.chat.CusRecyclerView
import com.example.demo.emotion.EmotionPagerView
import com.example.demo.emotion.Emotions
import com.example.demo.util.DisplayUtils
import com.rd.PageIndicatorView

/**
 * 内容区域支持多种布局，不再限定为线性布局
 * Created by yummyLau on 2020/05/07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
class ContentActivity : AppCompatActivity() {
    private var mHelper: PanelSwitchHelper? = null
    private lateinit var mAdapter: ChatAdapter
    private var mLinearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent.getIntExtra(Constants.KEY_PAGE_TYPE, ContentType.CUS)) {
            ContentType.Linear -> {
                setContentView(R.layout.chat_content_linear_layout)
            }
            ContentType.Frame -> {
                setContentView(R.layout.chat_content_frame_layout)
            }
            ContentType.Relative -> {
                setContentView(R.layout.chat_content_relative_layout)
            }
            else -> {
                setContentView(R.layout.chat_content_cus_layout)
            }
        }
        initView()
    }

    private val recyclerView: CusRecyclerView
        get() = findViewById<View>(R.id.recycler_view) as CusRecyclerView

    private val sendView: View
        get() = findViewById(R.id.send)

    private val editView: EditText
        get() = findViewById(R.id.edit_text)

    private fun getRoot(): View = window.decorView.findViewById<View>(Window.ID_ANDROID_CONTENT).findViewById(R.id.root_view);

    private val emotionView: View
        get() = findViewById(R.id.emotion_btn)

    private fun initView() {
        mLinearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLinearLayoutManager
        mAdapter = ChatAdapter(this, 4)
        recyclerView.adapter = mAdapter
        sendView.setOnClickListener(View.OnClickListener {
            val content = editView.text.toString()
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this@ContentActivity, "当前没有输入", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            mAdapter.insertInfo(ChatInfo.CREATE(content))
            //                如果超过某些条目，可开启滑动外部，使得更为流畅
            if (mAdapter.itemCount > 10) {
                mHelper?.scrollOutsideEnable(true)
            }
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
                                        val pagerView: EmotionPagerView = getRoot().findViewById(R.id.view_pager)
                                        val viewPagerSize = height - DisplayUtils.dip2px(this@ContentActivity, 30f)
                                        pagerView.buildEmotionViews(
                                                getRoot().findViewById<View>(R.id.pageIndicatorView) as PageIndicatorView,
                                                editView,
                                                Emotions.getEmotions(), width, viewPagerSize)
                                    }
                                    R.id.panel_addition -> {
                                    }
                                }
                            }
                        }
                    }
                    .contentCanScrollOutside(false)
                    .logTrack(true) //output log
                    .build()
            recyclerView.setResetPanel { mHelper?.hookSystemBackByPanelSwitcher() }
        }
    }

    override fun onBackPressed() {
        if (mHelper != null && mHelper!!.hookSystemBackByPanelSwitcher()) {
            return
        }
        super.onBackPressed()
    }

    companion object {
        @JvmStatic
        fun start(context: Context, @ContentType type: Int) {
            val intent = Intent(context, ContentActivity::class.java)
            intent.putExtra(Constants.KEY_CONTENT_TYPE, type)
            context.startActivity(intent)
        }

        private const val TAG = "ChatActivity"
    }
}