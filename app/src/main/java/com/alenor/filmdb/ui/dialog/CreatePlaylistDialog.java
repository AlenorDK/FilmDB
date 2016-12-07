package com.alenor.filmdb.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alenor.filmdb.R;

public class CreatePlaylistDialog extends DialogFragment {

    public static CreatePlaylistDialog newInstance(OnPlaylistCreatedListener listener) {
        CreatePlaylistDialog dialog = new CreatePlaylistDialog();
        dialog.setOnPlaylistCreatedListener(listener);
        return dialog;
    }

    public interface OnPlaylistCreatedListener {
        void onPlaylistCreated(String playlistName, String playlistDescription);
    }

    private EditText nameText;
    private EditText descriptionText;
    private OnPlaylistCreatedListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_playlist_dialog_layout, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameText = (EditText) view.findViewById(R.id.create_playlist_dialog_layout_playlist_name_text);
        descriptionText = (EditText) view.findViewById(R.id.create_playlist_dialog_layout_playlist_description_text);
    }

    public void setOnPlaylistCreatedListener(OnPlaylistCreatedListener listener) {
        this.listener = listener;
    }
}
