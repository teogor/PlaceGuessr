package com.frozencode.placeguessr.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class RoundSharedPreferences {

    private static final String PREF_NAME = "Round";
    private static RoundSharedPreferences sInstance;
    private static SharedPreferences sSharedPref;
    private static SharedPreferences.Editor sEditor;

    private RoundSharedPreferences(Context context) {
        sSharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sEditor = sSharedPref.edit();
    }


    public static void init(Context context) {
        if(sInstance == null) {
            sInstance = new RoundSharedPreferences(context);
        }
    }


    public static void saveCurrentRound() {
        //save the current round
        sEditor.putInt("current_round", getCurrentRound() + 1);
        sEditor.apply();
    }


    public static int getCurrentRound() {
        //return the current round
        return sSharedPref.getInt("current_round", 0);
    }


    public static void  resetRound() {
        //reset the round when the user exit or finish the game
        sEditor.putInt("current_round", 0);
        sEditor.apply();
    }
}
