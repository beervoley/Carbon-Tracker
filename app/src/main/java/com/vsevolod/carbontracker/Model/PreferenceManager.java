package com.vsevolod.carbontracker.Model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
/**
 * Class that manages SharedPreferences.
 */
public class PreferenceManager {
    Context context;

    private Singleton singleton = Singleton.getInstance();

    public PreferenceManager(Context context) {
        this.context = context;

    }
    public void saveObject(String key, Singleton mySingleton) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mySingleton);
        editor.putString(key, json);
        editor.commit();
    }
    public Singleton getObject(int modeType, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null); // key - pot list
        Singleton mySingleton = gson.fromJson(json,singleton.getClass());
        return mySingleton;
    }
    // Function to delete all the previous data about pots entered earlier (debugging only).
    public void cleanPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pot list", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
