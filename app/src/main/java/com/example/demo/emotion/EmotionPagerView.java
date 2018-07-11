package com.example.demo.emotion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.demo.bean.Emotion;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yummyLau on 18-7-11
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */
public class EmotionPagerView extends ViewPager {

    private int currentWidth = -1;
    private int currentHeight = -1;
    private Adapter mAdapter;

    public EmotionPagerView(@NonNull Context context) {
        this(context, null);
    }

    public EmotionPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void buildEmotionViews(final PageIndicatorView indicatorView, final EditText editText, List<Emotion> data, int width, int height) {
        if (data == null || data.isEmpty() || indicatorView == null || editText == null) {
            return;
        }
        if (currentWidth == width && currentHeight == height) {
            return;
        }
        currentWidth = width;
        currentHeight = height;
        int emotionViewContainSize = EmotionView.calSizeForContainEmotion(getContext(), currentWidth, currentHeight);
        if (emotionViewContainSize == 0) {
            return;
        }
        int pagerCount = data.size() / emotionViewContainSize;
        pagerCount += (data.size() % emotionViewContainSize == 0) ? 0 : 1;
        int index = 0;
        List<EmotionView> emotionViews = new ArrayList<>();
        for (int i = 0; i < pagerCount; i++) {
            EmotionView emotionView = new EmotionView(getContext(), editText);
            int end = (i + 1) * emotionViewContainSize;
            if (end > data.size()) {
                end = data.size();
            }
            emotionView.buildEmotions(data.subList(index, end));
            emotionViews.add(emotionView);
            index = end;
        }
        mAdapter = new Adapter(emotionViews);
        setAdapter(mAdapter);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static class Adapter extends PagerAdapter {

        private List<EmotionView> mList;

        public Adapter(List<EmotionView> mList) {
            this.mList = mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mList.get(position));
            return mList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }
    }
}
