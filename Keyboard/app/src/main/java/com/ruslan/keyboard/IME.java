package com.ruslan.keyboard;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.ruslan.keyboard.entities.IMESettings;
import com.ruslan.keyboard.linguistic_services.Addition;
import com.ruslan.keyboard.linguistic_services.Orthocorrector;
import com.ruslan.keyboard.linguistic_services.PredictiveInput;
import com.ruslan.keyboard.stores.CollocationStore;
import com.ruslan.keyboard.stores.IMESettingsStore;
import com.ruslan.keyboard.stores.WordStore;

public class IME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    public static final String TAG = "KEYBOARD";

    public static int sLimitMaxChars = 1000000;
    public static int sLingServNum = Constants.ADDIT_LING_SERV_NUM;

    private LinearLayout mGeneralContainer;
    private LinearLayout.LayoutParams mParamsGeneralContainer;

    private KeyboardView mKeyboardView;
    private Keyboard mKeyboard;

    public static Constants.KEYS_TYPE currentLocale;

    private Constants.KEYS_TYPE mPreviousLocale;

    private boolean mIsCapsOn = true;

    private LinearLayout mCandidateView;
    private Button mBtn;
    private Button mBtn2;
    private Button mBtn3;

    private Orthocorrector mOrthocorrector;
    private PredictiveInput mPredictiveInput;
    private Addition mAddition;

    private DatabaseInteraction mDatabaseInteraction;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        mKeyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        currentLocale = Constants.KEYS_TYPE.RUSSIAN;
        mKeyboard = getKeyboard(currentLocale);
        mKeyboard.setShifted(mIsCapsOn);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(this);

        mCandidateView = null;

        mGeneralContainer = new LinearLayout(this);
        mGeneralContainer.setOrientation(LinearLayout.VERTICAL);
        mParamsGeneralContainer = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mGeneralContainer.addView(mKeyboardView, mParamsGeneralContainer);
        return mGeneralContainer;
    }

    private void createCandidatesView() {
        mGeneralContainer.removeAllViews();
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
                    if (sLingServNum == Constants.PRED_LING_SERV_NUM || sLingServNum == Constants.ADDIT_LING_SERV_NUM) {
                        mPredictiveInput.process();
                    }
                }
                else if (sLingServNum == Constants.PRED_LING_SERV_NUM) {
                    mPredictiveInput.clickBtnAny(button.getText());
                }
                else {
                    mAddition.clickBtnAny(button.getText());
                }
            }
        };
        mBtn.setOnClickListener(listener);
        mBtn2.setOnClickListener(listener);
        mBtn3.setOnClickListener(listener);
        mGeneralContainer.addView(mCandidateView, mParamsGeneralContainer);
        mGeneralContainer.addView(mKeyboardView, mParamsGeneralContainer);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initOrthocorrector() {
        mOrthocorrector = new Orthocorrector(mBtn, mBtn2, mBtn3, mDatabaseInteraction);
        mOrthocorrector.setIc(getCurrentInputConnection());
        mOrthocorrector.process(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initPredictiveInput() {
        mPredictiveInput = new PredictiveInput(mBtn, mBtn2, mBtn3, mDatabaseInteraction);
        mPredictiveInput.setIc(getCurrentInputConnection());
        if (sLingServNum == Constants.PRED_LING_SERV_NUM || sLingServNum == Constants.ADDIT_LING_SERV_NUM)
            mPredictiveInput.process();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initAddition() {
        mAddition = new Addition(mBtn, mBtn2, mBtn3, mDatabaseInteraction);
        mAddition.setIc(getCurrentInputConnection());
        if (sLingServNum == Constants.ADDIT_LING_SERV_NUM)
            mAddition.process();
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        System.out.println("3WordStore.words=" + WordStore.words);
        mDatabaseInteraction = new DatabaseInteraction(this);
        mDatabaseInteraction.selectIMESettings();
        if (IMESettingsStore.imeSettings == null) {
            mDatabaseInteraction.insertIMESettings(new IMESettings());
            mDatabaseInteraction.selectIMESettings();
        }
        if (mCandidateView == null
                && IMESettingsStore.imeSettings.getCandidates() == Constants.TRUE)
            createCandidatesView();
        else if (mCandidateView != null
                && IMESettingsStore.imeSettings.getCandidates() == Constants.FALSE)
            destroyCandidatesView();
        if (mCandidateView != null) {
            int backgroundColor;
            String font;

            backgroundColor = IMESettingsStore.imeSettings.getCanBackgroundColor();
            font = IMESettingsStore.imeSettings.getCanFont();
            mCandidateView.setBackgroundColor(backgroundColor);
            mBtn.setBackgroundColor(backgroundColor);
            mBtn2.setBackgroundColor(backgroundColor);
            mBtn3.setBackgroundColor(backgroundColor);
            mBtn.setTypeface(Typeface.create(font, Typeface.NORMAL));
            mBtn2.setTypeface(Typeface.create(font, Typeface.NORMAL));
            mBtn3.setTypeface(Typeface.create(font, Typeface.NORMAL));
            mDatabaseInteraction.selectWords();
            if (WordStore.words == null) {
                mDatabaseInteraction.insertWords();
                mDatabaseInteraction.selectWords();
            }
            mDatabaseInteraction.selectCollocations();
            initOrthocorrector();
            initPredictiveInput();
            initAddition();
        }
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        System.out.println("FFFFIIIIIIIINNNNNNNNNNNNNIIIIIIIIIIIISSSSSSSSSSSHHHHHHHHHHHH");
        IMESettingsStore.imeSettings = null;
        if (mCandidateView != null) {
            WordStore.words = null;
            CollocationStore.collocations = null;
            clearHints();
        }
        super.onFinishInputView(finishingInput);
    }

    private void clearHints() {
        sLingServNum = Constants.ADDIT_LING_SERV_NUM;
        System.out.println("Otpuskaet");
        mBtn.setText(Constants.EMPTY_SYM);
        mBtn2.setText(Constants.EMPTY_SYM);
        mBtn3.setText(Constants.EMPTY_SYM);
    }

    private void destroyCandidatesView() {
        mGeneralContainer.removeView(mCandidateView);
        mCandidateView = null;
    }

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

    private void vibrate() {
        mKeyboardView.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
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
        if (mCandidateView != null) {
            mOrthocorrector.setIc(ic);
            mPredictiveInput.setIc(ic);
            mAddition.setIc(ic);
        }
        if (IMESettingsStore.imeSettings.getSound() == Constants.TRUE)
            playClick(primaryCode);
        if (IMESettingsStore.imeSettings.getVibration() == Constants.TRUE)
            vibrate();
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
                    if (mCandidateView != null)
                        if (sLingServNum == Constants.ORTHO_LING_SERV_NUM || sLingServNum == Constants.ADDIT_LING_SERV_NUM)
                            mOrthocorrector.process(true);
                }
                else {
                    char code = (char) primaryCode;
                    if (Character.isLetter(code) && mIsCapsOn) {
                        code = Character.toUpperCase(code);
                    }
                    ic.commitText(String.valueOf(code), 1);
                    if (mCandidateView != null)
                        if (sLingServNum == Constants.ORTHO_LING_SERV_NUM || sLingServNum == Constants.ADDIT_LING_SERV_NUM)
                            mOrthocorrector.process(false);
                }
                if (mCandidateView != null)
                    if (sLingServNum == Constants.PRED_LING_SERV_NUM || sLingServNum == Constants.ADDIT_LING_SERV_NUM)
                        mPredictiveInput.process();
                if (mCandidateView != null)
                    if (sLingServNum == Constants.ADDIT_LING_SERV_NUM)
                        mAddition.process();
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
        if (currentLocale != Constants.KEYS_TYPE.SYMBOLS) {
            mPreviousLocale = currentLocale;
            currentLocale = Constants.KEYS_TYPE.SYMBOLS;
            mKeyboard = getKeyboard(currentLocale);
        } else {
            mKeyboard = getKeyboard(mPreviousLocale);
            currentLocale = mPreviousLocale;
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
        if (currentLocale == Constants.KEYS_TYPE.RUSSIAN) {
            currentLocale = Constants.KEYS_TYPE.ENGLISH;
        } else {
            currentLocale = Constants.KEYS_TYPE.RUSSIAN;
        }
        mKeyboard = getKeyboard(currentLocale);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboard.setShifted(mIsCapsOn);
        mKeyboardView.invalidateAllKeys();
    }
}
