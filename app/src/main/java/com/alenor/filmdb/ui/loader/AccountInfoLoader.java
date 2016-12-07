package com.alenor.filmdb.ui.loader;

import android.content.Context;
import android.os.Bundle;

import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.AccountInfo;
import com.alenor.filmdb.ui.LoginActivity;
import com.alenor.filmdb.MovieDBApplication;

import java.io.IOException;

import retrofit2.Response;

public class AccountInfoLoader extends BaseLoader<AccountInfo> {

    private String sessionId;

    public AccountInfoLoader(Context context) {
        super(context);
        sessionId = SharedPrefUtils.getSessionId(context);
    }

    @Override
    public AccountInfo loadInBackground() {
        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();
        try {
            Response<AccountInfo> response = movieDBService.getAccountInfo(sessionId).execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
