package com.ruslan.keyboard.linguistic_services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.Word;
import com.ruslan.keyboard.repos.WordRepo;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//public class Orthocorrector extends Service {
//    @Override
//    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public void onCreate() {
//        System.out.println("CCCCCCCCCCCCCCCCCCCCCCreated");
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSStarted");
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        System.out.println("DDDDDDDDDDDDDDDDDDDDDDDestroyed");
//    }
//}

public class Orthocorrector {

    private WordClientImpl mWordClientImpl;
    private WordRepo mWordRepo;

    public Orthocorrector(WordClientImpl wordClientImpl, WordRepo wordRepo) {
        mWordClientImpl = wordClientImpl;
        mWordRepo = wordRepo;
    }

    public void getInfo(Integer userId) {
        mWordClientImpl.setCallGet(userId);
        mWordClientImpl.getCallGet().enqueue(new Callback<Word[]>() {
            @Override
            public void onResponse(Call<Word[]> call, Response<Word[]> response) {
                if (response.isSuccessful()) {
                    System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                    Word[] words = response.body();
                    for (int i = 0; i < words.length; i++) {
                        System.out.println(words[i].getWord());
                    }
                    mWordRepo.open();
                    for (int i = 0; i < words.length; i++) {
                        mWordRepo.insert(words[i]);
                    }
                    words = mWordRepo.select();
                    System.out.println("WAAAAAAAAARRRRNING" + words);
                    for (int i = 0; i < words.length; i++) {
                        System.out.println(words[i].getWord());
                    }
                    mWordRepo.close();
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

    public void postInfo(Word word) {
        mWordClientImpl.setCallPost(word);
        mWordClientImpl.getCallPost().enqueue(new Callback<Word>() {
            @Override
            public void onResponse(Call<Word> call, Response<Word> response) {
                if (response.isSuccessful()) {
                    System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
                    Word w = response.body();
                    System.out.println(w.getWord());
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
}