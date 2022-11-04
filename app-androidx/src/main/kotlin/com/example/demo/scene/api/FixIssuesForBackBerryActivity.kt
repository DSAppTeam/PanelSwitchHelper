package com.example.demo.scene.api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.effective.R
import com.effective.android.panel.PanelSwitchHelper
import com.effective.android.panel.compat.KeyboardHeightCompat
import com.effective.android.panel.view.panel.PanelView
import com.effective.databinding.CommonChatLayoutBinding
import com.example.demo.scene.chat.adapter.ChatAdapter
import com.example.demo.scene.chat.adapter.ChatInfo
import com.example.demo.scene.chat.emotion.EmotionPagerView
import com.example.demo.scene.chat.emotion.Emotions
import com.example.demo.util.DisplayUtils

/**
 * author : linzheng
 * e-mail : z.hero.dodge@gmail.com
 * time   : 2022/9/19
 * desc   :
 * version: 1.0
 */
class FixIssuesForBackBerryActivity : AppCompatActivity() {

    companion object {
        const val TAG = "FixIssuesForBackBerry"
        @JvmStatic
        fun start(context: Context?) {
            if (context == null) {
                return
            }
            val intent = Intent(context, FixIssuesForBackBerryActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var mBinding: CommonChatLayoutBinding
    private lateinit var mHelper: PanelSwitchHelper
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout)
        initView()
    }


    private fun initView() {

        supportActionBar?.title = "Keyboard Height On BackBerry"

        mLinearLayoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.layoutManager = mLinearLayoutManager
        mAdapter = ChatAdapter(this, 20)
        mBinding.recyclerView.adapter = mAdapter
        mBinding.send.setOnClickListener { v ->
            val content = mBinding.editText.text.toString()
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this@FixIssuesForBackBerryActivity, "当前没有输入", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mAdapter.insertInfo(ChatInfo.CREATE(content))
            mBinding.editText.setText(null)
            scrollToBottom()
        }
    }


    private fun scrollToBottom() {
        mBinding.root.post { mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1) }
    }


    override fun onStart() {
        super.onStart()

        if (!this::mHelper.isInitialized) {
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
                .addPanelChangeListener {
                    onKeyboard {
                        Log.d(TAG, "唤起系统输入法")
                    }
                    onNone {
                        Log.d(TAG, "隐藏所有面板")
                    }
                    onPanel {
                        Log.d(TAG, "唤起面板 : $it")
                        if (it is PanelView) {
                            mBinding.emotionBtn.isSelected = it.id == R.id.panel_emotion
                        }
                    }
                    onPanelSizeChange { panelView, _, _, _, width, height ->
                        if (panelView is PanelView) {
                            when (panelView.id) {
                                R.id.panel_emotion -> {
                                    val pagerView: EmotionPagerView = findViewById(R.id.view_pager)
                                    val viewPagerSize = height - DisplayUtils.dip2px(this@FixIssuesForBackBerryActivity, 30f)
                                    pagerView.buildEmotionViews(
                                        findViewById(R.id.pageIndicatorView),
                                        mBinding.editText,
                                        Emotions.getEmotions(), width, viewPagerSize
                                    )
                                }
                                R.id.panel_addition -> {
                                }
                            }
                        }
                    }
                }
                .addPanelHeightMeasurer {
                    // compat BackBerry with a Physics Keyboard
                    synchronizeKeyboardHeight { !KeyboardHeightCompat.hasPhysicsKeyboard() }

                    getTargetPanelDefaultHeight {
                        val defaultHeight = DisplayUtils.dip2px(this@FixIssuesForBackBerryActivity, 300f)
                        return@getTargetPanelDefaultHeight KeyboardHeightCompat.panelDefaultHeight(defaultHeight)
                    }

                    getPanelTriggerId { R.id.emotion_btn }

                }

                .addPanelHeightMeasurer {
                    // compat BackBerry with a Physics Keyboard
                    synchronizeKeyboardHeight { !KeyboardHeightCompat.hasPhysicsKeyboard() }

                    getTargetPanelDefaultHeight {
                        val defaultHeight = DisplayUtils.dip2px(this@FixIssuesForBackBerryActivity, 300f)
                        return@getTargetPanelDefaultHeight KeyboardHeightCompat.panelDefaultHeight(defaultHeight)
                    }

                    getPanelTriggerId { R.id.add_btn }
                }
                .logTrack(true) //output log
                .build()
        }

    }


}