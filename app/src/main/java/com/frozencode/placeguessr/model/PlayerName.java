package com.frozencode.placeguessr.model;

import com.google.android.gms.maps.model.LatLng;

public class PlayerName {

    private String playerName;
    private int id;

    public PlayerName() {

    }

    public String getPlayerName() {
        //return the location
        return this.playerName;
    }

    public int getId() {
        return this.id;
    }

    public void setPlayerName(String playerName) {
        //set the location
        this.playerName = playerName;
    }

    public void setId(int id) {
        this.id = id;
    }

}
