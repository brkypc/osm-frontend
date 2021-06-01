package com.ytuce.osmroutetracking.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LocalObjectStorage<C> {

    public void putList(ArrayList<C> list, String fileName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("size", list.size());

        String json;
        Gson gson = new Gson();

        for (int i = 0; i < list.size(); i++) {
            json = gson.toJson(list.get(i));
            editor.putString(String.valueOf(i), json);
        }

        editor.apply();
    }

    public void putElement(C object, String fileName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int size = preferences.getInt("size", 0);

        String json;
        Gson gson = new Gson();

        json = gson.toJson(object);
        editor.putString(String.valueOf(size), json);

        editor.putInt("size", size + 1);
        editor.apply();
    }

    public ArrayList<C> getList(String fileName, Context context, Type type) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        ArrayList<C> list = new ArrayList<>();

        int size = preferences.getInt("size", 0);

        String json;
        Gson gson = new Gson();

        for (int i = 0; i < size; i++) {
            json = preferences.getString(String.valueOf(i), "");
            C trackingItem = gson.fromJson(json, type);
            list.add(trackingItem);
        }

        return list;
    }

    public void deleteList(String fileName, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("size", 0);
        editor.apply();
    }

}
