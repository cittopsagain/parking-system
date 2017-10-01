package com.citparkingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.citparkingsystem.lib.ParkingAreas;
import com.citparkingsystem.requests.Parking;

import java.util.ArrayList;

/**
 * Created by Walter Ybanez on 1/10/2017.
 */

public class EditViolation extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = EditViolation.class.getSimpleName();

    private Toolbar mToolbar;
    private Spinner spinnerArea;
    private EditText txtPlateNumber;
    private EditText txtCarModel;
    private EditText txtCarMake;
    private EditText txtCarColor;
    private EditText txtViolationType;
    private EditText txtAdditionalDetails;
    private Button btnUpdateViolation;

    private Parking parking; // Handles all the send/receive through the server
    private String selectedArea; // Spinner selected area, Used when updating the violation
    private int id; // Id of the violation to be updated
    int index = 0; // Which position of parking area, Used in the spinner default display

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_violation);
        mToolbar = (Toolbar) findViewById(R.id.dashboard_toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Violation");

        parking = new Parking(getApplicationContext());

        // Get the parameters passed from ViolationsFragment, from violation list view onclick event
        Intent intent = getIntent();
        String[] values = intent.getStringArrayExtra("values");
        Log.e(TAG, values[0]);

        spinnerArea = (Spinner) findViewById(R.id.area_spinner_id);
        txtPlateNumber = (EditText) findViewById(R.id.plate_no_text_view_id);
        txtCarModel = (EditText) findViewById(R.id.car_model_text_view_id);
        txtCarMake = (EditText) findViewById(R.id.car_make_text_view_id);
        txtCarColor = (EditText) findViewById(R.id.car_color_text_view_id);
        txtViolationType = (EditText) findViewById(R.id.violation_type_text_view_id);
        txtAdditionalDetails = (EditText) findViewById(R.id.additional_details_text_view_id);

        txtPlateNumber.setText(values[1]);
        txtCarModel.setText(values[4]);
        txtCarMake.setText(values[6]);
        txtCarColor.setText(values[5]);
        txtViolationType.setText(values[0]);
        txtAdditionalDetails.setText(values[7]);
        id = Integer.valueOf(values[3]);

        btnUpdateViolation = (Button) findViewById(R.id.update_violation_btn);
        btnUpdateViolation.setOnClickListener(this); // Attach onclick event

        for (int i = 0; i < ParkingAreas.area.length; i++) {
            if (ParkingAreas.area[i].equals(values[2].trim())) {
                index = i;
                break;
            }
        }

        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < ParkingAreas.area.length; i++) {
            arrayList.add(ParkingAreas.area[i].toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinnerArea.setAdapter(adapter);
        spinnerArea.setSelection(index);

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedArea = ParkingAreas.area[position].toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.update_violation_btn) {
            // Update now
            parking.updateViolation(id,
            txtPlateNumber.getText().toString().trim(),
            txtViolationType.getText().toString().trim(), selectedArea,
            txtCarModel.getText().toString(), txtCarColor.getText().toString(),
            txtCarMake.getText().toString(),
            txtAdditionalDetails.getText().toString(),
            new Parking.Callback() {
                @Override
                public void successResponse(Object object) {
                    Toast.makeText(getApplicationContext(), R.string.success_edit,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void errorResponse(Object object) {
                    Toast.makeText(getApplicationContext(), (String)object, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
