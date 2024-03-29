package com.ruslan.keyboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ruslan.keyboard.clients_impl.CollocationClientImpl;
import com.ruslan.keyboard.clients_impl.UserClientImpl;
import com.ruslan.keyboard.clients_impl.WordClientImpl;
import com.ruslan.keyboard.entities.Collocation;
import com.ruslan.keyboard.entities.IMESettings;
import com.ruslan.keyboard.entities.User;
import com.ruslan.keyboard.entities.Word;
import com.ruslan.keyboard.stores.CollocationStore;
import com.ruslan.keyboard.stores.IMESettingsStore;
import com.ruslan.keyboard.stores.UserStore;
import com.ruslan.keyboard.stores.WordStore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IMESettingsActivity extends AppCompatActivity implements Removable {

    private final static String TAG = "IMESettingsActivity";

    private UserClientImpl mUserClientImpl;
    private WordClientImpl mWordClientImpl;
    private CollocationClientImpl mCollocationClientImpl;
    private DatabaseInteraction mDatabaseInteraction;

    public static String errorMessage;

    private boolean mIsSyn;
    private boolean mIsClearPersonDict;
    private boolean mIsDelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Intent intent = new Intent(this, IME.class);
        startService(intent);

        setTitle(R.string.ime_settings_activity);
        mUserClientImpl = new UserClientImpl();
        mWordClientImpl = new WordClientImpl();
        mCollocationClientImpl = new CollocationClientImpl();
        mDatabaseInteraction = new DatabaseInteraction(this);
        mDatabaseInteraction.selectIMESettings();
        if (IMESettingsStore.imeSettings == null) {
            mDatabaseInteraction.insertIMESettings(new IMESettings());
            mDatabaseInteraction.selectIMESettings();
        }
        mDatabaseInteraction.selectWords();
        if (WordStore.words == null)
            mDatabaseInteraction.insertWords();
        errorMessage = Constants.EMPTY_SYM;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        UserStore.user = null;
        WordStore.words = null;
        CollocationStore.collocations = null;
        IMESettingsStore.imeSettings = null;
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mDatabaseInteraction.selectUser();
        if (UserStore.user != null) {
            setContentView(R.layout.activity_ime_settings_user);
            TextView userLogin = findViewById(R.id.userLogin);
            userLogin.setText(UserStore.user.getLogin());
            Button exitButton = findViewById(R.id.exitButton);
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    localDeleteUser();
                }
            });
        }
        else {
            setContentView(R.layout.activity_ime_settings);
            Button authButton = findViewById(R.id.authButton);
            authButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentActivity = new Intent(IMESettingsActivity.this, AuthActivity.class);
                    startActivity(intentActivity);
                }
            });
        }
        if (IMESettingsStore.imeSettings == null)
            mDatabaseInteraction.selectIMESettings();
        ListView imeSettingsCheckboxList = findViewById(R.id.imeSettingsCheckboxList);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,
                    getResources().getStringArray(R.array.ime_settings_checkbox));
        imeSettingsCheckboxList.setAdapter(adapter);
        imeSettingsCheckboxList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem;

                if (IMESettingsStore.imeSettings == null)
                    mDatabaseInteraction.selectIMESettings();
                selectedItem = adapter.getItem(position);
                if (imeSettingsCheckboxList.isItemChecked(position)) {
                    if (selectedItem.equals(getString(R.string.sound_checkbox))) {
                        System.out.println("Установить звук");
                        IMESettingsStore.imeSettings.setSound(Constants.TRUE);
                    }
                    else if (selectedItem.equals(getString(R.string.vibration_checkbox))) {
                        System.out.println("Установить вибрацию");
                        IMESettingsStore.imeSettings.setVibration(Constants.TRUE);
                    }
                    else {
                        System.out.println("Установить подсказки");
                        IMESettingsStore.imeSettings.setCandidates(Constants.TRUE);
                    }
                }
                else {
                    if (selectedItem.equals(getString(R.string.sound_checkbox))) {
                        System.out.println("Отключить звук");
                        IMESettingsStore.imeSettings.setSound(Constants.FALSE);
                    }
                    else if (selectedItem.equals(getString(R.string.vibration_checkbox))) {
                        System.out.println("Отключить вибрацию");
                        IMESettingsStore.imeSettings.setVibration(Constants.FALSE);
                    }
                    else {
                        System.out.println("Отключить подсказки");
                        IMESettingsStore.imeSettings.setCandidates(Constants.FALSE);
                    }
                }
                mDatabaseInteraction.updateIMESettings(IMESettingsStore.imeSettings.getId(),
                        IMESettingsStore.imeSettings);
            }
        });
        initIMESettingsCheckboxList(imeSettingsCheckboxList);
        ListView imeSettingsList = findViewById(R.id.imeSettingsList);
        imeSettingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentActivity = null;
                String selectedItem = (String) parent.getItemAtPosition(position);
                if (selectedItem.equals(getString(R.string.synchronization_activity))) {
                    System.out.println(getString(R.string.synchronization_activity));
                    intentActivity = new Intent(IMESettingsActivity.this, SynActivity.class);
                }
                else if (selectedItem.equals(getString(R.string.can_theme_activity))) {
                    System.out.println(getString(R.string.can_theme_activity));
                    intentActivity = new Intent(IMESettingsActivity.this, CanThemeActivity.class);
                }
                else if (selectedItem.equals(getString(R.string.test_activity))) {
                    System.out.println(getString(R.string.test_activity));
                    intentActivity = new Intent(IMESettingsActivity.this, TestActivity.class);
                }
                else if (selectedItem.equals(getString(R.string.learning_rate_activity))) {
                    System.out.println(getString(R.string.learning_rate_activity));
                    intentActivity = new Intent(IMESettingsActivity.this, LearningRateActivity.class);
                }
                if (intentActivity != null)
                    startActivity(intentActivity);
                if (selectedItem.equals(getString(R.string.clear_personal_dictionary))) {
                    System.out.println(getString(R.string.clear_personal_dictionary));
                    mIsSyn = false;
                    mIsClearPersonDict = true;
                    mIsDelUser = false;
                    showWarningDialog();
                }
                else if (selectedItem.equals(getString(R.string.delete_account))) {
                    System.out.println(getString(R.string.delete_account));
                    mIsSyn = false;
                    mIsClearPersonDict = false;
                    mIsDelUser = true;
                    showWarningDialog();
                }
            }
        });
        if (errorMessage.length() != 0) {
            mIsSyn = true;
            mIsClearPersonDict = false;
            mIsDelUser = false;
            showErrorDialog();
        }
    }

    private void initIMESettingsCheckboxList(ListView imeSettingsCheckboxList) {
        if (IMESettingsStore.imeSettings.getSound() == Constants.TRUE)
            imeSettingsCheckboxList.setItemChecked(0, true);
        if (IMESettingsStore.imeSettings.getVibration() == Constants.TRUE)
            imeSettingsCheckboxList.setItemChecked(1, true);
        if (IMESettingsStore.imeSettings.getCandidates() == Constants.TRUE)
            imeSettingsCheckboxList.setItemChecked(2, true);
    }

    private void setErrorMessage(String errorMessage) {
        if (mIsClearPersonDict) {
            IMESettingsActivity.errorMessage = "Если Вы хотите также очистить словарь и на стороне сервера, то ";
            if (errorMessage.equals(Constants.ERROR_CONNECTION))
                IMESettingsActivity.errorMessage += "проверьте подключение к Интернету и повторите попытку";
            else
                IMESettingsActivity.errorMessage += "повторите попытку позже, так как возникли неполадки с сервером";
        }
        else if (mIsDelUser)
            IMESettingsActivity.errorMessage = errorMessage;
        showErrorDialog();
    }

    private void showErrorDialog() {
        String keyErrorTitle = "errorTitle";
        ErrorDialogFragment errorDialog = new ErrorDialogFragment();
        Bundle args = new Bundle();
        if (mIsSyn)
            args.putString(keyErrorTitle, "Ошибка синхронизации");
        else if (mIsClearPersonDict)
            args.putString(keyErrorTitle, "Ошибка очистки на стороне сервера");
        else if (mIsDelUser)
            args.putString(keyErrorTitle, "Ошибка удаления аккаунта");
        args.putString("errorMessage", errorMessage);
        errorDialog.setArguments(args);
        errorDialog.show(getSupportFragmentManager(), "error");
        errorMessage = Constants.EMPTY_SYM;
    }

    private void showWarningDialog() {
        String keyWarningMessage = "warningMessage";
        String keyActionButton = "actionButton";
        WarningDialogFragment warningDialog = new WarningDialogFragment();
        Bundle args = new Bundle();
        if (mIsClearPersonDict) {
            args.putString(keyWarningMessage, "Вы действительно хотите очистить свой персональный словарь?");
            args.putString(keyActionButton, "Очистить");
        }
        else if (mIsDelUser) {
            args.putString(keyWarningMessage, "Вы действительно хотите удалить свой аккаунт?");
            args.putString(keyActionButton, "Удалить");
        }
        warningDialog.setArguments(args);
        warningDialog.show(getSupportFragmentManager(), "warning");
    }

    @Override
    public void remove() {
        if (mIsClearPersonDict) {
            System.out.println("REMOVE ClearPersonDict");
            clearPersonalDictionary();
        }
        else if (mIsDelUser) {
            System.out.println("REMOVE DelUser");
            deleteCollocationsToApi(UserStore.user.getId());
        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    private void clearPersonalDictionary() {
        localClearPersonalDictionary();
        if (UserStore.user != null) {
            deleteCollocationsToApi(UserStore.user.getId());
        }
    }

    private void localClearPersonalDictionary() {
        mDatabaseInteraction.deleteCollocations();
        mDatabaseInteraction.deleteWords();
        mDatabaseInteraction.insertWords();
    }

    private void localDeleteUser() {
        mDatabaseInteraction.deleteUser();
        Intent intentActivity = new Intent(IMESettingsActivity.this, IMESettingsActivity.class);
        intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentActivity);
    }

    private void deleteCollocationsToApi(Integer userId) {
        mCollocationClientImpl.setCallDelete(userId);
        mCollocationClientImpl.getCallDelete().enqueue(new Callback<Collocation[]>() {
            @Override
            public void onResponse(Call<Collocation[]> call, Response<Collocation[]> response) {
                if (response.isSuccessful()) {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
                    deleteWordsToApi(userId);
                }
                else {
                    System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    setErrorMessage(Constants.ERROR_TRANSFER_DATA);
                }
            }
            @Override
            public void onFailure(Call<Collocation[]> call, Throwable t) {
                System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
                setErrorMessage(Constants.ERROR_CONNECTION);
            }
        });
        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }

    private void deleteWordsToApi(Integer userId) {
        mWordClientImpl.setCallDelete(userId);
        mWordClientImpl.getCallDelete().enqueue(new Callback<Word[]>() {
            @Override
            public void onResponse(Call<Word[]> call, Response<Word[]> response) {
                if (response.isSuccessful()) {
                    System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
                    if (mIsDelUser)
                        deleteUserToApi(userId);
                }
                else {
                    System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    setErrorMessage(Constants.ERROR_TRANSFER_DATA);
                }
            }
            @Override
            public void onFailure(Call<Word[]> call, Throwable t) {
                System.out.println("FFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
                setErrorMessage(Constants.ERROR_CONNECTION);
            }
        });
        System.out.println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }

    private void deleteUserToApi(Integer id) {
        mUserClientImpl.setCallDelete(id);
        mUserClientImpl.getCallDelete().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    System.out.println("UUUUUUUUUUUUUUUUUDDDDDDDDDDDDDDDDDD");
                    localDeleteUser();
                }
                else {
                    System.out.println("UUUUUUUUUUUUUUUUUUUUUUUUEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
                    setErrorMessage(Constants.ERROR_TRANSFER_DATA);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("UUUUUUUUUUUUUUUUUUUUUUUUUFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAAAAAA");
                setErrorMessage(Constants.ERROR_CONNECTION);
            }
        });
        System.out.println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
    }
}
