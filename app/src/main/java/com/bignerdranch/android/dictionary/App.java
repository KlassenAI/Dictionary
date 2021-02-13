package com.bignerdranch.android.dictionary;

import android.app.Application;

import androidx.room.Room;

import com.bignerdranch.android.dictionary.db.AppDatabase;
import com.bignerdranch.android.dictionary.utils.Util;

public class App extends Application {

    private static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        appDatabase = Room
                .databaseBuilder(this, AppDatabase.class, Util.DATABASE_NAME)
                .build();
    }

    public static AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
