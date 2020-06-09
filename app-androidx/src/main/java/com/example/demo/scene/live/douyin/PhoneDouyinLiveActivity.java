package com.example.demo.scene.live.douyin;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.effective.databinding.ActivityDouyinLiveLayoutBinding;
import com.example.demo.scene.chat.emotion.EmotionPagerView;
import com.example.demo.scene.chat.emotion.Emotions;
import com.example.demo.systemui.StatusbarHelper;
import com.example.demo.util.DisplayUtils;

import java.util.LinkedList;


/**
 * 手机直播效果，只有竖屏
 * created by yummylau on 2020/06/01
 */
public class PhoneDouyinLiveActivity extends AppCompatActivity {

    private ActivityDouyinLiveLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private LinearLayoutManager mLinearLayoutManager;
    private ChatAdapter mAdapter;
    private Handler handler = new Handler();
    private Runnable insertMessage = new Runnable() {
        @Override
        public void run() {
            if (mAdapter != null) {
                mAdapter.insertMessage(new Message("yummylau", "我来啦 " + mAdapter.getItemCount()));
            }
            handler.postDelayed(this, 5000);
        }
    };
    private static final String TAG = PhoneDouyinLiveActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusbarHelper.setStatusBarColor(this, Color.BLACK);
        initView();
    }

    private void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_douyin_live_layout);
        Pair<Integer, Integer> size = DisplayUtils.getScreenSize(this);
        mBinding.videoView.getLayoutParams().width = size.first;
        mBinding.videoView.getLayoutParams().height = size.second;
        mBinding.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.yexiaoma));
        mBinding.videoView.setOnPreparedListener(mp -> {
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mp.start();
            mp.setLooping(true);
        });
        mBinding.videoView.start();
        mBinding.gift.setSelected(true);
        mBinding.actionRealLayout.setVisibility(View.GONE);
        mBinding.actionShowLayout.setVisibility(View.VISIBLE);
        mBinding.close.setOnClickListener(v -> finish());
        mBinding.inputAction.setOnClickListener(v -> {
            mBinding.actionRealLayout.setVisibility(View.VISIBLE);
            mBinding.actionShowLayout.setVisibility(View.GONE);
            mBinding.input.requestFocus();
        });
        mBinding.send.setOnClickListener(v -> {
            mAdapter.insertMessage(new Message("yummylau", mBinding.input.getText().toString()));
            mBinding.input.setText("");
        });

        mLinearLayoutManager = new LinearLayoutManager(this);
        mBinding.commentList.setLayoutManager(mLinearLayoutManager);
        LinkedList<Message> messages = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            messages.addFirst(new Message("yummylau" + i, "唱的好好听哦"));
        }
        mAdapter = new ChatAdapter(this, messages);
        mBinding.commentList.setAdapter(mAdapter);
        scrollToBottom();
        handler.postDelayed(insertMessage, 5000);
    }

    private void scrollToBottom() {
        mBinding.getRoot().post(() -> mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount()-1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(insertMessage);
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
                            mBinding.actionRealLayout.setVisibility(View.GONE);
                            mBinding.actionShowLayout.setVisibility(View.VISIBLE);
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
                                        int viewPagerSize = height - DisplayUtils.dip2px(PhoneDouyinLiveActivity.this, 30f);
                                        pagerView.buildEmotionViews(
                                                mBinding.getRoot().findViewById(R.id.pageIndicatorView),
                                                mBinding.input,
                                                Emotions.getEmotions(), width, viewPagerSize);
                                        break;
                                    }
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
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
            return;
        }
        super.onBackPressed();
    }


    public class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {

        private LinkedList<Message> list;
        private Context context;

        public ChatAdapter(Context context, LinkedList<Message> list) {
            this.context = context;
            this.list = list;
            if (this.list == null) {
                this.list = new LinkedList<>();
            }
        }

        public void insertMessage(Message message) {
            if (message != null) {
                this.list.addLast(message);
                notifyItemInserted(getItemCount()-1);
                scrollToBottom();
            }
        }

        @NonNull
        @Override
        public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChatHolder(LayoutInflater.from(context).inflate(R.layout.holder_douyin_chat_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
            holder.bindData(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    public class ChatHolder extends RecyclerView.ViewHolder {
        public ChatHolder(View itemView) {
            super(itemView);
        }

        public void bindData(Message message) {
            ((TextView) itemView.findViewById(R.id.name)).setText(message.name + ": ");
            ((TextView) itemView.findViewById(R.id.content)).setText(message.content);
            itemView.findViewById(R.id.name).setOnClickListener(v -> Toast.makeText(PhoneDouyinLiveActivity.this, "点击了用户 " + message.name, Toast.LENGTH_SHORT).show());
        }
    }

    public class Message {
        public String name;
        public String content;

        public Message(String name, String content) {
            this.name = name;
            this.content = content;
        }
    }
}
