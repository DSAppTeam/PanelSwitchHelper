package com.example.demo.scene.video;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.utils.DisplayUtil;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.example.demo.util.DisplayUtils;

public class BiliBiliCommentPopWindow extends PopupWindow {

    private Activity activity;
    private PanelSwitchHelper mHelper;

    public BiliBiliCommentPopWindow(final Activity activity) {
        super(activity);
        this.activity = activity;
        final View view = LayoutInflater.from(activity).inflate(R.layout.pop_bilibili_video_comment_layout, null, false);
        setFocusable(true);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        setBackgroundDrawable(dw);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(view);
        view.findViewById(R.id.send).setOnClickListener(v -> ((EditText) view.findViewById(R.id.edit_text)).setText(""));
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(activity.getWindow(), getContentView())
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            getContentView().findViewById(R.id.add_btn).setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            getContentView().findViewById(R.id.add_btn).setSelected(false);
                            dismiss();
                        }

                        @Override
                        public void onPanel(IPanelView panelView) {
                            getContentView().findViewById(R.id.add_btn).setSelected(true);
                        }

                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if (panelView instanceof PanelView) {
                                switch (((PanelView) panelView).getId()) {
                                    case R.id.panel_bilibili: {
                                        View root = view.findViewById(R.id.danmu_setting);
                                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
                                        if (layoutParams.width != width || layoutParams.height == height) {
                                            layoutParams.width = width;
                                            layoutParams.height = height;
                                            root.setLayoutParams(layoutParams);
                                        }
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
        boolean isP = DisplayUtils.isPortrait(getContentView().getContext());
        adjustDanmuPanel(isP);
        if (isP) {
            getContentView().findViewById(R.id.add_btn).setBackground(ContextCompat.getDrawable(activity, R.drawable.se_bilibili_panel_key_p));
            getContentView().findViewById(R.id.add_btn).setSelected(false);
            getContentView().findViewById(R.id.send).setSelected(false);
            getContentView().findViewById(R.id.panel_container).setBackgroundColor(ContextCompat.getColor(activity, R.color.common_page_bg_color));
            getContentView().findViewById(R.id.input_layout).setBackgroundColor(Color.WHITE);
            getContentView().findViewById(R.id.edit_text).setBackground(ContextCompat.getDrawable(activity, R.drawable.sh_bilibili_input_p));
        } else {
            getContentView().findViewById(R.id.add_btn).setBackground(ContextCompat.getDrawable(activity, R.drawable.se_bilibili_panel_key_l));
            getContentView().findViewById(R.id.add_btn).setSelected(false);
            getContentView().findViewById(R.id.send).setSelected(true);
            getContentView().findViewById(R.id.panel_container).setBackgroundColor(Color.BLACK);
            getContentView().findViewById(R.id.input_layout).setBackgroundColor(Color.BLACK);
            getContentView().findViewById(R.id.edit_text).setBackground(ContextCompat.getDrawable(activity, R.drawable.sh_bilibili_input_l_but_white));
        }
        super.showAtLocation(parent, gravity, x, y);
    }

    public void adjustDanmuPanel(boolean isPortrait) {
        getContentView().findViewById(R.id.color_for_p).setVisibility(isPortrait ? View.VISIBLE : View.GONE);
        getContentView().findViewById(R.id.color_for_l).setVisibility(isPortrait ? View.GONE : View.VISIBLE);
        ((TextView) getContentView().findViewById(R.id.danmu_font_title)).setTextColor(isPortrait ? Color.parseColor("#999999") : Color.WHITE);
        ((TextView) getContentView().findViewById(R.id.danmu_location_title)).setTextColor(isPortrait ? Color.parseColor("#999999") : Color.WHITE);
        ((TextView) getContentView().findViewById(R.id.color_for_p_title)).setTextColor(isPortrait ? Color.parseColor("#999999") : Color.WHITE);
        ((TextView) getContentView().findViewById(R.id.color_for_l_title)).setTextColor(isPortrait ? Color.parseColor("#999999") : Color.WHITE);
        ((ViewGroup.MarginLayoutParams)getContentView().findViewById(R.id.danmu_location).getLayoutParams()).topMargin = isPortrait ? DisplayUtil.dip2px(getContentView().getContext(),15f) : DisplayUtil.dip2px(getContentView().getContext(),20f);
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
