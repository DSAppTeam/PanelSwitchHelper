package com.example.demo.scene.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
        // 适配Android 15
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });

        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT);
        fragment = new ChatFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(
                    window.getDecorView().getSystemUiVisibility() |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }*/
    }

    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.hookOnBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
