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
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.clients_impl.CollocationClientImpl;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.linguistic_services.Orthocorrector;
import com.ruslan.keyboard.linguistic_services.PredictiveInput;
import com.ruslan.keyboard.stores.CollocationStore;
import com.ruslan.keyboard.stores.UserStore;
import com.ruslan.keyboard.stores.WordStore;

public class IME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    public static final String TAG = "KEYBOARD";

    public static int sLimitMaxChars = 1000000;
    public static int sLingServNum = Constants.DEF_LING_SERV_NUM;

    private LinearLayout mGeneralContainer;
    private LinearLayout.LayoutParams mParamsGeneralContainer;

    private KeyboardView mKeyboardView;
    private Keyboard mKeyboard;
    private Constants.KEYS_TYPE mCurrentLocale;
    private Constants.KEYS_TYPE mPreviousLocale;
    private boolean mIsCapsOn = true;

    private LinearLayout mCandidateView;
    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

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

        mGeneralContainer = new LinearLayout(this);
        mGeneralContainer.setOrientation(LinearLayout.VERTICAL);
        mParamsGeneralContainer = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mGeneralContainer.addView(mKeyboardView, mParamsGeneralContainer);
        return mGeneralContainer;
    }

    private void createCandidatesView() {
        mCandidateView = (LinearLayout) getLayoutInflater().inflate(R.layout.candidates, null);
        mBtn = mCandidateView.findViewById(R.id.btn);
        mBtn2 = mCandidateView.findViewById(R.id.btn2);
        mBtn3 = mCandidateView.findViewById(R.id.btn3);
        View.OnClickListener listener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                System.out.println("BBBBBUUUUUUUTTTTTTTTTOOOOOOOOONNNNNNNNNNN");
                Button button = (Button) v;
                if (button.getText() == Constants.EMPTY_SYM)
                    return;
                if (sLingServNum == Constants.ORTHO_LING_SERV_NUM) {
                    mOrthocorrector.clickBtnAny(button.getText());
                    if (sLingServNum == Constants.PRED_LING_SERV_NUM || sLingServNum == Constants.DEF_LING_SERV_NUM) {
                        if (UserStore.user != null)
                            mPredictiveInput.process();
                    }
                }
                else if (sLingServNum == Constants.PRED_LING_SERV_NUM) {
                    mPredictiveInput.clickBtnAny(button.getText());
                }

            }
        };
        mBtn.setOnClickListener(listener);
        mBtn2.setOnClickListener(listener);
        mBtn3.setOnClickListener(listener);
        mGeneralContainer.removeView(mKeyboardView);
        mGeneralContainer.addView(mCandidateView, mParamsGeneralContainer);
        mGeneralContainer.addView(mKeyboardView, mParamsGeneralContainer);


//        int color = getResources().getColor(R.color.green);
//        int color = mBtn.getResources().getColor(R.color.green);
//        mBtn.setTextColor(color);
//        mBtn.setText("Hello");
    }

    private void initOrthocorrector() {
        mOrthocorrector = new Orthocorrector(new WordClientImpl(), mBtn, mBtn2, mBtn3);
        mOrthocorrector.getFromApi(UserStore.user.getId());
    }

    private void initPredictiveInput() {
        mPredictiveInput = new PredictiveInput(new CollocationClientImpl(), mBtn, mBtn2, mBtn3);
        mPredictiveInput.getFromApi(UserStore.user.getId(), Constants.EXPAND);
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        if (mCandidateView == null)
            createCandidatesView();
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
        clearHints();
        super.onFinishInputView(finishingInput);
    }

    private void clearHints() {
        sLingServNum = Constants.DEF_LING_SERV_NUM;
        System.out.println("Otpuskaet");
        mBtn.setText(Constants.EMPTY_SYM);
        mBtn2.setText(Constants.EMPTY_SYM);
        mBtn3.setText(Constants.EMPTY_SYM);
    }

    /**
     * @param locale - keys of keyboard
     * @return localized keyboard
     */
    private Keyboard getKeyboard(Constants.KEYS_TYPE locale) {
        switch (locale) {
            case RUSSIAN:
                return new Keyboard(this, R.xml.keys_definition_ru);
            case ENGLISH:
                return new Keyboard(this, R.xml.keys_definition_en);
            case SYMBOLS:
                return new Keyboard(this, R.xml.keys_definition_symbols);
            default:
                return new Keyboard(this, R.xml.keys_definition_ru);
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
            case Keyboard.KEYCODE_DONE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Constants.KeyCode.RETURN:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
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
        switch (primaryCode) {
            case Keyboard.KEYCODE_SHIFT:
                handleShift();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_ALT:
                handleSymbolsSwitch();
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                handleLanguageSwitch();
                break;
            default:
                if (primaryCode == Keyboard.KEYCODE_DELETE) {
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
