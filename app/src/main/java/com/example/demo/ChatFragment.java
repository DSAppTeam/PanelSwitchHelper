package com.example.demo;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.view.PanelView;
import com.effective.databinding.CommonChatWithTitlebarLayoutBinding;
import com.example.demo.anno.PageType;
import com.example.demo.chat.ChatAdapter;
import com.example.demo.chat.ChatInfo;
import com.example.demo.emotion.EmotionPagerView;
import com.example.demo.emotion.Emotions;
import com.example.demo.systemui.StatusbarHelper;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private CommonChatWithTitlebarLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private Runnable mScrollToBottomRunnable;
    private static final String TAG = "ChatFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.common_chat_with_titlebar_layout, container, false);

        int type = getArguments().getInt(Constants.KEY_PAGE_TYPE);

        switch (type) {
            case PageType.COLOR_STATUS_BAR: {
                StatusbarHelper.setStatusBarColor(getActivity(), ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.common_page_bg_color));
                mBinding.titleBar.setVisibility(View.VISIBLE);
                mBinding.titleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mBinding.title.setText(R.string.app_name);
                break;
            }
            case PageType.TRANSPARENT_STATUS_BAR: {
                StatusbarHelper.setStatusBarColor(getActivity(), Color.TRANSPARENT);
                mBinding.titleBar.setVisibility(View.VISIBLE);
                mBinding.title.setText(R.string.app_name);
                break;
            }
            default: {
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.common_page_bg_color));
                mBinding.titleBar.setVisibility(type == PageType.DEFAULT ? View.GONE : View.VISIBLE);
                mBinding.titleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mBinding.title.setText(R.string.app_name);
            }
        }
        initView();
        return mBinding.getRoot();
    }


    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerView.setLayoutManager(mLinearLayoutManager);
        ((SimpleItemAnimator) mBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        List<ChatInfo> chatInfos = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            chatInfos.add(ChatInfo.CREATE("模拟数据第" + (i + 1) + "条"));
        }
        mAdapter = new ChatAdapter(getContext(), chatInfos);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mBinding.editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getContext(), "当前没有输入", Toast.LENGTH_SHORT).show();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .bindPanelSwitchLayout(R.id.panel_switch_layout)
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
                        }
                    })
                    //可选
                    .addViewClickListener(new OnViewClickListener() {
                        @Override
                        public void onClickBefore(View view) {
                            Log.d(TAG, "点击了View : " + view);
                        }
                    })
                    //可选
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            Log.d(TAG, "唤起系统输入法");
                            scrollToBottom();
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
                            scrollToBottom();
                            mBinding.emotionBtn.setSelected(view.getId() == R.id.panel_emotion ? true : false);
                        }

                        @Override
                        public void onPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            switch (panelView.getId()) {
                                case R.id.panel_emotion: {
                                    EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
                                    int viewPagerSize = height - Utils.dip2px(getContext(), 30f);
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

    public boolean hookOnBackPressed() {
        return mHelper != null && mHelper.hookSystemBackByPanelSwitcher();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
