package com.example.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.sample.R;
import com.sample.android.panel.PanelSwitchHelper;
import com.sample.android.panel.panel.IPanelView;

public class SampleActivity extends AppCompatActivity {

    private PanelSwitchHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        initView();
    }

    private void initView() {
        helper = new PanelSwitchHelper.Builder(this)
                .bindContentView(findViewById(R.id.content_view))
                .bindEmptyView(findViewById(R.id.empty_view))
                .bindEditText((EditText) findViewById(R.id.edit_text))
                .bindPanelItem(findViewById(R.id.red_click), (IPanelView) findViewById(R.id.panel_red), true)
                .bindPanelItem(findViewById(R.id.green_click), (IPanelView) findViewById(R.id.panel_green), true)
                .bindPanelItem(findViewById(R.id.blue_click), (IPanelView) findViewById(R.id.panel_blue), true)
                .logTrack(true)
                .build();
    }

    @Override
    public void onBackPressed() {
        if (helper != null && helper.hookSystemBackForHindPanel()) {
            return;
        }
        super.onBackPressed();
    }
}
