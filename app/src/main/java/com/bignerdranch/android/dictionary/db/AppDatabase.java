package com.bignerdranch.android.dictionary.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.bignerdranch.android.dictionary.entity.Word;

@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WordDao getWordDao();
}
