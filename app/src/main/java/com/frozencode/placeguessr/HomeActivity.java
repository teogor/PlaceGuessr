package com.frozencode.placeguessr;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.frozencode.placeguessr.helper.GameSharedPreferences;
import com.frozencode.placeguessr.helper.LocationsSharedPreferences;
import com.frozencode.placeguessr.helper.RoundSharedPreferences;
import com.frozencode.placeguessr.helper.ScoreSharedPreferences;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout statusBar;
    private ImageView play, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GameSharedPreferences.init(this);
        LocationsSharedPreferences.init(this);
        RoundSharedPreferences.init(this);
        ScoreSharedPreferences.init(this);

        statusBar = findViewById(R.id.statusBar);
        play = findViewById(R.id.play);
        settings = findViewById(R.id.settings);

        loadStatusBar();
        loadButtons();

    }

    private void loadStatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            statusBar.setMinimumHeight(getStatusBarHeight());
        } else {
            statusBar.setVisibility(View.GONE);
        }

    }

    private void loadButtons() {

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ChooseGameModeActivity.class));
            }
        });

        play.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(HomeActivity.this, "Play", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
