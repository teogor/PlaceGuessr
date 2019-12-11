package com.frozencode.placeguessr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frozencode.placeguessr.helper.GameSharedPreferences;
import com.frozencode.placeguessr.helper.RoundSharedPreferences;
import com.frozencode.placeguessr.helper.ScoreSharedPreferences;

public class ChooseGameModeActivity extends AppCompatActivity {

    private CardView cardViewSingleMode, cardViewMultipleMode;
    private ImageView icStatisticsSingle, icStatisticsMultiplayer;
    private RelativeLayout rlStatisticsSingle, rlStatisticsMultiplayer;
    private LinearLayout llStatisticsSingleMode, llStatisticsMultiplayerMode;
    private boolean singleModeStatisticsShow = false,  multiplayerModeStatisticsShow = false;
    private TextView txtHighScoreSingle, txtLowDistanceSingle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_game_mode);

        cardViewSingleMode = findViewById(R.id.cardViewSingleMode);
        cardViewMultipleMode = findViewById(R.id.cardViewMultipleMode);
        icStatisticsSingle = findViewById(R.id.icStatisticsSingle);
        rlStatisticsSingle = findViewById(R.id.rlStatisticsSingle);
        llStatisticsSingleMode = findViewById(R.id.llStatisticsSingleMode);
        icStatisticsMultiplayer = findViewById(R.id.icStatisticsMultiplayer);
        rlStatisticsMultiplayer = findViewById(R.id.rlStatisticsMultiplayer);
        llStatisticsMultiplayerMode = findViewById(R.id.llStatisticsMultiplayerMode);
        txtLowDistanceSingle = findViewById(R.id.txtLowDistanceSingle);
        txtHighScoreSingle = findViewById(R.id.txtHighScoreSingle);

        loadCardView();
        loadStatistics();

    }

    private void loadCardView() {

        cardViewSingleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if the user exit the game on the way the last time
                if (GameSharedPreferences.isOnTheWay()) {
                    RoundSharedPreferences.resetRound();
                    ScoreSharedPreferences.resetScore();
                }

                GameSharedPreferences.startGame();

                Intent intent = (new Intent(ChooseGameModeActivity.this, StreetViewActivity.class));
                startActivity(intent);

            }
        });

        cardViewMultipleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = (new Intent(ChooseGameModeActivity.this, MultiplayerActivity.class));
                startActivity(intent);

            }
        });

    }

    private void loadStatistics() {

        loadSingleModeStatistics();
        loadMultiplayerModeStatistics();

    }

    @SuppressLint("SetTextI18n")
    private void loadSingleModeStatistics() {

        if (ScoreSharedPreferences.getRecordDistance() == 99999999) {
            txtLowDistanceSingle.setText("N/A");
        } else {
            txtLowDistanceSingle.setText(ScoreSharedPreferences.getRecordDistance() + " km");
        }
        txtHighScoreSingle.setText(ScoreSharedPreferences.getRecordScore() + " points");

        //noinspection deprecation
        icStatisticsSingle.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_down_black_48));
        llStatisticsSingleMode.setVisibility(View.GONE);

        rlStatisticsSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (singleModeStatisticsShow) {
                    //noinspection deprecation
                    icStatisticsSingle.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_down_black_48));
                    llStatisticsSingleMode.setVisibility(View.GONE);
                } else {
                    //noinspection deprecation
                    icStatisticsSingle.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_up_black_48));
                    llStatisticsSingleMode.setVisibility(View.VISIBLE);
                }
                singleModeStatisticsShow = !singleModeStatisticsShow;
            }
        });

    }

    private void loadMultiplayerModeStatistics() {

        //noinspection deprecation
        icStatisticsMultiplayer.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_down_black_48));
        llStatisticsMultiplayerMode.setVisibility(View.GONE);

        rlStatisticsMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiplayerModeStatisticsShow) {
                    //noinspection deprecation
                    icStatisticsMultiplayer.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_down_black_48));
                    llStatisticsMultiplayerMode.setVisibility(View.GONE);
                } else {
                    //noinspection deprecation
                    icStatisticsMultiplayer.setImageDrawable(getResources().getDrawable(R.drawable.baseline_arrow_drop_up_black_48));
                    llStatisticsMultiplayerMode.setVisibility(View.VISIBLE);
                }
                multiplayerModeStatisticsShow = !multiplayerModeStatisticsShow;
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChooseGameModeActivity.this, HomeActivity.class));
    }
}
