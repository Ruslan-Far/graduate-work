package com.ruslan.keyboard;

import android.app.Dialog;
import android.os.Bundle;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ErrorDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String errorMessage = getArguments().getString("errorMessage");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Ошибка синхронизации")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .create();
    }
}
