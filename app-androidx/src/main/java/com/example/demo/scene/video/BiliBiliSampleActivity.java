package com.example.demo.scene.video;

import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.effective.R;
import com.effective.android.panel.utils.DisplayUtil;
import com.effective.databinding.ActivityBilibiliVideoLayoutBinding;
import com.example.demo.systemui.StatusbarHelper;
import com.example.demo.util.DisplayUtils;

/**
 * Created by yummyLau on 18-12-13
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class BiliBiliSampleActivity extends AppCompatActivity {

    private ActivityBilibiliVideoLayoutBinding mBinding;
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
        initView();
    }

    private void initView() {
        Pair<Integer, Integer> size = DisplayUtils.getScreenSize(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bilibili_video_layout);
        mBinding.videoView.getLayoutParams().width = size.first;
        mBinding.videoView.getLayoutParams().height = size.first * 9 / 16;
        mBinding.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.uzi));
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
        psize = DisplayUtils.getScreenSize(this);
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
            mBinding.checkout.setVisibility(View.VISIBLE);
        } else {
            checkoutSystemUIMode(false);
            Pair<Integer, Integer> size = DisplayUtils.getScreenSize(this);
            layoutParams.width = size.first;
            videoViewLayoutParams.width = size.first;
            layoutParams.height = psize.first;
            videoViewLayoutParams.height = psize.first;
            mBinding.inputH.setVisibility(View.VISIBLE);
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
}
