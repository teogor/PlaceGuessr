package com.frozencode.placeguessr;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.frozencode.placeguessr.helper.LocationsSharedPreferences;
import com.frozencode.placeguessr.helper.ScoreSharedPreferences;
import com.frozencode.placeguessr.model.LocationModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;

public class SummaryActivity extends AppCompatActivity
        implements GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private FrameLayout statusBar;
    private GoogleMap mMap;
    private List<String> userLocationsLat = new ArrayList<>(), userLocationsLng = new ArrayList<>(),
            correctLocationsLat = new ArrayList<>(), correctLocationsLng = new ArrayList<>();
    private LocationModel mCorrectLm = new LocationModel(SummaryActivity.this);
    private LocationModel mUserGuessLm = new LocationModel(SummaryActivity.this);
    private TextView scoreStreetView;
    private ImageView btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        statusBar = findViewById(R.id.statusBar);
        scoreStreetView = findViewById(R.id.scoreStreetView);
        btnDone = findViewById(R.id.btnDone);

        animateNumberIncrease(0, ScoreSharedPreferences.getCurrentScore());

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ScoreSharedPreferences.resetDistance();
                ScoreSharedPreferences.resetScore();
                startActivity(new Intent(SummaryActivity.this, ChooseGameModeActivity.class));

            }
        });

        loadStatusBar();
        startGoogleMap();

    }

    private void animateNumberIncrease(int startNumber, int endNumber) {

        ValueAnimator animator = ValueAnimator.ofInt(startNumber, endNumber);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scoreStreetView.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();

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

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void startGoogleMap() {
        //google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(false);

        mMap.setOnMapClickListener(this);

        userLocationsLat.addAll(LocationsSharedPreferences.getUserLocationsLat());
        userLocationsLng.addAll(LocationsSharedPreferences.getUserLocationsLng());
        correctLocationsLat.addAll(LocationsSharedPreferences.getCorrectLocationsLat());
        correctLocationsLng.addAll(LocationsSharedPreferences.getCorrectLocationsLng());

        for (int i=0; i<5; i++) {

            LatLng userLatLng = new LatLng(Double.parseDouble(userLocationsLat.get(i)), Double.parseDouble(userLocationsLng.get(i)));
            mCorrectLm.setLocation(userLatLng);
            LatLng correctLatLng = new LatLng(Double.parseDouble(correctLocationsLat.get(i)), Double.parseDouble(correctLocationsLng.get(i)));
            mCorrectLm.setLocation(correctLatLng);

            drawPolyLine(correctLatLng, userLatLng, i);

        }

    }

    private void drawPolyLine(LatLng correctLocation, LatLng userGuess, int round) {
        //draw polyline between the two locations
        PolylineOptions plo = new PolylineOptions();
        plo.add(correctLocation);
        plo.add(userGuess);
        plo.color(Color.parseColor("#7bed9f"));
        plo.geodesic(false);//draw a straight line
        plo.startCap(new RoundCap());
        plo.width(12);
        plo.jointType(JointType.BEVEL);

        String snippet = "Round " + String.valueOf(round) + " of 5";

        mMap.addPolyline(plo);
        mMap.addMarker(new MarkerOptions().position(userGuess).title("Your Guess").snippet(snippet));
        mMap.addMarker(new MarkerOptions().position(correctLocation).title("Correct Location").snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }
}
