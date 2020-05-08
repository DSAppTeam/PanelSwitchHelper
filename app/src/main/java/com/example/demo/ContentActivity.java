package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnEditFocusChangeListener;
import com.effective.android.panel.interfaces.listener.OnKeyboardStateListener;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;
import com.effective.android.panel.view.PanelView;
import com.effective.databinding.CommonChatLayoutBinding;
import com.example.demo.anno.ContentType;
import com.example.demo.anno.PageType;
import com.example.demo.chat.ChatAdapter;
import com.example.demo.chat.ChatInfo;
import com.example.demo.chat.CusRecyclerView;
import com.example.demo.emotion.EmotionPagerView;
import com.example.demo.emotion.Emotions;
import com.example.demo.util.DisplayUtils;
import com.rd.PageIndicatorView;

/**
 * 内容区域支持多种布局，不再限定为线性布局
 * Created by yummyLau on 2020/05/07
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class ContentActivity extends AppCompatActivity {

    public static void start(Context context, @ContentType int type) {
        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtra(Constants.KEY_CONTENT_TYPE, type);
        context.startActivity(intent);
    }

    private PanelSwitchHelper mHelper;
    private ChatAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        int type = getIntent().getIntExtra(Constants.KEY_PAGE_TYPE, ContentType.CUS);
        switch (type) {
            case ContentType.Linear: {
                setContentView(R.layout.chat_content_linear_layout);
                break;
            }
            case ContentType.Frame: {
                setContentView(R.layout.chat_content_frame_layout);
                break;
            }
            case ContentType.Relative: {
                setContentView(R.layout.chat_content_relative_layout);
                break;
            }
            default: {
                setContentView(R.layout.chat_content_cus_layout);
            }
        }
        initView();
    }

    private CusRecyclerView getRecyclerView() {
        return (CusRecyclerView) findViewById(R.id.recycler_view);
    }
    
    private View getSendView(){
        return findViewById(R.id.send);
    }

    private EditText getEditView(){
        return findViewById(R.id.edit_text);
    }

    private View getRoot(){
        return getWindow().getDecorView();
    }

    private View getEmotionView(){
        return findViewById(R.id.emotion_btn);
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        getRecyclerView().setLayoutManager(mLinearLayoutManager);
        mAdapter = new ChatAdapter(this, 4);
        getRecyclerView().setAdapter(mAdapter);
        getSendView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = getEditView().getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(ContentActivity.this, "当前没有输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAdapter.insertInfo(ChatInfo.CREATE(content));
//                如果超过某些条目，可开启滑动外部，使得更为流畅
                if (mAdapter.getItemCount() > 10) {
                    mHelper.scrollOutsideEnable(true);
                }
                getEditView().setText(null);
                scrollToBottom();
            }
        });
    }

    private void scrollToBottom() {
        getRoot().post(new Runnable() {
            @Override
            public void run() {
                mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    //可选
                    .addKeyboardStateListener(new OnKeyboardStateListener() {
                        @Override
                        public void onKeyboardChange(boolean visible) {
                            Log.d(TAG, "系统键盘是否可见 : " + visible);

                        }
                    })
                    //可选
                    .addEditTextFocusChangeListener(new OnEditFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            Log.d(TAG, "输入框是否获得焦点 : " + hasFocus);
                            if (hasFocus) {
                                scrollToBottom();
                            }
                        }
                    })
                    //可选
                    .addViewClickListener(new OnViewClickListener() {
                        @Override
                        public void onClickBefore(View view) {
                            switch (view.getId()) {
                                case R.id.edit_text:
                                case R.id.add_btn:
                                case R.id.emotion_btn: {
                                    scrollToBottom();
                                }
                            }
                            Log.d(TAG, "点击了View : " + view);
                        }
                    })
                    //可选
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            Log.d(TAG, "唤起系统输入法");
                            getEmotionView().setSelected(false);
                            scrollToBottom();
                        }

                        @Override
                        public void onNone() {
                            Log.d(TAG, "隐藏所有面板");
                            getEmotionView().setSelected(false);
                        }

                        @Override
                        public void onPanel(PanelView view) {
                            Log.d(TAG, "唤起面板 : " + view);
                            getEmotionView().setSelected(view.getId() == R.id.panel_emotion ? true : false);
                            scrollToBottom();
                        }

                        @Override
                        public void onPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            switch (panelView.getId()) {
                                case R.id.panel_emotion: {
                                    EmotionPagerView pagerView = getRoot().findViewById(R.id.view_pager);
                                    int viewPagerSize = height - DisplayUtils.dip2px(ContentActivity.this, 30f);
                                    pagerView.buildEmotionViews(
                                            (PageIndicatorView) getRoot().findViewById(R.id.pageIndicatorView),
                                            getEditView(),
                                            Emotions.getEmotions(), width, viewPagerSize);
                                    break;
                                }
                                case R.id.panel_addition: {
                                    //auto center,nothing to do
                                    break;
                                }
                            }
                        }
                    })
                    .contentCanScrollOutside(false)
                    .logTrack(true)             //output log
                    .build();
            getRecyclerView().setResetPanel(new CusRecyclerView.ResetPanel() {
                @Override
                public void resetPanel() {
                    mHelper.hookSystemBackByPanelSwitcher();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
            return;
        }
        super.onBackPressed();
    }
}
