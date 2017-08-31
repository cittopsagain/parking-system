package com.citparkingsystem;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.citparkingsystem.lib.ParkingAreaHelper;
import com.citparkingsystem.lib.SessionManager;
import com.citparkingsystem.lib.VolleySingleton;
import com.citparkingsystem.requests.Parking;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Dave Tolentin on 7/30/2017.
 */

public class ParkingAreaFragment extends Fragment {

    private final static String TAG = ParkingAreaFragment.class.getSimpleName();
    private int parkingArea;

    public ParkingAreaFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            parkingArea = bundle.getInt("keyParkingArea", 0);
        }
        /*if (parkingArea == "academic") {
            Log.e(TAG, "EHERAFADf");
        }
        Log.e(TAG, "DFSDFSDFQE@#$WFSDF");*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return new ParkingAreaHelper(getActivity(), parkingArea);
    }
}
