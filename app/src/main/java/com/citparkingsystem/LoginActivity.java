package com.citparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.citparkingsystem.lib.SessionManager;
import com.citparkingsystem.requests.Login;

/**
 * Created by Walter Ybanez on 7/23/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Login login;
    private SessionManager sessionManager;

    private TextView txtUsername;
    private TextView txtPassword;

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        login = new Login(this);

        txtUsername = (TextView) findViewById(R.id.user_name_text_view_id);
        txtPassword = (TextView) findViewById(R.id.password_text_view_id);

        btnLogin = (Button) findViewById(R.id.login_button_id);
        btnLogin.setOnClickListener(this);

        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button_id) {
            String username = txtUsername.getText().toString();
            String password = txtPassword.getText().toString();
            if (username.trim().length() > 0 && password.trim().length() > 0) {
                login.doLogin(username, password);
            } else {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_LONG).show();
            }
        }
    }
}
