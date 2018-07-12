package com.example.demo;

import android.databinding.DataBindingUtil;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.Constants;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.view.PanelView;
import com.effective.databinding.ActivitySampleLayoutBinding;
import com.example.demo.adapter.ChatAdapter;
import com.example.demo.bean.ChatInfo;
import com.example.demo.emotion.EmotionPagerView;
import com.example.demo.emotion.EmotionView;
import com.example.demo.emotion.Emotions;
import com.rd.PageIndicatorView;

/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class SampleActivity extends AppCompatActivity {

    private ActivitySampleLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private Runnable mScrollToBottomRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample_layout);
        initView();
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(mLinearLayoutManager);
        ((SimpleItemAnimator) mBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter = new ChatAdapter(this);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mBinding.editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(SampleActivity.this, "当前没有输入", Toast.LENGTH_SHORT).show();
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
        mBinding.recyclerView.postDelayed(mScrollToBottomRunnable, 300);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .bindPanelSwitchLayout(R.id.panel_switch_layout)
                    .bindPanelContainerId(R.id.panel_container)
                    .bindContentContainerId(R.id.content_view)
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            scrollToBottom();
                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onPanel(PanelView view) {
                            scrollToBottom();
                            mBinding.emotionBtn.setSelected(view.getId() == R.id.panel_emotion ? true : false);
                        }

                        @Override
                        public void onPanelSizeChange(PanelView panelView, int oldWidth, int oldHeight, int width, int height) {
                            switch (panelView.getId()){
                                case R.id.panel_emotion:{
                                    EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
                                    int viewPagerSize = height - Utils.dip2px(SampleActivity.this, 30f);
                                    pagerView.buildEmotionViews(
                                            (PageIndicatorView) mBinding.getRoot().findViewById(R.id.pageIndicatorView),
                                            mBinding.editText,
                                            Emotions.getEmotions(), width, viewPagerSize);
                                    break;
                                }
                                case R.id.panel_addition:{
                                    //auto center,nothing to do
                                    break;
                                }
                            }

                        }
                    })
                    .logTrack(true)
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
        mBinding.recyclerView.removeCallbacks(mScrollToBottomRunnable);
    }
}
