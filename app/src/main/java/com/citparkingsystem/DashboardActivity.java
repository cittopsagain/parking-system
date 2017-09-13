package com.citparkingsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.citparkingsystem.lib.ServerAddress;
import com.citparkingsystem.lib.SessionManager;
import com.citparkingsystem.lib.VolleySingleton;
import com.citparkingsystem.requests.Parking;

import java.util.ArrayList;

/**
 * Created by Dave Tolentin on 7/23/2017.
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

    private Toolbar mToolbar;
    private TextView txtFullName;
    private ArrayList<String> slotsArray = new ArrayList<>();

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
        /*networkImageView.setImageUrl("http://"+serverAddress.IP+
                serverAddress.PORT+"/"+serverAddress.PACKAGE+"images/profile2.jpg", imageLoader);*/

        networkImageView.setImageUrl("http://"+serverAddress.IP+"/"+serverAddress.PACKAGE+
                "images/profile2.jpg", imageLoader);

        /*Log.e(TAG, "http://"+serverAddress.IP+
                        serverAddress.PORT+"/"+serverAddress.PACKAGE+"images/profile2.jpg");*/
        /*String fullName = sharedPreferences.getString("keyFirstName", "")+" "+
                sharedPreferences.getString("keyLastName", "");*/
        txtFullName.setText("Welcome Guest!");
        String parkingArea = sharedPreferences.getString("keyParkingArea", "");
        slotsArray.add(0, "academic");
        slotsArray.add(1, "st");
        slotsArray.add(2, "backgate");
        slotsArray.add(3, sharedPreferences.getString("keyHsSlots", ""));
        slotsArray.add(4, "canteen");
        Log.e(TAG, "Hs Slot: "+sharedPreferences.getString("keyHsSlots", "").split(","));
        Parking p = new Parking(this);
        p.getParkingSlots();
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

    private void logout() {
        // Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        Intent i = new Intent(getApplicationContext(), SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
