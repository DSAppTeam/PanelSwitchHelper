package com.example.demo.scene.chat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;

public class AutoHidePanelRecyclerView extends RecyclerView {

    PanelSwitchHelper panelSwitchHelper;

    public void setPanelSwitchHelper(PanelSwitchHelper panelSwitchHelper) {
        this.panelSwitchHelper = panelSwitchHelper;
    }

    public AutoHidePanelRecyclerView(Context context) {
        this(context, null);
    }

    public AutoHidePanelRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoHidePanelRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(context));
        addOnItemTouchListener(new OnItemTouchListener() {

            public boolean isTouchInView(View view, MotionEvent event) {
                if (view == null) {
                    return false;
                }
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];
                return x < event.getRawX() && event.getRawX() < x + view.getWidth()
                        && y < event.getRawY() && event.getRawY() < y + view.getHeight();
            }

            public void hidePanel() {
                if (panelSwitchHelper != null) {
                    panelSwitchHelper.hookSystemBackByPanelSwitcher();
                }
            }

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                int action = e.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    View view = rv.findChildViewUnder(e.getX(), e.getY());
                    if (view == null) {
                        hidePanel();
                        return false;
                    }
                    View text = view.findViewById(R.id.text);
                    View avatar = view.findViewById(R.id.avatar);
                    if (text == null || avatar == null) {
                        hidePanel();
                        return false;
                    }

                    if (!isTouchInView(text, e) && !isTouchInView(avatar, e)) {
                        hidePanel();
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }
}
