package com.ruslan.keyboard.linguistic_services;

import android.os.Build;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.DatabaseInteraction;
import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.R;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.Word;
import com.ruslan.keyboard.stores.WordStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Addition {

    private WordClientImpl mWordClientImpl;

    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private DatabaseInteraction mDatabaseInteraction;

    private InputConnection mIc;

    public Addition(WordClientImpl wordClientImpl,
                    Button btn, Button btn2, Button btn3, DatabaseInteraction databaseInteraction) {
        mWordClientImpl = wordClientImpl;
        mBtn = btn;
        mBtn2 = btn2;
        mBtn3 = btn3;
        mDatabaseInteraction = databaseInteraction;
    }

    public InputConnection getIc() {
        return mIc;
    }

    public void setIc(InputConnection ic) {
        mIc = ic;
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
    private void start() {
        String[] hints;
        List<Word> copyWords;

        System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
        System.out.println(WordStore.words);
        copyWords = new ArrayList<>(WordStore.words);
        if (IME.currentLocale == Constants.KEYS_TYPE.ENGLISH)
            copyWords = copyWords.stream()
                    .filter(x -> x.getWord().toLowerCase().charAt(0) >= 'a'
                                && x.getWord().toLowerCase().charAt(0) <= 'z')
                    .collect(Collectors.toList());
        else
            copyWords = copyWords.stream()
                    .filter(x -> x.getWord().toLowerCase().charAt(0) >= 'а'
                                && x.getWord().toLowerCase().charAt(0) <= 'я')
                    .collect(Collectors.toList());
        hints = getWordsWithMaxCount(copyWords.stream()
                .filter(x -> x.getCount() >= Constants.NEEDED_MAX_WORDS_COUNT)
                .collect(Collectors.toList())
        );
        System.out.println("START_AAAAAAAAAAA_HINTS");
        setupHints(hints);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void process() {
        String textBeforeCursor;
        String lastWord;
        String[] hints;

        textBeforeCursor = mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString();
        System.out.println("TTTTEEEEXXTTTBEFORECURSOR:" + textBeforeCursor + "len:" + textBeforeCursor.length());
        if (textBeforeCursor.length() == 0) {
            start();
            return;
        }
        lastWord = searchLastWord(textBeforeCursor);
        System.out.println("lastWord:" + lastWord + "len:" + lastWord.length());
        if (lastWord.length() == 0) {
            System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
            start();
            return;
        }
        hints = getWordsWithMaxCount(
                WordStore.words.stream()
                    .filter(x -> x.getWord().startsWith(lastWord)
                            && x.getWord().length() > lastWord.length()
                            && x.getCount() >= Constants.NEEDED_MAX_WORDS_COUNT)
                    .collect(Collectors.toList())
        );
        setupHints(hints);
    }

    private String searchLastWord(String textBeforeCursor) {
        StringBuilder lastWord;

        lastWord = new StringBuilder();
        for (int i = textBeforeCursor.length() - 1; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--)
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
        mDatabaseInteraction.updateWord(word.getId(), word);
        process();
    }
}
