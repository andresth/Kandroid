package in.andres.kandroid.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

import in.andres.kandroid.R;

/**
 * A login screen to input url/user/password.
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

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
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String serverurl = mServerURLView.getText().toString();
//        String apikey = mAPIKeyView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

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
                mAuthTask = new UserLoginTask(serverurl, username, password);
                mAuthTask.execute();
                showProgress(true);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final URL mUrl;
        private final String mServerUrl;
        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String serverurl, final String username, final String password) throws MalformedURLException {
            String tmpURL = serverurl;
            if (!serverurl.endsWith("jsonrpc.php")) {
                if (!serverurl.endsWith("/"))
                    tmpURL += "/";
                tmpURL += "jsonrpc.php";
            }
            mUrl = new URL(tmpURL);
            mServerUrl = serverurl;
            mUsername = username;
            mPassword = password;

            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }

            });
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            HttpsURLConnection con;
            try {
                con = (HttpsURLConnection) mUrl.openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(120000);
                con.setReadTimeout(120000);
                con.setDoOutput(true);
                con.setDoInput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes("{\"jsonrpc\": \"2.0\", \"method\": \"getMe\", \"id\": 1}");
                out.flush();
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuilder responseStr = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    responseStr.append(line);
                }
                in.close();

                JSONObject response = new JSONObject(responseStr.toString());

                if (response.has("error"))
                    return response.optJSONObject("error").optInt("code");
                else
                    return con.getResponseCode();
            } catch (UnknownHostException e) {
                return -1;
            } catch (ProtocolException e) {
                return -2;
            } catch (SocketTimeoutException e) {
                return -3;
            } catch (Exception e) {
                e.printStackTrace();
                return Integer.MIN_VALUE;
            }
        }

        @Override
        protected void onPostExecute(final Integer success) {
            mAuthTask = null;
            showProgress(false);

            if ((success >= 400) || success == -2) {
                mUsernameView.setError(getString(R.string.error_incorrect_username));
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            } else if (success == -3) {
                mServerURLView.setError(getString(R.string.error_server_url));
                mServerURLView.requestFocus();
            } else if (success == -1) {
                mServerURLView.setError(getString(R.string.error_host_unknown));
                mServerURLView.requestFocus();
            } else if (success == 200) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("serverurl", mServerUrl);
//                editor.putString("apikey", apikey);
                editor.putString("username", mUsername);
                editor.putString("password", mPassword);
                editor.apply();
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

