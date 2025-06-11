package com.bcm.us_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;

public class introActivity extends AppCompatActivity {

    Handler handler;

    // Intro 후 LoginActivity 화면으로 전환(화면효과를 fade_in, fade_out)
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);

//        getSupportActionBar().hide();
        handler = new Handler();
        handler.postDelayed(runnable, 2500);

    }


    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }



}
