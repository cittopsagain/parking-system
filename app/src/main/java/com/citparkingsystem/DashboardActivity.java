package com.citparkingsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.citparkingsystem.encapsulate.ParkingArea;
import com.citparkingsystem.lib.ParkingAreas;
import com.citparkingsystem.lib.ServerAddress;
import com.citparkingsystem.lib.SessionManager;
import com.citparkingsystem.lib.StringHelper;
import com.citparkingsystem.lib.VolleySingleton;
import com.citparkingsystem.requests.Parking;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Walter Ybanez on 7/23/2017.
 */

public class DashboardActivity extends AppCompatActivity
        implements DrawerFragment.FragmentDrawerListener, View.OnClickListener {

    private ServerAddress serverAddress;

    private static final String TAG = DashboardActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private SessionManager sessionManager;
    private DrawerFragment drawerFragment;
    private NetworkImageView networkImageView;
    private ImageLoader imageLoader;
    private AlertDialog.Builder builder;

    private Toolbar mToolbar;
    private TextView txtFullName;
    private ArrayList<String> slotsArray = new ArrayList<>();

    private Parking parking;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mToolbar = (Toolbar) findViewById(R.id.dashboard_toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtFullName = (TextView) findViewById(R.id.full_name_text_view_id);

        drawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_navigation_drawer_id);
        drawerFragment.setUp(R.id.fragment_navigation_drawer_id,
                (DrawerLayout) findViewById(R.id.drawer_layout_id), mToolbar);
        drawerFragment.setDrawerListener(this);

        sharedPreferences = getSharedPreferences("CIT_PARKING_SYSTEM", Context.MODE_PRIVATE);
        sessionManager = new SessionManager(getApplicationContext());
        imageLoader = VolleySingleton.getInstance().getImageLoader();
        if (imageLoader == null) {
            imageLoader = VolleySingleton.getInstance().getImageLoader();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this,
                    android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        parking = new Parking(this);
        serverAddress = new ServerAddress();

        /*if (!sessionManager.isLoggedIn()) {
            logout();
        }*/
        if (!sessionManager.isConnected()) {
            sessionManager.clearUserData();
            logout();
        }

        networkImageView = (NetworkImageView) findViewById(R.id.user_profile_circle_image_view_id);
        /*networkImageView.setImageUrl(sharedPreferences.getString("keyUserProfile", ""),
                                        imageLoader);*/
        networkImageView.setImageUrl("http://"+serverAddress.IP+
                serverAddress.PORT+"/"+serverAddress.PACKAGE+"images/ic_guard.png", imageLoader);

        /*networkImageView.setImageUrl("http://"+serverAddress.IP+"/"+serverAddress.PACKAGE+
                "images/ic_guard.png", imageLoader);*/

        Log.e(TAG, "http://"+serverAddress.IP+
                        serverAddress.PORT+"/"+serverAddress.PACKAGE+"images/ic_guard.png");
        /*String fullName = sharedPreferences.getString("keyFirstName", "")+" "+
                sharedPreferences.getString("keyLastName", "");*/
        txtFullName.setText("Welcome Guard!");
        String parkingArea = sharedPreferences.getString("keyParkingArea", "");
        slotsArray.add(0, "academic");
        slotsArray.add(1, "st");
        slotsArray.add(2, "backgate");
        slotsArray.add(3, sharedPreferences.getString("keyHsSlots", ""));
        slotsArray.add(4, "canteen");
        Log.e(TAG, "Hs Slot: "+sharedPreferences.getString("keyHsSlots", "").split(","));
        parking.getParkingSlots();
        displayView(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                title = getString(R.string.dashboard);
                fragment = new MenuFragment();
                /*Bundle bundle = new Bundle();
                bundle.putStringArrayList("slots", slotsArray);
                fragment.setArguments(bundle);*/
                break;
            case 1:
                title = getString(R.string.violations);
                fragment = new ViolationsFragment();
                break;
            /*case 2:
                title = getString(R.string.parking_history);
                fragment = new ParkingHistoryFragment();
                break;*/

            case 2:
                displayResetDialog();
                break;
            case 3:
                    /*sessionManager.clearUserData();
                    logout();*/
                sessionManager.clearUserData();
                this.finishAffinity();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body_frame_layout_id, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }
    }

    private void displayResetDialog() {
        final ArrayList <String> arrayList = new ArrayList<>();
        for (int i = 0; i < ParkingAreas.area.length; i++) {
            arrayList.add(ParkingAreas.area[i].toString().trim());
        }
        // Default to High School area
        final boolean checkedSlots[] = new boolean[]{
                false,
                false,
                false,
                true,
                false
        };
        builder.setTitle("Reset Parking Slot").setMultiChoiceItems(ParkingAreas.area, checkedSlots,
                new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedSlots[which] = isChecked;
                String currentItem = arrayList.get(which);
                /*Toast.makeText(getApplicationContext(),
                        currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();*/
            }
        }).setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String areas = "";
                for (int i = 0; i < checkedSlots.length; i++) {
                    boolean checked = checkedSlots[i];
                    if (checked) {
                        /*Toast.makeText(getApplicationContext(),
                                "Checked: "+arrayList.get(i), Toast.LENGTH_SHORT).show();*/
                        areas += arrayList.get(i)+", ";
                    }
                }

                parking.resetParkingAreas(StringHelper.implode(",", areas), new Parking.Callback() {
                    @Override
                    public void successResponse(Object object) {
                        Log.e(TAG, (String)object);
                        try {
                            JSONObject jsonObject = new JSONObject((String)object);
                            if (!jsonObject.getBoolean("reset") &&
                                    jsonObject.getBoolean("vacant_all")) {
                                // Cannot reset because all slots is already vacant
                                Toast.makeText(getApplicationContext(), "Unable to reset because " +
                                        "all slots area already vacant!", Toast.LENGTH_LONG).show();
                            } else if (jsonObject.getBoolean("reset") &&
                                    !jsonObject.getBoolean("vacant_all")) {
                                Toast.makeText(getApplicationContext(), "Reset success!",
                                        Toast.LENGTH_LONG).show();
                                parking.getParkingSlots();
                            } else {
                                Toast.makeText(getApplicationContext(), "Unable to reset!",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void errorResponse(Object object) {
                        Log.e(TAG, (String)object);
                    }
                });
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void logout() {
        // Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        Intent i = new Intent(getApplicationContext(), SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
