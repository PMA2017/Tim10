package com.example.pma_tim10.chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.utils.Constants;

/**
 * Created by Dorian on 6/12/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    ToggleButton toggleButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        toggleButton = (ToggleButton) findViewById(R.id.location_toggle_button);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLocation = prefs.getBoolean(Constants.LOCATION_STATE,true);
        toggleButton.setChecked(isLocation);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(Constants.LOCATION_STATE,b);
                    editor.commit();
            }
        });

    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    private void goToMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
