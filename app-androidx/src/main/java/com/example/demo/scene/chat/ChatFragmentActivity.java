package com.example.demo.scene.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.effective.R;
import com.effective.databinding.ActivityChatFragmentLayoutBinding;
import com.example.demo.Constants;
import com.example.demo.anno.ChatPageType;
import com.example.demo.systemui.StatusbarHelper;

public class ChatFragmentActivity extends FragmentActivity {

    public static void startFragment(Context context, @ChatPageType int pageType) {
        Intent intent = new Intent(context, ChatFragmentActivity.class);
        intent.putExtra(Constants.KEY_PAGE_TYPE, pageType);
        context.startActivity(intent);
    }

    private ActivityChatFragmentLayoutBinding mBinding;
    private ChatFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //涉及fragment页面沉浸的，建议统一都在fragment里面，这样做是为了多fragment的时候灵活控制
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_fragment_layout);
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT);
        fragment = new ChatFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.hookOnBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
