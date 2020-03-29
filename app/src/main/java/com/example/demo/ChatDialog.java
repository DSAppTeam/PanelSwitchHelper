package com.example.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.view.PanelView;
import com.effective.databinding.CommonChatWithTitlebarLayoutBinding;
import com.example.demo.chat.ChatAdapter;
import com.example.demo.chat.ChatInfo;
import com.example.demo.chat.CusRecyclerView;
import com.example.demo.emotion.EmotionPagerView;
import com.example.demo.emotion.Emotions;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class ChatDialog extends Dialog implements DialogInterface.OnKeyListener {

    private CommonChatWithTitlebarLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String TAG = "ChatDialog";
    private Activity activity;

    public ChatDialog(Activity context) {
        super(context);
        this.activity = context;
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.common_chat_with_titlebar_layout, null, false);
        setContentView(mBinding.getRoot());
        mBinding.titleBar.setVisibility(View.VISIBLE);
        mBinding.titleBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mBinding.title.setText(R.string.dialog_name);
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            window.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(),R.color.common_page_bg_color)));
        }
        setCanceledOnTouchOutside(true);
        setOnKeyListener(this);
        initView();
    }

    @Override
    public void show() {
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(activity.getWindow(), mBinding.getRoot())
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
            mBinding.recyclerView.setResetPanel(new CusRecyclerView.ResetPanel() {
                @Override
                public void resetPanel() {
                    mHelper.hookSystemBackByPanelSwitcher();
                }
            });
        }
        super.show();
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
    }

    private void scrollToBottom() {
        mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
                return true;
            } else {
                dismiss();
                return true;
            }
        }
        return false;
    }
}
