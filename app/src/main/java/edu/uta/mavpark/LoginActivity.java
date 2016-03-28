package edu.uta.mavpark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.uta.mavpark.data.MavParkDbHelper;
import edu.uta.mavpark.data.UserInfoContract;
import edu.uta.mavpark.models.LoginModel;
import edu.uta.mavpark.models.Token;
import edu.uta.mavpark.models.UserInfo;
import edu.uta.mavpark.requests.Account;
import edu.uta.mavpark.requests.RestService;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    RestService restService;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("LoginPref", 0);
        if (settings.getBoolean("isLogin", false)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            restService = new RestService(this);
            setTitle("MavPark Sign in");
            setContentView(R.layout.activity_login);
            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

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

            Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
            mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            Button mRegisterButton = (Button) findViewById(R.id.register_button);
            mRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }
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
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
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
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(this, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with proper login
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with proper logic
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

   /* @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }*/

   /* @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
       *//* while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);*//*
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
*/

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Context context;
        private Token token;

        UserLoginTask(Context context, String email, String password) {
            this.context = context;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            restService = new RestService(context, true);
            Account account = restService.create(Account.class);
            LoginModel loginModel = new LoginModel(mEmail, mPassword);
            Call<Token> call = account.token(loginModel.username, loginModel.password, loginModel.grant_type);
            try {
                Response<Token> response = call.execute();
                if (response.isSuccessful()) {
                    token = response.body();
                    SharedPreferences settings = getSharedPreferences("LoginPref", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    if (token != null) {
                        editor.putString("token", token.AccessToken);
                        editor.putString("issued", token.Issued.toString());
                        editor.putString("expires", token.Expires.toString());
                        editor.commit();
                        restService = new RestService(context);
                        account = restService.create(Account.class);
                        Call<UserInfo> userInfoCall = account.getUserInfo();
                        UserInfo userInfo = userInfoCall.execute().body();
                        MavParkDbHelper dBHelper = new MavParkDbHelper(context);
                        SQLiteDatabase db = dBHelper.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_NAME, userInfo.name);
                        values.put(UserInfoContract.UserInfoEntry.COLUMN_NAME_EMAIL, userInfo.email);

                        long newRowId = db.insert(
                                UserInfoContract.UserInfoEntry.TABLE_NAME,
                                null,
                                values);
                        editor.putBoolean("isLogin", true);
                        editor.commit();
                        return true;
                    } else {
                        ((LoginActivity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, "Please check the login details and try again", Toast.LENGTH_LONG).show();
                            }
                        });

                        editor.putBoolean("isLogin", false);
                        editor.commit();
                        return false;
                    }
                } else {
                    Gson gson = new GsonBuilder().create();
                    token = gson.fromJson(response.errorBody().string(), Token.class);
                    ((LoginActivity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, token.Error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                /*mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();*/
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

