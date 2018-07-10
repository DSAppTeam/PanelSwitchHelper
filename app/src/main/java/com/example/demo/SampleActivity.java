package com.example.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.effective.R;
import com.effective.android.panel.PanelSwitchHelper;
import com.effective.android.panel.interfaces.listener.OnViewClickListener;

public class SampleActivity extends AppCompatActivity {

    private PanelSwitchHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (helper == null) {
            helper = new PanelSwitchHelper.Builder(this)
                    .bindPanelSwitchLayout(R.id.panel_switch_layout)
                    .bindPanelContainerId(R.id.panel_container)
                    .bindContentContainerId(R.id.content_view)
                    .logTrack(true)
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
        if (helper != null && helper.hookSystemBackForHindPanel()) {
            return;
        }
        super.onBackPressed();
    }
}
