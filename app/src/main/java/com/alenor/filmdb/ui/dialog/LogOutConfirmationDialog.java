package com.alenor.filmdb.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class LogOutConfirmationDialog extends DialogFragment {

    public static LogOutConfirmationDialog newInstance(OnConfirmListener listener) {
        LogOutConfirmationDialog fragment = new LogOutConfirmationDialog();
        fragment.setOnConfirmListener(listener);
        return fragment;
    }

    public interface OnConfirmListener {
        void onConfirm(boolean isConfirmed);
    }

    private OnConfirmListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure?").setTitle("Log out")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirm(true);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onConfirm(false);
            }
        });
        return builder.create();
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
    }
}
