package com.example.demo.scene.api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import com.example.demo.Constants
import com.example.demo.anno.ApiResetType
import com.example.demo.scene.chat.adapter.ChatAdapter
import com.example.demo.scene.chat.adapter.ChatInfo
import com.example.demo.scene.chat.emotion.EmotionPagerView
import com.example.demo.scene.chat.emotion.Emotions
import com.example.demo.scene.chat.view.AutoHidePanelRecyclerView
import com.example.demo.util.DisplayUtils
import com.rd.PageIndicatorView

/**
 * 演示如何正确使用 auto_reset (点击内容区域自动隐藏面板) 的功能
 * auto_reset 可以被定义在 container 的扩展属性内,包含
 * 1. auto_reset_enable 表示是否支持点击内容区域内隐藏面板，默认打开。
 *    打开时，当区域内子view没有消费事件时，则会默认消费该事件并自动隐藏。
 * 2. auto_reset_area，当且仅当 auto_reset_enable 为 true 才有效，指定一个 view 的id，为 1 的消费事件限定区域。
 *    比如场景一，指定了空白透明 view  ，view 没有消费事件时，则才会自动隐藏面板；
 *    比如场景二，指定了列表的 recyclerview ，则recyclerview 没有消费事件时，则才会自动隐藏面板；
 *    比如场景三，场景二 recyclerview 时显然很难不消费事件，如果 holder 被点击（比如聊天项），则应该被正常消费，
 *              如果点击 recyclerview 内的空白，recyclerview 也会默认消费，因为可能需要滑动、
 *              为了解决这种下层应该消费点击滑动事件，而上层容器应该获取点击并自动隐藏，HookActionUpRecyclerView 就是该场景的 DEMO
 *              需要把下层消费完之后的 ACTION_UP 返回 false 让上层有机会处理。 ContentContainerImpl 内的实现预留了这种可能，用于处理该复杂场景。
 *
 * 可参考自定义 [CusContentContainer] 或库提供的多种Container实现类
 * created by yummylau on 2020/06/06
 */
class ResetActivity : AppCompatActivity() {
    private var mHelper: PanelSwitchHelper? = null
    private lateinit var mAdapter: ChatAdapter
    private var mLinearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        when (intent.getIntExtra(Constants.KEY_CONTENT_TYPE, ApiResetType.DISABLE)) {
            ApiResetType.DISABLE -> {
                setContentView(R.layout.activity_api_auto_reset_disable_layout)
                findViewById<TextView>(R.id.title).text = "关闭自动隐藏面板,点击空白处无法隐藏面板"
            }
            ApiResetType.ENABLE -> {
                setContentView(R.layout.activity_api_auto_reset_enable_layout)
                findViewById<TextView>(R.id.title).text = "打开自动隐藏面板,点击空白处即可隐藏面板"
            }
            ApiResetType.ENABLE_EmptyView -> {
                setContentView(R.layout.activity_api_auto_reset_enable_on_empty_view_layout)
                findViewById<TextView>(R.id.title).text = "打开自动隐藏面板-自定义EmptyView，不消费事件，点击可隐藏面板"
            }
            ApiResetType.ENABLE_RecyclerView -> {
                setContentView(R.layout.activity_api_auto_reset_enable_on_recyclerview_layout)
                findViewById<TextView>(R.id.title).text = "打开自动隐藏面板-原生RecyclerView，默认消费事件，点击无法隐藏面板"
            }
            ApiResetType.ENABLE_HookActionUpRecyclerview -> {
                setContentView(R.layout.activity_api_auto_reset_enbale_on_cus_recyclerview_layout)
                findViewById<TextView>(R.id.title).text = "打开自动隐藏面板-HookActionUpRecyclerView，重写消费逻辑，点击非空白可隐藏面板，列表可滑动，holder可点击"
            }
        }
        initView()
    }

    private val recyclerView: RecyclerView
        get() = findViewById<View>(R.id.recycler_view) as RecyclerView

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
        recyclerView.adapter = mAdapter
        sendView.setOnClickListener(View.OnClickListener {
            val content = editView.text.toString()
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this@ResetActivity, "当前没有输入", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            mAdapter.insertInfo(ChatInfo.CREATE(content))
            editView.text = null
            scrollToBottom()
        })
    }

    private fun scrollToBottom() {
        recyclerView.post { mLinearLayoutManager?.scrollToPosition(mAdapter.itemCount - 1) }
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
                                        val viewPagerSize = height - DisplayUtils.dip2px(this@ResetActivity, 30f)
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
        if (findViewById<RecyclerView>(R.id.recycler_view) is AutoHidePanelRecyclerView) {
            findViewById<AutoHidePanelRecyclerView>(R.id.recycler_view).setPanelSwitchHelper(mHelper)
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
        fun start(context: Context, @ApiResetType type: Int) {
            val intent = Intent(context, ResetActivity::class.java)
            intent.putExtra(Constants.KEY_CONTENT_TYPE, type)
            context.startActivity(intent)
        }

        private const val TAG = "ResetActivity"
    }

}