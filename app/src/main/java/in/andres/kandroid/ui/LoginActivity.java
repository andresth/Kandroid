/*
 * Copyright 2017 Thomas Andres
 *
 * This file is part of Kandroid.
 *
 * Kandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kandroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.andres.kandroid.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import in.andres.kandroid.Constants;
import in.andres.kandroid.R;
import in.andres.kandroid.kanboard.KanboardAPI;
import in.andres.kandroid.kanboard.KanboardError;
import in.andres.kandroid.kanboard.events.OnErrorListener;
import in.andres.kandroid.kanboard.events.OnGetVersionListener;

public class LoginActivity extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private KanboardAPI kanboardAPI = null;
    private Context self;


    // UI references.
    private EditText mServerURLView;
//    private EditText mAPIKeyView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();

        self = this;
        // Set up the login form.
        mServerURLView = (EditText) findViewById(R.id.serverurl);
//        mAPIKeyView = (EditText) findViewById(R.id.apikey);
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Load preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        mServerURLView.setText(preferences.getString("serverurl", ""));
//        mAPIKeyView.setText(preferences.getString("apikey", ""));
        mUsernameView.setText(preferences.getString("username", ""));
        mPasswordView.setText(preferences.getString("password", ""));
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (kanboardAPI != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String serverurl = mServerURLView.getText().toString();
//        String apikey = mAPIKeyView.getText().toString();
        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

//        if (TextUtils.isEmpty(apikey)) {
//            mAPIKeyView.setError(getString(R.string.error_field_required));
//            focusView = mAPIKeyView;
//            cancel = true;
//        }

        if (TextUtils.isEmpty(serverurl)) {
            mServerURLView.setError(getString(R.string.error_field_required));
            focusView = mServerURLView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            try {
                kanboardAPI = new KanboardAPI(serverurl, username, password);
                kanboardAPI.addErrorListener(new OnErrorListener() {
                    @Override
                    public void onError(KanboardError error) {
                        showProgress(false);
                        Log.e(Constants.TAG, String.format("%s (%d; %d)", error.Message, error.Code, error.HTTPReturnCode));
                        if (error.Code == -10) {
                            mServerURLView.setError(getString(R.string.error_host_unknown));
                            mServerURLView.requestFocus();
                        } else if (error.Code == -30) {
                            mServerURLView.setError(getString(R.string.error_server_url));
                            mServerURLView.requestFocus();
                        } else if (error.Code == -40) {
                            mServerURLView.setError(getString(R.string.error_ssl, error.Message));
                            mServerURLView.requestFocus();
                        } else if (error.Code == -50) {
                            mServerURLView.setError(getString(R.string.error_server_resonse));
                            mServerURLView.requestFocus();
                        } else if (error.HTTPReturnCode == 401 || error.HTTPReturnCode == 403 || error.Code == -20) {
                            mUsernameView.setError(getString(R.string.error_incorrect_username));
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                        }
                        kanboardAPI = null;
                    }
                });
                kanboardAPI.addOnGetVersionListener(new OnGetVersionListener() {
                    @Override
                    public void onGetVersion(boolean success, int[] version, String tag) {
                        showProgress(false);
                        if (version[0] == -1) {
                            // Development Version
                            new AlertDialog.Builder(self)
                                    .setTitle(android.R.string.dialog_alert_title)
                                    .setMessage(R.string.dlg_devel_version)
                                    .setNeutralButton(android.R.string.ok, null)
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("serverurl", serverurl.trim());
                            editor.putString("username", username);
                            editor.putString("password", password);
                            editor.apply();
                        } else if (isSupportedVersion(version)) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("serverurl", serverurl.trim());
                            editor.putString("username", username);
                            editor.putString("password", password);
                            editor.apply();
                            finish();
                        } else {
                            mServerURLView.setError(getString(R.string.error_wrong_kanboard_version, Constants.minKanboardVersion[0], Constants.minKanboardVersion[1], Constants.minKanboardVersion[2]));
                            mServerURLView.requestFocus();
                        }
                        kanboardAPI = null;
                    }
                });
                kanboardAPI.getVersion();
                showProgress(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isSupportedVersion(int[] version) {
        return version[0] > Constants.minKanboardVersion[0] ||
                (version[0] == Constants.minKanboardVersion[0] && version[1] > Constants.minKanboardVersion[1]) ||
                (version[0] == Constants.minKanboardVersion[0] && version[1] == Constants.minKanboardVersion[1] && version[2] >= Constants.minKanboardVersion[2]);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

