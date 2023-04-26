package com.example.demo.scene.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.ContentScrollMeasurer;
import com.effective.android.panel.interfaces.PanelHeightMeasurer;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.utils.PanelUtil;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.effective.databinding.ActivitySuperChatLayoutBinding;
import com.example.demo.scene.chat.adapter.ChatAdapter;
import com.example.demo.scene.chat.adapter.ChatInfo;
import com.example.demo.scene.chat.emotion.EditTextSelector;
import com.example.demo.scene.chat.emotion.EmotionPagerView;
import com.example.demo.scene.chat.emotion.Emotions;
import com.example.demo.util.DisplayUtils;

/**
 * 复杂的聊天界面，演示所有可能用到的api
 * 包括：滑动模式，控制内容区元素滑动，初始化面板适配，自定义面板等
 * Created by yummyLau on 20/07/13
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ChatSuperActivity extends AppCompatActivity implements EditTextSelector {

    public static void start(Context context) {
        Intent intent = new Intent(context, ChatCusContentScrollActivity.class);
        context.startActivity(intent);
    }

    private ActivitySuperChatLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String TAG = ChatCusContentScrollActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_super_chat_layout);
        mBinding.getRoot().setBackgroundColor(ContextCompat.getColor(this, R.color.common_page_bg_color));
        initView();
    }

    private void initView() {
        mBinding.tipViewTop.setVisibility(View.VISIBLE);
        mBinding.tipViewBottom.setVisibility(View.VISIBLE);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mBinding.recyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new ChatAdapter(this, 4);
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.send.setOnClickListener(v -> {
            String content = mBinding.editText.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(ChatSuperActivity.this, "当前没有输入", Toast.LENGTH_SHORT).show();
                return;
            }
            mAdapter.insertInfo(ChatInfo.CREATE(content));
            mBinding.editText.setText(null);
            scrollToBottom();
        });
        mBinding.title.setText("点击左侧 \"默认滑动演示 \" 可清除框架输入法高度缓存测试 \"功能面板不同步软键盘高度 & 表情面板同步软键盘高度\"");
        mBinding.tipView.setOnClickListener(v -> {
            PanelUtil.clearData(ChatSuperActivity.this);
            Toast.makeText(ChatSuperActivity.this, "已清除面板高度缓存，可拉起功能面板测试默认高度", Toast.LENGTH_SHORT).show();
        });
    }

    private void scrollToBottom() {
        mBinding.getRoot().post(() -> mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    //可选
                    .addKeyboardStateListener((visible, height) -> Log.d(TAG, "系统键盘是否可见 : " + visible + " 高度为：" + height))
                    .addEditTextFocusChangeListener((view, hasFocus) -> {
                        Log.d(TAG, "输入框是否获得焦点 : " + hasFocus);
                        if (hasFocus) {
                            scrollToBottom();
                        }
                    })
                    //可选
                    .addViewClickListener(view -> {
                        switch (view.getId()) {
                            case R.id.edit_text:
                            case R.id.add_btn:
                            case R.id.emotion_btn: {
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
                            scrollToBottom();
                        }

                        @Override
                        public void onNone() {
                            Log.d(TAG, "隐藏所有面板");
                            mBinding.emotionBtn.setSelected(false);
                            mBinding.mutilEditText.clearFocus();
                        }

                        @Override
                        public void onPanel(IPanelView view) {
                            Log.d(TAG, "唤起面板 : " + view);
                            if (view instanceof PanelView) {
                                mBinding.emotionBtn.setSelected(((PanelView) view).getId() == R.id.panel_emotion ? true : false);
                                scrollToBottom();
                            }
                        }

                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if (panelView instanceof PanelView) {
                                switch (((PanelView) panelView).getId()) {
                                    case R.id.panel_emotion: {
                                        EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
                                        int viewPagerSize = height - DisplayUtils.dip2px(ChatSuperActivity.this, 30f);
                                        pagerView.buildEmotionViewsWithSelector(
                                                mBinding.getRoot().findViewById(R.id.pageIndicatorView),
                                                ChatSuperActivity.this,
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
                    /**
                     * 根据recyclerview的内容填充度来滑动
                     */
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {
                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return defaultDistance - unfilledHeight;
                        }

                        @Override
                        public int getScrollViewId() {
                            return R.id.recycler_view;
                        }
                    })
                    /**
                     * tipViewBottom 确保底部输入栏不遮挡
                     */
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {
                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return defaultDistance - getBottomUnfilledHeight();
                        }

                        @Override
                        public int getScrollViewId() {
                            return R.id.tip_view_bottom;
                        }
                    })
                    /**
                     * tipViewTop 不跟随滑动
                     */
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {
                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return 0;
                        }

                        @Override
                        public int getScrollViewId() {
                            return R.id.tip_view_top;
                        }
                    })
                    /**
                     * 默认实现，contentContainer 内部的子view会随容器向上scroll defaultDistance 距离
                     */
                    .addContentScrollMeasurer(new ContentScrollMeasurer() {

                        @Override
                        public int getScrollDistance(int defaultDistance) {
                            return defaultDistance;
                        }

                        @Override
                        public int getScrollViewId() {
                            return R.id.tip_view;
                        }
                    })
                    /**
                     * 可选，可不设置
                     * 面板默认高度设置,输入法显示后会采纳输入法高度为面板高度，否则则以框架内部默认值为主
                     */
                    .addPanelHeightMeasurer(new PanelHeightMeasurer() {

                        /**
                         * false 为不同步输入法高度
                         * @return
                         */
                        @Override
                        public boolean synchronizeKeyboardHeight() {
                            return false;
                        }

                        @Override
                        public int getTargetPanelDefaultHeight() {
                            return DisplayUtils.dip2px(ChatSuperActivity.this, 200f);
                        }

                        @Override
                        public int getPanelTriggerId() {
                            return R.id.add_btn;
                        }
                    })
                    /**
                     * 可选，可不设置
                     * 面板默认高度设置,输入法显示后会采纳输入法高度为面板高度，否则则以框架内部默认值为主
                     */
                    .addPanelHeightMeasurer(new PanelHeightMeasurer() {

                        /**
                         * true表示当输入法显示过时，则同步输入法高度
                         * @return
                         */
                        @Override
                        public boolean synchronizeKeyboardHeight() {
                            return true;
                        }

                        @Override
                        public int getTargetPanelDefaultHeight() {
                            return DisplayUtils.dip2px(ChatSuperActivity.this, 400f);
                        }

                        @Override
                        public int getPanelTriggerId() {
                            return R.id.emotion_btn;
                        }
                    })
                    .logTrack(true)             //output log
                    .build();
            mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        int position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        View lastChildView = layoutManager.findViewByPosition(position);
                        if (lastChildView != null) {
                            int bottom = lastChildView.getBottom();
                            int listHeight = mBinding.recyclerView.getHeight() - mBinding.recyclerView.getPaddingBottom();
                            unfilledHeight = listHeight - bottom;
                        }
                    }
                }
            });
        }
        mHelper.addSecondaryInputView(mBinding.mutilEditText);
        //如果Edittext的生命周期小于当前activity，则需要remove避免内存泄漏
        // mHelper.removeSecondaryInputView(mBinding.mutilEditText);
        mBinding.recyclerView.setPanelSwitchHelper(mHelper);
    }

    private int unfilledHeight = 0;
    private int bottomUnfilledHeight = 0;

    private int getBottomUnfilledHeight() {
        if (bottomUnfilledHeight == 0) {
            bottomUnfilledHeight = mBinding.bottomAction.getTop() - mBinding.tipViewBottom.getBottom();
        }
        return bottomUnfilledHeight;
    }


    @Override
    public void onBackPressed() {
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
            return;
        }
        super.onBackPressed();
    }

    @NonNull
    @Override
    public EditText getEditText() {
        if (mBinding.mutilEditText.isFocused()) {
            return mBinding.mutilEditText;
        } else {
            return mBinding.editText;
        }
    }
}
