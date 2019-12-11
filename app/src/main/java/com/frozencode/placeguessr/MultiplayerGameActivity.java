package com.frozencode.placeguessr;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.danimahardhika.android.helpers.animation.AnimationHelper;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.frozencode.placeguessr.helper.GameSharedPreferences;
import com.frozencode.placeguessr.helper.LocationsSharedPreferences;
import com.frozencode.placeguessr.helper.RoundSharedPreferences;
import com.frozencode.placeguessr.helper.ScoreSharedPreferences;
import com.frozencode.placeguessr.model.LocationModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewSource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MultiplayerGameActivity extends AppCompatActivity
        implements GoogleMap.OnMapClickListener, OnMapReadyCallback, OnStreetViewPanoramaReadyCallback, SlidingUpPanelLayout.PanelSlideListener {

    private FrameLayout statusBar, statusBarPanel;
    private LocationModel mRandomLocationModel =
            new LocationModel(MultiplayerGameActivity.this);
    private SlidingUpPanelLayout mSlidingLayout;
    private RelativeLayout mBottomPanel, bottomPanel, toolbar, rlPointChose, rlLoadingStreetView;
    private LinearLayout toolbarPanel;
    private TextView roundStreetView, roundMapView, scoreStreetView, scoreMapView, messagePointChose, btnNextRound;
    private ImageView btnChooseLocation;
    private View map_view, street_view;
    private GoogleMap mMap;
    private LocationModel mCorrectLm = new LocationModel(MultiplayerGameActivity.this);
    private LocationModel mUserGuessLm = new LocationModel(MultiplayerGameActivity.this);
    private List<String> correctLocationsLat = new ArrayList<>(), correctLocationsLng = new ArrayList<>();
    boolean locationChoose = false;
    private SupportMapFragment mapFragment;
    int round;
    private DatabaseReference mThisRoom;

    private boolean mIsBottomPanelDragged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        RoundSharedPreferences.saveCurrentRound();

        round = RoundSharedPreferences.getCurrentRound();

        mThisRoom = FirebaseDatabase.getInstance().getReference().child("gamesRooms").child(GameSharedPreferences.getRoomName()).child("guesses")
                .child("round" + round);

        correctLocationsLat.addAll(LocationsSharedPreferences.getCorrectLocationsLatMP());
        correctLocationsLng.addAll(LocationsSharedPreferences.getCorrectLocationsLngMP());

        rlLoadingStreetView = findViewById(R.id.rlLoadingStreetView);
        statusBar = findViewById(R.id.statusBar);
        statusBarPanel = findViewById(R.id.statusBarPanel);
        mSlidingLayout = findViewById(R.id.mSlidingLayout);
        mBottomPanel = findViewById(R.id.mBottomPanel);
        bottomPanel = findViewById(R.id.bottomPanel);
        roundStreetView = findViewById(R.id.roundStreetView);
        roundMapView = findViewById(R.id.roundMapView);
        scoreStreetView = findViewById(R.id.scoreStreetView);
        scoreMapView = findViewById(R.id.scoreMapView);
        toolbarPanel = findViewById(R.id.toolbarPanel);
        toolbar = findViewById(R.id.toolbar);
        btnChooseLocation = findViewById(R.id.btnChooseLocation);
        map_view = findViewById(R.id.map_view);
        rlPointChose = findViewById(R.id.rlPointChose);
        messagePointChose = findViewById(R.id.messagePointChose);
        btnNextRound = findViewById(R.id.btnNextRound);
        street_view = findViewById(R.id.street_view);

        btnChooseLocation.setVisibility(View.GONE);
        rlPointChose.setVisibility(View.GONE);
        rlLoadingStreetView.setVisibility(View.VISIBLE);

        mCorrectLm.setLocation(new LatLng(Double.parseDouble(correctLocationsLat.get(round)), Double.parseDouble(correctLocationsLng.get(round))));

        String roundText = "Round " + round + " of 5";

        roundStreetView.setText(roundText);
        roundMapView.setText(roundText);
        scoreStreetView.setText(String.valueOf(ScoreSharedPreferences.getCurrentScore()));
        scoreMapView.setText(String.valueOf(ScoreSharedPreferences.getCurrentScore()));

        loadStatusBar();
        startStreetView();
        startGoogleMap();
        loadSlidingPanel();

        toolbarPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!locationChoose) {
                    mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        });

        btnChooseLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationChoose = true;
                mSlidingLayout.setTouchEnabled(false);
                mMap.setOnMapClickListener(null);

                mThisRoom.child("Player" + GameSharedPreferences.getPlayerNumber()).setValue(mUserGuessLm.getLocation());

                int currentRound = RoundSharedPreferences.getCurrentRound();
                if (currentRound >=5) {
                    btnNextRound.setText("See Summary");
                }

                correctLocationsLat.add(currentRound - 1, String.valueOf(mCorrectLm.getLocation().latitude));
                correctLocationsLng.add(currentRound - 1, String.valueOf(mCorrectLm.getLocation().longitude));

                btnChooseLocation.setVisibility(View.GONE);
                rlPointChose.setVisibility(View.VISIBLE);
                drawPolyLine(mCorrectLm.getLocation(), mUserGuessLm.getLocation());
                int calculatedDistance = mCorrectLm.calculateDistance(mUserGuessLm.getLocation());
                int pointsReceived = calculateScore(calculatedDistance);

                //scoreStreetView.setText(String.valueOf(ScoreSharedPreferences.getCurrentScore() + pointsReceived));
                //scoreMapView.setText(String.valueOf(ScoreSharedPreferences.getCurrentScore() + pointsReceived));
                animateNumberIncrease(ScoreSharedPreferences.getCurrentScore(),
                        ScoreSharedPreferences.getCurrentScore() + pointsReceived);

                String messageRound;
                messageRound = "You were " + String.valueOf(calculatedDistance) +
                        "km away and got "+ String.valueOf(pointsReceived) + " points!";

                messagePointChose.setText(messageRound);

            }
        });

        btnNextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnNextRound.setEnabled(false);
                btnNextRound.setClickable(false);
                btnNextRound.setFocusable(false);

                int currentRound = RoundSharedPreferences.getCurrentRound();
                int calculatedDistance = mCorrectLm.calculateDistance(mUserGuessLm.getLocation());
                int pointsReceived = calculateScore(calculatedDistance);

                ScoreSharedPreferences.saveCurrentScore(pointsReceived);
                ScoreSharedPreferences.saveCurrentDistance(calculatedDistance);

                if (currentRound >= 5) {

                    ScoreSharedPreferences.updateRecordDistance(ScoreSharedPreferences.getCurrentDistance());
                    ScoreSharedPreferences.updateRecordScore(ScoreSharedPreferences.getCurrentScore());
                    startActivity(new Intent(MultiplayerGameActivity.this, SummaryActivity.class));

                } else {

                    startActivity(new Intent(MultiplayerGameActivity.this, MultiplayerGameActivity.class));

                }
            }
        });

    }

    private void animateNumberIncrease(int startNumber, int endNumber) {

        ValueAnimator animator = ValueAnimator.ofInt(startNumber, endNumber);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scoreStreetView.setText(animation.getAnimatedValue().toString());
                scoreMapView.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();

    }

    public int calculateScore(double distance) {
        return (int) Math.round(Math.pow(2.718281828459045d,-6.420252E-4d * distance) * 6000.0d);
    }

    private void drawPolyLine(LatLng correctLocation, LatLng userGuess) {
        //draw polyline between the two locations
        PolylineOptions plo = new PolylineOptions();
        plo.add(correctLocation);
        plo.add(userGuess);
        plo.color(Color.parseColor("#7bed9f"));
        plo.geodesic(false);//draw a straight line
        plo.startCap(new RoundCap());
        plo.width(16);
        plo.jointType(JointType.BEVEL);

        mMap.addPolyline(plo);
        mMap.addMarker(new MarkerOptions().position(userGuess).title("Your Guess"));
        mMap.addMarker(new MarkerOptions().position(correctLocation).title("Correct Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }

    private void loadStatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            statusBar.setMinimumHeight(getStatusBarHeight());
            statusBarPanel.setMinimumHeight(getStatusBarHeight());
        } else {
            statusBar.setVisibility(View.GONE);
            statusBarPanel.setVisibility(View.GONE);
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

    private void startStreetView() {
        StreetViewPanoramaFragment streetViewFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.street_view);
        streetViewFragment.getStreetViewPanoramaAsync(this);
    }

    private void startGoogleMap() {
        //google map
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama streetViewPanorama) {
        //set the position randomly
        streetViewPanorama.setPosition(mCorrectLm.getLocation(),100000, StreetViewSource.OUTDOOR);
        streetViewPanorama.setStreetNamesEnabled(false);//user cannot see the name of the streets

        //check if it's valid or not every time the streetview is set
        streetViewPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {

                mSlidingLayout.setTouchEnabled(true);
                rlLoadingStreetView.setVisibility(View.GONE);

            }
        });
    }

    private void loadSlidingPanel() {

        toolbarPanel.setVisibility(View.INVISIBLE);
        map_view.setVisibility(View.INVISIBLE);

        mSlidingLayout.setTouchEnabled(false);
        mSlidingLayout.setDragView(mBottomPanel);
        mSlidingLayout.setCoveredFadeColor(Color.TRANSPARENT);
        mSlidingLayout.addPanelSlideListener(this);
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (slideOffset > 0.1f) {
            if (mIsBottomPanelDragged) return;

            if (!toolbarPanel.isShown()) {
                toolbarPanel.setVisibility(View.VISIBLE);
            }

            mIsBottomPanelDragged = true;
            int fromColor = ColorHelper.get(this, Color.parseColor("#45000000"));
            int toColor = ColorHelper.get(this, Color.parseColor("#FFF9DF"));

            int fromColorStatusBar = ColorHelper.get(this, Color.parseColor("#00FFFFFF"));
            int toColorStatusBar = ColorHelper.get(this, Color.parseColor("#45000000"));

            AnimationHelper.setBackgroundColor(mBottomPanel, fromColor, toColor)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(600)
                    .start();

            AnimationHelper.setBackgroundColor(statusBarPanel, fromColorStatusBar, toColorStatusBar)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(600)
                    .start();

            AnimationHelper.show(toolbarPanel)
                    .duration(600)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .start();

            AnimationHelper.show(map_view)
                    .duration(600)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .start();

            AnimationHelper.hide(toolbar)
                    .duration(600)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .start();

            AnimationHelper.hide(bottomPanel)
                    .duration(600)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .start();

        } else {
            if (!mIsBottomPanelDragged) return;

            mIsBottomPanelDragged = false;
            int fromColor = ColorHelper.get(this, Color.parseColor("#FFF9DF"));
            int toColor = ColorHelper.get(this, Color.parseColor("#45000000"));

            int fromColorStatusBar = ColorHelper.get(this, Color.parseColor("#45000000"));
            int toColorStatusBar = ColorHelper.get(this, Color.parseColor("#00FFFFFF"));

            AnimationHelper.setBackgroundColor(mBottomPanel, fromColor, toColor)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(400)
                    .start();

            AnimationHelper.setBackgroundColor(statusBarPanel, fromColorStatusBar, toColorStatusBar)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .duration(400)
                    .start();

            AnimationHelper.hide(toolbarPanel)
                    .duration(400)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .start();

            AnimationHelper.hide(map_view)
                    .duration(400)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .start();

            AnimationHelper.show(toolbar)
                    .duration(400)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .start();

            AnimationHelper.show(bottomPanel)
                    .duration(400)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .start();
        }

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingLayout.setTouchEnabled(false);
        } else {
            mSlidingLayout.setTouchEnabled(true);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        //null
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your Guess"));
        btnChooseLocation.setVisibility(View.VISIBLE);
        mUserGuessLm.setLocation(latLng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(false);

        mMap.setOnMapClickListener(this);
        uiSettings.setZoomControlsEnabled(true);

        //prevent info window from showing up
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED && !locationChoose) {
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (locationChoose) {
            //locationChose
        } else {
            startActivity(new Intent(MultiplayerGameActivity.this, ChooseGameModeActivity.class));
        }
    }



    /**
    public ArrayList<LatLng> bounds;
    private GoogleMap googleMap;
    private LatLng location;
    private int oppScore;
    Marker pickedMarker;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.pickedMarker = null;
        if (((GameActivity) getActivity()).gameHelper != null) {
            ((GameActivity) getActivity()).gameHelper.setPickedMarker(null);
        }
        this.view = inflater.inflate(C0757R.layout.fragment_location_picker, container, false);
        this.toolbar = (Toolbar) this.view.findViewById(C0757R.id.toolbar);
        if (VERSION.SDK_INT >= 19) {
            LayoutParams lp = (LayoutParams) this.toolbar.getLayoutParams();
            lp.topMargin = getStatusBarHeight();
            this.toolbar.setLayoutParams(lp);
            View roundOverPanel = this.view.findViewById(C0757R.id.round_over_panel);
            LayoutParams lp2 = (LayoutParams) roundOverPanel.getLayoutParams();
            lp2.topMargin = getStatusBarHeight();
            roundOverPanel.setLayoutParams(lp2);
        }
        ((AppCompatActivity) getActivity()).setSupportActionBar(this.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle((CharSequence) "");
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(C0757R.id.map)).getMapAsync(this);
        onHiddenChanged(false);
        return this.view;
    }

    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (Constants.DEBUG) {
            Log.d("GG8", "On hidden changed");
        }
        if (!hidden) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(this.toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle((CharSequence) "");
            if (((GameActivity) getActivity()).isTimedOut && this.googleMap != null) {
                if (Constants.DEBUG) {
                    Log.d("GG8", "Timed out");
                }
                timeout();
                ((GameActivity) getActivity()).isTimedOut = false;
            }
            if (this.bounds != null && this.googleMap != null) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                Iterator it = this.bounds.iterator();
                while (it.hasNext()) {
                    builder.include((LatLng) it.next());
                }
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), Callback.DEFAULT_DRAG_ANIMATION_DURATION));
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (((GameActivity) getActivity()) != null) {
            ((GameActivity) getActivity()).gameHelper.setCameraPosition(this.googleMap.getCameraPosition());
            ((GameActivity) getActivity()).gameHelper.setPickedMarker(this.pickedMarker);
        }
    }

    public void showRoundOverPanel() {
        this.view.findViewById(C0757R.id.round_over_panel).setVisibility(0);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        if (((GameActivity) getActivity()).gameHelper.getRound() >= 6) {
            ((TextView) this.view.findViewById(C0757R.id.next_round)).setText(getString(C0757R.string.view_score));
        }
    }

    public void hideRoundOverPanel() {
        this.view.findViewById(C0757R.id.round_over_panel).setVisibility(8);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    public void cancelCountdown() {
        if (this.timer != null) {
            this.timer.onFinish();
        }
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (((GameActivity) getActivity()) != null) {
            LatLngBounds.Builder builder;
            if (((GameActivity) getActivity()).gameHelper.getCameraPosition() != null) {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(((GameActivity) getActivity()).gameHelper.getCameraPosition()));
            } else {
                int count = getActivity().getIntent().getIntExtra(TextModalInteraction.EVENT_KEY_ACTION_POSITION, 0);
                if (count >= 8) {
                    try {
                        ArrayList<ArrayList<LatLng>> latLngs = ((Country) new CountryHelper().getCountries(getActivity(), false).get(count)).getLatLngs();
                        builder = new LatLngBounds.Builder();
                        for (int y = 0; y < latLngs.size(); y++) {
                            for (int i = 0; i < ((ArrayList) latLngs.get(y)).size(); i++) {
                                builder.include((LatLng) ((ArrayList) latLngs.get(y)).get(i));
                            }
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), Callback.DEFAULT_DRAG_ANIMATION_DURATION));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (((GameActivity) getActivity()).isTimedOut && googleMap != null) {
                if (Constants.DEBUG) {
                    Log.d("GG8", "Timed out");
                }
                timeout();
                ((GameActivity) getActivity()).isTimedOut = false;
            }
            if (((GameActivity) getActivity()).gameHelper.getPickedMarker() != null) {
                this.pickedMarker = googleMap.addMarker(new MarkerOptions().position(((GameActivity) getActivity()).gameHelper.getPickedMarker().getPosition()).title(getString(C0757R.string.your_guess)));
            }
            if (this.bounds != null) {
                builder = new LatLngBounds.Builder();
                Iterator it = this.bounds.iterator();
                while (it.hasNext()) {
                    builder.include((LatLng) it.next());
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), Callback.DEFAULT_DRAG_ANIMATION_DURATION));
            }
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.setOnMapClickListener(this);
            googleMap.setOnInfoWindowClickListener(new C07451());
        }
    }

    public void finishRound(View view) {
        GameActivity activity = (GameActivity) getActivity();
        final Object[] r;
        final int score;
        JSONObject json;
        JSONObject roundJson;
        if (activity.mMultiplayer && this.location == null) {
            this.waitDialog = new Builder(activity).title((int) C0757R.string.waiting_opponent).content((int) C0757R.string.waiting_opponent_c).cancelable(false).progress(false, 30, false).show();
            r = calculateData();
            if (r != null) {
                score = ((Integer) r[0]).intValue();
                if (activity.mMultiplayer) {
                    try {
                        json = new JSONObject();
                        roundJson = new JSONObject();
                        roundJson.put(Param.SCORE, score);
                        roundJson.put("latitude", this.pickedMarker.getPosition().latitude);
                        roundJson.put("longitude", this.pickedMarker.getPosition().longitude);
                        json.put("endRound", roundJson);
                        activity.sendMessage(json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        json = new JSONObject();
                        roundJson = new JSONObject();
                        roundJson.put(ApptentiveMessage.KEY_TYPE, 1);
                        json.put("notification", roundJson);
                        ((GameActivity) getActivity()).sendMessage(json.toString());
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                    final View view2 = view;
                    this.timer = new CountDownTimerPausable(30000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            LocationPickerFragment.this.waitDialog.incrementProgress(1);
                        }

                        public void onFinish() {
                            LocationPickerFragment.this.waitDialog.dismiss();
                            TextView roundOver = (TextView) LocationPickerFragment.this.view.findViewById(C0757R.id.round_over);
                            SharedPreferences prefs = LocationPickerFragment.this.getActivity().getSharedPreferences(LocationPickerFragment.this.getActivity().getPackageName() + "_prefs", 0);
                            String unit = LocationPickerFragment.this.getString(C0757R.string.km);
                            long distance = ((Long) r[1]).longValue();
                            if (prefs.getInt("unit", 0) == 1) {
                                unit = LocationPickerFragment.this.getString(C0757R.string.m);
                                distance = LocationPickerFragment.this.kmToMi((double) distance);
                            }
                            roundOver.setText(LocationPickerFragment.this.getString(C0757R.string.round_over_c, String.valueOf(distance), unit, Integer.valueOf(score)));
                            ((GameActivity) LocationPickerFragment.this.getActivity()).gameHelper.appendPoints(score);
                            ((GameActivity) LocationPickerFragment.this.getActivity()).gameHelper.addActualLocation(((GameActivity) LocationPickerFragment.this.getActivity()).gameHelper.getActualLatLng());
                            ((GameActivity) LocationPickerFragment.this.getActivity()).gameHelper.addGuessedLocation(LocationPickerFragment.this.pickedMarker.getPosition());
                            ((GameActivity) LocationPickerFragment.this.getActivity()).gameHelper.appendRound();
                            view2.setVisibility(8);
                            LocationPickerFragment.this.googleMap.setOnMapClickListener(null);
                            LocationPickerFragment.this.showRoundOverPanel();
                            ((GameActivity) LocationPickerFragment.this.getActivity()).isTimedOut = false;
                            if (LocationPickerFragment.this.googleMap != null && LocationPickerFragment.this.location != null) {
                                LocationPickerFragment.this.googleMap.addMarker(new MarkerOptions().position(LocationPickerFragment.this.location).title(LocationPickerFragment.this.getString(C0757R.string.opponent_guess)).icon(BitmapDescriptorFactory.defaultMarker(300.0f)));
                                LocationPickerFragment.this.googleMap.addPolyline(new PolylineOptions().add(LocationPickerFragment.this.location).add(((GameActivity) LocationPickerFragment.this.getActivity()).gameHelper.getActualLatLng()).color(SupportMenu.CATEGORY_MASK));
                                roundOver.setText(roundOver.getText() + "\n" + LocationPickerFragment.this.getString(C0757R.string.round_over_o, Integer.valueOf(LocationPickerFragment.this.oppScore)));
                            }
                        }
                    }.start();
                    this.waitDialog.show();
                    return;
                }
                return;
            }
            return;
        }
        r = calculateData();
        if (r != null) {
            score = ((Integer) r[0]).intValue();
            TextView roundOver = (TextView) this.view.findViewById(C0757R.id.round_over);
            SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getPackageName() + "_prefs", 0);
            String unit = getString(C0757R.string.km);
            long distance = ((Long) r[1]).longValue();
            if (prefs.getInt("unit", 0) == 1) {
                unit = getString(C0757R.string.m);
                distance = kmToMi((double) distance);
            }
            roundOver.setText(getString(C0757R.string.round_over_c, String.valueOf(distance), unit, Integer.valueOf(score)));
            ((GameActivity) getActivity()).gameHelper.appendPoints(score);
            ((GameActivity) getActivity()).gameHelper.addActualLocation(((GameActivity) getActivity()).gameHelper.getActualLatLng());
            ((GameActivity) getActivity()).gameHelper.addGuessedLocation(this.pickedMarker.getPosition());
            ((GameActivity) getActivity()).gameHelper.appendRound();
            view.setVisibility(8);
            this.googleMap.setOnMapClickListener(null);
            if (this.location != null) {
                this.googleMap.addMarker(new MarkerOptions().position(this.location).title(getString(C0757R.string.opponent_guess)).icon(BitmapDescriptorFactory.defaultMarker(300.0f)));
                this.googleMap.addPolyline(new PolylineOptions().add(this.location).add(((GameActivity) getActivity()).gameHelper.getActualLatLng()).color(SupportMenu.CATEGORY_MASK));
                TextView textView = roundOver;
                textView.setText(roundOver.getText() + "\n" + getString(C0757R.string.round_over_o, Integer.valueOf(this.oppScore)));
            }
            if (((GameActivity) getActivity()).timer != null) {
                ((GameActivity) getActivity()).timer.cancel();
            }
            ((GameActivity) getActivity()).isTimedOut = false;
            showRoundOverPanel();
            if (activity.mMultiplayer) {
                try {
                    json = new JSONObject();
                    roundJson = new JSONObject();
                    roundJson.put(Param.SCORE, score);
                    roundJson.put("latitude", this.pickedMarker.getPosition().latitude);
                    roundJson.put("longitude", this.pickedMarker.getPosition().longitude);
                    json.put("endRound", roundJson);
                    activity.sendMessage(json.toString());
                } catch (JSONException e22) {
                    e22.printStackTrace();
                }
            }
        }
    }

    public void nextRound(View view) {
        if (((GameActivity) getActivity()).gameHelper.getRound() >= 6) {
            hideRoundOverPanel();
            this.googleMap.clear();
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0.0d, 0.0d), 0.0f));
            this.pickedMarker = null;
            ((GameActivity) getActivity()).gameHelper.pickedMarker = null;
            ((GameActivity) getActivity()).switchToSV();
            if (((GameActivity) getActivity()).mMultiplayer) {
                ((GameActivity) getActivity()).finishGameMP();
            } else {
                ((GameActivity) getActivity()).finishGame();
            }
            this.googleMap.setOnMapClickListener(this);
        } else if (!((GameActivity) getActivity()).mMultiplayer || ((GameActivity) getActivity()).isHost) {
            hideRoundOverPanel();
            this.googleMap.clear();
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0.0d, 0.0d), 0.0f));
            this.pickedMarker = null;
            ((GameActivity) getActivity()).switchToSV();
            ((GameActivity) getActivity()).newGame();
            this.googleMap.setOnMapClickListener(this);
        } else {
            Snackbar.make(view, getString(C0757R.string.notify_ready), 0).show();
            try {
                JSONObject json = new JSONObject();
                JSONObject roundJson = new JSONObject();
                roundJson.put(ApptentiveMessage.KEY_TYPE, 0);
                json.put("notification", roundJson);
                ((GameActivity) getActivity()).sendMessage(json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            view.setEnabled(false);
            view.setClickable(false);
        }
    }

    public void onMapClick(LatLng latLng) {
        if (this.pickedMarker != null) {
            this.pickedMarker.remove();
        }
        this.pickedMarker = this.googleMap.addMarker(new MarkerOptions().position(latLng).title(getString(C0757R.string.your_guess)));
        ((FloatingActionButton) this.view.findViewById(C0757R.id.guess)).setVisibility(0);
    }

    public void newGame() {
        hideRoundOverPanel();
        this.googleMap.clear();
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0.0d, 0.0d), 0.0f));
        this.pickedMarker = null;
        ((GameActivity) getActivity()).switchToSV();
        this.googleMap.setOnMapClickListener(this);
    }

    public void setBounds() {
        if (this.googleMap != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            Iterator it = this.bounds.iterator();
            while (it.hasNext()) {
                builder.include((LatLng) it.next());
            }
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), Callback.DEFAULT_DRAG_ANIMATION_DURATION));
        }
    }

    public void setupMap() {
        if (((GameActivity) getActivity()).gameHelper.getCameraPosition() == null) {
            int count = getActivity().getIntent().getIntExtra(TextModalInteraction.EVENT_KEY_ACTION_POSITION, 0);
            if (count >= 8) {
                try {
                    ArrayList<ArrayList<LatLng>> latLngs = ((Country) new CountryHelper().getCountries(getActivity(), false).get(count)).getLatLngs();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (int y = 0; y < latLngs.size(); y++) {
                        for (int i = 0; i < ((ArrayList) latLngs.get(y)).size(); i++) {
                            builder.include((LatLng) ((ArrayList) latLngs.get(y)).get(i));
                        }
                    }
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), Callback.DEFAULT_DRAG_ANIMATION_DURATION));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    */

}
