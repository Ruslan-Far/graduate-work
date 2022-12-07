package com.ruslan.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.clients_impl.CollocationClientImpl;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.linguistic_services.Orthocorrector;
import com.ruslan.keyboard.linguistic_services.PredictiveInput;
import com.ruslan.keyboard.repos.UserRepo;
import com.ruslan.keyboard.stores.UserStore;

public class IME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    public static final String TAG = "KEYBOARD";

    public static int sLimitMaxChars = 1000000;
    public static int sLingServNum = -1; // 0 - Orthocorrector, 1 - PredictiveInput

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
    private PredictiveInput mPredictiveInput;

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

//    @SuppressLint("InflateParams")
//    @Override
//    public View onCreateCandidatesView() {
//        mCandidateView = getLayoutInflater().inflate(R.layout.candidate, null);
//        mBtn = mCandidateView.findViewById(R.id.btn);
//        mBtn2 = mCandidateView.findViewById(R.id.btn2);
//        mBtn3 = mCandidateView.findViewById(R.id.btn3);
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("BBBBBUUUUUUUTTTTTTTTTOOOOOOOOONNNNNNNNNNN");
//                if (((Button) v).getText() == "")
//                    return;
//                if (sLingServNum == 0) {
//                    mOrthocorrector.clickBtnAny((Button) v);
//                }
//            }
//        };
//        mBtn.setOnClickListener(listener);
//        mBtn2.setOnClickListener(listener);
//        mBtn3.setOnClickListener(listener);
//
//        setCandidatesViewShown(true);
////        setCandidatesViewShown(false);
//
//        System.out.println("CCCCCCAAAAAAAAAANNNNNNNNNDDDDDDDDDDDDIIIIIIIIIIIIDDDDDDAAAAAAAAAATTTTTTTTTTTEEEEEEEE");
//        return mCandidateView;
//    }

    @SuppressLint("InflateParams")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateCandidatesView() {
        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View wordBar = li.inflate(R.layout.wordbar, null);
//        LinearLayout ll = (LinearLayout) wordBar.findViewById(R.id.words);
//        Button btn = (Button) wordBar.findViewById(R.id.button1);
//        btn.setOnClickListener(this);
        mCandidateView = li.inflate(R.layout.candidate, null);
        mBtn = mCandidateView.findViewById(R.id.btn);
        mBtn2 = mCandidateView.findViewById(R.id.btn2);
        mBtn3 = mCandidateView.findViewById(R.id.btn3);
//        mCandidateView = new CandidateView(this);
//        mCandidateView.setSe
        setCandidatesViewShown(true);
        mCandidateView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        ll.addView(mCandidateView);
//        return wordBar;
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

    private void initPredictiveInput() {
        mPredictiveInput = new PredictiveInput(new CollocationClientImpl(), mBtn, mBtn2, mBtn3);
        mPredictiveInput.getFromApi(UserStore.user.getId(), Constants.EXPAND);
    }

//    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
//        requestShowSelf(0);
//        System.out.println("FULL" + isFullscreenMode());
//        if (isFullscreenMode())
//            setExtractViewShown(false);
        initUserStore();
        initOrthocorrector();
        initPredictiveInput();
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
//        setCandidatesViewShown(true);
//        requestHideSelf(0);

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

//    @Override
//    public void onExtractedCursorMovement (int dx, int dy) {
////        super.onExtractedCursorMovement(dx, dy);
//        System.out.println("CCCCCCCUUUUUUUUUUUUURRRRRRRRRRRSSSSSSSSSOOOOOOOOORRRRRRRRRRRRRRRR");
//    }

    @Override
    public void onExtractedSelectionChanged (int start,
                                             int end) {
        System.out.println("111111111111111111111111111111111111111");
    }

    public void onExtractedTextClicked () {
        System.out.println("222222222222222222222222222222222222222222");
    }

    public void onExtractingInputChanged (EditorInfo ei) {
        System.out.println("33333333333333333333333333333333333333333333");
    }

//    @RequiresApi(api = Build.VERSION_CODES.R)
    @RequiresApi(api = Build.VERSION_CODES.S)
    public void onUpdateExtractingVisibility (EditorInfo ei) {
//        super.onUpdateExtractingVisibility(ei);
//        setExtractViewShown(true);
        System.out.println("4444444444444444444444444444444444444444444444444");
        System.out.println(ei.getInitialSelectedText(0).chars());
        System.out.println(ei.getInitialSurroundingText(1, 0, 0).getText());
    }

    public void onUpdateSelection (int oldSelStart,
                                   int oldSelEnd,
                                   int newSelStart,
                                   int newSelEnd,
                                   int candidatesStart,
                                   int candidatesEnd) {
        System.out.println("555555555555555555555555555555555555onUpdateSelection");
    }

    public void onExtractedCursorMovement (int dx,
                                           int dy) {
        System.out.println("66666666666666666666666666666666666666666666666_dx" + dx + "dy" + dy);
    }
}
