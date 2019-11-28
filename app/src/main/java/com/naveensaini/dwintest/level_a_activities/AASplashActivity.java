package com.naveensaini.dwintest.level_a_activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.naveensaini.dwintest.R;

public class AASplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_activity_splash);


        // Starting Login Activity
        new Handler().postDelayed(() -> {

            Intent intent = new Intent(getApplicationContext(), BPasscodeActivity.class);
            startActivity(intent);
            finish();

        }, 1000);
    }
}