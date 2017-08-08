package com.citparkingsystem.lib;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Dave Tolentin on 7/16/2017.
 */

public class ProcessRequest extends ServerAddress {

    private final static String TAG = ProcessRequest.class.getSimpleName();

    public ProcessRequest() {

    }

    public static void sendRequest(final String task, final String key[], final String value[],
                                   final VolleyResponseListener<Object> listener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
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
                try {
                    if (key.length != 0 && value.length != 0) {
                        for (int i = 0; i < key.length; i++) {
                            params.put(key[i], value[i]);
                        }
                    }
                } catch (Exception ex) {
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
