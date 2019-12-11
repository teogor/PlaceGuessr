package com.frozencode.placeguessr.model;

import android.support.annotation.NonNull;

public class RoomId {

    public String roomId;

    public <T extends RoomId> T withId(@NonNull final String id) {
        this.roomId = id;
        return (T) this;
    }

}
