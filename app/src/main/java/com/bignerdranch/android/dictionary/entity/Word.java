package com.bignerdranch.android.dictionary.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class Word implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "word_id")
    public long id;

    @ColumnInfo(name = "word_english_version")
    public String englishVersion;

    @ColumnInfo(name = "word_russian_version")
    public String russianVersion;

    public Word(long id, String englishVersion, String russianVersion) {
        this.id = id;
        this.englishVersion = englishVersion;
        this.russianVersion = russianVersion;
    }

    @Ignore
    public Word(String englishVersion, String russianVersion) {
        this.englishVersion = englishVersion;
        this.russianVersion = russianVersion;
    }

    @Ignore
    public Word() {
    }

    public long getId() {
        return id;
    }

    public String getEnglishVersion() {
        return englishVersion;
    }

    public String getRussianVersion() {
        return russianVersion;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEnglishVersion(String englishVersion) {
        this.englishVersion = englishVersion;
    }

    public void setRussianVersion(String russianVersion) {
        this.russianVersion = russianVersion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(englishVersion);
        dest.writeString(russianVersion);
    }

    public Word(Parcel in) {
        id = in.readLong();
        englishVersion = in.readString();
        russianVersion = in.readString();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}
