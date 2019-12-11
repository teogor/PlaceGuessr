package com.frozencode.placeguessr.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//class that saves whether the user exit the game on the way the last time
public class LocationsSharedPreferences {

    private static final String PREF_NAME = "Locations";
    private static LocationsSharedPreferences sInstance;
    private static SharedPreferences sSharedPref;
    private static SharedPreferences.Editor sEditor;

    private LocationsSharedPreferences(Context context) {
        sSharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sEditor = sSharedPref.edit();
    }


    public static void init(Context context) {
        if(sInstance == null) {
            sInstance = new LocationsSharedPreferences(context);
        }
        resetLocations();
    }

    public static void updateLocations(List<String> userLocationsLat, List<String> userLocationsLng,
                                       List<String> correctLocationsLat, List<String> correctLocationsLng) {

        Gson gsonUserLocationsLat = new Gson();
        String jsonUserLocationsLat = gsonUserLocationsLat.toJson(userLocationsLat);
        sEditor.putString("user_locations_lat", jsonUserLocationsLat);

        Gson gsonUserLocationsLng = new Gson();
        String jsonUserLocationsLng = gsonUserLocationsLng.toJson(userLocationsLng);
        sEditor.putString("user_locations_lng", jsonUserLocationsLng);

        Gson gsonCorrectLocationsLat = new Gson();
        String jsonCorrectLocationsLat = gsonCorrectLocationsLat.toJson(correctLocationsLat);
        sEditor.putString("correct_locations_lat", jsonCorrectLocationsLat);

        Gson gsonCorrectLocationsLng = new Gson();
        String jsonCorrectLocationsLng = gsonCorrectLocationsLng.toJson(correctLocationsLng);
        sEditor.putString("correct_locations_lng", jsonCorrectLocationsLng);

        sEditor.apply();

    }

    public static List<String> getUserLocationsLat() {

        Gson gsonUserLocationsLat = new Gson();
        String jsonUserLocationsLat = sSharedPref.getString("user_locations_lat", null);
        String[] stringUserLocationsLat = gsonUserLocationsLat.fromJson(jsonUserLocationsLat, String[].class);

        if (stringUserLocationsLat != null) {
            return new ArrayList<>(Arrays.asList(stringUserLocationsLat));
        } else {
            return new ArrayList<>();
        }

    }

    public static List<String> getUserLocationsLng() {

        Gson gsonCorrectLocationsLat = new Gson();
        String jsonUserLocationsLng = sSharedPref.getString("user_locations_lng", null);
        String[] stringUserLocationsLng = gsonCorrectLocationsLat.fromJson(jsonUserLocationsLng, String[].class);

        if (stringUserLocationsLng != null) {
            return new ArrayList<>(Arrays.asList(stringUserLocationsLng));
        } else {
            return new ArrayList<>();
        }

    }

    public static List<String> getCorrectLocationsLat() {

        Gson gsonCorrectLocationsLat = new Gson();
        String jsonCorrectLocationsLat = sSharedPref.getString("correct_locations_lat", null);
        String[] stringCorrectLocationsLat = gsonCorrectLocationsLat.fromJson(jsonCorrectLocationsLat, String[].class);

        if (stringCorrectLocationsLat != null) {
            return new ArrayList<>(Arrays.asList(stringCorrectLocationsLat));
        } else {
            return new ArrayList<>();
        }

    }

    public static List<String> getCorrectLocationsLng() {

        Gson gsonCorrectLocationsLng = new Gson();
        String jsonCorrectLocationsLng = sSharedPref.getString("correct_locations_lng", null);
        String[] stringCorrectLocationsLng = gsonCorrectLocationsLng.fromJson(jsonCorrectLocationsLng, String[].class);

        if (stringCorrectLocationsLng != null) {
            return new ArrayList<>(Arrays.asList(stringCorrectLocationsLng));
        } else {
            return new ArrayList<>();
        }

    }

    public static void resetLocations() {
        sEditor.putString("user_locations_lat", null);
        sEditor.putString("user_locations_lng", null);
        sEditor.putString("correct_locations_lat", null);
        sEditor.putString("correct_locations_lng", null);
        sEditor.putString("correct_locations_lat_multiplayer", null);
        sEditor.putString("correct_locations_lng_multiplayer", null);
        sEditor.apply();
    }

    public static void updateLocationsCorrect(List<String> correctLocationsLat, List<String> correctLocationsLng) {

        Gson gsonCorrectLocationsLat = new Gson();
        String jsonCorrectLocationsLat = gsonCorrectLocationsLat.toJson(correctLocationsLat);
        sEditor.putString("correct_locations_lat_multiplayer", jsonCorrectLocationsLat);

        Gson gsonCorrectLocationsLng = new Gson();
        String jsonCorrectLocationsLng = gsonCorrectLocationsLng.toJson(correctLocationsLng);
        sEditor.putString("correct_locations_lng_multiplayer", jsonCorrectLocationsLng);

        sEditor.apply();

    }

    public static List<String> getCorrectLocationsLatMP() {

        Gson gsonCorrectLocationsLat = new Gson();
        String jsonCorrectLocationsLat = sSharedPref.getString("correct_locations_lat_multiplayer", null);
        String[] stringCorrectLocationsLat = gsonCorrectLocationsLat.fromJson(jsonCorrectLocationsLat, String[].class);

        if (stringCorrectLocationsLat != null) {
            return new ArrayList<>(Arrays.asList(stringCorrectLocationsLat));
        } else {
            return new ArrayList<>();
        }

    }

    public static List<String> getCorrectLocationsLngMP() {

        Gson gsonCorrectLocationsLng = new Gson();
        String jsonCorrectLocationsLng = sSharedPref.getString("correct_locations_lng_multiplayer", null);
        String[] stringCorrectLocationsLng = gsonCorrectLocationsLng.fromJson(jsonCorrectLocationsLng, String[].class);

        if (stringCorrectLocationsLng != null) {
            return new ArrayList<>(Arrays.asList(stringCorrectLocationsLng));
        } else {
            return new ArrayList<>();
        }

    }

}
