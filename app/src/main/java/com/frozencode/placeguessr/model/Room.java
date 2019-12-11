package com.frozencode.placeguessr.model;

import java.util.List;

public class Room {

    private int roomStatus, roomSize;
    private List<PlayerName> playerNames;
    private List<LocationMultiplayerModel> locations;

    public Room() {

    }

    public int getStatus() {
        return this.roomStatus;
    }

    public int getSize() {
        return this.roomSize;
    }

    public List<PlayerName> getPlayers() {
        return this.playerNames;
    }

    public List<LocationMultiplayerModel> getStreetLocations() {
        return this.locations;
    }

    public void setStatus(int roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void setSize(int roomSize) {
        this.roomSize = roomSize;
    }

    public void setPlayers(List<PlayerName> playerNames) {
        this.playerNames = playerNames;
    }

    public void setStreetLocations(List<LocationMultiplayerModel> locations) {
        this.locations = locations;
    }

}
