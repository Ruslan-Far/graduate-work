package com.ruslan.keyboard;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.clients_impl.CollocationClientImpl;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.linguistic_services.Orthocorrector;
import com.ruslan.keyboard.linguistic_services.PredictiveInput;
import com.ruslan.keyboard.stores.CollocationStore;
import com.ruslan.keyboard.stores.UserStore;
import com.ruslan.keyboard.stores.WordStore;

import java.util.List;

public class IME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    public static final String TAG = "KEYBOARD";

    public static int sLimitMaxChars = 1000000;
    public static int sLingServNum = Constants.DEF_LING_SERV_NUM;

    private KeyboardView mKeyboardView;
    private android.inputmethodservice.Keyboard mKeyboard;
    private Constants.KEYS_TYPE mCurrentLocale;
    private Constants.KEYS_TYPE mPreviousLocale;
    private boolean mIsCapsOn = true;

    private Keyboard.Key mCan;
    private Keyboard.Key mCan2;
    private Keyboard.Key mCan3;

    private Orthocorrector mOrthocorrector;
    private PredictiveInput mPredictiveInput;

    private DatabaseInteraction mDatabaseInteraction;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        mKeyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        mCurrentLocale = Constants.KEYS_TYPE.RUSSIAN;
        mKeyboard = getKeyboard(mCurrentLocale);
        mKeyboard.setShifted(mIsCapsOn);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(this);

        return mKeyboardView;
    }

    private void initCandidates() {
        List<Keyboard.Key> keys = mKeyboard.getKeys();
        mCan = keys.get(0);
        mCan2 = keys.get(1);
        mCan3 = keys.get(2);
        mCan.label = Constants.EMPTY_SYM;
        mCan2.label = Constants.EMPTY_SYM;
        mCan3.label = Constants.EMPTY_SYM;
    }

    private void initOrthocorrector() {
        mOrthocorrector = new Orthocorrector(new WordClientImpl(), mCan, mCan2, mCan3);
        mOrthocorrector.getFromApi(UserStore.user.getId());
    }

    private void initPredictiveInput() {
        mPredictiveInput = new PredictiveInput(new CollocationClientImpl(), mCan, mCan2, mCan3);
        mPredictiveInput.getFromApi(UserStore.user.getId(), Constants.EXPAND);
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        initCandidates();
        mDatabaseInteraction = new DatabaseInteraction(this);
        mDatabaseInteraction.selectUser();
        if (UserStore.user != null) {
            initOrthocorrector();
            initPredictiveInput();
        }
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        System.out.println("FFFFIIIIIIIINNNNNNNNNNNNNIIIIIIIIIIIISSSSSSSSSSSHHHHHHHHHHHH");
        UserStore.user = null;
        WordStore.words = null;
        CollocationStore.collocations = null;
        super.onFinishInputView(finishingInput);
    }

    /**
     * @param locale - keys of keyboard
     * @return localized keyboard
     */
    private android.inputmethodservice.Keyboard getKeyboard(Constants.KEYS_TYPE locale) {
        switch (locale) {
            case RUSSIAN:
                return new android.inputmethodservice.Keyboard(this, R.xml.keys_definition_ru);
            case ENGLISH:
                return new android.inputmethodservice.Keyboard(this, R.xml.keys_definition_en);
            case SYMBOLS:
                return new android.inputmethodservice.Keyboard(this, R.xml.keys_definition_symbols);
            default:
                return new android.inputmethodservice.Keyboard(this, R.xml.keys_definition_ru);
        }
    }

    /**
     * Play sound on key press
     *
     * @param keyCode of pressed key
     */
    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case Constants.KeyCode.SPACE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case android.inputmethodservice.Keyboard.KEYCODE_DONE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Constants.KeyCode.RETURN:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case android.inputmethodservice.Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void selectLingServ(CharSequence candidate) {
        if (UserStore.user != null)
            if (sLingServNum == Constants.ORTHO_LING_SERV_NUM)
                mOrthocorrector.clickCanAny(candidate);
            else if (sLingServNum == Constants.PRED_LING_SERV_NUM)
                mPredictiveInput.clickCanAny(candidate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void candidatesListener(int primaryCode) {
        if (primaryCode == mCan.codes[0] && mCan.label != Constants.EMPTY_SYM)
            selectLingServ(mCan.label);
        else if (primaryCode == mCan2.codes[0] && mCan2.label != Constants.EMPTY_SYM)
            selectLingServ(mCan2.label);
        else if (mCan3.label != Constants.EMPTY_SYM)
            selectLingServ(mCan3.label);
    }

    @Override
    public void onPress(int i) {
        Log.d(TAG, "onPress " + i);
    }

    @Override
    public void onRelease(int i) {
        Log.d(TAG, "onRelease " + i);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onKey(int primaryCode, int[] ints) {
        Log.d(TAG, "onKey " + primaryCode);
        InputConnection ic = getCurrentInputConnection();
        if (UserStore.user != null) {
            mOrthocorrector.setIc(ic);
            mPredictiveInput.setIc(ic);
        }
        playClick(primaryCode);
        if (primaryCode >= mCan.codes[0] && primaryCode <= mCan3.codes[0]) {
            candidatesListener(primaryCode);
            return;
        }
        switch (primaryCode) {
            case android.inputmethodservice.Keyboard.KEYCODE_SHIFT:
                handleShift();
                break;
            case android.inputmethodservice.Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case android.inputmethodservice.Keyboard.KEYCODE_ALT:
                handleSymbolsSwitch();
                break;
            case android.inputmethodservice.Keyboard.KEYCODE_MODE_CHANGE:
                handleLanguageSwitch();
                break;
            default:
                if (primaryCode == android.inputmethodservice.Keyboard.KEYCODE_DELETE) {
                    ic.deleteSurroundingText(1, 0);
                    if (sLingServNum == Constants.ORTHO_LING_SERV_NUM || sLingServNum == Constants.DEF_LING_SERV_NUM)
                        if (UserStore.user != null)
                            mOrthocorrector.process(true);
                }
                else {
                    char code = (char) primaryCode;
                    if (Character.isLetter(code) && mIsCapsOn) {
                        code = Character.toUpperCase(code);
                    }
                    ic.commitText(String.valueOf(code), 1);
                    if (sLingServNum == Constants.ORTHO_LING_SERV_NUM || sLingServNum == Constants.DEF_LING_SERV_NUM)
                        if (UserStore.user != null)
                            mOrthocorrector.process(false);
                }
                if (sLingServNum == Constants.PRED_LING_SERV_NUM || sLingServNum == Constants.DEF_LING_SERV_NUM) {
                    if (UserStore.user != null)
                        mPredictiveInput.process();
                }
                break;
        }
    }

    @Override
    public void onUpdateSelection(int oldSelStart,
                                   int oldSelEnd,
                                   int newSelStart,
                                   int newSelEnd,
                                   int candidatesStart,
                                   int candidatesEnd) {
        System.out.println("555555555555555555555555555555555555onUpdateSelection");
        System.out.println("oldSelStart:" + oldSelStart);
        System.out.println("oldSelEnd:" + oldSelEnd);
        System.out.println("newSelStart:" + newSelStart);
        System.out.println("newSelEnd:" + newSelEnd);
        System.out.println("candidatesStart:" + candidatesStart);
        System.out.println("candidatesEnd:" + candidatesEnd);

    }

    @Override
    public void onText(CharSequence charSequence) {
        Log.d(TAG, "onText " + charSequence);
    }

    @Override
    public void swipeLeft() {
        Log.d(TAG, "swipeLeft ");
    }

    @Override
    public void swipeRight() {
        Log.d(TAG, "swipeRight ");
    }

    @Override
    public void swipeDown() {
        Log.d(TAG, "swipeDown ");
    }

    @Override
    public void swipeUp() {
        Log.d(TAG, "swipeUp ");
    }

    private void handleSymbolsSwitch() {
        if (mCurrentLocale != Constants.KEYS_TYPE.SYMBOLS) {
            mKeyboard = getKeyboard(Constants.KEYS_TYPE.SYMBOLS);
            mPreviousLocale = mCurrentLocale;
            mCurrentLocale = Constants.KEYS_TYPE.SYMBOLS;
        } else {
            mKeyboard = getKeyboard(mPreviousLocale);
            mCurrentLocale = mPreviousLocale;
            mKeyboard.setShifted(mIsCapsOn);
        }
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.invalidateAllKeys();
    }

    private void handleShift() {
        mIsCapsOn = !mIsCapsOn;
        mKeyboard.setShifted(mIsCapsOn);
        mKeyboardView.invalidateAllKeys();
    }

    private void handleLanguageSwitch() {
        if (mCurrentLocale == Constants.KEYS_TYPE.RUSSIAN) {
            mCurrentLocale = Constants.KEYS_TYPE.ENGLISH;
            mKeyboard = getKeyboard(Constants.KEYS_TYPE.ENGLISH);
        } else {
            mCurrentLocale = Constants.KEYS_TYPE.RUSSIAN;
            mKeyboard = getKeyboard(Constants.KEYS_TYPE.RUSSIAN);
        }

        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboard.setShifted(mIsCapsOn);
        mKeyboardView.invalidateAllKeys();
    }
}
