package com.citparkingsystem.requests;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.citparkingsystem.encapsulate.Violation;
import com.citparkingsystem.lib.ParkingAreas;
import com.citparkingsystem.lib.ProcessRequest;
import com.citparkingsystem.lib.SessionManager;

import org.json.JSONArray;
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

    private final static String TAG = Parking.class.getSimpleName();

    public Parking(Context context) {
        processRequest = new ProcessRequest();
        sessionManager = new SessionManager(context);
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

    public void addViolation(String plateNumber, String violationType, String whatArea,
                              final Callback callback) {
        key = new String[] {"plateNumber", "violationType", "whatParkingArea"};
        value = new String[] {plateNumber, violationType, whatArea};

        processRequest.sendRequest("addViolation", key, value,
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

    public void getParkingSlots() {
        processRequest.sendRequest("getParkingSlots", key, value,
                new ProcessRequest.VolleyResponseListener<Object>() {
            @Override
            public void getSuccessResult(Object object) {
                 try {
                    JSONArray jsonArray = new JSONArray((String)object);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String area = jsonObject.getString("area");
                        if (area.equals("academic")) {
                            String available = jsonObject.getString("available_slots");
                            Log.e(TAG, "Academic available slots: "+available);
                            sessionManager.parkingAreaAvailableSlotsAcademic(available);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Exception: "+e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void getErrorResult(Object object) {
                Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateParkingAreaSlot(final String slot, final int whatParkingArea,
                                      final String index, final String what) {
        key = new String[] {"whatParkingArea", "slot"};
        value = new String[] {ParkingAreas.area[whatParkingArea].toString(), slot};

        processRequest.sendRequest("updateParkingAreaSlot", key, value,
                new ProcessRequest.VolleyResponseListener<Object>() {
            @Override
            public void getSuccessResult(Object object) {
                Log.e(TAG, (String) object);
                Toast.makeText(context, "Slot #"+index+" successfully set to "+what,
                        Toast.LENGTH_LONG).show();
                if (what == "occupied") {
                    addToParkingHistory(index, ParkingAreas.area[whatParkingArea].toString());
                }
            }

            @Override
            public void getErrorResult(Object object) {
                Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addToParkingHistory(String slot, String area) {
        key = new String[] {"whatParkingArea", "slot"};
        value = new String[] {area, slot};
        processRequest.sendRequest("addToParkingHistory", key, value,
                new ProcessRequest.VolleyResponseListener<Object>() {
            @Override
            public void getSuccessResult(Object object) {
                Log.e(TAG, (String) object);
            }

            @Override
            public void getErrorResult(Object object) {
                Log.e(TAG, (String) object);
            }
        });
    }

    public void getParkingHistory(final Callback callback) {
        processRequest.sendRequest("getParkingHistory", key, value,
                new ProcessRequest.VolleyResponseListener<Object>() {
            @Override
            public void getSuccessResult(Object object) {
                Log.e(TAG, (String) object);
                callback.successResponse(object);
            }

            @Override
            public void getErrorResult(Object object) {
                callback.errorResponse(object);
                Log.e(TAG, (String) object);
            }
        });
    }

    public interface Callback {
        void successResponse(Object object);
        void errorResponse(Object object);
    }
}
