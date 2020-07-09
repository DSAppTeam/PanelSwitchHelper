package com.example.demo.scene.chat;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.ViewDistanceMeasurer;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.effective.databinding.CommonChatLayoutBinding;
import com.example.demo.Constants;
import com.example.demo.anno.ChatPageType;
import com.example.demo.scene.chat.adapter.ChatAdapter;
import com.example.demo.scene.chat.adapter.ChatInfo;
import com.example.demo.scene.chat.emotion.EmotionPagerView;
import com.example.demo.scene.chat.emotion.Emotions;
import com.example.demo.systemui.StatusbarHelper;
import com.example.demo.util.DisplayUtils;
import com.rd.PageIndicatorView;

import org.jetbrains.annotations.NotNull;

/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ChatActivity extends AppCompatActivity {

    public static void start(Context context, @ChatPageType int type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.KEY_PAGE_TYPE, type);
        context.startActivity(intent);
    }

    private CommonChatLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private static final String TAG = ChatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra(Constants.KEY_PAGE_TYPE, ChatPageType.DEFAULT);
        switch (type) {
            case ChatPageType.TITLE_BAR: {
                mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout);
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
                getSupportActionBar().setTitle("Activity-有标题栏");
                break;
            }
            case ChatPageType.COLOR_STATUS_BAR: {
                mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout);
                StatusbarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
                mBinding.statusBar.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Activity-有标题栏，状态栏着色");
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
                break;
            }
            case ChatPageType.DEFAULT: {
                supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
                mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout);
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
                break;
            }
            case ChatPageType.CUS_TITLE_BAR: {
                supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
                mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout);
                mBinding.cusTitleBar.setVisibility(View.VISIBLE);
                mBinding.title.setText("Activity-自定义标题栏");
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
                break;
            }
            case ChatPageType.TRANSPARENT_STATUS_BAR: {
                supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
                mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout);
                mBinding.statusBar.setVisibility(View.VISIBLE);
                StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT);
                mBinding.getRoot().setBackgroundResource(R.drawable.bg_gradient);
                break;
            }
            case ChatPageType.TRANSPARENT_STATUS_BAR_DRAW_UNDER: {
                supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
                mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout);
                StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT);
                mBinding.getRoot().setBackgroundResource(R.drawable.bg_gradient);
                break;
            }
        }
        initView();
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new ChatAdapter(this, 4);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.send.setOnClickListener(v -> {
            String content = mBinding.editText.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(ChatActivity.this, "当前没有输入", Toast.LENGTH_SHORT).show();
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
                                        int viewPagerSize = height - DisplayUtils.dip2px(ChatActivity.this, 30f);
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
                    .addDistanceMeasurer(new ViewDistanceMeasurer() {
                        @Override
                        public int getUnfilledHeight() {
                            return unfilledHeight;
                        }

                        @NotNull
                        @Override
                        public String getViewTag() {
                            return "recycler_view";
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
                        int childCount = recyclerView.getChildCount();
                        if (childCount > 0) {
                            View lastChildView = recyclerView.getChildAt(childCount - 1);
                            int bottom = lastChildView.getBottom();
                            int listHeight = mBinding.recyclerView.getHeight() - mBinding.recyclerView.getPaddingBottom();
                            int listUnfilledHeight = listHeight - bottom;
                            if (listUnfilledHeight > 0) {
                                unfilledHeight = listUnfilledHeight;
                            } else {
                                unfilledHeight = 0;
                            }
                        }
                    }
                }
            });
        }
    }

    private int unfilledHeight = 0;


    @Override
    public void onBackPressed() {
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
            return;
        }
        super.onBackPressed();
    }
}