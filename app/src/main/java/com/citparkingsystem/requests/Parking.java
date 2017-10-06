package com.citparkingsystem.requests;

import android.app.ProgressDialog;
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

import java.util.ArrayList;

/**
 * Created by Walter Ybanez on 7/27/2017.
 */

public class Parking {

    private ProgressDialog pDialog;
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

    // Will get all the violations
    public void getViolations(String sortBy, final Callback callback) {
        key = new String[] {"sortBy"};
        value = new String[] {sortBy};
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

    public void getSelectedViolation(int id, final Callback callback) {
        key = new String[] {"id"};
        value = new String[] {String.valueOf(id)};
        processRequest.sendRequest("getSelectedViolation", key, value,
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

    // Will update a specific violation
    public void updateViolation(int id, String pNumber, String violation, String area,
                                String carModel, String carColor, String carMake,
                                String additionalDetails, final Callback callback) {
        key = new String[] {"id", "plateNumber", "violation", "area", "carModel", "carColor",
                            "carMake", "additionalDetails"};
        value = new String[] {String.valueOf(id), pNumber, violation, area, carModel, carColor,
                                carMake, additionalDetails};
        processRequest.sendRequest("updateViolation", key, value,
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

    // Will delete a specific violation
    public void deleteViolation(int id, final Callback callback) {
        key = new String[] {"id"};
        value = new String[] {String.valueOf(id)};

        processRequest.sendRequest("deleteViolation", key, value,
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

    // Add violation
    public void addViolation(String plateNumber, String violationType, String whatArea,
                             String carModel, String carColor, String carMake,
                             String additionalDetails, final Callback callback) {
        key = new String[] {"plateNumber", "violationType", "whatParkingArea", "carModel",
                            "carColor", "carMake", "additionalDetails"};
        value = new String[] {plateNumber, violationType, whatArea, carModel, carColor, carMake,
                                additionalDetails};

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

    // Get all available parking slots
    public void getParkingSlots() {
        pDialog = new ProgressDialog(this.context);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        processRequest.sendRequest("getParkingSlots", key, value,
                new ProcessRequest.VolleyResponseListener<Object>() {
            @Override
            public void getSuccessResult(Object object) {
                 try {
                    JSONArray jsonArray = new JSONArray((String)object);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String area = jsonObject.getString("area");
                        if (area.equals("High School Area")) {
                            String available = jsonObject.getString("available_slots");
                            Log.e(TAG, "Hs slots: "+available);
                            sessionManager.parkingAreaAvailableSlotsHs(available);
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Exception: "+e.getMessage());
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }

            @Override
            public void getErrorResult(Object object) {
                pDialog.dismiss();
                Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Get all available parking slots, Used in parking area fragment
    public void availableSlots(final Callback callback) {
        processRequest.sendRequest("getParkingSlots", key, value,
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

    // Update the available slot
    public void updateParkingAreaSlot(final String slot, final int whatParkingArea,
                                      final String index, final String what) {
        key = new String[] {"whatParkingArea", "slot"};
        value = new String[] {ParkingAreas.area[whatParkingArea].toString(), slot};
        final Parking p = new Parking(context);

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
                // p.getParkingSlots();
            }

            @Override
            public void getErrorResult(Object object) {
                Toast.makeText(context, (String) object, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Add to parking history if slot number is set to occupied
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

    // Will reset the specific parking area
    public void resetParkingAreas(String areas, final Callback callback) {
        key = new String[] {"areas"};
        value = new String[] {areas};
        processRequest.sendRequest("resetParkingAreas", key, value,
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

    public interface Callback {
        void successResponse(Object object);
        void errorResponse(Object object);
    }
}
