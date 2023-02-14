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

import java.io.IOException;
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

    private List<Word> mTmpWordsFromApi;
    private List<Word> mSynWordsFromApi;

    private int mCountWordsInSynWordsFromApi;

    private List<Collocation> mTmpCollocationsFromApi;
    private List<Collocation> mSynCollocationsFromApi;

    private int mCountCollocationsInSynCollocationsFromApi;

    private final String mGoodMessage = "Синхронизация прошла успешно";

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
//                mMessage.setText(Constants.EMPTY_SYM);
//                getWordsFromApi(UserStore.user.getId());
//                System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                setErrorMessage(Constants.ERROR_TRANSFER_DATA);
            }
        });
        mMessage = findViewById(R.id.message);
    }

    private void synWords() {
        Word word;
        mSynWordsFromApi = new ArrayList<>();
        mCountWordsInSynWordsFromApi = 0;

        for (int i = 0; i < WordStore.words.size(); i++) {
            for (int j = 0; mTmpWordsFromApi != null && j < mTmpWordsFromApi.size(); j++) {
                if (WordStore.words.get(i).getWord().equals(mTmpWordsFromApi.get(j).getWord())) {
                    if (WordStore.words.get(i).getCount() > mTmpWordsFromApi.get(j).getCount()) {
                        word = prepareWordForPut(mTmpWordsFromApi.get(j), WordStore.words.get(i).getCount());
                        putWordToApi(word.getId(), word);
                        mCountWordsInSynWordsFromApi++;
                    }
                    else if (WordStore.words.get(i).getCount() < mTmpWordsFromApi.get(j).getCount()) {
                        word = prepareWordForPut(WordStore.words.get(i), mTmpWordsFromApi.get(j).getCount());
                        mDatabaseInteraction.updateWord(word.getId(), word);
                        mSynWordsFromApi.add(mTmpWordsFromApi.get(j));
                        mCountWordsInSynWordsFromApi++;
                    }
                    mTmpWordsFromApi.remove(j);
                    break;
                }
            }
            word = prepareWordForPost(WordStore.words.get(i));
            postWordToApi(word);
            mCountWordsInSynWordsFromApi++;
        }
        for (int i = 0; mTmpWordsFromApi != null && i < mTmpWordsFromApi.size(); i++) {
            mDatabaseInteraction.insertWord(mTmpWordsFromApi.get(i));
            mSynWordsFromApi.add(mTmpWordsFromApi.get(i));
            mCountWordsInSynWordsFromApi++;
        }
    }

    private Word prepareWordForPost(Word wordForPost) {
        Word word = new Word();
        word.setUserId(UserStore.user.getId());
        word.setWord(wordForPost.getWord());
        word.setCount(wordForPost.getCount());
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
                    mTmpWordsFromApi = new ArrayList<>(Arrays.asList(response.body()));
                    for (int i = 0; i < mTmpWordsFromApi.size(); i++) {
                        System.out.println(mTmpWordsFromApi.get(i).getWord());
                    }
                    synWords();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("second thread");
                            while (mSynWordsFromApi.size() != mCountWordsInSynWordsFromApi
                                    && IMESettingsActivity.errorMessage.length() == 0) {}
                            if (IMESettingsActivity.errorMessage.length() != 0) {
                                System.out.println("Была ошибка!!!  (getWordsFromApi)");
                                return;
                            }
                            System.out.println("Все в порядке!!!   (getWordsFromApi)");
                            getCollocationsFromApi(UserStore.user.getId(), Constants.EXPAND);
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
                else {
                    System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    setErrorMessage(Constants.ERROR_TRANSFER_DATA);
                }
            }
            @Override
            public void onFailure(Call<Word[]> call, Throwable t) {
                System.out.println("FFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
                setErrorMessage(Constants.ERROR_CONNECTION);
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
                    mSynWordsFromApi.add(w);
                }
                else {
                    System.out.println("2222222222222222222EEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    setErrorMessage(Constants.ERROR_TRANSFER_DATA);
                }
            }
            @Override
            public void onFailure(Call<Word> call, Throwable t) {
                System.out.println("2222222222FFFFFFFFFFFFFFAAAAAAAAAAAAA");
                setErrorMessage(Constants.ERROR_CONNECTION);
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
                    mSynWordsFromApi.add(w);
                }
                else {
                    System.out.println("333333333333333333333333EEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    setErrorMessage(Constants.ERROR_TRANSFER_DATA);
                }
            }
            @Override
            public void onFailure(Call<Word> call, Throwable t) {
                System.out.println("33333333333333333FFFFFFFFFFFFFFAAAAAAAAAAAAA");
                setErrorMessage(Constants.ERROR_CONNECTION);
            }
        });
        System.out.println("33333333333333333333333333333333NNNNNNNNNNNNNNNNNNN");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void synCollocations() {
        Collocation collocation;
        mSynCollocationsFromApi = new ArrayList<>();
        mCountCollocationsInSynCollocationsFromApi = 0;

        mDatabaseInteraction.selectCollocations();
        for (int i = 0; i < CollocationStore.collocations.size(); i++) {
            for (int j = 0; mTmpCollocationsFromApi != null && j < mTmpCollocationsFromApi.size(); j++) {
                if (CollocationStore.collocations.get(i).getWordResources()[0].getWord()
                        .equals(mTmpCollocationsFromApi.get(j).getWordResources()[0].getWord())
                            && CollocationStore.collocations.get(i).getWordResources()[1].getWord()
                                .equals(mTmpCollocationsFromApi.get(j).getWordResources()[1].getWord())) {
                    if (CollocationStore.collocations.get(i).getCount()
                        > mTmpCollocationsFromApi.get(j).getCount()) {
                        collocation = prepareCollocationForPut(
                                mTmpCollocationsFromApi.get(j), CollocationStore.collocations.get(i).getCount()
                        );
                        putCollocationToApi(collocation.getId(), collocation);
                        mCountCollocationsInSynCollocationsFromApi++;
                    }
                    else if (CollocationStore.collocations.get(i).getCount()
                        < mTmpCollocationsFromApi.get(j).getCount()) {
                        collocation = prepareCollocationForPut(
                                CollocationStore.collocations.get(i), mTmpCollocationsFromApi.get(j).getCount()
                        );
                        mDatabaseInteraction.updateCollocation(collocation.getId(), collocation);
                        mSynCollocationsFromApi.add(mTmpCollocationsFromApi.get(j));
                        mCountCollocationsInSynCollocationsFromApi++;
                    }
                    mTmpCollocationsFromApi.remove(j);
                    break;
                }
            }
            collocation = prepareCollocationForPost(mSynWordsFromApi, CollocationStore.collocations.get(i));
            postCollocationToApi(collocation);
            mCountCollocationsInSynCollocationsFromApi++;
        }
        for (int i = 0; mTmpCollocationsFromApi != null && i < mTmpCollocationsFromApi.size(); i++) {
            collocation = prepareCollocationForPost(WordStore.words, mTmpCollocationsFromApi.get(i));
            mDatabaseInteraction.insertCollocation(collocation);
            mSynCollocationsFromApi.add(mTmpCollocationsFromApi.get(i));
            mCountCollocationsInSynCollocationsFromApi++;
        }
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<Collocation[]> call, Response<Collocation[]> response) {
                if (response.isSuccessful()) {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                    mTmpCollocationsFromApi = new ArrayList<>(Arrays.asList(response.body()));
                    for (int i = 0; i < mTmpCollocationsFromApi.size(); i++) {
                        System.out.println(mTmpCollocationsFromApi.get(i).getPrevId() + " " + mTmpCollocationsFromApi.get(i).getNextId());
                        System.out.println(mTmpCollocationsFromApi.get(i).getWordResources()[0].getWord());
                        System.out.println(mTmpCollocationsFromApi.get(i).getWordResources()[1].getWord());
                    }
                    synCollocations();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("second CCCCCCCCCCCCCCCCCCCCCC thread");
                            while (mSynCollocationsFromApi.size() != mCountCollocationsInSynCollocationsFromApi
                                        && IMESettingsActivity.errorMessage.length() == 0) {}
//                            resetFields();
                            if (IMESettingsActivity.errorMessage.length() != 0) {
                                System.out.println("Была ошибка!!!  (getCollocationsFromApi)");
                                return;
                            }
                            System.out.println("Все в порядке!!!   (getCollocationsFromApi)");
                            mMessage.setText(mGoodMessage);
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    setErrorMessage(Constants.ERROR_TRANSFER_DATA);
                }
            }
            @Override
            public void onFailure(Call<Collocation[]> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
                setErrorMessage(Constants.ERROR_CONNECTION);
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
                    mSynCollocationsFromApi.add(c);
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCC22222222222222222222222EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    setErrorMessage(Constants.ERROR_TRANSFER_DATA);
                }
            }
            @Override
            public void onFailure(Call<Collocation> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCC2222222222222222222222222222222FFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
                setErrorMessage(Constants.ERROR_CONNECTION);
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
                    mSynCollocationsFromApi.add(c);
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCC333333333333333333333EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    setErrorMessage(Constants.ERROR_TRANSFER_DATA);
                }
            }
            @Override
            public void onFailure(Call<Collocation> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCC333333333333333333333FFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
                setErrorMessage(Constants.ERROR_CONNECTION);
            }
        });
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC3333333333333333333NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }

    private void setErrorMessage(String errorMessage) {
        IMESettingsActivity.errorMessage = errorMessage;
        finish();
    }

    private void resetFields() {
        mTmpWordsFromApi = null;
        mTmpCollocationsFromApi = null;
        mSynWordsFromApi = null;
        mCountWordsInSynWordsFromApi = 0;
        mSynCollocationsFromApi = null;
        mCountCollocationsInSynCollocationsFromApi = 0;
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
