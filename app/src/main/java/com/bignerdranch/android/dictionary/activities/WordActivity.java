package com.bignerdranch.android.dictionary.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bignerdranch.android.dictionary.App;
import com.bignerdranch.android.dictionary.R;
import com.bignerdranch.android.dictionary.db.AppDatabase;
import com.bignerdranch.android.dictionary.entity.Word;
import com.bignerdranch.android.dictionary.utils.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordActivity extends AppCompatActivity {

    private EditText mWordEnglishVersion;
    private EditText mWordRussianVersion;
    private Button mWordEditButton;
    private FloatingActionButton mDeleteWordFloatingActionButton;
    private boolean mIsUpdating;
    private Word mEditWord;
    private RequestQueue mRequestQueue;
    private ListView mTranslationsListView;

    private AppDatabase appDatabase = App.getAppDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        mWordEnglishVersion = findViewById(R.id.word_english_version_edit_text);
        mWordEnglishVersion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mIsUpdating) addTranslationOptions();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mWordRussianVersion = findViewById(R.id.word_russian_version_edit_text);
        mWordEditButton = findViewById(R.id.edit_word_button);
        mWordEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWordEnglishVersion.getText().toString().isEmpty() ||
                        mWordRussianVersion.getText().toString().isEmpty()) {
                    Toast.makeText(WordActivity.this,
                            "Слово и его перевод не должны быть пустыми",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mIsUpdating) {
                    mEditWord.setEnglishVersion(mWordEnglishVersion.getText().toString());
                    mEditWord.setRussianVersion(mWordRussianVersion.getText().toString());
                    updateWord(mEditWord);
                } else {
                    String wordEnglishVersion = mWordEnglishVersion.getText().toString();
                    String wordRussianVersion = mWordRussianVersion.getText().toString();
                    mEditWord = new Word(wordEnglishVersion, wordRussianVersion);
                    insertWord(mEditWord);
                }
            }
        });
        mDeleteWordFloatingActionButton = findViewById(R.id.delete_word_button);
        mDeleteWordFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialogDeleteWord(mEditWord);
            }
        });

        mTranslationsListView = findViewById(R.id.translations_list_view);

        Intent intent = getIntent();
        mIsUpdating = intent.getBooleanExtra(Util.EDIT_WORD_KEY, false);
        if (mIsUpdating) {
            mEditWord = (Word) intent.getParcelableExtra(Util.OBJECT_WORD_KEY);
            mWordEditButton.setText(getResources().getString(R.string.change_translation));
            mWordEnglishVersion.setText(mEditWord.getEnglishVersion());
            mWordRussianVersion.setText(mEditWord.getRussianVersion());
        } else {
            mRequestQueue = Volley.newRequestQueue(this);
            mDeleteWordFloatingActionButton.hide();
        }
    }

    public void startDialogDeleteWord(final Word word) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удаление слова");
        builder.setMessage("Вы действительно хотите удалить это слово?");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteWord(word);
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

    private void addTranslationOptions() {
        String key = "dict.1.1.20210329T185443Z.48373290204fd139.456a108770506cd1a3538eed592261f196b12e96";
        String url = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=" + key + "&lang=en-ru&text=" + mWordEnglishVersion.getText().toString();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("def");

                    final List<String> translations = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        JSONArray array = jsonObject.getJSONArray("tr");
                        for (int k = 0; k < array.length(); k++) {
                            String translation = array.getJSONObject(k).getString("text");
                            translations.add(translation);
                        }
                        if (translations.size() > 6) {
                            break;
                        }
                    }

                    if (translations.size() > 0) mTranslationsListView.setVisibility(View.VISIBLE);
                    else mTranslationsListView.setVisibility(View.GONE);

                    ArrayAdapter<String> adapter = new ArrayAdapter(WordActivity.this,
                            android.R.layout.simple_list_item_1, translations);
                    mTranslationsListView.setAdapter(adapter);
                    mTranslationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            mWordRussianVersion.setText(translations.get(position));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);

    }

    private void insertWord(final Word word) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.getWordDao().insertWord(word);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }

    private void updateWord(final Word word) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.getWordDao().updateWord(word);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }

    private void deleteWord(final Word word) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.getWordDao().deleteWord(word);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }
}
