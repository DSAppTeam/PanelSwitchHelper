package com.example.demo.scene.video;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.example.demo.emotion.EmotionPagerView;
import com.example.demo.emotion.Emotions;
import com.example.demo.interfaces.PopContentSupport;
import com.example.demo.util.DisplayUtils;
import com.rd.PageIndicatorView;

public class VideoPopWindow extends PopupWindow{

    private PanelSwitchHelper mHelper;
    private View emotionBtn;
    private EditText editText;
    private View inputLayout;
    private View emptyView;

    public VideoPopWindow(final Activity activity, final PopContentSupport popContentSupport) {
        super(activity);
        final View view = LayoutInflater.from(activity).inflate(R.layout.pop_video_layout, null, false);
        emotionBtn = view.findViewById(R.id.emotion_btn);
        editText = view.findViewById(R.id.edit_text);
        inputLayout = view.findViewById(R.id.input_layout);
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
            mHelper = new PanelSwitchHelper.Builder(activity.getWindow(),getContentView())
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
                        public void onPanel(IPanelView view) {
                            if(view instanceof PanelView) {
                                emotionBtn.setSelected(((PanelView)view).getId() == R.id.panel_emotion ? true : false);
                            }
                        }

                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if(panelView instanceof PanelView) {
                                switch (((PanelView) panelView).getId()) {
                                    case R.id.panel_emotion: {
                                        EmotionPagerView pagerView = view.findViewById(R.id.view_pager);
                                        int viewPagerSize = height - DisplayUtils.dip2px(activity, 30f);
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
                        }
                    })
                    .logTrack(true)
                    .build(false);
        }
    }

    public void showKeyboard(){
        if(mHelper != null){
            mHelper.toKeyboardState();
        }
    }

    @Override
    public void dismiss() {
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
            return;
        }
        super.dismiss();
    }
}
