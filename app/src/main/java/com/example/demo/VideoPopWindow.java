package com.example.demo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.IPopupSupport;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.view.PanelView;
import com.example.demo.emotion.EmotionPagerView;
import com.example.demo.emotion.Emotions;
import com.example.demo.interfaces.PopContentSupport;
import com.rd.PageIndicatorView;

public class VideoPopWindow extends PopupWindow implements IPopupSupport {

    private PanelSwitchHelper mHelper;
    private View emotionBtn;
    private EditText editText;
    private View inputLayout;
    private View emptyView;
    private Activity activity;

    @NonNull
    @Override
    public Activity getActivity() {
        return activity;
    }

    @NonNull
    @Override
    public PopupWindow getPopupWindow() {
        return this;
    }

    public VideoPopWindow(final Activity activity, final PopContentSupport popContentSupport) {
        super(activity);
        final View view = LayoutInflater.from(activity).inflate(R.layout.pop_video_sample_layout, null, false);
        this.activity = activity;;
        emotionBtn = view.findViewById(R.id.emotion_btn);
        editText = view.findViewById(R.id.edit_text);
        inputLayout = view.findViewById(R.id.input_layout);
        emptyView = view.findViewById(R.id.empty_view);
        view.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(activity, "当前没有输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                popContentSupport.sendContent(content);
                editText.setText(null);
            }
        });
        setFocusable(true);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        setBackgroundDrawable(dw);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(view);
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .bindPanelSwitchLayout(R.id.panel_switch_layout)
                    .bindPanelContainerId(R.id.panel_container)
                    .bindContentContainerId(R.id.content_view)
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            inputLayout.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.VISIBLE);
                            emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            emotionBtn.setSelected(false);
                            dismiss();
                        }

                        @Override
                        public void onPanel(PanelView view) {
                            emotionBtn.setSelected(view.getId() == R.id.panel_emotion ? true : false);
                        }

                        @Override
                        public void onPanelSizeChange(PanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            switch (panelView.getId()) {
                                case R.id.panel_emotion: {
                                    EmotionPagerView pagerView = view.findViewById(R.id.view_pager);
                                    int viewPagerSize = height - Utils.dip2px(activity, 30f);
                                    pagerView.buildEmotionViews(
                                            (PageIndicatorView) view.findViewById(R.id.pageIndicatorView),
                                            editText,
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
                    .logTrack(true)
                    .build(false);
        }
    }

    public void shoyKeyboard(){
        mHelper.showKeyboard();
    }

    @Override
    public void dismiss() {
        if (mHelper != null && mHelper.hookSystemBackForHindPanel()) {
            return;
        }
        super.dismiss();
    }
}
