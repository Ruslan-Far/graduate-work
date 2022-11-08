package com.ruslan.keyboard.linguistic_services;

import android.util.Log;
import android.widget.Button;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.stores.WordStore;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Orthocorrector {

    private WordClientImpl mWordClientImpl;

    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private StringBuilder mLastWord;

    public Orthocorrector(WordClientImpl wordClientImpl, Button btn, Button btn2, Button btn3) {
        mWordClientImpl = wordClientImpl;
        mBtn = btn;
        mBtn2 = btn2;
        mBtn3 = btn3;
        mLastWord = new StringBuilder();
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
        if (!Character.isLetter(textBeforeCursor.charAt(textBeforeCursor.length() - 1))) {
            getLastWord(textBeforeCursor);

            Log.d("PROCESS", mLastWord.toString() + "=Length=" + mLastWord.length());
        }
        else {
            mLastWord = new StringBuilder();
            Log.d("PROCESS2", mLastWord.toString() + "=Length=" + mLastWord.length());
        }
    }

    private void getLastWord(String textBeforeCursor) {
        int i;

        for (i = textBeforeCursor.length() - 1; i >= 0 && !Character.isLetter(textBeforeCursor.charAt(i)); i--);
        for (; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--) {
            mLastWord.append(textBeforeCursor.charAt(i));
        }
        mLastWord = mLastWord.reverse();
    }

    private String[] checkForSpelling() {
        String[] hints = new String[Constants.NUMBER_OF_HINTS];

        for (int i = 0; i < WordStore.words.size(); i++) {
            if (mLastWord.toString().equals(WordStore.words.get(i).getWord())) {
                if (WordStore.words.get(i).getCount() == Constants.LIMIT_MAX_WORDS_COUNT)
                    break;
//                List<Word> dupWords = new ArrayList<>(WordStore.words);
//                for (int j = 0; j < hints.length; j++) {
//                    hints[j] = getApproximateWord(dupWords);
//                }
                hints = getApproximateWords();
                break;
            }
        }
        return hints;
    }

    private String[] getApproximateWords() {
        int iMin = -1;
        int iMin2 = iMin;
        int iMin3 = iMin;
        String[] hints = new String[Constants.NUMBER_OF_HINTS];

        if (WordStore.words.size() == 0)
            return hints;
        for (int i = 0; i < WordStore.words.size(); i++) {
            if (WordStore.words.get(i).getCount() < Constants.LIMIT_MAX_WORDS_COUNT || WordStore.words.get(i).getWord().length() == 0)
                continue;
            int min = 999999999;
            int min2 = min;
            int min3 = min;
            int radius = 1;
            int countLetters = 0;
            StringBuilder wordFromStore = new StringBuilder(WordStore.words.get(i).getWord());
            for (int j = 0; j < mLastWord.length(); j++) {
                for (int k = 0; k < wordFromStore.length(); k++) {
                    if (Math.abs(k - j) > radius)
                        if (k - j < 0)
                            continue;
                        else
                            break;
                    if (mLastWord.charAt(j) == wordFromStore.charAt(k)) {
                        countLetters++;
                        wordFromStore.setCharAt(k, '0');
                        break;
                    }
                }
            }
            int e;
            if (mLastWord.length() > wordFromStore.length())
                e = Math.abs(countLetters - mLastWord.length());
            else
                e = Math.abs(countLetters - wordFromStore.length());
            if (e < min) {
                min = e;
                iMin = i;
            }
            else if (e < min2) {
                min2 = e;
                iMin2 = i;
            }
            else if (e < min3) {
                min3 = e;
                iMin3 = i;
            }
        }
        if (iMin == -1)
            return hints;
        if (iMin2 == -1) {
            hints[0] = WordStore.words.get(iMin).getWord();
            return hints;
        }
        if (iMin3 == -1) {
            hints[0] = WordStore.words.get(iMin).getWord();
            hints[1] = WordStore.words.get(iMin2).getWord();
            return hints;
        }
        hints[0] = WordStore.words.get(iMin).getWord();
        hints[1] = WordStore.words.get(iMin2).getWord();
        hints[2] = WordStore.words.get(iMin3).getWord();
        return hints;
    }
}