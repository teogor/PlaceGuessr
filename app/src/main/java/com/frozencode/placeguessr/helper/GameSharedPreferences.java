package com.frozencode.placeguessr.helper;

import android.content.Context;
import android.content.SharedPreferences;

//class that saves whether the user exit the game on the way the last time
public class GameSharedPreferences {

    private static final String PREF_NAME = "Game";
    private static GameSharedPreferences sInstance;
    private static SharedPreferences sSharedPref;
    private static SharedPreferences.Editor sEditor;

    private GameSharedPreferences(Context context) {
        sSharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sEditor = sSharedPref.edit();
    }


    public static void init(Context context) {
        if(sInstance == null) {
            sInstance = new GameSharedPreferences(context);
        }
    }


    public static boolean isOnTheWay() {
        //the user is in game
        return sSharedPref.getBoolean("is_on_the_way", false);
    }


    public static void startGame() {
        //the user is on the way
        sEditor.putBoolean("is_on_the_way", true);
        sEditor.apply();
    }


    public static void finishGame() {
        //the user finish the game
        sEditor.putBoolean("is_on_the_way", false);
        sEditor.apply();
    }

    public static void saveRoomName(String roomName) {
        //save the room name
        sEditor.putString("room_name", roomName);
        sEditor.apply();
    }


    public static String getRoomName() {
        //return the room name
        return sSharedPref.getString("room_name", null);
    }

    public static void savePlayerNumber(int playerNumber) {
        //save the player number
        sEditor.putInt("player_number", playerNumber);
        sEditor.apply();
    }


    public static int getPlayerNumber() {
        //return te player number
        return sSharedPref.getInt("player_number", 1);
    }

}
