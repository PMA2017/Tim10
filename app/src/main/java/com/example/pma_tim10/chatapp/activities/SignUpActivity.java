package com.example.pma_tim10.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameField = (EditText) findViewById(R.id.input_name);
        surnameField = (EditText) findViewById(R.id.input_surname);
        emailField = (EditText) findViewById(R.id.input_email);
        passwordField = (EditText) findViewById(R.id.input_password);

        findViewById(R.id.btn_create_account).setOnClickListener(this);


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
            if (!validateFields())
                return;
        } else if (i == R.id.link_login) {
            Log.d(TAG,"\n\nLink login\n\n");
            goToEmailPasswordActivity();
        }
    }

    private void goToEmailPasswordActivity(){
        Log.d(TAG,"Going to main activity");
        Intent intent = new Intent(this,EmailPasswordActivity.class);
        startActivity(intent);
        finish();
    }

}
