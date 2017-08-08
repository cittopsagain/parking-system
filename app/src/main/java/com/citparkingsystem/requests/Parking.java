package com.citparkingsystem.requests;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.citparkingsystem.lib.ProcessRequest;
import com.citparkingsystem.lib.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dave Tolentin on 7/27/2017.
 */

public class Parking {

    private ProcessRequest processRequest;
    private SessionManager sessionManager;
    private Context context;
    private String key[] = {};
    private String value[] = {};
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private final static String TAG = Parking.class.getSimpleName();

    public Parking(Context context) {
        processRequest = new ProcessRequest();
        sessionManager = new SessionManager(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
        this.context = context;
    }

    public void getViolations(final Callback callback) {
        processRequest.sendRequest("getViolations", key, value,
                new ProcessRequest.VolleyResponseListener<Object>() {

            @Override
            public void getSuccessResult(Object object) {
                callback.successResponse(object);
            }

            @Override
            public void getErrorResult(Object object) {
                callback.errorResponse(object);
            }
        });
    }

    public void saveViolation(String plateNumber, String violationType) {

    }

    public void getParkingSlots(final String parkingArea) {
        key = new String[] {"whatParkingArea"};
        value = new String[] {parkingArea};
        processRequest.sendRequest("getParkingSlots", key, value,
                new ProcessRequest.VolleyResponseListener<Object>() {
            @Override
            public void getSuccessResult(Object object) {
                try {
                    JSONObject jsonObject = new JSONObject((String)object);
                    sessionManager.parkingAreaAvailableSlots(jsonObject.getString("available_slots"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getErrorResult(Object object) {
                Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateParkingAreaSlot(String slot, String whatParkingArea) {
        key = new String[] {"whatParkingArea", "slot"};
        value = new String[] {whatParkingArea, slot};
        processRequest.sendRequest("updateParkingAreaSlot", key, value,
                new ProcessRequest.VolleyResponseListener<Object>() {
            @Override
            public void getSuccessResult(Object object) {
                Log.e(TAG, (String) object);
                Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
            }

            @Override
            public void getErrorResult(Object object) {
                Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface Callback {
        void successResponse(Object object);
        void errorResponse(Object object);
    }
}
