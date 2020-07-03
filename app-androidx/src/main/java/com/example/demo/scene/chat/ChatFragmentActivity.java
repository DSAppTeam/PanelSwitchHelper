package com.example.demo.scene.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import com.effective.R;
import com.effective.databinding.ActivityChatFragmentLayoutBinding;
import com.example.demo.Constants;
import com.example.demo.anno.ChatPageType;
import com.example.demo.systemui.StatusbarHelper;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

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
//        fragment = new ChatFragment();
//        fragment.setArguments(getIntent().getExtras());
//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Fragment fragment = new ChatFragment();
            fragment.setArguments(getIntent().getExtras());
            fragments.add(fragment);
        }
        mBinding.pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                Log.d("setAdapter","instantiateItem: " + position);
                return super.instantiateItem(container, position);
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                super.destroyItem(container, position, object);
                Log.d("setAdapter","destroyItem position : " + position);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.hookOnBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
