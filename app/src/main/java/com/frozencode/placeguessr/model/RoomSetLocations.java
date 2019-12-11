package com.frozencode.placeguessr.model;

import java.util.List;

public class RoomSetLocations {

    private int status, size;
    private List<PlayerName> players;
    private List<LocationMultiplayerModel> streetLocations;

    public RoomSetLocations() {

    }

    public int getStatus() {
        return this.status;
    }

    public int getSize() {
        return this.size;
    }

    public List<PlayerName> getPlayers() {
        return this.players;
    }

    public List<LocationMultiplayerModel> getStreetLocations() {
        return this.streetLocations;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPlayers(List<PlayerName> players) {
        this.players = players;
    }

    public void setStreetLocations(List<LocationMultiplayerModel> streetLocations) {
        this.streetLocations = streetLocations;
    }

}
