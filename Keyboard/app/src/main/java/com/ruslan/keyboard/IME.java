package com.ruslan.keyboard;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.linguistic_services.Orthocorrector;
import com.ruslan.keyboard.repos.UserRepo;
import com.ruslan.keyboard.stores.UserStore;

public class IME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    public static final String TAG = "KEYBOARD";

    public static int sLimitMaxChars = 1000000;
    public static int sLingServNum = -1; // 0 - ortho, 1 - predicVvod

    private KeyboardView mKeyboardView;
    private android.inputmethodservice.Keyboard mKeyboard;
    private Constants.KEYS_TYPE mCurrentLocale;
    private Constants.KEYS_TYPE mPreviousLocale;
    private boolean isCapsOn = true;

    private View mCandidateView;
    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private Orthocorrector mOrthocorrector;

    private UserRepo mUserRepo;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        mKeyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        mCurrentLocale = Constants.KEYS_TYPE.RUSSIAN;
        mKeyboard = getKeyboard(mCurrentLocale);
        mKeyboard.setShifted(isCapsOn);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(this);

        return mKeyboardView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateCandidatesView() {
        mCandidateView = getLayoutInflater().inflate(R.layout.candidate, null);
        mBtn = mCandidateView.findViewById(R.id.btn);
        mBtn2 = mCandidateView.findViewById(R.id.btn2);
        mBtn3 = mCandidateView.findViewById(R.id.btn3);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sLingServNum == 0) {
                    mOrthocorrector.clickBtnAny((Button) v);
                }
            }
        };
        mBtn.setOnClickListener(listener);
        mBtn2.setOnClickListener(listener);
        mBtn3.setOnClickListener(listener);

        setCandidatesViewShown(true);

        return mCandidateView;
    }

    private void initUserStore() {
        mUserRepo = new UserRepo(this);
        mUserRepo.open();
//        mUserRepo.insert(new User(3, "r", "49"));
        UserStore.user = mUserRepo.select();
        mUserRepo.close();
    }

    private void initOrthocorrector() {
        mOrthocorrector = new Orthocorrector(new WordClientImpl(), mBtn, mBtn2, mBtn3);
        mOrthocorrector.getFromApi(UserStore.user.getId());
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        initUserStore();
        initOrthocorrector();
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

    @Override
    public void onPress(int i) {
        Log.d(TAG, "onPress " + i);
    }

    @Override
    public void onRelease(int i) {
        Log.d(TAG, "onRelease " + i);
    }

    @Override
    public void onKey(int primaryCode, int[] ints) {
        Log.d(TAG, "onKey " + primaryCode);
        InputConnection ic = getCurrentInputConnection();
        mOrthocorrector.setIc(ic);
        playClick(primaryCode);

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
                    mOrthocorrector.process(true);
                }
                else {
                    char code = (char) primaryCode;
                    if (Character.isLetter(code) && isCapsOn) {
                        code = Character.toUpperCase(code);
                    }
                    ic.commitText(String.valueOf(code), 1);
                    mOrthocorrector.process(false);
                }
                break;
        }
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
            mKeyboard.setShifted(isCapsOn);
        }
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.invalidateAllKeys();
    }

    private void handleShift() {
        isCapsOn = !isCapsOn;
        mKeyboard.setShifted(isCapsOn);
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
        mKeyboard.setShifted(isCapsOn);
        mKeyboardView.invalidateAllKeys();
    }
}
