package com.example.pma_tim10.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.AuthService;
import com.example.pma_tim10.chatapp.service.AuthServiceImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Dorian on 5/22/2017.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private EditText nameField;
    private EditText surnameField;
    private EditText emailField;
    private EditText passwordField;

    private Button createAccountButton;
    private TextView alreadyMemberText;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private AuthService authService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameField = (EditText) findViewById(R.id.input_name);
        surnameField = (EditText) findViewById(R.id.input_surname);
        emailField = (EditText) findViewById(R.id.input_email);
        passwordField = (EditText) findViewById(R.id.input_password);

        findViewById(R.id.btn_create_account).setOnClickListener(this);

        this.mAuth = FirebaseAuth.getInstance();
        this.authService = new AuthServiceImpl();

    }

    private boolean validateFields () {
        boolean valid = true;

        String name = nameField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameField.setError("Required.");
            valid = false;
        } else {
            nameField.setError(null);
        }

        String surname = surnameField.getText().toString();
        if (TextUtils.isEmpty(surname)) {
            surnameField.setError("Required.");
            valid = false;
        } else {
            surnameField.setError(null);
        }

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_create_account) {
            createAccount();
        } else if (i == R.id.link_login) {
            Log.d(TAG,"\n\nLink login\n\n");
            goToEmailPasswordActivity();
        }
    }

    private void createAccount() {
        Log.d(TAG, "createAccount:" + emailField.getText().toString());
        if (!validateFields()) {
            return;
        }

        final User newUser = new User();
        newUser.setName(nameField.getText().toString());
        newUser.setSurname(surnameField.getText().toString());
        newUser.setEmail(emailField.getText().toString());

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //register new user to real time db
                            newUser.setUid(user.getUid());
                            authService.registerUser(user.getUid(), newUser);
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        // [END create_user_with_email]
    }

    private void goToEmailPasswordActivity(){
        Log.d(TAG,"Going to EmailPasswordActivity activity");
        Intent intent = new Intent(this,EmailPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMainActivity(){
        Log.d(TAG,"Going to main activity");
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goToEmailPasswordActivity();
    }
}
