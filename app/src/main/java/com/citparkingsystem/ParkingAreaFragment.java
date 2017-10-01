package com.citparkingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.citparkingsystem.lib.DeviceHelper;
import com.citparkingsystem.lib.OnPinchListener;;
import com.citparkingsystem.lib.ParkingAreaHelper;
import com.citparkingsystem.lib.StringHelper;
import com.citparkingsystem.requests.Parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Walter Ybanez on 7/30/2017.
 */

public class ParkingAreaFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = ParkingAreaFragment.class.getSimpleName();
    private int parkingArea;
    private ScaleGestureDetector scaleGestureDetector;
    private ImageView imageView[] = new ImageView[71];
    private SharedPreferences sharedPreferences;
    // private String[] slots = new String[71];
    private Parking parking;
    private AlertDialog.Builder builder;
    private int width = DeviceHelper.getScreenWidth();
    private int height = DeviceHelper.getScreenHeight();

    public ParkingAreaFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(),
                    android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }

        sharedPreferences = getActivity().getSharedPreferences("CIT_PARKING_SYSTEM", Context.MODE_PRIVATE);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            parkingArea = bundle.getInt("keyParkingArea", 0);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        scaleGestureDetector = new ScaleGestureDetector(getActivity(),
                new OnPinchListener(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // slots = sharedPreferences.getString("keyHsSlots", "").split(",");
        // You can switch between canvas type no zoom function or a layout type with zoom function
        // Layout type with zoom
        parking = new Parking(getActivity());
        // parking.getParkingSlots();
        View rootView = inflater.inflate(R.layout.fragment_hs_parking_area, container, false);

        int upperStartX = 108;
        int upperStartY = 45;

        int belowUpperStartX = 100;
        int belowUpperStartY = 180;

        int middleStartX = 290;
        int middleStartY = 124;

        int belowMiddleStartX = 308;
        int belowMiddleStartY = 274;
        for (int i = 1; i < 71; i++) {
            final int index = i;
            // Get all ids dynamically
            int id = getResources().getIdentifier("circle_"+i, "id", getActivity().getPackageName());
            try {
                imageView[i] = (ImageView) rootView.findViewById(id);
                RelativeLayout.LayoutParams lp =
                        new RelativeLayout.LayoutParams(imageView[i].getLayoutParams());

                // The default color of the circle is red, so set to green if its available
                parking.availableSlots(new Parking.Callback() {
                    @Override
                    public void successResponse(Object object) {
                        try {
                            JSONArray jsonArray = new JSONArray((String)object);
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                String area = jsonObject.getString("area");
                                if (area.equals("High School Area")) {
                                    String []available = jsonObject.getString("available_slots").split(",");
                                    for (int k = 0; k < available.length; k++) {
                                        if (!available[k].trim().equals("")) {
                                            if (index == Integer.valueOf(available[k].trim())) {
                                                // Set to green color of the circle
                                                imageView[index].setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_green));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Exception: "+e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void errorResponse(Object object) {

                    }
                });


                // Adjust the x and y
                if (i >= 1 && i <= 5) {
                    if (height == 800 && width == 480) {
                        lp.setMargins(upperStartX, upperStartY, 0, 0);
                    }
                    imageView[i].setLayoutParams(lp);
                    upperStartX += 38;
                    upperStartY += 10;
                }
                if (i >= 6 && i <= 11) {
                    if (height == 800 && width == 480) {
                        lp.setMargins(belowUpperStartX, belowUpperStartY, 0, 0);
                    }
                    imageView[i].setLayoutParams(lp);
                    belowUpperStartX += 35;
                    belowUpperStartY -= 13;
                }

                if (i >= 12 && i <= 16) {
                    if (height == 800 && width == 480) {
                        lp.setMargins(middleStartX, middleStartY, 0, 0);
                    }
                    imageView[i].setLayoutParams(lp);
                    middleStartX += 7;
                    middleStartY += 34;
                }

                if (i >= 17 && i <= 24) {
                    if (height == 800 && width == 480) {
                        lp.setMargins(belowMiddleStartX, belowMiddleStartY, 0, 0);
                    }
                    imageView[i].setLayoutParams(lp);
                    belowMiddleStartX += 7;
                    belowMiddleStartY += 34;
                }

                // Log.e(TAG, "Not affected by break");

                // Attach event listener dynamically
                imageView[i].setOnClickListener(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Canvas type
        return new ParkingAreaHelper(getActivity(), parkingArea);
        // Relative Layout with zoom
        // return rootView;
    }

    @Override
    public void onClick(View v) {
        for (int i = 1; i < 71; i++) {
            final int index = i;
            if (v == imageView[i]) {
                parking.availableSlots(new Parking.Callback() {
                    @Override
                    public void successResponse(Object object) {
                        try {
                            JSONArray jsonArray = new JSONArray((String)object);
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                String area = jsonObject.getString("area");
                                if (area.equals("High School Area")) {
                                    final String []slots = jsonObject.getString("available_slots").split(",");
                                    boolean flag = false;
                                    for (int k = 0; k < slots.length; k++) {
                                        if (!slots[k].trim().equals("")) {
                                            if (index == Integer.valueOf(slots[k].trim())) {
                                                flag = true;
                                            }
                                        }
                                    }
                                    if (flag) {
                                        String newSlots = "";
                                        for (int l = 0; l < slots.length; l++) {
                                            if (!slots[l].trim().equals("")) {
                                                if (Integer.valueOf(slots[l].trim()) != index) {
                                                    newSlots += slots[l]+ " ";
                                                }
                                            }
                                        }
                                        String []x = newSlots.split(" ");
                                        // slots = StringHelper.implode(", ", x).split(", ");

                                        // Update now the table
                                        parking.updateParkingAreaSlot(StringHelper.implode(", ", x),
                                                parkingArea, String.valueOf(index), "occupied");
                                        // parking.getParkingSlots();
                                        ParkingAreaFragment fragment = (ParkingAreaFragment)
                                                getFragmentManager().findFragmentById(R.id.container_body_frame_layout_id);
                                        getFragmentManager().beginTransaction()
                                                .detach(fragment)
                                                .attach(fragment)
                                                .commit();
                                        /*ParkingAreaFragment p = new ParkingAreaFragment();
                                        FragmentTransaction tr = getFragmentManager().beginTransaction();
                                        tr.replace(R.id.container_body_frame_layout_id, p);
                                        tr.commit();*/
                                    } else {
                                        builder.setTitle("Alert")
                                                .setMessage("Are you sure you want to vacate this slot?")
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                String newSlots = "";
                                                                for (int l = 0; l < slots.length + 1; l++) {
                                                                    if (l  == slots.length) {
                                                                        newSlots += index;
                                                                    } else {
                                                                        newSlots += slots[l]+ " ";
                                                                    }
                                                                }
                                                                String []x = newSlots.split(" ");
                                                               parking.updateParkingAreaSlot(StringHelper.implode(", ", x),
                                                                        parkingArea, String.valueOf(index), "vacant");
                                                                ParkingAreaFragment fragment = (ParkingAreaFragment)
                                                                        getFragmentManager().findFragmentById(R.id.container_body_frame_layout_id);
                                                                getFragmentManager().beginTransaction()
                                                                        .detach(fragment)
                                                                        .attach(fragment)
                                                                        .commit();
                                                                /*ParkingAreaFragment p = new ParkingAreaFragment();
                                                                FragmentTransaction tr = getFragmentManager().beginTransaction();
                                                                tr.replace(R.id.container_body_frame_layout_id, p);
                                                                tr.commit();*/
                                                            }
                                                        }
                                                ).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).show();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Exception: "+e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void errorResponse(Object object) {

                    }
                });
            }
        }
        /*FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();*/
    }

    private static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @Override
    public void onResume() {
        super.onResume();
        // ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
}
