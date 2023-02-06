package com.ruslan.keyboard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ruslan.keyboard.clients_impl.UserClientImpl;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.User;
import com.ruslan.keyboard.entities.Word;
import com.ruslan.keyboard.stores.UserStore;
import com.ruslan.keyboard.stores.WordStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SynActivity extends AppCompatActivity {

    private final static String TAG = "SynActivity";

    private WordClientImpl mWordClientImpl;
    private DatabaseInteraction mDatabaseInteraction;

    private TextView mMessage;

    private List<Word> tmpWordsFromApi;
    private List<Word> synWordsFromApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setTitle(R.string.synchronization_activity);
        mWordClientImpl = new WordClientImpl();
        mDatabaseInteraction = new DatabaseInteraction(this);
        getWordsFromApi(UserStore.user.getId());
        setContentView(R.layout.activity_syn);
        Button synButton = findViewById(R.id.synButton);
        synButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synWords();
            }
        });
        mMessage = findViewById(R.id.message);
    }

    private void synWords() {
        Word word;
        synWordsFromApi = new ArrayList<>();

        for (int i = 0; i < WordStore.words.size(); i++) {
            for (int j = 0; tmpWordsFromApi != null && j < tmpWordsFromApi.size(); j++) {
                if (WordStore.words.get(i).getWord().equals(tmpWordsFromApi.get(j).getWord())) {
                    if (WordStore.words.get(i).getCount() > tmpWordsFromApi.get(j).getCount()) {
                        word = prepareForPut(tmpWordsFromApi.get(j), WordStore.words.get(i).getCount());
                        putWordToApi(word.getId(), word);
                    }
                    else if (WordStore.words.get(i).getCount() < tmpWordsFromApi.get(j).getCount()) {
                        word = prepareForPut(WordStore.words.get(i), tmpWordsFromApi.get(j).getCount());
                        mDatabaseInteraction.updateWord(word.getId(), word);
                        synWordsFromApi.add(tmpWordsFromApi.get(j));
                    }
                    tmpWordsFromApi.remove(j);
                    break;
                }
            }
            word = prepareForPost(WordStore.words.get(i));
            postWordToApi(word);
//            postWordToApi(WordStore.words.get(i));
        }
        for (int i = 0; tmpWordsFromApi != null && i < tmpWordsFromApi.size(); i++) {
            mDatabaseInteraction.insertWord(tmpWordsFromApi.get(i));
            synWordsFromApi.add(tmpWordsFromApi.get(i));
        }
        tmpWordsFromApi = null;
    }

    private Word prepareForPost(Word wordForPost) {
        Word word = new Word();
        word.setUserId(UserStore.user.getId());
        word.setWord(wordForPost.getWord());
        word.setCount(word.getCount());
        return word;
    }

    private Word prepareForPut(Word wordForPut, Integer count) {
        Word word = new Word();
        word.setId(wordForPut.getId());
        word.setUserId(UserStore.user.getId());
        word.setWord(wordForPut.getWord());
        word.setCount(count);
        return word;
    }

    private void getWordsFromApi(Integer userId) {
        mWordClientImpl.setCallGet(userId);
        mWordClientImpl.getCallGet().enqueue(new Callback<Word[]>() {
            @Override
            public void onResponse(Call<Word[]> call, Response<Word[]> response) {
                if (response.isSuccessful()) {
                    System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                    tmpWordsFromApi = new ArrayList<>(Arrays.asList(response.body()));
                    for (int i = 0; i < tmpWordsFromApi.size(); i++) {
                        System.out.println(tmpWordsFromApi.get(i).getWord());
                    }
                }
                else {
                    System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Word[]> call, Throwable t) {
                System.out.println("FFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
            }
        });
        System.out.println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }

    private void postWordToApi(Word word) {
        mWordClientImpl.setCallPost(word);
        mWordClientImpl.getCallPost().enqueue(new Callback<Word>() {
            @Override
            public void onResponse(Call<Word> call, Response<Word> response) {
                if (response.isSuccessful()) {
                    System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
                    Word w = response.body();
                    System.out.println(w.getWord());
//                    WordStore.postToStore(w);
                    synWordsFromApi.add(w);
                }
                else {
                    System.out.println("2222222222222222222EEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Word> call, Throwable t) {
                System.out.println("2222222222FFFFFFFFFFFFFFAAAAAAAAAAAAA");
            }
        });
        System.out.println("222222222222222222222222222222222222NNNNNNNNNNNNNNNNNNN");
    }

    private void putWordToApi(Integer id, Word word) {
        mWordClientImpl.setCallPut(id, word);
        mWordClientImpl.getCallPut().enqueue(new Callback<Word>() {
            @Override
            public void onResponse(Call<Word> call, Response<Word> response) {
                if (response.isSuccessful()) {
                    System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPUUUUUUUUUUUUUUUUUTTTTTTTTTTTTTTTTTTT");
                    Word w = response.body();
                    System.out.println(w.getWord());
//                    WordStore.putToStore(w.getId(), w);
                    synWordsFromApi.add(w);
                }
                else {
                    System.out.println("333333333333333333333333EEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Word> call, Throwable t) {
                System.out.println("33333333333333333FFFFFFFFFFFFFFAAAAAAAAAAAAA");
            }
        });
        System.out.println("33333333333333333333333333333333NNNNNNNNNNNNNNNNNNN");
    }






    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}
