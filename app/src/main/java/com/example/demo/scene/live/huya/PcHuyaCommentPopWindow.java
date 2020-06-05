package com.example.demo.scene.live.huya;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.example.demo.scene.chat.emotion.EmotionPagerView;
import com.example.demo.scene.chat.emotion.Emotions;
import com.example.demo.util.DisplayUtils;

public class PcHuyaCommentPopWindow extends PopupWindow {

    private Activity activity;
    private PanelSwitchHelper mHelper;

    public PcHuyaCommentPopWindow(final Activity activity) {
        super(activity);
        this.activity = activity;
        final View view = LayoutInflater.from(activity).inflate(R.layout.pop_huya_live_comment_layout, null, false);
        setFocusable(true);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        setBackgroundDrawable(dw);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(view);
        view.findViewById(R.id.send).setOnClickListener(v -> ((EditText) view.findViewById(R.id.input)).setText(""));
        ((EditText) view.findViewById(R.id.input)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                view.findViewById(R.id.send).setEnabled(s.length() != 0);
            }
        });
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(activity.getWindow(), getContentView())
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            getContentView().findViewById(R.id.emotion_btn).setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            getContentView().findViewById(R.id.emotion_btn).setSelected(false);
                            dismiss();
                        }

                        @Override
                        public void onPanel(IPanelView panelView) {
                            getContentView().findViewById(R.id.emotion_btn).setSelected(true);
                        }

                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if (panelView instanceof PanelView) {
                                switch (((PanelView) panelView).getId()) {
                                    case R.id.panel_emotion: {
                                        EmotionPagerView pagerView = getContentView().findViewById(R.id.view_pager);
                                        int viewPagerSize = height - DisplayUtils.dip2px(activity, 30f);
                                        pagerView.buildEmotionViews(
                                                getContentView().findViewById(R.id.pageIndicatorView),
                                                getContentView().findViewById(R.id.input),
                                                Emotions.getEmotions(), width, viewPagerSize);
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

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        getContentView().findViewById(R.id.emotion_btn).setSelected(false);
        getContentView().findViewById(R.id.send).setSelected(true);
        ((TextView)getContentView().findViewById(R.id.input)).setText("");
        super.showAtLocation(parent, gravity, x, y);
    }

    public void showKeyboard() {
        if (mHelper != null) {
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
