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
        Bundle args = getArguments();
        String errorTitle = args.getString("errorTitle");
        String errorMessage = args.getString("errorMessage");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(errorTitle)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .create();
    }
}
