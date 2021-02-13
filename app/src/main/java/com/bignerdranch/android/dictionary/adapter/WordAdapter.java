package com.bignerdranch.android.dictionary.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.android.dictionary.R;
import com.bignerdranch.android.dictionary.activities.MainActivity;
import com.bignerdranch.android.dictionary.activities.WordActivity;
import com.bignerdranch.android.dictionary.entity.Word;
import com.bignerdranch.android.dictionary.utils.Util;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private Context mContext;
    private List<Word> mWordsList;
    private MainActivity mMainActivity;

    public WordAdapter(Context context, List<Word> words, MainActivity mainActivity) {
        this.mContext = context;
        this.mWordsList = words;
        this.mMainActivity = mainActivity;
    }

    public void setWords(List<Word> words) {
        if (!mWordsList.isEmpty()) mWordsList.clear();
        mWordsList.addAll(words);
        notifyDataSetChanged();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {

        private TextView englishVersionTextView;
        private TextView russianVersionTextView;

        public WordViewHolder(View itemView) {
            super(itemView);
            englishVersionTextView = itemView.findViewById(R.id.english_version_text_view);
            russianVersionTextView = itemView.findViewById(R.id.russian_version_text_view);
        }

        public void bind(Word word) {
            englishVersionTextView.setText(word.getEnglishVersion());
            russianVersionTextView.setText(word.getRussianVersion());
        }
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_item_view, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, final int position) {
        final Word word = mWordsList.get(position);
        holder.bind(word);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mMainActivity, WordActivity.class);
                intent.putExtra(Util.EDIT_WORD_KEY, true);
                intent.putExtra(Util.OBJECT_WORD_KEY, word);
                mMainActivity.startActivityForResult(intent, Util.UPDATE_WORD_REQUEST_CODE);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMainActivity.startDialogDeleteCar(word, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWordsList.size();
    }
}
