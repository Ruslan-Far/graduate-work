package com.ruslan.keyboard.linguistic_services;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.Constants;
import com.ruslan.keyboard.DatabaseInteraction;
import com.ruslan.keyboard.IME;
import com.ruslan.keyboard.stores.IMESettingsStore;
import com.ruslan.keyboard.stores.WordStore;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Orthocorrector {

    private WordClientImpl mWordClientImpl;

    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private DatabaseInteraction mDatabaseInteraction;

    private InputConnection mIc;

    private StringBuilder mLastOther;
    private StringBuilder mLastWord;
    private int mIndexInWordStore;

    public Orthocorrector(WordClientImpl wordClientImpl,
                          Button btn, Button btn2, Button btn3, DatabaseInteraction databaseInteraction) {
        mWordClientImpl = wordClientImpl;
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
        System.out.println("Otpuskaet ORTHO");
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

        color = IMESettingsStore.imeSettings.getCanOrthoTextColor();
        mBtn.setTextColor(color);
        mBtn2.setTextColor(color);
        mBtn3.setTextColor(color);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    public void process(boolean isDel) {
        String textBeforeCursor;
        String[] hints;

        textBeforeCursor = mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString();
        if (textBeforeCursor.length() == 0)
            return;
        if (!Character.isLetter(textBeforeCursor.charAt(textBeforeCursor.length() - 1))) {
            String oldLastWord = mLastWord.toString();
            searchLastWordAndOther(textBeforeCursor);
            if (mLastWord.length() == 0 || oldLastWord.equals(mLastWord.toString())) {
                System.out.println("ORTHOCORRECTOR");
                System.out.println("mLastWord:" + mLastWord + "len:" + mLastWord.length());
                System.out.println("oldLastWord:" + oldLastWord + "len:" + oldLastWord.length());
                return;
            }
            hints = checkForSpelling();
            Log.d("PROCESS", mLastWord.toString() + "=Length=" + mLastWord.length());
            if (mIndexInWordStore != -2) {
                System.out.println("HINTS");
                System.out.println(hints[0]);
                System.out.println(hints[1]);
                System.out.println(hints[2]);
                IME.sLingServNum = Constants.ORTHO_LING_SERV_NUM;
                System.out.println("Work ORTHO");
                setupColorHints();
                checkHintsOnNullAndEmpty(hints);
                mBtn.setText(hints[0]);
                mBtn2.setText(hints[1]);
                mBtn3.setText(hints[2]);
            }
            else {
                Word word = prepareForPut(
                        WordStore.words.stream()
                                .filter(x -> x.getWord().equals(mLastWord.toString()))
                                .collect(Collectors.toList())
                                .get(0)
                );
                mDatabaseInteraction.updateWord(word.getId(), word);
                resetFields();
            }
        }
        else if (!isDel && mLastWord.length() != 0 && mLastOther.length() != 0) {
            if (mIndexInWordStore == -1) {
                Word word = prepareForPost();
                mDatabaseInteraction.insertWord(word);
            }
            else if (mIndexInWordStore > -1) {
                Word word = prepareForPut(WordStore.words.get(mIndexInWordStore));
                mDatabaseInteraction.updateWord(word.getId(), word);
            }
            resetFields();
            clearHints();
        }
        else {
            resetFields();
            clearHints();
        }
    }

    private Word prepareForPost() {
        Word word = new Word();
        word.setWord(mLastWord.toString());
        word.setCount(1);
        return word;
    }

    private Word prepareForPut(Word wordForPut) {
        Word word = new Word();
        word.setId(wordForPut.getId());
        word.setWord(wordForPut.getWord());
        word.setCount(wordForPut.getCount() + 1);
        return word;
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
        System.out.println("checkForSpelling");
        for (int i = 0; i < WordStore.words.size(); i++)
            System.out.println(WordStore.words.get(i).getWord());

        for (int i = 0; i < WordStore.words.size(); i++) {
            if (mLastWord.toString().equals(WordStore.words.get(i).getWord())) {
                if (WordStore.words.get(i).getCount() >= IMESettingsStore.imeSettings.getLearningRate())
                    return new String[Constants.NUMBER_OF_HINTS];
                mIndexInWordStore = i;
                return getApproximateWords();
            }
        }
        mIndexInWordStore = -1;
        return getApproximateWords();
    }

    private List<Word> copyWords() {
        List<Word> copyWords;

        copyWords = new ArrayList<>();
        for (int i = 0; i < WordStore.words.size(); i++) {
            if (IME.currentLocale == Constants.KEYS_TYPE.ENGLISH) {
                if (!(WordStore.words.get(i).getWord().toLowerCase().charAt(0) >= 'a'
                        && WordStore.words.get(i).getWord().toLowerCase().charAt(0) <= 'z'))
                    continue;
            }
            else {
                if (!(WordStore.words.get(i).getWord().toLowerCase().charAt(0) >= 'а'
                        && WordStore.words.get(i).getWord().toLowerCase().charAt(0) <= 'я'))
                    continue;
            }
            copyWords.add(new Word(WordStore.words.get(i).getId(),
                    WordStore.words.get(i).getWord(), WordStore.words.get(i).getCount()));
        }
        return copyWords;
    }

    private List<Word> getCopyProcessedWords() {
        List<Word> copyProcessedWords;

        copyProcessedWords = copyWords();
        if (copyProcessedWords.size() <= Constants.NUMBER_OF_HINTS)
            return copyProcessedWords;
        for (int i = 0; i < copyProcessedWords.size() - 1; i++) {
            for (int j = i + 1; j < copyProcessedWords.size(); j++) {
                if (copyProcessedWords.get(i).getWord().equals(Constants.MARK))
                    break;
                if (copyProcessedWords.get(i).getWord().equalsIgnoreCase(copyProcessedWords.get(j).getWord())) {
                    if (copyProcessedWords.get(i).getCount() > copyProcessedWords.get(j).getCount())
                        copyProcessedWords.get(j).setWord(Constants.MARK);
                    else
                        copyProcessedWords.get(i).setWord(Constants.MARK);
                    break;
                }
            }
        }
        System.out.println("copyProcessedWords");
        for (int i = 0; i < copyProcessedWords.size(); i++) {
            System.out.println(copyProcessedWords.get(i).getWord());
        }
        return copyProcessedWords;
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
        List<Word> copyProcessedWords;

        if (WordStore.words.size() == 0)
            return hints;
        copyProcessedWords = getCopyProcessedWords();
        for (int i = 0; i < copyProcessedWords.size(); i++) {
            if (copyProcessedWords.get(i).getCount() < IMESettingsStore.imeSettings.getLearningRate()
                    || copyProcessedWords.get(i).getWord().length() == 0
                    || copyProcessedWords.get(i).getWord().equals(Constants.MARK))
                continue;
            countLetters = 0;
            wordFromStore = new StringBuilder(copyProcessedWords.get(i).getWord());
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
            hints[0] = copyProcessedWords.get(iMin).getWord();
            return hints;
        }
        if (iMin3 == -1) {
            hints[0] = copyProcessedWords.get(iMin).getWord();
            hints[1] = copyProcessedWords.get(iMin2).getWord();
            return hints;
        }
        hints[0] = copyProcessedWords.get(iMin).getWord();
        hints[1] = copyProcessedWords.get(iMin2).getWord();
        hints[2] = copyProcessedWords.get(iMin3).getWord();
        return hints;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clickBtnAny(CharSequence hint) {
        searchLastWordAndOther(mIc.getTextBeforeCursor(IME.sLimitMaxChars, 0).toString());
        mIc.deleteSurroundingText(mLastOther.length() + mLastWord.length(), 0);
        mIc.commitText(hint.toString() + mLastOther, 0);
        Word word = prepareForPut(
                WordStore.words.stream()
                        .filter(x -> x.getWord().equals(hint.toString()))
                        .collect(Collectors.toList())
                        .get(0)
        );
        mDatabaseInteraction.updateWord(word.getId(), word);
        clearHints();
    }
}
