package com.citparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.citparkingsystem.lib.SessionManager;

/**
 * Created by Dave Tolentin on 8/31/2017.
 */

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 7000;
    private SessionManager sessionManager;

    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView) findViewById(R.id.img_view_cit_logo);
        sessionManager = new SessionManager(this);

        if (sessionManager.isConnected()) {
            Intent mainIntent = new Intent(SplashActivity.this, DashboardActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sessionManager.setSuccessConnecting(true);
                Intent mainIntent = new Intent(SplashActivity.this, DashboardActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();

                // Check if connected to server
                // if success, Proceed to dashboard
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
