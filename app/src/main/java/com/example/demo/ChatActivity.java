package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.view.PanelView;
import com.effective.databinding.CommonChatLayoutBinding;
import com.example.demo.anno.PageType;
import com.example.demo.chat.ChatAdapter;
import com.example.demo.chat.ChatInfo;
import com.example.demo.chat.CusRecyclerView;
import com.example.demo.emotion.EmotionPagerView;
import com.example.demo.emotion.Emotions;
import com.example.demo.systemui.StatusbarHelper;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ChatActivity extends AppCompatActivity {

    public static void start(Context context, @PageType int type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.KEY_PAGE_TYPE, type);
        context.startActivity(intent);
    }

    private CommonChatLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra(Constants.KEY_PAGE_TYPE, PageType.DEFAULT);

        //这里只是demo提前隐藏标题栏，如果应用自己实现了标题栏或者通过自定义view开发标题栏，根据业务隐藏或者设置透明色就可以了，随便扩展
        if (type == PageType.TRANSPARENT_STATUS_BAR || type == PageType.TRANSPARENT_STATUS_BAR_DRAW_UNDER || type == PageType.DEFAULT || type == PageType.CUS_TOOLBAR) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.common_chat_layout);

        if (type == PageType.CUS_TOOLBAR) {
            mBinding.cusTitleBar.setVisibility(View.VISIBLE);
        }

        switch (type) {
            case PageType.COLOR_STATUS_BAR: {
                StatusbarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
                //可以在代码设置，也可以在xml设置
                mBinding.getRoot().setFitsSystemWindows(true);
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
                break;
            }
            case PageType.TRANSPARENT_STATUS_BAR: {
                mBinding.getRoot().setFitsSystemWindows(true);
                StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT);
                mBinding.getRoot().setBackgroundResource(R.drawable.bg_gradient);
                break;
            }
            case PageType.TRANSPARENT_STATUS_BAR_DRAW_UNDER: {
                mBinding.getRoot().setFitsSystemWindows(false);
                StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT);
                mBinding.getRoot().setBackgroundResource(R.drawable.bg_gradient);
                break;
            }
            default: {
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
            }
        }
        initView();
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(mLinearLayoutManager);
        ((SimpleItemAnimator) mBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        List<ChatInfo> chatInfos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            chatInfos.add(ChatInfo.CREATE("模拟数据第" + (i + 1) + "条"));
        }
        mAdapter = new ChatAdapter(this, chatInfos);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mBinding.editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(ChatActivity.this, "当前没有输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAdapter.insertInfo(ChatInfo.CREATE(content));
//                如果超过某些条目，可开启滑动外部，使得更为流畅
                if(mAdapter.getItemCount() > 10){
                    mHelper.scrollOutsideEnable(true);
                }
                mBinding.editText.setText(null);
                scrollToBottom();
            }
        });
    }

    private void scrollToBottom() {
        mBinding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        }) ;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    //可选
                    .addKeyboardStateListener(new OnKeyboardStateListener() {
                        @Override
                        public void onKeyboardChange(boolean visible) {
                            Log.d(TAG, "系统键盘是否可见 : " + visible);

                        }
                    })
                    //可选
                    .addEditTextFocusChangeListener(new OnEditFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            Log.d(TAG, "输入框是否获得焦点 : " + hasFocus);
                            if(hasFocus){
                                scrollToBottom();
                            }
                        }
                    })
                    //可选
                    .addViewClickListener(new OnViewClickListener() {
                        @Override
                        public void onClickBefore(View view) {
                            switch (view.getId()){
                                case R.id.edit_text:
                                case R.id.add_btn:
                                case R.id.emotion_btn:{
                                    scrollToBottom();
                                }
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
                            scrollToBottom();
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
                            scrollToBottom();
                        }

                        @Override
                        public void onPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            switch (panelView.getId()) {
                                case R.id.panel_emotion: {
                                    EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
                                    int viewPagerSize = height - Utils.dip2px(ChatActivity.this, 30f);
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
                    .contentCanScrollOutside(false)
                    .logTrack(true)             //output log
                    .build();
            mBinding.recyclerView.setResetPanel(new CusRecyclerView.ResetPanel() {
                @Override
                public void resetPanel() {
                    mHelper.hookSystemBackByPanelSwitcher();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
            return;
        }
        super.onBackPressed();
    }
}
