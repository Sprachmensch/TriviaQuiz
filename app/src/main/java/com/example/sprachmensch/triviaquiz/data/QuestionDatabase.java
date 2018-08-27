package com.example.sprachmensch.triviaquiz.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.sprachmensch.triviaquiz.utils.RoomConverter;

import timber.log.Timber;

@Database(entities = {Question.class}, version = 5, exportSchema = false)
@TypeConverters({RoomConverter.class})
public abstract class QuestionDatabase extends RoomDatabase {
    private static QuestionDatabase instance;
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "questions.db";
    private static boolean isBuild = false;

    public static boolean isBuild() {
        return isBuild;
    }

    public abstract DAOAccess daoAccess();

    public static QuestionDatabase getInstance(Context context) {
        Timber.plant(new Timber.DebugTree());

        if (instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        QuestionDatabase.class, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
                Timber.tag("database").d("was build");
                isBuild = true;
            }
        }
        return instance;
    }


}
