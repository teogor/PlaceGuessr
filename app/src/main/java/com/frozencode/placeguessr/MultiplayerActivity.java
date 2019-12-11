package com.frozencode.placeguessr;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danimahardhika.android.helpers.animation.AnimationHelper;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.frozencode.placeguessr.adapter.PlayersJoinedRoomAdapter;
import com.frozencode.placeguessr.helper.GameSharedPreferences;
import com.frozencode.placeguessr.helper.LocationsSharedPreferences;
import com.frozencode.placeguessr.helper.RoundSharedPreferences;
import com.frozencode.placeguessr.helper.ScoreSharedPreferences;
import com.frozencode.placeguessr.model.LocationModel;
import com.frozencode.placeguessr.model.LocationMultiplayerModel;
import com.frozencode.placeguessr.model.PlayerName;
import com.frozencode.placeguessr.model.PlayersName;
import com.frozencode.placeguessr.model.Room;
import com.frozencode.placeguessr.model.RoomSetLocations;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class MultiplayerActivity extends Activity
        implements OnStreetViewPanoramaReadyCallback {

    private FrameLayout statusBar;
    private ImageView btnToolbar, btnStartPlay;
    private EditText edtTxtRoomName, edtTxtPlayerName;
    private RelativeLayout rlRoomName, rlRoomSize, rlProgressBar, rlPlayerName, rlRoomInteracted, rlToolbarData;
    private TextView txtRoomInteracted, txtTitleToolbar;
    private int mNumberOfPlayers;
    private int mPlayerNumber;
    private String mRoomStatus, mRoomName;
    private HorizontalPicker horizontalPicker;
    private boolean roomCreate = false;
    private int roundGenerated = 1;
    private FirebaseFirestore mFirestore;
    private DocumentReference mRoomDB;
    private Room mRoomModel, mRoomDataChanged;
    private RoomSetLocations roomSetLocations = new RoomSetLocations();
    private PlayerName playerNameSet = new PlayerName();
    private PlayersName playersNameUpdate = new PlayersName();
    private LocationModel mRandomLocationModel =
            new LocationModel(MultiplayerActivity.this);
    private ProgressBar loadingProgressBar;
    private DatabaseReference mThisRoom;
    private LinearLayout llInitialMessage, llRoomIntearacted;
    private RecyclerView usersJoinedRoom;
    private List<String> correctLocationsLat = new ArrayList<>(), correctLocationsLng = new ArrayList<>();
    private int playersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        loadStatusBar();

        mFirestore = FirebaseFirestore.getInstance();

        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        btnToolbar = findViewById(R.id.btnToolbar);
        usersJoinedRoom = findViewById(R.id.usersJoinedRoom);
        edtTxtRoomName = findViewById(R.id.edtTxtRoomName);
        txtTitleToolbar = findViewById(R.id.txtTitleToolbar);
        rlRoomName = findViewById(R.id.rlRoomName);
        rlRoomSize = findViewById(R.id.rlRoomSize);
        rlPlayerName = findViewById(R.id.rlPlayerName);
        rlProgressBar = findViewById(R.id.rlProgressBar);
        edtTxtPlayerName = findViewById(R.id.edtTxtPlayerName);
        horizontalPicker = findViewById(R.id.horizontalPicker);
        txtRoomInteracted = findViewById(R.id.txtRoomInteracted);
        rlRoomInteracted = findViewById(R.id.rlRoomInteracted);
        rlToolbarData = findViewById(R.id.rlToolbarData);
        llInitialMessage = findViewById(R.id.llInitialMessage);
        llRoomIntearacted = findViewById(R.id.llRoomIntearacted);
        btnStartPlay = findViewById(R.id.btnStartPlay);

        btnToolbar.setVisibility(View.GONE);
        rlRoomName.setVisibility(View.GONE);
        rlRoomSize.setVisibility(View.GONE);
        rlPlayerName.setVisibility(View.GONE);
        rlProgressBar.setVisibility(View.GONE);
        rlRoomInteracted.setVisibility(View.GONE);
        llRoomIntearacted.setVisibility(View.GONE);
        btnStartPlay.setVisibility(View.GONE);

        btnStartPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GameSharedPreferences.isOnTheWay()) {
                    RoundSharedPreferences.resetRound();
                    ScoreSharedPreferences.resetScore();
                }

                GameSharedPreferences.saveRoomName(mRoomName);
                GameSharedPreferences.savePlayerNumber(playersCount);
                GameSharedPreferences.startGame();

                startActivity(new Intent(MultiplayerActivity.this, MultiplayerGameActivity.class));
                finish();

            }
        });

        horizontalPicker.setValues(new CharSequence[] {"2", "3", "4", "5", "6"});
        horizontalPicker.setSelectedItem(0);

        rlRoomName.setVisibility(View.VISIBLE);

        loadRoomName();

        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnToolbar.setVisibility(View.GONE);
                rlProgressBar.setVisibility(View.VISIBLE);

                if (rlRoomName.isShown()) {
                    edtTxtRoomName.setEnabled(false);
                    connectRoomNameDb(String.valueOf(edtTxtRoomName.getText()));
                } else if (rlRoomSize.isShown()) {
                    horizontalPicker.setEnabled(false);
                    setRoomSize(horizontalPicker.getSelectedItem());
                } else if (rlPlayerName.isShown()) {
                    edtTxtPlayerName.setEnabled(false);
                    setPlayerName(String.valueOf(edtTxtPlayerName.getText()));
                }

            }
        });

    }

    private void loadStatusBar() {

        statusBar = findViewById(R.id.statusBar);

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

    List<PlayerName> mPlayersNames = new ArrayList<>();
    PlayersJoinedRoomAdapter playersJoinedRoomAdapter;

    private void roomInteracted() {

        rlPlayerName.setVisibility(View.GONE);
        rlProgressBar.setVisibility(View.GONE);
        btnToolbar.setVisibility(View.GONE);
        llInitialMessage.setVisibility(View.GONE);
        rlRoomInteracted.setVisibility(View.VISIBLE);
        llRoomIntearacted.setVisibility(View.VISIBLE);

        txtTitleToolbar.setText(mRoomName + " Room");
        txtTitleToolbar.setTypeface(txtTitleToolbar.getTypeface(), Typeface.BOLD);

        usersJoinedRoom.setHasFixedSize(true);
        usersJoinedRoom.setItemViewCacheSize(20);
        usersJoinedRoom.setLayoutManager(new LinearLayoutManager(MultiplayerActivity.this));

        playersJoinedRoomAdapter = new PlayersJoinedRoomAdapter(mPlayersNames, MultiplayerActivity.this);

        usersJoinedRoom.setAdapter(playersJoinedRoomAdapter);

        loadRoomDetails();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                rlRoomInteracted.setVisibility(View.GONE);

            }

        }, 4000L);

    }

    private void loadRoomDetails() {

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

                int i = 1;
                for (DataSnapshot dsp : dataSnapshot.child("locations").getChildren()) {
                    dsp.child("Location" + i);
                    double lat = (double) dsp.child("latitude").getValue();
                    double lng = (double) dsp.child("longitude").getValue();

                    correctLocationsLat.add(String.valueOf(lat));
                    correctLocationsLng.add(String.valueOf(lng));

                    LocationsSharedPreferences.updateLocationsCorrect(correctLocationsLat,  correctLocationsLng);

                    i += 1;
                }

                long playersNumber = dataSnapshot.child("playerNames").getChildrenCount();
                int roomSize = (int) (long) dataSnapshot.child("roomSize").getValue();

                if (roomSize == playersNumber) {
                    mThisRoom.child("data").child("roomStatus").setValue(2);
                    btnStartPlay.setVisibility(View.VISIBLE);
                }

                for (i=0; i<playersNumber; i++) {

                    PlayerName playerNameSet = new PlayerName();
                    playerNameSet.setId((int) (long) dataSnapshot.child("playerNames").child("Player" + i).child("id").getValue());
                    playerNameSet.setPlayerName((String) dataSnapshot.child("playerNames").child("Player" + i).child("playerName").getValue());
                    mPlayersNames.add(i, playerNameSet);
                    playersJoinedRoomAdapter.notifyItemInserted(i);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

                int i = 1;
                for (DataSnapshot dsp : dataSnapshot.child("locations").getChildren()) {
                    dsp.child("Location" + i);
                    double lat = (double) dsp.child("latitude").getValue();
                    double lng = (double) dsp.child("longitude").getValue();

                    correctLocationsLat.add(String.valueOf(lat));
                    correctLocationsLng.add(String.valueOf(lng));

                    LocationsSharedPreferences.updateLocationsCorrect(correctLocationsLat,  correctLocationsLng);

                    i += 1;
                }

                long playersNumber = dataSnapshot.child("playerNames").getChildrenCount();
                long roomSize = (int) (long) dataSnapshot.child("roomSize").getValue();

                if (roomSize == playersNumber) {
                    mThisRoom.child("data").child("roomStatus").setValue(2);
                    btnStartPlay.setVisibility(View.VISIBLE);
                }

                for (i=0; i<playersNumber; i++) {

                    PlayerName playerNameSet = new PlayerName();
                    playerNameSet.setId((int) (long) dataSnapshot.child("playerNames").child("Player" + i).child("id").getValue());
                    playerNameSet.setPlayerName((String) dataSnapshot.child("playerNames").child("Player" + i).child("playerName").getValue());
                    mPlayersNames.add(i, playerNameSet);
                    playersJoinedRoomAdapter.notifyItemInserted(i);

                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mThisRoom = FirebaseDatabase.getInstance().getReference().child("gamesRooms").child(mRoomName);
        mThisRoom.addChildEventListener(childEventListener);

    }

    private void loadRoomName() {

        edtTxtRoomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 2) {

                    if(!btnToolbar.isShown()) {
                        btnToolbar.setVisibility(View.VISIBLE);
                    }

                } else {

                    if(btnToolbar.isShown()) {
                        btnToolbar.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void loadPlayerName() {

        edtTxtPlayerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() > 1) {

                    if(!btnToolbar.isShown()) {
                        btnToolbar.setVisibility(View.VISIBLE);
                    }

                } else {

                    if(btnToolbar.isShown()) {
                        btnToolbar.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void connectRoomNameDb(final String roomName) {

        mRoomName = roomName;
        try {
            mThisRoom = FirebaseDatabase.getInstance().getReference().child("gamesRooms").child(mRoomName).child("data");
            mThisRoom.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child("roomStatus").getValue() == null) {
                        //no such room

                        roomCreate = true;
                        mPlayerNumber = 1;

                        //no such room, so create it and set the room size
                        mThisRoom.child("roomStatus").setValue(0);

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                btnToolbar.setVisibility(View.VISIBLE);
                                rlProgressBar.setVisibility(View.GONE);
                                rlRoomName.setVisibility(View.GONE);
                                rlRoomSize.setVisibility(View.VISIBLE);

                            }

                        }, 1000L);

                    } else {

                        int roomStatus = (int) (long) dataSnapshot.child("roomStatus").getValue();

                        if (roomStatus == 0) {

                            btnToolbar.setVisibility(View.VISIBLE);
                            rlProgressBar.setVisibility(View.GONE);

                            int fromColor = ColorHelper.get(MultiplayerActivity.this, getResources().getColor(R.color.colorPrimary));
                            int toColor = ColorHelper.get(MultiplayerActivity.this, Color.parseColor("#FFFF8800"));

                            AnimationHelper.setBackgroundColor(statusBar, fromColor, toColor)
                                    .interpolator(new LinearOutSlowInInterpolator())
                                    .duration(1000)
                                    .start();

                            AnimationHelper.setBackgroundColor(rlToolbarData, fromColor, toColor)
                                    .interpolator(new LinearOutSlowInInterpolator())
                                    .duration(1000)
                                    .start();

                            loadingProgressBar.setIndeterminateTintList(ColorStateList.valueOf(toColor));
                            btnToolbar.setImageTintList(ColorStateList.valueOf(toColor));

                            edtTxtRoomName.setEnabled(true);

                        } else if (roomStatus == 1) {

                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    btnToolbar.setVisibility(View.GONE);
                                    rlProgressBar.setVisibility(View.GONE);
                                    rlRoomName.setVisibility(View.GONE);
                                    rlPlayerName.setVisibility(View.VISIBLE);
                                    loadPlayerName();

                                }

                            }, 1000L);

                        } else if (roomStatus == 2) {

                            btnToolbar.setVisibility(View.VISIBLE);
                            rlProgressBar.setVisibility(View.GONE);

                            int fromColor = ColorHelper.get(MultiplayerActivity.this, getResources().getColor(R.color.colorPrimary));
                            int toColor = ColorHelper.get(MultiplayerActivity.this, Color.parseColor("#FFEB3B5A"));

                            AnimationHelper.setBackgroundColor(statusBar, fromColor, toColor)
                                    .interpolator(new LinearOutSlowInInterpolator())
                                    .duration(1000)
                                    .start();

                            AnimationHelper.setBackgroundColor(rlToolbarData, fromColor, toColor)
                                    .interpolator(new LinearOutSlowInInterpolator())
                                    .duration(1000)
                                    .start();

                            loadingProgressBar.setIndeterminateTintList(ColorStateList.valueOf(toColor));
                            btnToolbar.setImageTintList(ColorStateList.valueOf(toColor));

                            edtTxtRoomName.setText(null);
                            edtTxtRoomName.setEnabled(true);

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (DatabaseException e) {

        }

        /**

        mRoomDB = mFirestore.collection("multiplayerRooms")
                .document(roomName);
        mRoomDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;

                    if(doc.get("status")!=null){

                        String roomId = doc.getId();
                        mRoomModel = doc.toObject(Room.class).withId(roomId);
                        mPlayerNumber = mRoomModel.getPlayers().size() + 1;

                        if (mRoomModel.getStatus() == 0) {

                            btnToolbar.setVisibility(View.VISIBLE);
                            rlProgressBar.setVisibility(View.GONE);

                            int fromColor = ColorHelper.get(MultiplayerActivity.this, getResources().getColor(R.color.colorPrimary));
                            int toColor = ColorHelper.get(MultiplayerActivity.this, Color.parseColor("#FFFF8800"));

                            AnimationHelper.setBackgroundColor(statusBar, fromColor, toColor)
                                    .interpolator(new LinearOutSlowInInterpolator())
                                    .duration(1000)
                                    .start();

                            AnimationHelper.setBackgroundColor(rlToolbarData, fromColor, toColor)
                                    .interpolator(new LinearOutSlowInInterpolator())
                                    .duration(1000)
                                    .start();

                            loadingProgressBar.setIndeterminateTintList(ColorStateList.valueOf(toColor));
                            btnToolbar.setImageTintList(ColorStateList.valueOf(toColor));

                        } else if (mRoomModel.getStatus() == 2) {

                            btnToolbar.setVisibility(View.VISIBLE);
                            rlProgressBar.setVisibility(View.GONE);

                            int fromColor = ColorHelper.get(MultiplayerActivity.this, getResources().getColor(R.color.colorPrimary));
                            int toColor = ColorHelper.get(MultiplayerActivity.this, Color.parseColor("#FFEB3B5A"));

                            AnimationHelper.setBackgroundColor(statusBar, fromColor, toColor)
                                    .interpolator(new LinearOutSlowInInterpolator())
                                    .duration(1000)
                                    .start();

                            AnimationHelper.setBackgroundColor(rlToolbarData, fromColor, toColor)
                                    .interpolator(new LinearOutSlowInInterpolator())
                                    .duration(1000)
                                    .start();

                            loadingProgressBar.setIndeterminateTintList(ColorStateList.valueOf(toColor));
                            btnToolbar.setImageTintList(ColorStateList.valueOf(toColor));

                        } else {


                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    btnToolbar.setVisibility(View.GONE);
                                    rlProgressBar.setVisibility(View.GONE);
                                    rlRoomName.setVisibility(View.GONE);
                                    rlPlayerName.setVisibility(View.VISIBLE);

                                }

                            }, 1000L);

                        }

                    } else {

                        roomCreate = true;
                        mPlayerNumber = 0;
                        //no such room, so create it and set the room size
                        Map<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", 0);

                        mFirestore.collection("multiplayerRooms")
                                .document(roomName)
                                .set(hashMap);

                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                mRoomName = roomName;
                                playersNameUpdate.setStatus(0);

                                btnToolbar.setVisibility(View.VISIBLE);
                                rlProgressBar.setVisibility(View.GONE);
                                rlRoomName.setVisibility(View.GONE);
                                rlRoomSize.setVisibility(View.VISIBLE);

                            }

                        }, 1000L);

                    }

                } else {
                    //error
                }

            }
        });
         */

    }

    private void setPlayerName(final String playerName) {

        if (roomCreate) {

            PlayerName playerNameSet = new PlayerName();
            playerNameSet.setId(0);
            playerNameSet.setPlayerName(playerName);
            mThisRoom.child("playerNames").child("Player" + playerNameSet.getId()).setValue(playerNameSet);
            txtRoomInteracted.setText("Room was created.\nWait for other players.");
            setRoomRounds();

        } else {

            DatabaseReference playersNo = FirebaseDatabase.getInstance().getReference().child("gamesRooms").child(mRoomName)
                    .child("data");
            playersNo.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    playersCount = (int) dataSnapshot.child("playerNames").getChildrenCount();
                    int roomSize = (int) (long) dataSnapshot.child("roomSize").getValue();

                    if (roomSize == playersCount + 1) {
                        mThisRoom.child("roomStatus").setValue(2);
                    }

                    PlayerName playerNameSet = new PlayerName();
                    playerNameSet.setId(playersCount);
                    playerNameSet.setPlayerName(playerName);
                    mThisRoom.child("playerNames").child("Player" + playerNameSet.getId()).setValue(playerNameSet);
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            txtRoomInteracted.setText("You've joined this room.");
                            roomInteracted();

                        }

                    }, 1000L);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            /**
            List<PlayerName> mPlayersNames = mRoomModel.getPlayers();
            playerNameSet.setPlayerName(playerName);
            playerNameSet.setId(mPlayerNumber);
            mPlayersNames.add(playerNameSet);
            mRoomModel.setPlayers(mPlayersNames);
            mRoomModel.setSize(mRoomModel.getSize() + 1);

            mFirestore.collection("multiplayerRooms")
                    .document(mRoomName)
                    .set(mRoomModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            //generateStreetView Locations
                            roomInteracted();

                        }
                    });*/

        }


        /**
        mThisRoom.child("playerNames")
                .child("Player" + mPlayerNumber).setValue(playerName);

        mThisRoom.child("roomStatus")
                .setValue("0");

        if (roomCreate) {
            setRoomRounds();
            rlProgressBar.setVisibility(View.VISIBLE);
            txtRoomInteracted.setText("Room was created.\nWait for other players.");
        } else {
            txtRoomInteracted.setText("You've joined this room.");

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    roomInteracted();

                }

            }, 1000L);
        }*/

    }

    private void setRoomRounds() {
        //make onStreetViewPanoramaReady works
        StreetViewPanoramaFragment streetViewFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetViewGenerateMultiplayer);
        streetViewFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {

        generateLocation(streetViewPanorama);

    }

    private void generateLocation(final StreetViewPanorama streetViewPanorama) {

        mRandomLocationModel.setLocation(mRandomLocationModel.generateRandomStreetView());
        streetViewPanorama.setPosition(mRandomLocationModel.getLocation(), 100000, StreetViewSource.OUTDOOR);

        //keep generating the random locations until a valid streetview is generated
        streetViewPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {

                if (streetViewPanoramaLocation == null || streetViewPanoramaLocation.links == null) {
                    mRandomLocationModel.setLocation(mRandomLocationModel.generateRandomStreetView());
                    streetViewPanorama.setPosition(mRandomLocationModel.getLocation(), 100000, StreetViewSource.OUTDOOR);
                } else {
                    mRandomLocationModel.setLocation(streetViewPanorama.getLocation().position);
                    mThisRoom.child("locations").child("Location" + roundGenerated).setValue(mRandomLocationModel.getLocation());

                    roundGenerated++;

                    if (roundGenerated <= 5) {
                        generateLocation(streetViewPanorama);
                    } else {

                        mThisRoom.child("roomStatus").setValue(1);
                        Handler mHandler = new Handler();
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                roomInteracted();

                            }

                        }, 1000L);

                    }
                }
            }
        });

    }

    private void setRoomSize(final int selectedItem) {

        //setRoomSize
        mThisRoom.child("roomSize").setValue(selectedItem + 2);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                rlProgressBar.setVisibility(View.GONE);
                rlRoomSize.setVisibility(View.GONE);
                rlPlayerName.setVisibility(View.VISIBLE);

                loadPlayerName();

            }

        }, 1000L);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MultiplayerActivity.this, ChooseGameModeActivity.class));
    }

}
