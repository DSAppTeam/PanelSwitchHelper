package com.example.demo.scene.live.huya;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.effective.android.panel.utils.DisplayUtil;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.effective.databinding.ActivityHuyaLiveLayoutBinding;
import com.example.demo.scene.chat.emotion.EmotionPagerView;
import com.example.demo.scene.chat.emotion.Emotions;
import com.example.demo.systemui.StatusbarHelper;
import com.example.demo.util.DisplayUtils;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * pc虎牙直播效果，竖屏顶部区域，横屏满屏
 * created by yummylau on 2020/06/01
 */
public class PcHuyaLiveActivity extends AppCompatActivity {

    private static final String TAG = "PcHuyaLiveActivity";
    private ActivityHuyaLiveLayoutBinding mBinding;
    private PcHuyaCommentPopWindow videoPopWindow;
    private LinearLayoutManager mLinearLayoutManager;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private Handler handler = new Handler();
    private Runnable insertMessage = new Runnable() {
        @Override
        public void run() {
            if (mAdapter != null) {
                mAdapter.insertMessage(new Message("yummylau", "模拟插入数据" + System.currentTimeMillis()));
            }
            handler.postDelayed(this, 1500);
        }
    };
    private Runnable showKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            if (videoPopWindow != null) {
                videoPopWindow.showKeyboard();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusbarHelper.setStatusBarColor(this, Color.BLACK);
        initView();
    }

    private void initView() {
        Pair<Integer, Integer> size = DisplayUtils.getScreenSize(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_huya_live_layout);
        mBinding.videoView.getLayoutParams().width = size.first;
        mBinding.videoView.getLayoutParams().height = size.first * 9 / 16;
        mBinding.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.uzi));
        mBinding.videoView.setOnPreparedListener(mp -> {
            mp.start();
            mp.setLooping(true);
        });
        mBinding.videoView.start();
        mBinding.checkout.setOnClickListener(v -> DisplayUtils.checkoutOrientation(PcHuyaLiveActivity.this));
        mBinding.back.setOnClickListener(v -> onBackPressed());
        View.OnClickListener inputClick = v -> {
            if(videoPopWindow == null){
                videoPopWindow = new PcHuyaCommentPopWindow(PcHuyaLiveActivity.this);
            }
            videoPopWindow.showAtLocation(mBinding.getRoot(), Gravity.NO_GRAVITY, 0, 0);
            mBinding.getRoot().postDelayed(showKeyboardRunnable, 200);
        };
        mBinding.inputH.setOnClickListener(inputClick);
        mBinding.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBinding.send.setEnabled(s.length() != 0);
            }
        });
        mBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.insertMessage(new Message("yummylau", mBinding.input.getText().toString()));
                mBinding.input.setText("");
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(this);
        mBinding.chatList.setLayoutManager(mLinearLayoutManager);
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            messages.add(new Message("yummylau" + i, "进入直播间"));
        }
        mAdapter = new ChatAdapter(this, messages);
        mBinding.chatList.setAdapter(mAdapter);
        scrollToBottom();
        handler.postDelayed(insertMessage, 1500);
        psize = DisplayUtils.getScreenSize(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (showKeyboardRunnable != null) {
            mBinding.getRoot().removeCallbacks(showKeyboardRunnable);
        }
        handler.removeCallbacks(insertMessage);
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
                                        int viewPagerSize = height - DisplayUtils.dip2px(PcHuyaLiveActivity.this, 30f);
                                        pagerView.buildEmotionViews(
                                                (PageIndicatorView) mBinding.getRoot().findViewById(R.id.pageIndicatorView),
                                                mBinding.input,
                                                Emotions.getEmotions(), width, viewPagerSize);
                                        break;
                                    }
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
        if (!DisplayUtils.isPortrait(this)) {
            if (videoPopWindow != null && videoPopWindow.isShowing()) {
                videoPopWindow.dismiss();
            } else {
                DisplayUtils.checkoutOrientation(PcHuyaLiveActivity.this);
            }
            return;
        } else {
            if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
                return;
            }
        }
        super.onBackPressed();
    }

    Pair<Integer, Integer> psize;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBinding.videoContainer.getLayoutParams();
        RelativeLayout.LayoutParams videoViewLayoutParams = (RelativeLayout.LayoutParams) mBinding.videoView.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            checkoutSystemUIMode(true);
            psize = DisplayUtils.getScreenSize(this);
            layoutParams.width = psize.first;
            videoViewLayoutParams.width = psize.first;
            layoutParams.height = psize.first * 9 / 16;
            videoViewLayoutParams.height = psize.first * 9 / 16;
            mBinding.inputH.setVisibility(View.GONE);
            mBinding.panelRoot.setVisibility(View.VISIBLE);
            mBinding.checkout.setVisibility(View.VISIBLE);
        } else {
            checkoutSystemUIMode(false);
            Pair<Integer, Integer> size = DisplayUtils.getScreenSize(this);
            layoutParams.width = size.first;
            videoViewLayoutParams.width = size.first;
            layoutParams.height = psize.first;
            videoViewLayoutParams.height = psize.first;
            mBinding.inputH.setVisibility(View.VISIBLE);
            mBinding.panelRoot.setVisibility(View.GONE);
            mBinding.checkout.setVisibility(View.GONE);
        }
        mBinding.videoContainer.setLayoutParams(layoutParams);
        mBinding.videoView.setLayoutParams(videoViewLayoutParams);
    }

    private void checkoutSystemUIMode(boolean isP){
        if(isP){
            getWindow().getDecorView().setSystemUiVisibility(0); //重置
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            StatusbarHelper.setStatusBarColor(this, Color.BLACK);
        }else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                Window window = getWindow();
                getWindow().getDecorView().setSystemUiVisibility(0);
                window.getDecorView().setSystemUiVisibility(
                        window.getDecorView().getSystemUiVisibility() |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && !DisplayUtil.isPortrait(this)){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                Window window = getWindow();
                getWindow().getDecorView().setSystemUiVisibility(0);
                window.getDecorView().setSystemUiVisibility(
                        window.getDecorView().getSystemUiVisibility() |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    public class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {

        private List<Message> list;
        private Context context;

        public ChatAdapter(Context context, List<Message> list) {
            this.context = context;
            this.list = list;
            if (this.list == null) {
                this.list = new ArrayList<>();
            }
        }

        public void insertMessage(Message message) {
            if (message != null) {
                this.list.add(message);
                notifyItemInserted(this.list.size() - 1);
                scrollToBottom();
            }
        }

        @NonNull
        @Override
        public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChatHolder(LayoutInflater.from(context).inflate(R.layout.holder_huya_chat_item, parent, false));
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
            itemView.findViewById(R.id.name).setOnClickListener(v -> Toast.makeText(PcHuyaLiveActivity.this, "点击了用户 " + message.name, Toast.LENGTH_SHORT).show());
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
