package com.example.demo;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.effective.R;
import com.effective.databinding.ActivityMainLayoutBinding;
import com.example.demo.anno.ApiContentType;
import com.example.demo.anno.ApiResetType;
import com.example.demo.anno.ChatPageType;
import com.example.demo.scene.api.ContentActivity;
import com.example.demo.scene.api.PanelActivity;
import com.example.demo.scene.api.ResetActivity;
import com.example.demo.scene.chat.ChatActivity;
import com.example.demo.scene.chat.ChatDialog;
import com.example.demo.scene.chat.ChatDialogFragment;
import com.example.demo.scene.chat.ChatFragmentActivity;
import com.example.demo.scene.chat.ChatPopupWindow;
import com.example.demo.scene.feed.FeedActivity;
import com.example.demo.scene.live.huya.PcHuyaLiveActivity;
import com.example.demo.scene.live.douyin.PhoneDouyinLiveActivity;
import com.example.demo.scene.video.BiliBiliSampleActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainLayoutBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_layout);

        //activity
        mBinding.chat.setOnClickListener(this);
        mBinding.chatToolbar.setOnClickListener(this);
        mBinding.chatCusToolbar.setOnClickListener(this);
        mBinding.chatColor.setOnClickListener(this);
        mBinding.chatTransparent.setOnClickListener(this);
        mBinding.chatTransparentUnderStatusBar.setOnClickListener(this);

        //fragment
        mBinding.chatFragment.setOnClickListener(this);
        mBinding.chatFragmentToolbar.setOnClickListener(this);
        mBinding.chatColorFragment.setOnClickListener(this);
        mBinding.chatTransparentFragment.setOnClickListener(this);

        //dialogFragment
        mBinding.chatSpecialPageDialogFragment.setOnClickListener(this);
        mBinding.chatSpecialPagePopupwindow.setOnClickListener(this);
        mBinding.chatSpecialPageDialog.setOnClickListener(this);

        //直播
        mBinding.livePc.setOnClickListener(this);
        mBinding.livePhone.setOnClickListener(this);

        //信息流
        mBinding.feed.setOnClickListener(this);

        //视频
        mBinding.video.setOnClickListener(this);

        //api - 扩展内容区域布局结构
        mBinding.linearContent.setOnClickListener(this);
        mBinding.relativeContent.setOnClickListener(this);
        mBinding.frameContent.setOnClickListener(this);
        mBinding.cusContent.setOnClickListener(this);

        //api - 隐藏面板
        mBinding.resetByDisable.setOnClickListener(this);
        mBinding.resetByEnable.setOnClickListener(this);
        mBinding.resetByEnableEmptyView.setOnClickListener(this);
        mBinding.resetByEnableRecyclerView.setOnClickListener(this);
        mBinding.resetByEnableCusRecyclerView.setOnClickListener(this);

        //api - 自定义 PanelView
        mBinding.cusPanel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat: {
                ChatActivity.start(MainActivity.this, ChatPageType.DEFAULT);
                break;
            }
            case R.id.chat_toolbar: {
                ChatActivity.start(MainActivity.this, ChatPageType.TITLE_BAR);
                break;
            }
            case R.id.chat_cus_toolbar: {
                ChatActivity.start(MainActivity.this, ChatPageType.CUS_TITLE_BAR);
                break;
            }
            case R.id.chat_color: {
                ChatActivity.start(MainActivity.this, ChatPageType.COLOR_STATUS_BAR);
                break;
            }
            case R.id.chat_transparent: {
                ChatActivity.start(MainActivity.this, ChatPageType.TRANSPARENT_STATUS_BAR);
                break;
            }
            case R.id.chat_transparent_under_status_bar: {
                ChatActivity.start(MainActivity.this, ChatPageType.TRANSPARENT_STATUS_BAR_DRAW_UNDER);
                break;
            }
            case R.id.chat_fragment: {
                ChatFragmentActivity.startFragment(MainActivity.this, ChatPageType.DEFAULT);
                break;
            }
            case R.id.chat_fragment_toolbar: {
                ChatFragmentActivity.startFragment(MainActivity.this, ChatPageType.TITLE_BAR);
                break;
            }
            case R.id.chat_color_fragment: {
                ChatFragmentActivity.startFragment(MainActivity.this, ChatPageType.COLOR_STATUS_BAR);
                break;
            }
            case R.id.chat_transparent_fragment: {
                ChatFragmentActivity.startFragment(MainActivity.this, ChatPageType.TRANSPARENT_STATUS_BAR);
                break;
            }
            case R.id.chat_special_page_dialog_fragment: {
                DialogFragment dialogFragment = new ChatDialogFragment();
                dialogFragment.showNow(getSupportFragmentManager(), "dialogFragment");
                break;
            }
            case R.id.chat_special_page_popupwindow: {
                PopupWindow popupWindow = new ChatPopupWindow(this);
                popupWindow.showAtLocation(mBinding.getRoot(), Gravity.NO_GRAVITY, 0, 0);
                break;
            }
            case R.id.chat_special_page_dialog: {
                Dialog dialog = new ChatDialog(MainActivity.this);
                dialog.show();
                break;
            }
            case R.id.video: {
                startActivity(new Intent(MainActivity.this, BiliBiliSampleActivity.class));
                break;
            }
            case R.id.feed: {
                startActivity(new Intent(MainActivity.this, FeedActivity.class));
                break;
            }
            case R.id.live_pc: {
                startActivity(new Intent(MainActivity.this, PcHuyaLiveActivity.class));
                break;
            }
            case R.id.live_phone: {
                startActivity(new Intent(MainActivity.this, PhoneDouyinLiveActivity.class));
                break;
            }
            case R.id.linear_content: {
                ContentActivity.start(MainActivity.this, ApiContentType.Linear);
                break;
            }

            case R.id.frame_content: {
                ContentActivity.start(MainActivity.this, ApiContentType.Frame);
                break;
            }

            case R.id.relative_content: {
                ContentActivity.start(MainActivity.this, ApiContentType.Relative);
                break;
            }

            case R.id.reset_by_enable: {
                ResetActivity.start(MainActivity.this, ApiResetType.ENABLE);
                break;
            }

            case R.id.reset_by_enable_empty_view: {
                ResetActivity.start(MainActivity.this, ApiResetType.ENABLE_EmptyView);
                break;
            }

            case R.id.reset_by_enable_recycler_view: {
                ResetActivity.start(MainActivity.this, ApiResetType.ENABLE_RecyclerView);
                break;
            }

            case R.id.reset_by_enable_cus_recycler_view: {
                ResetActivity.start(MainActivity.this, ApiResetType.ENABLE_HookActionUpRecyclerview);
                break;
            }

            case R.id.reset_by_disable: {
                ResetActivity.start(MainActivity.this, ApiResetType.DISABLE);
                break;
            }

            case R.id.cus_panel: {
                startActivity(new Intent(MainActivity.this, PanelActivity.class));
                break;
            }

            case R.id.cus_content: {
                ContentActivity.start(MainActivity.this, ApiContentType.CUS);
                break;
            }
        }
    }
}
