package com.ruslan.keyboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class WarningDialogFragment extends DialogFragment {

    private Removable removable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        removable = (Removable) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String warningMessage = args.getString("warningMessage");
        String actionButton = args.getString("actionButton");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Предупреждение")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(warningMessage)
                .setPositiveButton(actionButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removable.remove();
                    }
                })
                .setNegativeButton("Отмена", null)
                .create();
    }
}
