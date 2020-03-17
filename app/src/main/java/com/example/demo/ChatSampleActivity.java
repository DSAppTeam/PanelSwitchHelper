package com.example.demo;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.view.PanelView;
import com.effective.databinding.ActivityChatLayoutBinding;
import com.example.demo.chat.ChatAdapter;
import com.example.demo.chat.ChatInfo;
import com.example.demo.emotion.EmotionPagerView;
import com.example.demo.emotion.Emotions;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ChatSampleActivity extends AppCompatActivity {
    private ActivityChatLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private Runnable mScrollToBottomRunnable;
    private static final String TAG = "ChatSampleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_layout);
        int type = getIntent().getIntExtra(Constants.KEY_PAGE_TYPE, PageType.DEFAULT);
        //这里只是demo提前隐藏标题栏，如果应用自己实现了标题栏或者通过自定义view开发标题栏，根据业务隐藏或者设置透明色就可以了，随便扩展
        if(type == PageType.TRANSPARENT_STATUS_BAR){
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout);

        switch (type) {
            case PageType.COLOR_STATUS_BAR: {
                StatusbarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
                //可以在代码设置，也可以在xml设置
                mBinding.getRoot().setFitsSystemWindows(true);
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
                break;
            }
            case PageType.TRANSPARENT_STATUS_BAR: {
                StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT);
                mBinding.getRoot().setFitsSystemWindows(true);
                mBinding.getRoot().setBackgroundResource(R.drawable.bg_gradient);
                mBinding.panelContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
                break;
            }
            default: {
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
            }
        }
>>>>>>> Stashed changes:app/src/main/java/com/example/demo/ChatActivity.java
        initView();
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(mLinearLayoutManager);
        ((SimpleItemAnimator) mBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        List<ChatInfo> chatInfos = new ArrayList<>();
        for(int i = 0; i < 50; i++){
            chatInfos.add(ChatInfo.CREATE("模拟数据第" + (i+1) + "条"));
        }
        mAdapter = new ChatAdapter(this,chatInfos);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mBinding.editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(ChatSampleActivity.this, "当前没有输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAdapter.insertInfo(ChatInfo.CREATE(content));
                mBinding.editText.setText(null);
                scrollToBottom();
            }
        });
        mScrollToBottomRunnable = new Runnable() {
            @Override
            public void run() {
                if (mAdapter.getItemCount() > 0) {
                    mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        };
    }

    private void scrollToBottom() {
        mBinding.recyclerView.postDelayed(mScrollToBottomRunnable, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .bindPanelSwitchLayout(R.id.panel_switch_layout)
                    .bindPanelContainerId(R.id.panel_container)
                    .bindContentContainerId(R.id.content_view)
                    //可选
                    .addKeyboardStateListener(new OnKeyboardStateListener() {
                        @Override
                        public void onKeyboardChange(boolean visible) {
                            if(visible){
                                scrollToBottom();
                            }
                            Log.d(TAG, "系统键盘是否可见 : " + visible);

                        }
                    })
                    //可选
                    .addEdittextFocesChangeListener(new OnEditFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            Log.d(TAG, "输入框是否获得焦点 : " + hasFocus);
                        }
                    })
                    //可选
                    .addViewClickListener(new OnViewClickListener() {
                        @Override
                        public void onViewClick(View view) {
                            if(view.getId() != R.id.empty_view){
                                scrollToBottom();
                            }
                            Log.d(TAG, "点击了View : " + view);
                        }
                    })
                    //可选
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            Log.d(TAG, "唤起系统输入法");
                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            Log.d(TAG, "隐藏所有面板");
                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onPanel(PanelView view) {
                            Log.d(TAG, "唤起面板 : " + view);
                            mBinding.emotionBtn.setSelected(view.getId() == R.id.panel_emotion ? true : false);
                        }

                        @Override
                        public void onPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            switch (panelView.getId()) {
                                case R.id.panel_emotion: {
                                    EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
                                    int viewPagerSize = height - Utils.dip2px(ChatSampleActivity.this, 30f);
                                    pagerView.buildEmotionViews(
                                            (PageIndicatorView) mBinding.getRoot().findViewById(R.id.pageIndicatorView),
                                            mBinding.editText,
                                            Emotions.getEmotions(), width, viewPagerSize);
                                    break;
                                }
                                case R.id.panel_addition: {
                                    //auto center,nothing to do
                                    break;
                                }
                            }
                        }
                    })
                    .logTrack(true)             //output log
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
        if (mHelper != null && mHelper.hookSystemBackForHindPanel()) {
            return;
        }
        super.onBackPressed();
    }

    @Override

    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.onDestroy();
        }
//        mBinding.recyclerView.removeCallbacks(mScrollToBottomRunnable);
    }
}
