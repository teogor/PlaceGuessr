package com.frozencode.placeguessr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rgToolbarItem;
    private RadioButton rbPointsToolbar, rbDistanceToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rgToolbarItem = findViewById(R.id.rgToolbarItem);
        rbPointsToolbar = findViewById(R.id.rbPointsToolbar);
        rbDistanceToolbar = findViewById(R.id.rbDistanceToolbar);

    }
}
