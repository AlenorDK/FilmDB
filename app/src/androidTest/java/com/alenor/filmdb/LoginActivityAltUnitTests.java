package com.alenor.filmdb;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.Session;
import com.alenor.filmdb.model.Token;
import com.alenor.filmdb.ui.LoginActivityAlt;

import org.junit.Test;
import org.mockito.Mockito;

import rx.Observable;
import rx.functions.Action1;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;

public class LoginActivityAltUnitTests {

    //LOGIN
    private static final String CORRECT_LOGIN = "asdasd";
    private static final String INCORRECT_LOGIN_NUMBERS = "111";
    private static final String INCORRECT_LOGIN_SYMBOLS = "*&@!";
    private static final String EMPTY_LOGIN = "";
    //PASSWORD
    private static final String CORRECT_PASSWORD = "hjvf@1*ajoksd";
    private static final String INCORRECT_PASSWORD = "hjvf@";
    private static final String EMPTY_PASSWORD = "";
    //SESSION
    private static final String SESSION_PREFERENCES = "session_preferences";
    private static final int PREFERENCES_MODE = 0;
    private static final String SESSION_ID = "session_id";
    private static final String FAKE_SESSION_ID = "acdfg21123ujgbfb211";
    //LOGIN
    private static final String FAKE_REQUEST_TOKEN = "asdasd";
    private static final String FAKE_CORRECT_USERNAME = "qwe";
    private static final String FAKE_CORRECT_PASSWORD = "qweqqwe";
    private static final String FAKE_INCORRECT_USERNAME = "zxczxc";
    private static final String FAKE_INCORRECT_PASSWORD = "zxczxzzxc";


    @Test
    public void LoginActivityAlt_correctValidateLogin_ReturnsTrue() {
        assertThat(LoginActivityAlt.validateLogin(CORRECT_LOGIN), is(true));
    }

    @Test
    public void LoginActivityAlt_incorrectValidateLogin_ReturnsFalse() {
        assertThat(LoginActivityAlt.validateLogin(INCORRECT_LOGIN_NUMBERS), is(false));
        assertThat(LoginActivityAlt.validateLogin(INCORRECT_LOGIN_SYMBOLS), is(false));
    }

    @Test
    public void LoginActivityAlt_emptyValidateLogin_ReturnsFalse() {
        assertThat(LoginActivityAlt.validateLogin(EMPTY_LOGIN), is(false));
    }

    @Test
    public void LoginActivityAlt_correctValidatePassword_ReturnsTrue() {
        assertThat(LoginActivityAlt.validatePassword(CORRECT_PASSWORD), is(true));
    }

    @Test
    public void LoginActivityAlt_incorrectValidatePassword_ReturnsFalse() {
        assertThat(LoginActivityAlt.validatePassword(INCORRECT_PASSWORD), is(false));
    }

    @Test
    public void LoginActivityAlt_emptyValidatePassword_ReturnsFalse() {
        assertThat(LoginActivityAlt.validatePassword(EMPTY_PASSWORD), is(false));
    }

    @Test
    public void LoginActivityAlt_isOnline_ReturnsTrue() {
        final ConnectivityManager cm = Mockito.mock(ConnectivityManager.class);
        final NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(cm.getActiveNetworkInfo()).thenReturn(networkInfo);
        Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
        assertThat(LoginActivityAlt.isOnline(cm), is(true));
    }

    @Test
    public void LoginActivityAlt_isOnline_ReturnsFalse() {
        final ConnectivityManager cm = Mockito.mock(ConnectivityManager.class);
        final NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        Mockito.when(cm.getActiveNetworkInfo()).thenReturn(networkInfo);
        Mockito.when(networkInfo.isConnectedOrConnecting()).thenReturn(false);
        assertThat(LoginActivityAlt.isOnline(cm), is(false));
    }

    @Test
    public void LoginActivityAlt_doesSessionExist_ReturnsTrue() {
        final Context context = Mockito.mock(Context.class);
        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE)).thenReturn(preferences);
        Mockito.when(preferences.getString(SESSION_ID, null)).thenReturn(FAKE_SESSION_ID);
        assertThat(LoginActivityAlt.doesSessionExist(context), is(true));
    }

    @Test
    public void LoginActivityAlt_doesSessionExist_ReturnsFalse() {
        final Context context = Mockito.mock(Context.class);
        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(context.getSharedPreferences(SESSION_PREFERENCES, PREFERENCES_MODE)).thenReturn(preferences);
        Mockito.when(preferences.getString(SESSION_ID, null)).thenReturn(null);
        assertThat(LoginActivityAlt.doesSessionExist(context), is(false));
    }

    @Test
    public void LoginActivityAlt_LogIn_Successful() {
        MovieDBService movieDBService = Mockito.mock(MovieDBService.class);
        Token token = new Token(FAKE_REQUEST_TOKEN, true);
        Mockito.when(movieDBService.getToken()).thenReturn(Observable.just(token));

        Mockito.when(movieDBService.validateWithLogin(token.getRequestToken(), FAKE_CORRECT_USERNAME, FAKE_CORRECT_PASSWORD)).thenReturn(Observable.just(token));

        Session session = new Session(null, true, FAKE_SESSION_ID);
        Mockito.when(movieDBService.createNewSession(token.getRequestToken())).thenReturn(Observable.just(session));
        Action1<Session> success = Mockito.mock(Action1.class);
        movieDBService.createNewSession(token.getRequestToken()).subscribe(success);
        Mockito.verify(success).call(eq(session));
    }

}
