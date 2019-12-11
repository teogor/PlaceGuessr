package com.frozencode.placeguessr.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationModel {

    private LatLng mLocation;
    private Context mContext;

    public LocationModel(Context context) {
        this.mContext = context;
    }


    public LatLng getLocation() {
        //return the location
        return this.mLocation;
    }


    public void setLocation(LatLng location) {
        //set the location
        this.mLocation = location;
    }


    public LatLng generateRandomStreetView() {
        //generate random street view and return it
        double lat = Math.random() * 170 - 85;
        double lng = Math.random() * 360 - 180;
        return new LatLng(lat, lng);
    }


    public int calculateDistance(LatLng anotherLocation) {
        //calculate the distance between the two locations and return the distance
        Location fromLoc = new Location("GoogleMapsAPI");
        fromLoc.setLatitude(this.mLocation.latitude);
        fromLoc.setLongitude(this.mLocation.longitude);

        Location toLoc = new Location("GoogleMapsAPI");
        toLoc.setLatitude(anotherLocation.latitude);
        toLoc.setLongitude(anotherLocation.longitude);

        double distance = Math.floor(fromLoc.distanceTo(toLoc) / 1000);
        return (int) distance;
    }


    public String getCountryByLocation() {
        //return the country name by the location
        Geocoder gcd = new Geocoder(this.mContext, Locale.getDefault());
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = gcd.getFromLocation(this.mLocation.latitude, this.mLocation.longitude, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String countryName = "";
        if (addresses!=null && addresses.size()!=0) {
            countryName = addresses.get(0).getCountryName();
        }
        return countryName;
    }
}
