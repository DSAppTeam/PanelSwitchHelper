package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.effective.R;
import com.effective.databinding.ActivityChatSampleLayoutBinding;

public class ChatSampleFragmentActivity extends FragmentActivity {

    private static final String FRAGMENT_TYPE = "fragment_type";
    private static final String FRAGMENT_COMMON = "fragment";
    private static final String FRAGMENT_DIALOG = "dialog_fragment";

    public static void startFragment(Context context) {
        Intent intent = new Intent(context, ChatSampleFragmentActivity.class);
        intent.putExtra(FRAGMENT_TYPE, FRAGMENT_COMMON);
        context.startActivity(intent);
    }

    public static void startDialogFragment(Context context) {
        Intent intent = new Intent(context, ChatSampleFragmentActivity.class);
        intent.putExtra(FRAGMENT_TYPE, FRAGMENT_DIALOG);
        context.startActivity(intent);
    }

    private ActivityChatSampleLayoutBinding mBinding;
    private ChatSampleFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_sample_layout);
        String type = getIntent().getStringExtra(FRAGMENT_TYPE);
        switch (type) {
            case FRAGMENT_COMMON: {
                fragment = new ChatSampleFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
                break;
            }
            case FRAGMENT_DIALOG: {
                ChatSampleDialogFragment fragment = new ChatSampleDialogFragment();
                fragment.show(getSupportFragmentManager(), "");
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.hookOnBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
