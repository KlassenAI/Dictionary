package com.bignerdranch.android.dictionary.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bignerdranch.android.dictionary.entity.Word;

import java.util.List;

@Dao
public interface WordDao {

    @Insert
    long insertWord(Word word);

    @Update
    void updateWord(Word word);

    @Delete
    void deleteWord(Word word);

    @Query("select * from words")
    List<Word> getAllWords();

    @Query("select * from words where word_id ==:wordId")
    Word getWord(long wordId);
}
