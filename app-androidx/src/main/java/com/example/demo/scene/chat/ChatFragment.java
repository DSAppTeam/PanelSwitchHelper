package com.example.demo.scene.chat;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.effective.databinding.CommonChatWithTitlebarLayoutBinding;
import com.example.demo.Constants;
import com.example.demo.anno.ChatPageType;
import com.example.demo.scene.chat.adapter.ChatAdapter;
import com.example.demo.scene.chat.adapter.ChatInfo;
import com.example.demo.scene.chat.emotion.EmotionPagerView;
import com.example.demo.scene.chat.emotion.Emotions;
import com.example.demo.systemui.StatusbarHelper;
import com.example.demo.util.DisplayUtils;
import com.rd.PageIndicatorView;

public class ChatFragment extends Fragment {

    private CommonChatWithTitlebarLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String TAG = "ChatFragment";
    private boolean releaseWithPager = false;
    // 在ViewPager场景下，不可见时，尝试释放监听器

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initExtra();
    }

    private void initExtra() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            releaseWithPager = bundle.getBoolean(Constants.RELEASE_WITH_PAGER, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.common_chat_with_titlebar_layout, container, false);

        int type = getArguments().getInt(Constants.KEY_PAGE_TYPE);

        switch (type) {
            case ChatPageType.COLOR_STATUS_BAR: {
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.common_page_bg_color));
                mBinding.statusBar.setColor(R.color.colorPrimary);
                mBinding.titleBar.setVisibility(View.VISIBLE);
                mBinding.titleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mBinding.title.setText("Fragment-自定义标题栏,状态栏着色");
                break;
            }
            case ChatPageType.TRANSPARENT_STATUS_BAR: {
                StatusbarHelper.setStatusBarColor(getActivity(), Color.TRANSPARENT);
                mBinding.titleBar.setVisibility(View.VISIBLE);
                mBinding.title.setText("Fragment-自定义标题栏，状态栏透明");
                break;
            }
            default: {
                mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.common_page_bg_color));
                mBinding.statusBar.setColor(R.color.colorPrimary);
                mBinding.titleBar.setVisibility(type == ChatPageType.DEFAULT ? View.GONE : View.VISIBLE);
                mBinding.titleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                mBinding.title.setText("Fragment-自定义标题栏");
            }
        }
        initView();
        return mBinding.getRoot();
    }


    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new ChatAdapter(getContext(), 50);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mBinding.editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getContext(), "当前没有输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAdapter.insertInfo(ChatInfo.CREATE(content));
                mBinding.editText.setText(null);
                scrollToBottom();
            }
        });
    }

    private void scrollToBottom() {
        mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .setWindowInsetsRootView(mBinding.getRoot())
                    //可选
                    .addKeyboardStateListener((visible, height) -> Log.d(TAG, "系统键盘是否可见 : " + visible + " 高度为：" + height))
                    //可选
                    .addEditTextFocusChangeListener((view, hasFocus) -> {
                        Log.d(TAG, "输入框是否获得焦点 : " + hasFocus);
                        if(hasFocus){
                            scrollToBottom();
                        }
                    })
                    //可选
                    .addViewClickListener(view -> {
                        switch (view.getId()){
                            case R.id.edit_text:
                            case R.id.add_btn:
                            case R.id.emotion_btn:{
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
                        }

                        @Override
                        public void onNone() {
                            Log.d(TAG, "隐藏所有面板");
                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onPanel(IPanelView view) {
                            Log.d(TAG, "唤起面板 : " + view);
                            if(view instanceof PanelView){
                                mBinding.emotionBtn.setSelected(((PanelView)view).getId() == R.id.panel_emotion ? true : false);
                            }
                        }

                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if(panelView instanceof PanelView){
                                switch (((PanelView)panelView).getId()) {
                                    case R.id.panel_emotion: {
                                        EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
                                        int viewPagerSize = height - DisplayUtils.dip2px(getContext(), 30f);
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
                        }
                    })
                    .logTrack(true)             //output log
                    .build();
        }
        mBinding.recyclerView.setPanelSwitchHelper(mHelper);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (releaseWithPager) {
            mBinding.panelSwitchLayout.recycle();
            mHelper = null;
        }
    }

    public boolean hookOnBackPressed() {
        return mHelper != null && mHelper.hookSystemBackByPanelSwitcher();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
