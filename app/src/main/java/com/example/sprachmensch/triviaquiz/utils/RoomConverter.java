package com.example.sprachmensch.triviaquiz.utils;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RoomConverter {

    @TypeConverter
    public static ArrayList<String> toQuestion(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromQuestion(ArrayList<String> list) {
        return new Gson().toJson(list);
    }
}
