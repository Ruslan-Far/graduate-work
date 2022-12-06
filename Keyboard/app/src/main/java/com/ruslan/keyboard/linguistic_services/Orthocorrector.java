package com.ruslan.keyboard.linguistic_services;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.R;
import com.ruslan.keyboard.stores.UserStore;
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

    private InputConnection mIc;

    private StringBuilder mLastOther;
    private StringBuilder mLastWord;
    private int mIndexInWordStore;

    public Orthocorrector(WordClientImpl wordClientImpl, Button btn, Button btn2, Button btn3) {
        mWordClientImpl = wordClientImpl;
        mBtn = btn;
        mBtn2 = btn2;
        mBtn3 = btn3;
        resetFields();
    }

    public InputConnection getIc() {
        return mIc;
    }

    public void setIc(InputConnection ic) {
        mIc = ic;
    }

    public void getFromApi(Integer userId) {
        mWordClientImpl.setCallGet(userId);
        mWordClientImpl.getCallGet().enqueue(new Callback<Word[]>() {
            @Override
            public void onResponse(Call<Word[]> call, Response<Word[]> response) {
                if (response.isSuccessful()) {
                    System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                    WordStore.words = new ArrayList<>(Arrays.asList(response.body()));
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
                    WordStore.postToStore(w);
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

    public void putToApi(Integer id, Word word) {
        mWordClientImpl.setCallPut(id, word);
        mWordClientImpl.getCallPut().enqueue(new Callback<Word>() {
            @Override
            public void onResponse(Call<Word> call, Response<Word> response) {
                if (response.isSuccessful()) {
                    System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPUUUUUUUUUUUUUUUUUTTTTTTTTTTTTTTTTTTT");
                    Word w = response.body();
                    System.out.println(w.getWord());
                    WordStore.putToStore(id, w);
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

    @SuppressLint("ResourceAsColor")
    public void process(boolean isDel) {
        String textBeforeCursor;
        String[] hints;

        textBeforeCursor = mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString();
        hints = new String[Constants.NUMBER_OF_HINTS];
        if (textBeforeCursor.length() == 0)
            return;
        if (!Character.isLetter(textBeforeCursor.charAt(textBeforeCursor.length() - 1))) {
            searchLastWordAndOther(textBeforeCursor);
            if (mLastWord.length() == 0)
                return;
            hints = checkForSpelling();
            Log.d("PROCESS", mLastWord.toString() + "=Length=" + mLastWord.length());
        }
        else if (!isDel && mLastWord.length() != 0 && mLastOther.length() != 0) {
            if (mIndexInWordStore == -1) {
                Word word = new Word();
                word.setUserId(UserStore.user.getId());
                word.setWord(mLastWord.toString());
                word.setCount(1);
                postToApi(word);
            }
            else if (mIndexInWordStore > -1) {
                Word word = new Word();
                word.setId(WordStore.words.get(mIndexInWordStore).getId());
                word.setUserId(WordStore.words.get(mIndexInWordStore).getUserId());
                word.setWord(WordStore.words.get(mIndexInWordStore).getWord());
                word.setCount(WordStore.words.get(mIndexInWordStore).getCount() + 1);
                putToApi(word.getId(), word);
            }
            resetFields();
        }
        System.out.println("HINTS");
        System.out.println(hints[0]);
        System.out.println(hints[1]);
        System.out.println(hints[2]);
        IME.sLingServNum = 0;
        mBtn.setText(hints[0]);
        mBtn2.setText(hints[1]);
        mBtn3.setText(hints[2]);
        mBtn.setTextColor(R.color.green);
        mBtn2.setTextColor(R.color.green);
        mBtn3.setTextColor(R.color.green);
    }

    private void resetFields() {
        mLastOther = new StringBuilder();
        mLastWord = new StringBuilder();
        mIndexInWordStore = -2;
    }

    private void searchLastWordAndOther(String textBeforeCursor) {
        int i;

        resetFields();
        for (i = textBeforeCursor.length() - 1; i >= 0
                && !Character.isLetter(textBeforeCursor.charAt(i)); i--)
            mLastOther.append(textBeforeCursor.charAt(i));
        mLastOther.reverse();
        for (; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--)
            mLastWord.append(textBeforeCursor.charAt(i));
        mLastWord.reverse();
    }

    private String[] checkForSpelling() {
        for (int i = 0; i < WordStore.words.size(); i++) {
            if (mLastWord.toString().equals(WordStore.words.get(i).getWord())) {
                if (WordStore.words.get(i).getCount() >= Constants.LIMIT_MAX_WORDS_COUNT)
                    return new String[Constants.NUMBER_OF_HINTS];
                mIndexInWordStore = i;
                return getApproximateWords();
            }
        }
        mIndexInWordStore = -1;
        return getApproximateWords();
    }

    private String[] getApproximateWords() {
        int min = 999999999;
        int min2 = min;
        int min3 = min;
        int iMin = -1;
        int iMin2 = iMin;
        int iMin3 = iMin;
        int radius = 1;
        int countLetters;
        StringBuilder wordFromStore;
        String[] hints = new String[Constants.NUMBER_OF_HINTS];

        if (WordStore.words.size() == 0)
            return hints;
        for (int i = 0; i < WordStore.words.size(); i++) {
            if (WordStore.words.get(i).getCount() < Constants.LIMIT_MAX_WORDS_COUNT
                    || WordStore.words.get(i).getWord().length() == 0)
                continue;
            countLetters = 0;
            wordFromStore = new StringBuilder(WordStore.words.get(i).getWord());
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
                min3 = min2;
                iMin3 = iMin2;
                min2 = min;
                iMin2 = iMin;
                min = e;
                iMin = i;
            }
            else if (e < min2) {
                min3 = min2;
                iMin3 = iMin2;
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

    public void clickBtnAny(Button btnAny) {
        CharSequence hint;

        hint = btnAny.getText();
        searchLastWordAndOther(mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString());
        mIc.deleteSurroundingText(mLastOther.length() + mLastWord.length(), 0);
//        mIc.commitText(hint.toString() + mLastOther, IME.sLimitMaxChars);
        mIc.commitText(hint.toString() + mLastOther, 0);
    }
}
