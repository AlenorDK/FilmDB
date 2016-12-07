package com.alenor.filmdb.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@SuppressWarnings("ConstantConditions")
public class LoginActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
    }

    public static final String EXTRA_USERNAME = "username_text";
    public static final String EXTRA_PASSWORD = "password_text";

    private EditText usernameEditText;
    private EditText passwordEditText;

    private boolean isLoginCorrect;
    private boolean isPasswordCorrect;

    private Subscription sessionSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        String sessionId = SharedPrefUtils.getSessionId(this);
        if (sessionId != null) {
            AccountActivity.startActivity(this);
        }

        final Button loginButton;
        final TextView guestText;

        final TextInputLayout usernameInputLayout = (TextInputLayout) findViewById(R.id.login_activity_username_layout);
        final TextInputLayout passwordInputLayout = (TextInputLayout) findViewById(R.id.login_activity_password_layout);
        usernameEditText = usernameInputLayout.getEditText();
        passwordEditText = passwordInputLayout.getEditText();

        loginButton = (Button) findViewById(R.id.login_activity_login_button);

        RxTextView.editorActionEvents(usernameEditText).subscribe(event -> {
            passwordEditText.requestFocus();
        });

        RxTextView.editorActionEvents(passwordEditText).subscribe(event -> {
            loginButton.performClick();
        });

        RxView.clicks(loginButton).subscribe(aVoid -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            RxTextView.textChangeEvents(usernameEditText).subscribe(textChangeEvent -> {
                isLoginCorrect = !TextUtils.equals(usernameEditText.getText().toString(), "");
            });
            RxTextView.textChangeEvents(passwordEditText).subscribe(textChangeEvent -> {
                isPasswordCorrect = !TextUtils.equals(passwordEditText.getText().toString(), "");
            });
            if (isOnline()) {
                if (isLoginCorrect && isPasswordCorrect) {
                    logIn();
                } else {
                    Snackbar.make(getCurrentFocus(), "Both fields must be filled!", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(getCurrentFocus(), R.string.no_network_connection_error_text, Snackbar.LENGTH_LONG).show();
            }
        });

        guestText = (TextView) findViewById(R.id.login_activity_guest_label);
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

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void logIn() {
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

                                    SharedPrefUtils.setSessionId(LoginActivity.this, sessionId);
                                    SharedPrefUtils.setUsername(LoginActivity.this, username);
                                    AccountActivity.startActivity(LoginActivity.this);

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
