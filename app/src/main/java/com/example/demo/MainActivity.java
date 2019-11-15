package com.example.demo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.effective.R;
import com.effective.databinding.ActivityMainLayoutBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainLayoutBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_layout);
        mBinding.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChatSampleActivity.class));
            }
        });
        mBinding.chatFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatSampleFragmentActivity.startFragment(MainActivity.this);
            }
        });
        mBinding.chatDialogFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatSampleFragmentActivity.startDialogFragment(MainActivity.this);
            }
        });
        mBinding.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,VideoSampleActivity.class));
            }
        });
    }
}
