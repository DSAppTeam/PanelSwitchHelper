package com.example.demo.scene.feed;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.ContentScrollMeasurer;
import com.effective.android.panel.interfaces.listener.OnPanelChangeListener;
import com.effective.android.panel.utils.DisplayUtil;
import com.effective.android.panel.utils.PanelUtil;
import com.effective.android.panel.view.panel.IPanelView;
import com.effective.android.panel.view.panel.PanelView;
import com.effective.databinding.ActivityFeedLayoutBinding;
import com.example.demo.scene.chat.ChatCusContentScrollActivity;
import com.example.demo.scene.chat.emotion.EmotionPagerView;
import com.example.demo.scene.chat.emotion.Emotions;
import com.example.demo.systemui.StatusbarHelper;
import com.example.demo.util.DisplayUtils;
import com.rd.PageIndicatorView;

/**
 * 类微博/微信朋友圈信息流 - 非 dialog 实现
 * created by yummylau on 2020/06/01
 */
public class FeedActivity extends AppCompatActivity {

    private static final String TAG = FeedActivity.class.getSimpleName();
    private ActivityFeedLayoutBinding mBinding;
    private PanelSwitchHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusbarHelper.setStatusBarColor(this, Color.TRANSPARENT);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed_layout);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setAdapter(new FeedAdapter(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .addPanelChangeListener(new OnPanelChangeListener() {
                        @Override
                        public void onKeyboard() {
                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            mBinding.emotionBtn.setSelected(false);
                            mBinding.bottomAction.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPanel(IPanelView view) {
                            if (view instanceof PanelView) {
                                mBinding.emotionBtn.setSelected(((PanelView) view).getId() == R.id.panel_emotion ? true : false);
                            }
                        }

                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
                            if (panelView instanceof PanelView) {
                                switch (((PanelView) panelView).getId()) {
                                    case R.id.panel_emotion: {
                                        EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
                                        int viewPagerSize = height - DisplayUtils.dip2px(FeedActivity.this, 30f);
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
                    .contentScrollOutsideEnable(false)
                    .logTrack(true)             //output log
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
        if (mHelper != null && mHelper.hookSystemBackByPanelSwitcher()) {
            return;
        }
        super.onBackPressed();
    }

    public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;

        public FeedAdapter(Context context) {
            super();
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return viewType == 0 ? new CoverHolder(LayoutInflater.from(context).inflate(R.layout.holder_feed_cover_layout, parent, false))
                    : new FeedItemHolder(LayoutInflater.from(context).inflate(R.layout.holder_feed_item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof FeedItemHolder) {
                ((FeedItemHolder) holder).bindData(position, position == getItemCount() - 1);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

    public class FeedItemHolder extends RecyclerView.ViewHolder {

        private FeedActionPopup popup;
        private Rect selectedItemRect = new Rect();

        public FeedItemHolder(View itemView) {
            super(itemView);
        }

        @TargetApi(19)
        public void bindData(int position, boolean isLast) {
            Context context = itemView.getContext();
            ((ImageView) itemView.findViewById(R.id.image)).setImageDrawable(ContextCompat.getDrawable(context, context.getResources().getIdentifier("ic_uzi_" + position % 10, "drawable", context.getApplicationInfo().packageName)));
            itemView.findViewById(R.id.divider).setVisibility(isLast ? View.GONE : View.VISIBLE);
            final View action = itemView.findViewById(R.id.action);
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popup == null) {
                        popup = new FeedActionPopup(context, v1 -> {
                            itemView.getGlobalVisibleRect(selectedItemRect);
                            scrollView(selectedItemRect, PanelUtil.getKeyBoardHeight(v.getContext()));
                        });
                    }
                    if (popup.isShowing()) {
                        popup.dismiss();
                    } else {
                        //api 19 request,demo make targetApi 19.if your targerApi below 19,should user showAtLocation
                        popup.showAsDropDown(action, -DisplayUtil.dip2px(context, 10f) - action.getMeasuredWidth(), (DisplayUtil.dip2px(context, 30f) - action.getMeasuredHeight()) / 2 - DisplayUtil.dip2px(context, 30f), Gravity.RIGHT);
                    }
                }
            });
        }
    }

    private void scrollView(Rect selectedItemRect, int panelHeight) {
        mBinding.bottomAction.setVisibility(View.VISIBLE);
        mHelper.toKeyboardState(true);
        int dist = selectedItemRect.bottom - (mBinding.recyclerView.getBottom() - panelHeight - DisplayUtil.dip2px(FeedActivity.this, 50f));
        mBinding.recyclerView.scrollBy(0, dist);
    }

    public class CoverHolder extends RecyclerView.ViewHolder {

        public CoverHolder(View itemView) {
            super(itemView);
        }
    }

    public class FeedActionPopup extends PopupWindow {

        public FeedActionPopup(final Context context, View.OnClickListener clickListener) {
            final View view = LayoutInflater.from(context).inflate(R.layout.pop_feed_action_layout, null, false);
            setAnimationStyle(R.style.FeedActionPopup_anim_style);
            setFocusable(true);
            setWidth(DisplayUtil.dip2px(context, 150f));
            setHeight(DisplayUtil.dip2px(context, 34f));
            setOutsideTouchable(true);
            ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
            setBackgroundDrawable(dw);
            setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            setContentView(view);
            view.findViewById(R.id.comment).setOnClickListener(v -> {
                dismiss();
                clickListener.onClick(v);
            });

        }

    }


}
