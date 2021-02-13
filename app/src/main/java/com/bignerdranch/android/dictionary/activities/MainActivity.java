package com.bignerdranch.android.dictionary.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.bignerdranch.android.dictionary.App;
import com.bignerdranch.android.dictionary.R;
import com.bignerdranch.android.dictionary.adapter.WordAdapter;
import com.bignerdranch.android.dictionary.db.AppDatabase;
import com.bignerdranch.android.dictionary.entity.Word;
import com.bignerdranch.android.dictionary.utils.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private WordAdapter mWordsAdapter;
    private List<Word> mWordsList = new ArrayList<>();
    private RecyclerView mWordsRecyclerView;
    private FloatingActionButton mWordFloatingActionButton;

    private AppDatabase mWordAppDatabase = App.getAppDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWordsRecyclerView = findViewById(R.id.words_recycler_view);

        new GetAllWordsAsyncTask().execute();

        mWordsAdapter = new WordAdapter(this, mWordsList, MainActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mWordsRecyclerView.setLayoutManager(layoutManager);
        mWordsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWordsRecyclerView.setAdapter(mWordsAdapter);

        mWordFloatingActionButton = findViewById(R.id.add_word_button);
        mWordFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WordActivity.class);
                intent.putExtra(Util.EDIT_WORD_KEY, false);
                startActivityForResult(intent, Util.ADD_WORD_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == Util.ADD_WORD_REQUEST_CODE || requestCode == Util.UPDATE_WORD_REQUEST_CODE)
                && resultCode == RESULT_OK) {
            loadWords();
        }
    }

    private void loadWords() {
        new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... voids) {
                return mWordAppDatabase.getWordDao().getAllWords();
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                mWordsAdapter.setWords(words);
            }
        }.execute();
    }

    private class GetAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mWordsList.addAll(mWordAppDatabase.getWordDao().getAllWords());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mWordsAdapter.notifyDataSetChanged();
        }
    }

    private class InsertWordAsyncTask extends AsyncTask<Word, Void, Void> {

        @Override
        protected Void doInBackground(Word... words) {
            long id = mWordAppDatabase.getWordDao().insertWord(words[0]);
            Word word = mWordAppDatabase.getWordDao().getWord(id);
            if (word != null) {
                mWordsList.add(0, word);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mWordsAdapter.notifyDataSetChanged();
        }
    }

    private void insertWord(String englishVersion, String russianVersion) {
        new InsertWordAsyncTask().execute(new Word(0, englishVersion, russianVersion));
    }

    private class UpdateWordAsyncTask extends AsyncTask<Word, Void, Void> {

        @Override
        protected Void doInBackground(Word... words) {
            mWordAppDatabase.getWordDao().updateWord(words[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mWordsAdapter.notifyDataSetChanged();
        }
    }

    public void updateWord(String englishVersion, String russianVersion, int position) {
        Word word = mWordsList.get(position);
        word.setEnglishVersion(englishVersion);
        word.setRussianVersion(russianVersion);

        new UpdateWordAsyncTask().execute(word);

        mWordsList.set(position, word);
        mWordsAdapter.notifyDataSetChanged();
    }

    private class DeleteWordAsyncTask extends AsyncTask<Word, Void, Void> {

        @Override
        protected Void doInBackground(Word... words) {
            mWordAppDatabase.getWordDao().deleteWord(words[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mWordsAdapter.notifyDataSetChanged();
        }
    }

    private void deleteWord(Word word, int position) {
        mWordsList.remove(position);

        new DeleteWordAsyncTask().execute(word);

        mWordsAdapter.notifyDataSetChanged();
    }

    public void startDialogDeleteCar(final Word word, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление слова");
        builder.setMessage("Вы действительно хотите удалить это слово?");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteWord(word, position);
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
