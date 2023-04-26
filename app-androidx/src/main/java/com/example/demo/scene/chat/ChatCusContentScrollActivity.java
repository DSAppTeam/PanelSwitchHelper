package com.example.demo.scene.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.ContentScrollMeasurer;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.utils.PanelUtil;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.effective.databinding.CommonChatLayoutBinding;
import com.example.demo.scene.chat.adapter.ChatAdapter;
import com.example.demo.scene.chat.adapter.ChatInfo;
import com.example.demo.scene.chat.emotion.EmotionPagerView;
import com.example.demo.scene.chat.emotion.Emotions;
import com.example.demo.util.DisplayUtils;
import com.rd.PageIndicatorView;

/**
 * 自定义内容面板内的scroll
 * 处理单独的兼容聊天场景，比如聊天页面内部可能出现一些复层的UI，如新消息提示，悬浮view等，可以自定义兼容滑动行为。
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ChatCusContentScrollActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, ChatCusContentScrollActivity.class);
        context.startActivity(intent);
    }

    private CommonChatLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String TAG = ChatCusContentScrollActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout);
        mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
        mBinding.tipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PanelUtil.clearData(ChatCusContentScrollActivity.this);
            }
        });
        initView();
    }

    private void initView() {
        mBinding.tipViewTop.setVisibility(View.VISIBLE);
        mBinding.tipViewBottom.setVisibility(View.VISIBLE);
        mBinding.tipView.setVisibility(View.VISIBLE);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new ChatAdapter(this, 4);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.send.setOnClickListener(v -> {
            String content = mBinding.editText.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(ChatCusContentScrollActivity.this, "当前没有输入", Toast.LENGTH_SHORT).show();
                return;
            }
            mAdapter.insertInfo(ChatInfo.CREATE(content));
            mBinding.editText.setText(null);
            scrollToBottom();
        });
    }


    private void scrollToBottom() {
        mBinding.getRoot().post(() -> mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    //可选
                    .addKeyboardStateListener((visible, height) -> Log.d(TAG, "系统键盘是否可见 : " + visible + " 高度为：" + height))
                    .addEditTextFocusChangeListener((view, hasFocus) -> {
                        Log.d(TAG, "输入框是否获得焦点 : " + hasFocus);
                        if (hasFocus) {
                            scrollToBottom();
                        }
                    })
                    //可选
                    .addViewClickListener(view -> {
                        switch (view.getId()) {
                            case R.id.edit_text:
                            case R.id.add_btn:
                            case R.id.emotion_btn: {
                                scrollToBottom();
                            }
                        }
                        Log.d(TAG, "点击了View : " + view);
                    })
                    //可选
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            Log.d(TAG, "唤起系统输入法");
                            mBinding.emotionBtn.setSelected(false);
                            scrollToBottom();
                        }

                        @Override
                        public void onNone() {
                            Log.d(TAG, "隐藏所有面板");
                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onPanel(IPanelView view) {
                            Log.d(TAG, "唤起面板 : " + view);
                            if (view instanceof PanelView) {
                                mBinding.emotionBtn.setSelected(((PanelView) view).getId() == R.id.panel_emotion ? true : false);
                                scrollToBottom();
                            }
                        }

                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if (panelView instanceof PanelView) {
                                switch (((PanelView) panelView).getId()) {
                                    case R.id.panel_emotion: {
                                        EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
                                        int viewPagerSize = height - DisplayUtils.dip2px(ChatCusContentScrollActivity.this, 30f);
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
                        }
                    })
                    /**
                     * 根据recyclerview的内容填充度来滑动
                     */
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {
                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return defaultDistance - listUnfilledHeight;
                        }

                        @Override
                        public int getScrollViewId() {
                            return R.id.recycler_view;
                        }
                    })
                    /**
                     * tipViewBottom 确保底部输入栏不遮挡
                     */
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {
                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return defaultDistance - bottomUnfilledHeight;
                        }

                        @Override
                        public int getScrollViewId() {
                            return R.id.tip_view_bottom;
                        }
                    })
                    /**
                     * tipViewTop 不跟随滑动
                     */
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {
                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return 0;
                        }

                        @Override
                        public int getScrollViewId() {
                            return R.id.tip_view_top;
                        }
                    })
                    /**
                     * 默认实现，contentContainer 内部的子view会随容器向上scroll defaultDistance 距离
                     */
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {

                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return defaultDistance;
                        }

                        @Override
                        public int getScrollViewId() {
                            return R.id.tip_view;
                        }
                    })
                    .logTrack(true)             //output log
                    .build();
            mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        int position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        View lastChildView = layoutManager.findViewByPosition(position);
                        if (lastChildView != null) {
                            int bottom = lastChildView.getBottom();
                            int listHeight = mBinding.recyclerView.getHeight() - mBinding.recyclerView.getPaddingBottom();
                            listUnfilledHeight = listHeight - bottom;
                        }
                    }
                }
            });
            mBinding.recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    bottomUnfilledHeight = mBinding.bottomAction.getTop() - mBinding.tipViewBottom.getBottom();
                }
            });
        }
        mBinding.recyclerView.setPanelSwitchHelper(mHelper);
    }

    private int listUnfilledHeight = 0;
    private int bottomUnfilledHeight = 0;


    @Override
    public void onBackPressed() {
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
            return;
        }
        super.onBackPressed();
    }
}
