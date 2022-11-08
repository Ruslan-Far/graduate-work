package com.ruslan.keyboard.linguistic_services;

import android.util.Log;
import android.widget.Button;

import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.stores.WordStore;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.Word;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Orthocorrector {

    private WordClientImpl mWordClientImpl;

    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private String mLastWord;
    private String mLastOtherChars;

    public Orthocorrector(WordClientImpl wordClientImpl, Button btn, Button btn2, Button btn3) {
        mWordClientImpl = wordClientImpl;
        mBtn = btn;
        mBtn2 = btn2;
        mBtn3 = btn3;
    }

    public void getFromApi(Integer userId) {
        mWordClientImpl.setCallGet(userId);
        mWordClientImpl.getCallGet().enqueue(new Callback<Word[]>() {
            @Override
            public void onResponse(Call<Word[]> call, Response<Word[]> response) {
                if (response.isSuccessful()) {
                    System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                    WordStore.words = Arrays.asList(response.body());
                    for (int i = 0; i < WordStore.words.size(); i++) {
                        System.out.println(WordStore.words.get(i).getWord());
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

    public void postToApi(Word word) {
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

    public void process(String textBeforeCursor) {
//        System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ" + (char) primaryCode);
//        mBtn.setText(String.valueOf((char) primaryCode));
//        mBtn2.setText(WordStore.words.get(0).getWord());
//        mBtn3.setText(WordStore.words.get(1).getWord());
        System.out.println(Log.d("PROCESS", textBeforeCursor));
        System.out.println(Log.d("PROCESS", textBeforeCursor.length() + ""));
    }
}