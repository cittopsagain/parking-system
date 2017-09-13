package com.citparkingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.citparkingsystem.lib.ProcessRequest;
import com.citparkingsystem.lib.SessionManager;
import com.citparkingsystem.requests.Parking;

/**
 * Created by Dave Tolentin on 8/31/2017.
 */

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private final int SPLASH_DISPLAY_LENGTH = 7000;
    private SessionManager sessionManager;
    private ProcessRequest processRequest;
    private Parking parking;
    private AlertDialog.Builder builder;
    private Handler handler;
    private Runnable runnable;

    private static final String TAG = SplashActivity.class.getSimpleName();

    private ImageView imageView;
    private TextView txtConnectStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView) findViewById(R.id.img_view_cit_logo);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        txtConnectStatus = (TextView) findViewById(R.id.connect_status_text_view_id);

        sessionManager = new SessionManager(this);
        processRequest = new ProcessRequest();
        parking = new Parking(this);
        handler = new Handler();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        if (sessionManager.isConnected()) {
            Intent mainIntent = new Intent(SplashActivity.this, DashboardActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
        } else {
            connect();
        }
    }

    public void connect() {
        String key[] = {};
        String val[] = {};
        processRequest.sendRequest("connect", key, val,
        new ProcessRequest.VolleyResponseListener<Object>() {
            @Override
            public void getSuccessResult(Object object) {
                sessionManager.setSuccessConnecting(true);
                handler.postDelayed(runnable = new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(SplashActivity.this,
                                DashboardActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                        handler.postDelayed(this, 1000);
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }

            @Override
            public void getErrorResult(Object object) {
                Log.e(TAG, (String) object);
                progressBar.setVisibility(View.GONE);
                txtConnectStatus.setText("");
                builder.setTitle("Connect failed").
                        setMessage("Unable to connect to server!").setCancelable(false).
                        setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressBar.setVisibility(View.VISIBLE);
                                txtConnectStatus.setText("Please wait while reconnecting to " +
                                        "server....");
                                connect();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}