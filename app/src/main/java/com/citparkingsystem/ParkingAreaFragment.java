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
    private int width = DeviceHelper.getScreenWidth(); // Get screen width
    private int height = DeviceHelper.getScreenHeight(); // Get screen height

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
        Log.e(TAG, "Height: "+height+" Width: "+width);
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

        parking = new Parking(getActivity());
        // parking.getParkingSlots();

        // Layout type with zoom
        View rootView = inflater.inflate(R.layout.fragment_hs_parking_area, container, false);

        // X and Y
        int upperStartX = 0;
        int upperStartY = 0;
        int belowUpperStartX = 0;
        int belowUpperStartY = 0;
        int middleStartX = 0;
        int middleStartY = 0;
        int belowMiddleStartX = 0;
        int belowMiddleStartY = 0;
        int leftStartX = 0;
        int leftStartY = 0;
        int belowLeftStartX = 0;
        int belowLeftStartY = 0;
        int lastRightStartX = 0;
        int lastRightStartY = 0;
        // Support multiple screen devices
        if (height == 800 && width == 480) {
            upperStartX = 100;
            upperStartY = 30;

            belowUpperStartX = 90;
            belowUpperStartY = 150;

            middleStartX = 284;
            middleStartY = 108;

            belowMiddleStartX = 304;
            belowMiddleStartY = 260;

            leftStartX = 166;
            leftStartY = 550;

            belowLeftStartX = 188;
            belowLeftStartY = 650;

            lastRightStartX = 380;
            lastRightStartY = 290;
        } else if (height == 1184 && width == 768) {
            upperStartX = 130;
            upperStartY = 13;

            belowUpperStartX = 100;
            belowUpperStartY = 220;

            middleStartX = 550;
            middleStartY = 150;

            belowMiddleStartX = 480;
            belowMiddleStartY = 350;

            leftStartX = 260;
            leftStartY = 785;

            belowLeftStartX = 300;
            belowLeftStartY = 920;

            lastRightStartX = 610;
            lastRightStartY = 430;
        } else if (height == 2392 && width == 1440) {
            upperStartX = 300;
            upperStartY = 70;

            belowUpperStartX = 230;
            belowUpperStartY = 420;

            middleStartX = 840;
            middleStartY = 300;

            belowMiddleStartX = 890;
            belowMiddleStartY = 720;

            leftStartX = 490;
            leftStartY = 1600;

            belowLeftStartX = 560;
            belowLeftStartY = 1900;

            lastRightStartX = 1130;
            lastRightStartY = 900;
        } else if (height == 1920 && width == 1080) {
            // For Samsung S5 Device
            upperStartX = 230;
            upperStartY = 60;

            belowUpperStartX = 180;
            belowUpperStartY = 340;

            middleStartX = 620;
            middleStartY = 220;

            belowMiddleStartX = 670;
            belowMiddleStartY = 570;

            leftStartX = 360;
            leftStartY = 1280;

            belowLeftStartX = 420;
            belowLeftStartY = 1520;

            lastRightStartX = 850;
            lastRightStartY = 680;
        } else if (height == 1280 && width == 720) {
            // Devices like Samsung S3 and Oppo F1
            upperStartX = 140;
            upperStartY = 30;

            belowUpperStartX = 120;
            belowUpperStartY = 230;

            middleStartX = 410;
            middleStartY = 130;

            belowMiddleStartX = 450;
            belowMiddleStartY = 370;

            leftStartX = 240;
            leftStartY = 850;

            belowLeftStartX = 280;
            belowLeftStartY = 1010;

            lastRightStartX = 570;
            lastRightStartY = 450;
        }

        for (int i = 1; i < 55; i++) {
            final int index = i;
            // Get all ids dynamically
            int id = getResources().getIdentifier("circle_"+i, "id", getActivity().getPackageName());
            try {
                imageView[i] = (ImageView) rootView.findViewById(id);
                RelativeLayout.LayoutParams lp =
                        new RelativeLayout.LayoutParams(imageView[i].getLayoutParams());

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
                                                // The default color of the circle is red, so set to green if its available
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

                // Adjust the x and y, based on screen devices
                if (i >= 1 && i <= 5) {
                    lp.setMargins(upperStartX, upperStartY, 0, 0);
                    imageView[i].setLayoutParams(lp);
                    if (height == 800 && width == 480) {
                        upperStartX += 38;
                        upperStartY += 10;
                    } else if (height == 1184 && width == 768) {
                        upperStartX += 70;
                        upperStartY += 20;
                    } else if (height == 2392 && width == 1440)  {
                        upperStartX += 120;
                        upperStartY += 30;
                    } else if (height == 1920 && width == 1080) {
                        // For Samsung S5 Device
                        upperStartX += 80;
                        upperStartY += 20;
                    } else if (height == 1280 && width == 720) {
                        // Devices like Samsung S3 and Oppo F1
                        upperStartX += 60;
                        upperStartY += 16;
                    }
                }

                if (i >= 6 && i <= 11) {
                    lp.setMargins(belowUpperStartX, belowUpperStartY, 0, 0);
                    imageView[i].setLayoutParams(lp);
                    if (height == 800 && width == 480) {
                        belowUpperStartX += 35;
                        belowUpperStartY -= 13;
                    } else if (height == 1184 && width == 768) {
                        belowUpperStartX += 70;
                        belowUpperStartY -= 20;
                    } else if (height == 2392 && width == 1440)  {
                        belowUpperStartX += 114;
                        belowUpperStartY -= 35;
                    } else if (height == 1920 && width == 1080) {
                        // For Samsung S5 Device
                        belowUpperStartX += 80;
                        belowUpperStartY -= 30;
                    } else if (height == 1280 && width == 720) {
                        // Devices like Samsung S3 and Oppo F1
                        belowUpperStartX += 50;
                        belowUpperStartY -= 20;
                    }
                }

                if (i >= 12 && i <= 16) {
                    lp.setMargins(middleStartX, middleStartY, 0, 0);
                    imageView[i].setLayoutParams(lp);
                    if (height == 800 && width == 480) {
                        middleStartX += 7;
                        middleStartY += 30;
                    } else if (height == 1184 && width == 768) {
                        middleStartX += 70;
                        middleStartY += 20;
                    } else if (height == 2392 && width == 1440)  {
                        middleStartX += 20;
                        middleStartY += 80;
                    } else if (height == 1920 && width == 1080) {
                        // For Samsung S5 Device
                        middleStartX += 18;
                        middleStartY += 70;
                    } else if (height == 1280 && width == 720) {
                        // Devices like Samsung S3 and Oppo F1
                        middleStartX += 13;
                        middleStartY += 50;
                    }
                }

                if (i >= 17 && i <= 24) {
                    lp.setMargins(belowMiddleStartX, belowMiddleStartY, 0, 0);
                    imageView[i].setLayoutParams(lp);
                    if (height == 800 && width == 480) {
                        belowMiddleStartX += 6;
                        belowMiddleStartY += 28;
                    } else if (height == 1184 && width == 768) {
                        belowMiddleStartX += 11;
                        belowMiddleStartY += 42;
                    } else if (height == 2392 && width == 1440)  {
                        belowMiddleStartX += 22;
                        belowMiddleStartY += 90;
                    } else if (height == 1920 && width == 1080) {
                        // For Samsung S5 Device
                        belowMiddleStartX += 16;
                        belowMiddleStartY += 68;
                    } else if (height == 1280 && width == 720) {
                        // Devices like Samsung S3 and Oppo F1
                        belowMiddleStartX += 10;
                        belowMiddleStartY += 48;
                    }
                }

                if (i >= 25 && i <= 35) {
                    lp.setMargins(leftStartX, leftStartY, 0, 0);
                    imageView[i].setLayoutParams(lp);
                    if (height == 800 && width == 480) {
                        leftStartX += 18;
                        leftStartY -= 6.3;
                    } else if (height == 1184 && width == 768) {
                        leftStartX += 30;
                        leftStartY -= 11;
                    } else if (height == 2392 && width == 1440)  {
                        leftStartX += 54;
                        leftStartY -= 20;
                    } else if (height == 1920 && width == 1080) {
                        // For Samsung S5 Device
                        leftStartX += 44;
                        leftStartY -= 18;
                    } else if (height == 1280 && width == 720) {
                        // Devices like Samsung S3 and Oppo F1
                        leftStartX += 30;
                        leftStartY -= 12;
                    }
                }

                if (i >= 36 && i <= 46) {
                    lp.setMargins(belowLeftStartX, belowLeftStartY, 0, 0);
                    imageView[i].setLayoutParams(lp);
                    if (height == 800 && width == 480) {
                        belowLeftStartX += 18;
                        belowLeftStartY -= 6.3;
                    } else if (height == 1184 && width == 768) {
                        belowLeftStartX += 30;
                        belowLeftStartY -= 11;
                    } else if (height == 2392 && width == 1440)  {
                        belowLeftStartX += 54;
                        belowLeftStartY -= 20;
                    } else if (height == 1920 && width == 1080) {
                        // For Samsung S5 Device
                        belowLeftStartX += 44;
                        belowLeftStartY -= 18;
                    } else if (height == 1280 && width == 720) {
                        // Devices like Samsung S3 and Oppo F1
                        belowLeftStartX += 30;
                        belowLeftStartY -= 12;
                    }
                }

                if (i >= 47 && i <= 54) {
                    lp.setMargins(lastRightStartX, lastRightStartY, 0, 0);
                    imageView[i].setLayoutParams(lp);
                    if (height == 800 && width == 480) {
                        lastRightStartX += 5;
                        lastRightStartY += 28;
                    } else if (height == 1184 && width == 768) {
                        lastRightStartX += 30;
                        lastRightStartY += 11;
                    } else if (height == 2392 && width == 1440)  {
                        lastRightStartX += 18;
                        lastRightStartY += 90;
                    } else if (height == 1920 && width == 1080) {
                        // For Samsung S5 Device
                        lastRightStartX += 16;
                        lastRightStartY += 90;
                    } else if (height == 1280 && width == 720) {
                        // Devices like Samsung S3 and Oppo F1
                        lastRightStartX += 10;
                        lastRightStartY += 60;
                    }
                }

                // Attach event listener dynamically
                imageView[i].setOnClickListener(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Canvas type
        // return new ParkingAreaHelper(getActivity(), parkingArea);
        // Relative Layout with zoom
        return rootView;
    }

    @Override
    public void onClick(View v) {
        for (int i = 1; i < 55; i++) {
            final int index = i;
            if (v == imageView[i]) {
                parking.availableSlots(new Parking.Callback() {
                    @Override
                    public void successResponse(Object object) {
                        try {
                            JSONArray jsonArray = new JSONArray((String) object);
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(j);
                                String area = jsonObject.getString("area");
                                if (area.equals("High School Area")) {
                                    final String[] slots = jsonObject.getString("available_slots").split(",");
                                    boolean flag = false;
                                    for (int k = 0; k < slots.length; k++) {
                                        if (!slots[k].trim().equals("")) {
                                            if (index == Integer.valueOf(slots[k].trim())) {
                                                flag = true; // Vacant
                                            }
                                        }
                                    }
                                    // From vacant to occupied
                                    if (flag) {
                                        String newSlots = "";
                                        for (int l = 0; l < slots.length; l++) {
                                            if (!slots[l].trim().equals("")) {
                                                if (Integer.valueOf(slots[l].trim()) != index) {
                                                    newSlots += slots[l] + " ";
                                                }
                                            }
                                        }
                                        String[] x = newSlots.split(" ");

                                        // Update now the table
                                        parking.updateParkingAreaSlot(StringHelper.implode(", ", x),
                                                parkingArea, String.valueOf(index), "occupied");
                                        // Load again this fragment, inorder the slots to be updated
                                        ParkingAreaFragment fragment = (ParkingAreaFragment)
                                                getFragmentManager().findFragmentById(R.id.container_body_frame_layout_id);
                                        getFragmentManager().beginTransaction()
                                                .detach(fragment)
                                                .attach(fragment)
                                                .commit();
                                        // Query again for new slots
                                        parking.getParkingSlots();
                                    } else {
                                        // From occupied to vacant
                                        builder.setTitle("Alert")
                                                .setMessage("Are you sure you want to vacate this slot?")
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                String newSlots = "";
                                                                for (int l = 0; l < slots.length + 1; l++) {
                                                                    if (l == slots.length) {
                                                                        newSlots += index;
                                                                    } else {
                                                                        newSlots += slots[l] + " ";
                                                                    }
                                                                }
                                                                // New slots
                                                                String[] x = newSlots.split(" ");

                                                                // Update now the table
                                                                parking.updateParkingAreaSlot(StringHelper.implode(", ", x),
                                                                        parkingArea, String.valueOf(index), "vacant");
                                                                // Load again this fragment, inorder the slots to be updated
                                                                ParkingAreaFragment fragment = (ParkingAreaFragment)
                                                                        getFragmentManager().findFragmentById(R.id.container_body_frame_layout_id);
                                                                getFragmentManager().beginTransaction()
                                                                        .detach(fragment)
                                                                        .attach(fragment)
                                                                        .commit();
                                                                // Query again for new slots
                                                                parking.getParkingSlots();
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
                            Log.e(TAG, "Exception: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void errorResponse(Object object) {

                    }
                });
            }
        }
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
