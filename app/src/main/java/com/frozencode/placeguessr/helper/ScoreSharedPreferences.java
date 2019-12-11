package com.frozencode.placeguessr.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class ScoreSharedPreferences {

    private static final String PREF_NAME = "Score";
    private static ScoreSharedPreferences sInstance;
    private static SharedPreferences sSharedPref;
    private static SharedPreferences.Editor sEditor;

    private ScoreSharedPreferences(Context context) {
        sSharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sEditor = sSharedPref.edit();
    }


    public static void init(Context context) {
        if(sInstance == null) {
            sInstance = new ScoreSharedPreferences(context);
        }
    }

    public static void saveCurrentDistance(Integer distance) {
        //save the current score
        sEditor.putInt("current_distance", getCurrentDistance() + distance);
        sEditor.apply();
    }

    public static int getCurrentDistance() {
        //return the current score
        return sSharedPref.getInt("current_distance", 0);
    }

    public static void resetDistance() {
        //reset the score when the user exit or finish the game
        sEditor.putInt("current_distance", 0);
        sEditor.apply();
    }


    public static void saveCurrentScore(Integer score) {
        //save the current score
        sEditor.putInt("current_score", getCurrentScore() + score);
        sEditor.apply();
    }

    public static int getCurrentScore() {
        //return the current score
        return sSharedPref.getInt("current_score", 0);
    }

    public static void resetScore() {
        //reset the score when the user exit or finish the game
        sEditor.putInt("current_score", 0);
        sEditor.apply();
    }

    public static void updateRecordDistance(int finalDistance) {
        //set the record
        if (finalDistance < getRecordDistance()) {
            sEditor.putInt("recordDistance", finalDistance);
            sEditor.apply();
        }
    }

    public static void updateRecordScore(int finalScore) {
        //set the record
        if (finalScore > getRecordScore()) {
            sEditor.putInt("recordScore", finalScore);
            sEditor.apply();
        }
    }

    public static int getRecordDistance() {
        //return the record
        return sSharedPref.getInt("recordDistance", 99999999);
    }

    public static int getRecordScore() {
        //return the record
        return sSharedPref.getInt("recordScore", 0);
    }
}
