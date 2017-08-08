package com.citparkingsystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.citparkingsystem.lib.CircleImageView;

/**
 * Created by Dave Tolentin on 7/24/2017.
 */

public class MenuFragment extends Fragment implements View.OnClickListener {

    private CircleImageView academicArea;
    private final static String TAG = MenuFragment.class.getSimpleName();

    public MenuFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        academicArea = (CircleImageView)
                rootView.findViewById(R.id.academic_area_image_view_id);
        academicArea.startAnimation(anim);

        // Later.. stop the animation
        academicArea.setAnimation(null);
        academicArea.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        // Call the Parking area fragment
        ParkingAreaFragment parkingAreaFragment = new ParkingAreaFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        if (view.getId() == R.id.academic_area_image_view_id) {
            bundle.putString("keyParkingArea", "academic");
            parkingAreaFragment.setArguments(bundle);
        }
        fragmentTransaction.replace(R.id.container_body_frame_layout_id, parkingAreaFragment);
        fragmentTransaction.commit();
    }
}

