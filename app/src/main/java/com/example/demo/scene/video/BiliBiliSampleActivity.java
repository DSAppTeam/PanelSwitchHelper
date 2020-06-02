package com.example.demo.scene.video;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.effective.R;
import com.effective.databinding.ActivityVideoLayoutBinding;
import com.example.demo.systemui.StatusbarHelper;
import com.example.demo.util.DisplayUtils;

/**
 * Created by yummyLau on 18-12-13
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class BiliBiliSampleActivity extends AppCompatActivity {

    private ActivityVideoLayoutBinding mBinding;
    private BiliBiliCommentPopWindow videoPopWindow;
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
        StatusbarHelper.setStatusBarColor(this, Color.BLACK);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_layout);
        initView();
    }

    private void initView() {
        Pair<Integer, Integer> size = DisplayUtils.getScreenSize(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_layout);
        mBinding.videoView.getLayoutParams().width = size.first;
        mBinding.videoView.getLayoutParams().height = size.first * 9 / 16;
        mBinding.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.jz));
        mBinding.videoView.setOnPreparedListener(mp -> {
            mp.start();
            mp.setLooping(true);
        });
        mBinding.videoView.start();
        mBinding.checkout.setOnClickListener(v -> DisplayUtils.checkoutOrientation(BiliBiliSampleActivity.this));
        mBinding.back.setOnClickListener(v -> onBackPressed());
        View.OnClickListener inputClick = v -> {
            //如果横竖屏复用同一个popupwindow，则需要处理好切换的时候panel动态变化，因为横竖屏的高度不一样。bilibili看起来是两个不同的布局。
            //一个或两个都可以，如果采用一个，则需要处理popupwindow内布局的高度变化。
            videoPopWindow = new BiliBiliCommentPopWindow(BiliBiliSampleActivity.this);
            videoPopWindow.showAtLocation(mBinding.getRoot(), Gravity.NO_GRAVITY, 0, 0);
            mBinding.getRoot().postDelayed(runnable, 200);
        };
        mBinding.input.setOnClickListener(inputClick);
        mBinding.inputH.setOnClickListener(inputClick);
    }

    @Override
    public void onBackPressed() {
        if (!DisplayUtils.isPortrait(this)) {
            if (videoPopWindow != null && videoPopWindow.isShowing()) {
                videoPopWindow.dismiss();
            } else {
                DisplayUtils.checkoutOrientation(BiliBiliSampleActivity.this);
            }
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.videoView.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            StatusbarHelper.setStatusBarColor(this, Color.BLACK);
            Pair<Integer, Integer> size = DisplayUtils.getScreenSize(this);
            layoutParams.width = size.first;
            layoutParams.height = size.first * 9 / 16;
            mBinding.inputH.setVisibility(View.GONE);
            mBinding.checkout.setVisibility(View.VISIBLE);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Pair<Integer, Integer> size = DisplayUtils.getScreenSize(this);
            layoutParams.width = size.first;
            layoutParams.height = size.second;
            mBinding.inputH.setVisibility(View.VISIBLE);
            mBinding.checkout.setVisibility(View.GONE);
        }
        mBinding.videoView.setLayoutParams(layoutParams);
    }


}
