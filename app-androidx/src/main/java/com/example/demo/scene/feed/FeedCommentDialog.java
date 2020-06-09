package com.example.demo.scene.feed;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.utils.PanelUtil;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.effective.android.panel.window.PanelDialog;
import com.example.demo.scene.chat.emotion.EmotionPagerView;
import com.example.demo.scene.chat.emotion.Emotions;
import com.example.demo.util.DisplayUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FeedCommentDialog extends PanelDialog implements DialogInterface.OnKeyListener {

    private static final String TAG = FeedCommentDialog.class.getSimpleName();
    private Activity activity;
    private onDialogStatus status;

    @Override
    public int getDialogLayout() {
        return R.layout.dialog_feed_comment_layout;
    }

    public FeedCommentDialog(Activity activity,onDialogStatus status) {
        super(activity);
        this.activity = activity;
        this.status = status;
        setOnKeyListener(this);
        ((EditText) rootView.findViewById(R.id.edit_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                rootView.findViewById(R.id.send).setEnabled(s.length() != 0);
            }
        });
        rootView.findViewById(R.id.send).setOnClickListener(v -> ((EditText) rootView.findViewById(R.id.edit_text)).setText(""));
        rootView.findViewById(R.id.input_layout).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (status != null) {
                    status.onStatus(true, top + DisplayUtils.getStatusBarHeight(activity) - PanelUtil.getKeyBoardHeight(getContext()));
                }
            }
        });
    }

    @Override
    public void show() {
        if (helper == null) {
            helper = new PanelSwitchHelper.Builder(activity.getWindow(), rootView)
                    //可选
                    .addKeyboardStateListener((visible, height) -> Log.d(TAG, "系统键盘是否可见 : " + visible + " 高度为：" + height))
                    //可选
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            Log.d(TAG, "唤起系统输入法");
                            rootView.findViewById(R.id.emotion_btn).setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            Log.d(TAG, "隐藏所有面板");
                            rootView.findViewById(R.id.emotion_btn).setSelected(false);
                            dismiss();
                        }

                        @Override
                        public void onPanel(IPanelView view) {
                            Log.d(TAG, "唤起面板 : " + view);
                            if (view instanceof PanelView) {
                                rootView.findViewById(R.id.emotion_btn).setSelected(((PanelView) view).getId() == R.id.panel_emotion ? true : false);
                            }
                        }


                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if (panelView instanceof PanelView) {
                                switch (((PanelView) panelView).getId()) {
                                    case R.id.panel_emotion: {
                                        EmotionPagerView pagerView = rootView.findViewById(R.id.view_pager);
                                        int viewPagerSize = height - DisplayUtils.dip2px(getContext(), 30f);
                                        pagerView.buildEmotionViews(
                                                rootView.findViewById(R.id.pageIndicatorView),
                                                rootView.findViewById(R.id.edit_text),
                                                Emotions.getEmotions(), width, viewPagerSize);
                                        break;
                                    }
                                }
                            }
                        }
                    })
                    .logTrack(true)
                    .build(true);
        }
        super.show();
    }

    @Override
    public void dismiss() {
        if (status != null) {
            status.onStatus(false, 0);
        }
        super.dismiss();
    }

    @Override
    public boolean onKey(@Nullable DialogInterface dialog, int keyCode, @NotNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        }
        return false;
    }

    public interface onDialogStatus {
        void onStatus(boolean visible, int currentTop);
    }
}
