package com.ruslan.keyboard.linguistic_services;

import android.view.inputmethod.InputConnection;
import android.widget.Button;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.R;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.Word;
import com.ruslan.keyboard.stores.WordStore;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Addition {

    private WordClientImpl mWordClientImpl;

    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private InputConnection mIc;

    private StringBuilder mLastOther;
    private StringBuilder mLastWord;
    private int mIndexInWordStore;

    public Addition(WordClientImpl wordClientImpl, Button btn, Button btn2, Button btn3) {
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

//    public void getFromApi(Integer userId) {
//        mWordClientImpl.setCallGet(userId);
//        mWordClientImpl.getCallGet().enqueue(new Callback<Word[]>() {
//            @Override
//            public void onResponse(Call<Word[]> call, Response<Word[]> response) {
//                if (response.isSuccessful()) {
//                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
//                    WordStore.words = new ArrayList<>(Arrays.asList(response.body()));
//                    for (int i = 0; i < WordStore.words.size(); i++) {
//                        System.out.println(WordStore.words.get(i).getWord());
//                    }
//                }
//                else {
//                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
//                }
//            }
//            @Override
//            public void onFailure(Call<Word[]> call, Throwable t) {
//                System.out.println("AAAAAAAAAAAAAAAAAAAFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
//            }
//        });
//        System.out.println("AAAAAAAAAAAAAAAAAAAAAAANNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
//    }

    public void putToApi(Integer id, Word word) {
        mWordClientImpl.setCallPut(id, word);
        mWordClientImpl.getCallPut().enqueue(new Callback<Word>() {
            @Override
            public void onResponse(Call<Word> call, Response<Word> response) {
                if (response.isSuccessful()) {
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPUUUUUUUUUUUUUUUUUTTTTTTTTTTTTTTTTTTT");
                    Word w = response.body();
                    System.out.println(w.getWord());
                    WordStore.putToStore(w.getId(), w);
                }
                else {
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA333333333333333333333333EEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                }
            }
            @Override
            public void onFailure(Call<Word> call, Throwable t) {
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA33333333333333333FFFFFFFFFFFFFFAAAAAAAAAAAAA");
            }
        });
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA33333333333333333333333333333333NNNNNNNNNNNNNNNNNNN");
    }

    private void clearHints() {
        mBtn.setText(Constants.EMPTY_SYM);
        mBtn2.setText(Constants.EMPTY_SYM);
        mBtn3.setText(Constants.EMPTY_SYM);
    }

    private void checkHintsOnNullAndEmpty(String[] hints) {
        for (int i = 0; i < hints.length; i++) {
            if (hints[i] == null || hints[i].length() == 0)
                hints[i] = Constants.EMPTY_SYM;
        }
    }

    private void setupColorHints() {
        int color;

        color = mBtn.getResources().getColor(R.color.dark_cyan);
        mBtn.setTextColor(color);
        mBtn2.setTextColor(color);
        mBtn3.setTextColor(color);
    }

    public void start() {
        // задержать время
    }

    public void process() {

    }

    public void clickBtnAny(CharSequence hint) {
        searchLastWordAndOther(mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString());
        mIc.deleteSurroundingText(mLastOther.length() + mLastWord.length(), 0);
        mIc.commitText(hint.toString() + mLastOther, 0);
//        IME.sLingServNum = Constants.DEF_LING_SERV_NUM;
//        System.out.println("Otpuskaet ADDIT");
        process();
    }
}
