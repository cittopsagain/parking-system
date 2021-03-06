package com.citparkingsystem.lib;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Walter Ybanez on 7/16/2017.
 */

public class ProcessRequest extends ServerAddress {

    private final static String TAG = ProcessRequest.class.getSimpleName();

    public ProcessRequest() {

    }

    /**
     * Handles all the requests from the mobile through the server
     * Note: We used a volley library inorder for us to communicate to the web
     * For more information please visit https://developer.android.com/training/volley/index.html
     *
     * The purpose of this function is to handle dynamically all the requests from client
     */
    public static void sendRequest(final String task, final String key[], final String value[],
                                   final VolleyResponseListener<Object> listener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e(TAG, s);
                listener.getSuccessResult(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.getErrorResult(volleyError.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("task", task);
                // Assemble all the key value pair to be passed to web server
                try {
                    if (key.length != 0 && value.length != 0) {
                        for (int i = 0; i < key.length; i++) {
                            params.put(key[i], value[i]);
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    ex.printStackTrace();
                }

                return params;
            }
        };

        VolleySingleton.getInstance().addToRequestQueue(stringRequest, task);
    }

    public interface VolleyResponseListener<T> {
        void getSuccessResult(T object);
        void getErrorResult(T object);
    }
}
