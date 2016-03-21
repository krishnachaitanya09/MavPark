package edu.uta.mavpark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.uta.mavpark.models.ErrorModel;
import edu.uta.mavpark.models.RegisterModel;
import edu.uta.mavpark.requests.Account;
import edu.uta.mavpark.requests.RestService;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    RestService restService;
    private UserRegisterTask mRegisterTask = null;

    // UI references.
    private AutoCompleteTextView mNameView;
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUTAidView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private AppCompatCheckBox mVisitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restService = new RestService(this);
        setContentView(R.layout.activity_register);
        // Set up the register form.
        mNameView = (AutoCompleteTextView) findViewById(R.id.name);
        mUTAidView = (AutoCompleteTextView) findViewById(R.id.utaId);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirmPassword);
        mVisitor = (AppCompatCheckBox) findViewById(R.id.visitor);
        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mUTAidView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mVisitor.setError(null);

        String name = mNameView.getText().toString();
        String utaId = mUTAidView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();
        boolean isVisitor = mVisitor.isChecked();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!password.equals(confirmPassword)) {
            mConfirmPasswordView.setError("Password and confirm password must be same");
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
            mRegisterTask = new UserRegisterTask(this, name, utaId, email, password, confirmPassword, isVisitor);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with proper logic
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mUtaId;
        private final String mEmail;
        private final String mPassword;
        private final String mConfirmPassword;
        private final boolean mIsVisitor;
        private final Context context;

        UserRegisterTask(Context context, String name, String utaId, String email, String password, String confirmPassword, boolean isVisitor) {
            this.context = context;
            mName = name;
            mUtaId = utaId;
            mEmail = email;
            mPassword = password;
            mConfirmPassword = confirmPassword;
            mIsVisitor = isVisitor;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Account account = restService.create(Account.class);
            RegisterModel registerModel = new RegisterModel(mName, mUtaId, mEmail, mPassword, mConfirmPassword, mIsVisitor ? "Visitor" : "Primary");
            Call<Void> call = account.register(registerModel.name, registerModel.utaId, registerModel.email, registerModel.password, registerModel.confirmPassword,
                    registerModel.role);
            try {
               Response response = call.execute();
                if (response.isSuccessful()) {
                    return true;
                } else {
                    Gson gson = new GsonBuilder().create();
                    final ErrorModel errorModel = gson.fromJson(response.errorBody().string(), ErrorModel.class);
                    ((RegisterActivity) context).runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, errorModel.Message.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            showProgress(false);

            if (success) {
                finish();
                Toast.makeText(RegisterActivity.this, "Registered successfully. Please login to continue", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            showProgress(false);
        }
    }
}

