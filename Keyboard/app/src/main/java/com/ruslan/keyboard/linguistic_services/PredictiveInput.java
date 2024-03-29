package com.ruslan.keyboard.linguistic_services;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.DatabaseInteraction;
import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.entities.Collocation;
import com.ruslan.keyboard.stores.CollocationStore;
import com.ruslan.keyboard.stores.IMESettingsStore;
import com.ruslan.keyboard.stores.WordStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PredictiveInput {

    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private DatabaseInteraction mDatabaseInteraction;

    private InputConnection mIc;

    private StringBuilder mLastWord;

    public PredictiveInput(Button btn, Button btn2, Button btn3, DatabaseInteraction databaseInteraction) {
        mBtn = btn;
        mBtn2 = btn2;
        mBtn3 = btn3;
        mDatabaseInteraction = databaseInteraction;
        resetFields();
    }

    public InputConnection getIc() {
        return mIc;
    }

    public void setIc(InputConnection ic) {
        mIc = ic;
    }

    private void clearHints() {
        IME.sLingServNum = Constants.ADDIT_LING_SERV_NUM;
        System.out.println("Otpuskaet PRED");
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

        color = IMESettingsStore.imeSettings.getCanPredTextColor();
        mBtn.setTextColor(color);
        mBtn2.setTextColor(color);
        mBtn3.setTextColor(color);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void process() {
        String textBeforeCursor;
        String[] hints;

        textBeforeCursor = mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString();
        if (textBeforeCursor.length() == 0)
            return;
        if (Character.isLetter(textBeforeCursor.charAt(textBeforeCursor.length() - 1))) {
            clearHints();
            return;
        }
        if (mLastWord.length() != 0) {
            StringBuilder penultimateWord = searchPenultimateWord(textBeforeCursor);

            System.out.println("penultimateWord=" + penultimateWord + ":len=" + penultimateWord.length());
            System.out.println("lastWord=" + mLastWord + ":len=" + mLastWord.length());

            if (penultimateWord.toString().equals(mLastWord.toString())) {
                searchLastWord(textBeforeCursor);
                List<Collocation> filteredCollocations
                        = filterCollocationsByTwoWords(penultimateWord.toString(), mLastWord.toString());
                System.out.println("IIIIIIIIIIIIMMMMMMMMMMMMMMMHHHHHHHHHH");
                System.out.println("lastWord=" + mLastWord + ":len=" + mLastWord.length());
                if (filteredCollocations.size() == 0) {
                    Collocation collocation = prepareForPost(penultimateWord.toString());
                    mDatabaseInteraction.insertCollocation(collocation);
                }
                else {
                    System.out.println("1 filteredCollocations=" + filteredCollocations.get(0).getWordResources()[0]);
                    System.out.println("2 filteredCollocations=" + filteredCollocations.get(0).getWordResources()[1]);
                    Collocation collocation = prepareForPut(filteredCollocations.get(0));
                    mDatabaseInteraction.updateCollocation(collocation.getId(), collocation);
                }
            }
        }
        searchLastWord(textBeforeCursor);
        if (mLastWord.length() == 0)
            return;
        hints = getWordsToContinue();
        System.out.println("CCCCCCCCCCCCCCHINTS");
        System.out.println(hints[0]);
        System.out.println(hints[1]);
        System.out.println(hints[2]);
        IME.sLingServNum = Constants.PRED_LING_SERV_NUM;
        System.out.println("Work PRED");
        setupColorHints();
        checkHintsOnNullAndEmpty(hints);
        mBtn.setText(hints[0]);
        mBtn2.setText(hints[1]);
        mBtn3.setText(hints[2]);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Collocation prepareForPost(String penultimateWord) {
        Collocation collocation = new Collocation();
        collocation.setPrevId(getIdByWord(penultimateWord));
        collocation.setNextId(getIdByWord(mLastWord.toString()));
        collocation.setCount(1);
        return collocation;
    }

    private Collocation prepareForPut(Collocation collocation) {
        collocation.setCount(collocation.getCount() + 1);
        return collocation;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Collocation> filterCollocationsByTwoWords(String penultimateWord, String lastWord) {
        if (CollocationStore.collocations == null)
            return new ArrayList<>();
        return CollocationStore.collocations.stream()
                .filter(x -> x.getWordResources()[0].getWord().equals(penultimateWord)
                        && x.getWordResources()[1].getWord().equals(lastWord))
                .collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getIdByWord(String word) {
        return WordStore.words.stream()
                .filter(x -> x.getWord().equals(word))
                .collect(Collectors.toList())
                .get(0)
                .getId();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String[] getWordsToContinue() {
        String[] hints;
        List<Collocation> filteredCollocations;

        hints = new String[Constants.NUMBER_OF_HINTS];
        if (CollocationStore.collocations == null)
            return hints;
        filteredCollocations = CollocationStore.collocations.stream()
                .filter(x -> x.getWordResources()[0].getWord().equals(mLastWord.toString()))
                .collect(Collectors.toList());
        for (int i = 0; i < hints.length; i++) {
            if (filteredCollocations.size() != 0) {
                Collocation removedCollocation = filteredCollocations.stream().max((x, y) ->
                {
                    if (x.getCount() > y.getCount())
                        return 1;
                    return -1;
                }).get();
                hints[i] = removedCollocation.getWordResources()[1].getWord();
                filteredCollocations.remove(removedCollocation);
            }
            else break;
        }
        return hints;
    }

    private void resetFields() {
        mLastWord = new StringBuilder();
    }

    private StringBuilder searchPenultimateWord(String textBeforeCursor) {
        StringBuilder penultimateWord;
        int i;

        penultimateWord = new StringBuilder();
        i = textBeforeCursor.length() - 1;
        for (; i >= 0 && !Character.isLetter(textBeforeCursor.charAt(i)); i--);
        for (; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--);
        for (; i >= 0 && !Character.isLetter(textBeforeCursor.charAt(i)); i--);
        for (; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--)
            penultimateWord.append(textBeforeCursor.charAt(i));
        penultimateWord.reverse();
        return penultimateWord;
    }

    private void searchLastWord(String textBeforeCursor) {
        int i;

        resetFields();
        for (i = textBeforeCursor.length() - 1; i >= 0
                && !Character.isLetter(textBeforeCursor.charAt(i)); i--);
        for (; i >= 0 && Character.isLetter(textBeforeCursor.charAt(i)); i--)
            mLastWord.append(textBeforeCursor.charAt(i));
        mLastWord.reverse();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clickBtnAny(CharSequence hint) {
        List<Collocation> filteredCollocations = filterCollocationsByTwoWords(mLastWord.toString(), hint.toString());
        Collocation collocation = prepareForPut(filteredCollocations.get(0));
        mDatabaseInteraction.updateCollocation(collocation.getId(), collocation);
        mIc.commitText(hint.toString(), 0);
        resetFields();
        clearHints();
    }
}
