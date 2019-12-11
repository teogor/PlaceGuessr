package com.frozencode.placeguessr.model;

import java.util.List;

public class PlayersName {

    private int status, size;
    private List<PlayerName> players;

    public PlayersName() {

    }

    public List<PlayerName> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<PlayerName> players) {
        this.players = players;
    }

    public int getStatus() {
        return this.status;
    }

    public int getSize() {
        return this.size;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
