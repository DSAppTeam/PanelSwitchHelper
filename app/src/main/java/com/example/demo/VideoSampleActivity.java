package com.example.demo;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.view.PanelView;
import com.effective.databinding.ActivityVideoLayoutBinding;
import com.example.demo.emotion.EmotionPagerView;
import com.example.demo.emotion.Emotions;
import com.example.demo.interfaces.PopContentSupport;
import com.rd.PageIndicatorView;

/**
 * Created by yummyLau on 18-12-13
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class VideoSampleActivity extends AppCompatActivity implements PopContentSupport {

    private ActivityVideoLayoutBinding mBinding;
    private int portraitHeight;
    private PanelSwitchHelper mHelper;
    private boolean isPortrait = true;
    private VideoPopWindow videoPopWindow;
    private Runnable runnable = new Runnable() {
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_layout);
        initView();
    }



    @Override
    public void sendContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            mBinding.videoView.setText(mBinding.videoView.getText().toString() + "\n" + "yummylau： " + content);
        }
    }

    private void initView() {
        mBinding.checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPortrait = isPortrait();
                if (isPortrait) {
                    portraitHeight = mBinding.videoView.getMeasuredHeight();
                }
                checkoutOrientation(!isPortrait());
            }
        });
        mBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mBinding.editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(VideoSampleActivity.this, "当前没有输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                mBinding.videoView.setText(mBinding.videoView.getText().toString() + "\n" + "yummylau： " + content);
                mBinding.editText.setText(null);
            }
        });
        mBinding.input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPortrait) {
                    mBinding.inputLayout.setVisibility(View.VISIBLE);
                    mBinding.emptyView.setVisibility(View.VISIBLE);
                    mHelper.showKeyboard();
                } else {
                    if (videoPopWindow == null) {
                        videoPopWindow = new VideoPopWindow(VideoSampleActivity.this, VideoSampleActivity.this);
                    }
                    videoPopWindow.showAtLocation(mBinding.panelSwitchLayout, Gravity.NO_GRAVITY, 0, 0);
                    mBinding.getRoot().postDelayed(runnable, 500);
                }
            }
        });
        mBinding.inputLayout.setVisibility(View.GONE);
        mBinding.emptyView.setVisibility(View.GONE);
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
                            mBinding.inputLayout.setVisibility(View.VISIBLE);
                            mBinding.emptyView.setVisibility(View.VISIBLE);
                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            mBinding.emotionBtn.setSelected(false);
                            mBinding.inputLayout.setVisibility(View.GONE);
                            mBinding.emptyView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPanel(PanelView view) {
                            mBinding.emotionBtn.setSelected(view.getId() == R.id.panel_emotion ? true : false);
                        }

                        @Override
                        public void onPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            switch (panelView.getId()) {
                                case R.id.panel_emotion: {
                                    EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
                                    int viewPagerSize = height - Utils.dip2px(VideoSampleActivity.this, 30f);
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
                    .logTrack(true)
                    .build(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (videoPopWindow != null) {
            videoPopWindow.onDestroy();
        }
        mBinding.getRoot().removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!isPortrait) {
            if (videoPopWindow.isShowing()) {
                videoPopWindow.dismiss();
            } else {
                checkoutOrientation(true);
            }
            return;
        }
        if (mHelper != null && mHelper.hookSystemBackForHindPanel()) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.videoView.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams.height = portraitHeight;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            layoutParams.height = layoutParams.MATCH_PARENT;
        }
        mBinding.videoView.setLayoutParams(layoutParams);
    }

    public boolean isPortrait() {
        isPortrait = getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        return isPortrait;
    }

    public void checkoutOrientation(boolean portrait) {
        isPortrait = portrait;
        setRequestedOrientation(portrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

}
