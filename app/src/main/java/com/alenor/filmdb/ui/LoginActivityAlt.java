package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alenor.filmdb.MovieDBApplication;
import com.alenor.filmdb.R;
import com.alenor.filmdb.Utils.SharedPrefUtils;
import com.alenor.filmdb.api.MovieDBService;
import com.alenor.filmdb.model.Token;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.regex.Pattern;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivityAlt extends AppCompatActivity {

    private static final int CORRECT_PASSWORD_LENGTH = 6;
    private Subscription sessionSubscription;

    public static void start(Context context) {
        Intent i = new Intent(context, LoginActivityAlt.class);
        context.startActivity(i);
    }

    public static final String EXTRA_USERNAME = "username_text";
    public static final String EXTRA_PASSWORD = "password_text";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView loginInformation;
    private TextView guestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_alt);

        if (doesSessionExist(this)) {
            AccountActivity.startActivity(this);
        }

        usernameEditText = (EditText) findViewById(R.id.login_activity_alt_username_et);
        passwordEditText = (EditText) findViewById(R.id.login_activity_alt_password_et);
        loginButton = (Button) findViewById(R.id.login_activity_alt_login_button);
        loginInformation = (TextView) findViewById(R.id.login_activity_alt_login_information);
        guestText = (TextView) findViewById(R.id.login_activity_alt_guest_label);

        RxTextView.editorActionEvents(usernameEditText).subscribe(event -> {
            passwordEditText.requestFocus();
        });

        RxTextView.editorActionEvents(passwordEditText).subscribe(event -> {
            loginButton.performClick();
        });

        RxView.clicks(loginButton).subscribe(aVoid -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (isOnline(connectivityManager)) {
                if (!validateLogin(usernameEditText.getText().toString())) {
                    loginInformation.setText("Username must only contain latin letters and can not be empty");
                } else {
                    if (!validatePassword(passwordEditText.getText().toString())) {
                        loginInformation.setText("Password must contain at least 6 symbols");
                    } else {
                        logIn();
                    }
                }
            } else {
                loginInformation.setText("Can not connect to server. Please, check your network connection");
            }
        });

        RxView.clicks(guestText).subscribe(aVoid -> {
            GuestMenuActivity.start(this);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (usernameEditText != null && passwordEditText != null) {
            outState.putString(EXTRA_USERNAME, usernameEditText.getText().toString());
            outState.putString(EXTRA_PASSWORD, passwordEditText.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (usernameEditText != null && passwordEditText != null) {
            usernameEditText.setText(savedInstanceState.getString(EXTRA_USERNAME));
            passwordEditText.setText(savedInstanceState.getString(EXTRA_PASSWORD));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sessionSubscription != null) {
            sessionSubscription.unsubscribe();
        }
    }

    public static boolean validateLogin(String login) {
        boolean isEmpty = login.isEmpty();
        Pattern pattern = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(login).matches() && !isEmpty;
    }

    public static boolean validatePassword(String password) {
        boolean hasCorrectLength = password.length() >= CORRECT_PASSWORD_LENGTH;
        boolean isEmpty = password.isEmpty();
        return hasCorrectLength && !isEmpty;
    }

    public static boolean isOnline(ConnectivityManager cm) {
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo.isConnectedOrConnecting();
    }

    public static boolean doesSessionExist(Context context) {
        String sessionId = SharedPrefUtils.getSessionId(context);
        return sessionId != null;
    }

    public void logIn() {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        MovieDBService movieDBService = MovieDBApplication.getInstance().getMovieDBService();

        Subscriber<Token> validateWithLoginSubscriber = new Subscriber<Token>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Snackbar.make(getCurrentFocus(), "Invalid login or password", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onNext(Token token) {
                if (token != null) {
                    movieDBService.createNewSession(token.getRequestToken())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(session -> {
                                if (session != null) {
                                    String sessionId = session.getSessionId();
                                    SharedPrefUtils.setSessionId(LoginActivityAlt.this, sessionId);
                                    SharedPrefUtils.setUsername(LoginActivityAlt.this, username);
                                    AccountActivity.startActivity(LoginActivityAlt.this);
                                }
                            });
                }
            }
        };

        sessionSubscription = movieDBService.getToken()
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).cache()
                .subscribe(token -> {
                    if (token != null) {
                        movieDBService.validateWithLogin(token.getRequestToken(), username, password)
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(validateWithLoginSubscriber);
                    }
                });
    }

}
