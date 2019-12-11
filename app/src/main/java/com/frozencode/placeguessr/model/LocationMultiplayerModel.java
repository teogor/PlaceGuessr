package com.frozencode.placeguessr.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationMultiplayerModel {

    private LatLng mLocation;

    public LocationMultiplayerModel() {

    }

    public LatLng getLocation() {
        //return the location
        return this.mLocation;
    }

    public void setLocation(LatLng location) {
        //set the location
        this.mLocation = location;
    }

}
