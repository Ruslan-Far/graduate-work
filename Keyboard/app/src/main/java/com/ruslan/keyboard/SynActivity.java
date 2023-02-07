package com.ruslan.keyboard;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ruslan.keyboard.clients_impl.CollocationClientImpl;
import com.ruslan.keyboard.clients_impl.UserClientImpl;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.Collocation;
import com.ruslan.keyboard.entities.User;
import com.ruslan.keyboard.entities.Word;
import com.ruslan.keyboard.stores.CollocationStore;
import com.ruslan.keyboard.stores.UserStore;
import com.ruslan.keyboard.stores.WordStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SynActivity extends AppCompatActivity {

    private final static String TAG = "SynActivity";

    private WordClientImpl mWordClientImpl;
    private CollocationClientImpl mCollocationClientImpl;
    private DatabaseInteraction mDatabaseInteraction;

    private TextView mMessage;

    private List<Word> tmpWordsFromApi;
    private List<Word> synWordsFromApi;

    private List<Collocation> tmpCollocationsFromApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setTitle(R.string.synchronization_activity);
        mWordClientImpl = new WordClientImpl();
        mCollocationClientImpl = new CollocationClientImpl();
        mDatabaseInteraction = new DatabaseInteraction(this);
        setContentView(R.layout.activity_syn);
        Button synButton = findViewById(R.id.synButton);
        synButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                synWords();
                synCollocations();
            }
        });
        mMessage = findViewById(R.id.message);
    }

    private void synWords() {
        Word word;
        synWordsFromApi = new ArrayList<>();

        getWordsFromApi(UserStore.user.getId());
        for (int i = 0; i < WordStore.words.size(); i++) {
            for (int j = 0; tmpWordsFromApi != null && j < tmpWordsFromApi.size(); j++) {
                if (WordStore.words.get(i).getWord().equals(tmpWordsFromApi.get(j).getWord())) {
                    if (WordStore.words.get(i).getCount() > tmpWordsFromApi.get(j).getCount()) {
                        word = prepareWordForPut(tmpWordsFromApi.get(j), WordStore.words.get(i).getCount());
                        putWordToApi(word.getId(), word);
                    }
                    else if (WordStore.words.get(i).getCount() < tmpWordsFromApi.get(j).getCount()) {
                        word = prepareWordForPut(WordStore.words.get(i), tmpWordsFromApi.get(j).getCount());
                        mDatabaseInteraction.updateWord(word.getId(), word);
                        synWordsFromApi.add(tmpWordsFromApi.get(j));
                    }
                    tmpWordsFromApi.remove(j);
                    break;
                }
            }
            word = prepareWordForPost(WordStore.words.get(i));
            postWordToApi(word);
//            postWordToApi(WordStore.words.get(i));
        }
        for (int i = 0; tmpWordsFromApi != null && i < tmpWordsFromApi.size(); i++) {
            mDatabaseInteraction.insertWord(tmpWordsFromApi.get(i));
            synWordsFromApi.add(tmpWordsFromApi.get(i));
        }
        tmpWordsFromApi = null;
    }

    private Word prepareWordForPost(Word wordForPost) {
        Word word = new Word();
        word.setUserId(UserStore.user.getId());
        word.setWord(wordForPost.getWord());
        word.setCount(word.getCount());
        return word;
    }

    private Word prepareWordForPut(Word wordForPut, Integer count) {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void synCollocations() {
        Collocation collocation;

        mDatabaseInteraction.selectCollocations();
        getCollocationsFromApi(UserStore.user.getId(), Constants.EXPAND);
        for (int i = 0; i < CollocationStore.collocations.size(); i++) {
            for (int j = 0; tmpCollocationsFromApi != null && j < tmpCollocationsFromApi.size(); j++) {
                if (CollocationStore.collocations.get(i).getWordResources()[0].getWord()
                        .equals(tmpCollocationsFromApi.get(j).getWordResources()[0].getWord())
                            && CollocationStore.collocations.get(i).getWordResources()[1].getWord()
                                .equals(tmpCollocationsFromApi.get(j).getWordResources()[1].getWord())) {
                    if (CollocationStore.collocations.get(i).getCount()
                        > tmpCollocationsFromApi.get(j).getCount()) {
                        collocation = prepareCollocationForPut(
                                tmpCollocationsFromApi.get(j), CollocationStore.collocations.get(i).getCount()
                        );
                        putCollocationToApi(collocation.getId(), collocation);
                    }
                    else if (CollocationStore.collocations.get(i).getCount()
                        < tmpCollocationsFromApi.get(j).getCount()) {
                        collocation = prepareCollocationForPut(
                                CollocationStore.collocations.get(i), tmpCollocationsFromApi.get(j).getCount()
                        );
                        mDatabaseInteraction.updateCollocation(collocation.getId(), collocation);
                    }
                    tmpCollocationsFromApi.remove(j);
                    break;
                }
            }
            collocation = prepareCollocationForPost(synWordsFromApi, CollocationStore.collocations.get(i));
            postCollocationToApi(collocation);
        }
        for (int i = 0; tmpCollocationsFromApi != null && i < tmpCollocationsFromApi.size(); i++) {
            collocation = prepareCollocationForPost(WordStore.words, tmpCollocationsFromApi.get(i));
            mDatabaseInteraction.insertCollocation(collocation);
        }
        tmpCollocationsFromApi = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Collocation prepareCollocationForPost(List<Word> words, Collocation collocationForPost) {
        Collocation collocation = new Collocation();
        collocation.setPrevId(getIdByWord(words, collocationForPost.getWordResources()[0].getWord()));
        collocation.setNextId(getIdByWord(words, collocationForPost.getWordResources()[1].getWord()));
        collocation.setCount(collocationForPost.getCount());
        return collocation;
    }

    private Collocation prepareCollocationForPut(Collocation collocationForPut, Integer count) {
        collocationForPut.setCount(count);
        return collocationForPut;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getIdByWord(List<Word> words, String word) {
        return words.stream()
                .filter(x -> x.getWord().equals(word))
                .collect(Collectors.toList())
                .get(0)
                .getId();
    }

    private void getCollocationsFromApi(Integer userId, Object expand) {
        mCollocationClientImpl.setCallGet(userId, expand);
        mCollocationClientImpl.getCallGet().enqueue(new Callback<Collocation[]>() {
            @Override
            public void onResponse(Call<Collocation[]> call, Response<Collocation[]> response) {
                if (response.isSuccessful()) {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                    tmpCollocationsFromApi = new ArrayList<>(Arrays.asList(response.body()));
                    for (int i = 0; i < tmpCollocationsFromApi.size(); i++) {
                        System.out.println(tmpCollocationsFromApi.get(i).getPrevId() + " " + tmpCollocationsFromApi.get(i).getNextId());
                        System.out.println(tmpCollocationsFromApi.get(i).getWordResources()[0].getWord());
                        System.out.println(tmpCollocationsFromApi.get(i).getWordResources()[1].getWord());
                    }
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Collocation[]> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
            }
        });
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }

    public void postCollocationToApi(Collocation collocation) {
        mCollocationClientImpl.setCallPost(collocation);
        mCollocationClientImpl.getCallPost().enqueue(new Callback<Collocation>() {
            @Override
            public void onResponse(Call<Collocation> call, Response<Collocation> response) {
                if (response.isSuccessful()) {
                    System.out.println("CCCCCCCCCCCCCCCCC2222222222222222222PPPPPPPPPPOOOOOOOOOOOOOOOOOOOO");
                    Collocation c = response.body();
                    System.out.println(c.getWordResources()[0].getWord());
                    System.out.println(c.getWordResources()[1].getWord());
//                    CollocationStore.postToStore(c);
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCC22222222222222222222222EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Collocation> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCC2222222222222222222222222222222FFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
            }
        });
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC222222222222222222222NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }

    public void putCollocationToApi(Integer id, Collocation collocation) {
        mCollocationClientImpl.setCallPut(id, collocation);
        mCollocationClientImpl.getCallPut().enqueue(new Callback<Collocation>() {
            @Override
            public void onResponse(Call<Collocation> call, Response<Collocation> response) {
                if (response.isSuccessful()) {
                    System.out.println("CCCCCCCCCCCCCCCCC3333333333333333333333333PPPPPPPPPPUUUUUUUUUUUUUUUUUTTTTTTTTTTTTTTTTTTTTTTTT");
                    Collocation c = response.body();
                    System.out.println(c.getWordResources()[0].getWord());
                    System.out.println(c.getWordResources()[1].getWord());
//                    CollocationStore.putToStore(c.getId(), c);
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCC333333333333333333333EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Collocation> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCC333333333333333333333FFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
            }
        });
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC3333333333333333333NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
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
