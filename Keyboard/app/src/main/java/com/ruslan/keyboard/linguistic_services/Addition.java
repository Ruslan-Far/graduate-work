package com.ruslan.keyboard.linguistic_services;

import android.os.Build;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.R;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.Word;
import com.ruslan.keyboard.stores.WordStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Addition {

    private WordClientImpl mWordClientImpl;

    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private InputConnection mIc;

    public Addition(WordClientImpl wordClientImpl, Button btn, Button btn2, Button btn3) {
        mWordClientImpl = wordClientImpl;
        mBtn = btn;
        mBtn2 = btn2;
        mBtn3 = btn3;
    }

    public InputConnection getIc() {
        return mIc;
    }

    public void setIc(InputConnection ic) {
        mIc = ic;
    }

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

    private void setupHints(String[] hints) {
        System.out.println("AAAAAAAAAAA_HINTS");
        System.out.println(hints[0]);
        System.out.println(hints[1]);
        System.out.println(hints[2]);
        setupColorHints();
        checkHintsOnNullAndEmpty(hints);
        mBtn.setText(hints[0]);
        mBtn2.setText(hints[1]);
        mBtn3.setText(hints[2]);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String[] getWordsWithMaxCount(List<Word> tmp) {
        String[] hints;

        hints = new String[Constants.NUMBER_OF_HINTS];
        for (int i = 0; i < hints.length; i++) {
            if (tmp.size() != 0) {
                Word removedWord = tmp.stream().max((x, y) ->
                {
                    if (x.getCount() > y.getCount())
                        return 1;
                    return -1;
                }).get();
                hints[i] = removedWord.getWord();
                tmp.remove(removedWord);
            }
            else break;
        }
        return hints;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void start() {
        // задержать время
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("ERERERERERERERE");
            e.printStackTrace();
        }
        String[] hints;
        hints = getWordsWithMaxCount(new ArrayList<>(WordStore.words));
        System.out.println("START_AAAAAAAAAAA_HINTS");
        setupHints(hints);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void process() {
        String textBeforeCursor;
        String lastWord;
        String[] hints;

        textBeforeCursor = mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString();
        if (textBeforeCursor.length() == 0)
            return;
        lastWord = searchLastWord(textBeforeCursor);
        if (lastWord.length() == 0) {
            start();
            return;
        }
        hints = getWordsWithMaxCount(
                WordStore.words.stream()
                    .filter(x -> x.getWord().startsWith(lastWord) && x.getCount() >= Constants.NEEDED_MAX_WORDS_COUNT)
                    .collect(Collectors.toList())
        );
        System.out.println("AAAAAAAAAAA_HINTS");
        setupHints(hints);
    }

    private String searchLastWord(String textBeforeCursor) {
        StringBuilder lastWord;

        lastWord = new StringBuilder();
        for (int i = 0; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--)
            lastWord.append(textBeforeCursor.charAt(i));
        lastWord.reverse();
        return lastWord.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Word prepareForPut(String hint) {
        Word word;

        word = WordStore.words.stream()
                .filter(x -> x.getWord().equals(hint))
                .collect(Collectors.toList())
                .get(0);
        word.setCount(word.getCount() + 1);
        return word;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clickBtnAny(CharSequence hint) {
        String lastWord;
        Word word;

        lastWord = searchLastWord(mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString());
        mIc.deleteSurroundingText(lastWord.length(), 0);
        mIc.commitText(hint.toString(), 0);
        word = prepareForPut(hint.toString());
        putToApi(word.getId(), word);
        // задержать время
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("ERERERERERERERE2222");
            e.printStackTrace();
        }
        process();
    }
}
