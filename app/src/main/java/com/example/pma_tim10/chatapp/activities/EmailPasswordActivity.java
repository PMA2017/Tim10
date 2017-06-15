package com.example.pma_tim10.chatapp.activities;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.GPSTracker;
import com.example.pma_tim10.chatapp.service.IAuthService;
import com.example.pma_tim10.chatapp.service.UserService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class EmailPasswordActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = EmailPasswordActivity.class.getSimpleName();

    private EditText mEmailField;
    private EditText mPasswordField;
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private UserService userService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        // Views
        mEmailField = (EditText) findViewById(R.id.et_email);
        mPasswordField = (EditText) findViewById(R.id.et_password);
        mLoginButton = (LoginButton) findViewById(R.id.fb_login_btn);

        // Buttons
        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        //findViewById(R.id.btn_fb_log_in).setOnClickListener(this);
        findViewById(R.id.btn_google_log_in).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        // [END initialize_auth]

        userService = new UserService();

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"Login with facebook cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG,"Error on facebook login");
            }
        });
        checkPermissions();
    }

    private void checkPermissions() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (prefs.getLong("lastRunVersionCode", 0) < pInfo.versionCode) {
                ActivityCompat.requestPermissions(EmailPasswordActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("lastRunVersionCode", pInfo.versionCode);
                editor.commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error reading versionCode");
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            goToMainActivity();
        }
    }
    // [END on_start_check_user]

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END sign_in_with_email]
    }



    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_sign_up) {
            //createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            goToSignUpActivity();
        } else if (i == R.id.btn_sign_in) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.btn_google_log_in) {
            //TO-DO
        }
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User user = new User();
                            user.setUid(firebaseUser.getUid());
                            user.setFullName(firebaseUser.getDisplayName());
                            user.setPhotoURL(firebaseUser.getPhotoUrl().toString());
                            user.setEmail(firebaseUser.getEmail());
                            userService.checkUserByUid(firebaseUser.getUid(), user, new IFirebaseCallback() {
                                @Override
                                public void notifyUI(List data) {
                                    goToMainActivity();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    // [END auth_with_facebook]

    private void goToMainActivity(){
        Log.d(TAG,"Going to main activity");
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToSignUpActivity(){
        Log.d(TAG,"Going to sign up activity");
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}