package com.citparkingsystem.requests;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.citparkingsystem.DashboardActivity;
import com.citparkingsystem.R;
import com.citparkingsystem.lib.ProcessRequest;
import com.citparkingsystem.lib.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Walter Ybanez on 7/23/2017.
 */

public class Login {

    private ProcessRequest processRequest;
    private SessionManager sessionManager;
    private Context context;

    private final static String TAG = Login.class.getSimpleName();

    public Login(Context context) {
        processRequest = new ProcessRequest();
        sessionManager = new SessionManager(context);
        this.context = context;
    }

    public void doLogin(String username, String password) {
        new executeTask().execute(username, password);
    }

    class executeTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(final String... params) {
            String key[] = {"username", "password"};
            String value[] = {params[0], params[1]};
            processRequest.sendRequest("login", key, value,
                    new ProcessRequest.VolleyResponseListener<Object>() {

                @Override
                public void getSuccessResult(Object object) {
                    try {
                        JSONObject jsonObject = new JSONObject((String)object);
                        if (!jsonObject.getBoolean("exist")) {
                            Toast.makeText(context.getApplicationContext(), R.string.user_not_exist,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            String username = params[0];
                            String firstName = jsonObject.getString("fname");
                            String lastName = jsonObject.getString("lname");
                            String userProfile = jsonObject.getString("img_path");

                            sessionManager.setLoginCredentials(true, username, firstName, lastName,
                                                                userProfile);
                            Intent intent = new Intent(context, DashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void getErrorResult(Object object) {
                    Toast.makeText(context.getApplicationContext(), R.string.error_connect_server,
                            Toast.LENGTH_LONG).show();
                }
            });

            return null;
        }
    }
}
