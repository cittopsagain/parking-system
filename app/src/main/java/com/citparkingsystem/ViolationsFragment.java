package com.citparkingsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.citparkingsystem.adapters.ViolationAdapter;
import com.citparkingsystem.encapsulate.Violation;
import com.citparkingsystem.lib.ParkingAreas;
import com.citparkingsystem.requests.Parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dave Tolentin on 7/27/2017.
 */

public class ViolationsFragment extends Fragment {

    private Parking parking;
    private ViolationAdapter violationAdapter;
    private ArrayList<Violation> violationList = new ArrayList<Violation>();
    private ListView listView;
    private AlertDialog.Builder builder;

    private final static String TAG = ViolationsFragment.class.getSimpleName();

    public ViolationsFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parking = new Parking(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(),
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_violations, container, false);
        listView = (ListView) rootView.findViewById(R.id.violations_list_view_id);
        new executeTask().execute();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_violation, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search_violation).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                violationAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_violation) {
            final ArrayList selectedArea = new ArrayList();

            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText txtPlateNum = new EditText(getActivity());
            final EditText txtViolationType = new EditText(getActivity());

            txtPlateNum.setHint(R.string.enter_plate_num);
            txtViolationType.setHint(R.string.enter_violation);

            layout.addView(txtPlateNum);
            layout.addView(txtViolationType);
            builder.setTitle("Alert")
                    .setView(layout)
                    .setSingleChoiceItems(ParkingAreas.area, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedArea.add(0, ParkingAreas.area[which]);
                        }
                    })
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String plateNum = txtPlateNum.getText().toString();
                            String violation = txtViolationType.getText().toString();
                            if (plateNum.trim().length() > 0 || violation.trim().length() > 0) {
                                parking.addViolation(plateNum, violation,
                                        selectedArea.size() == 0 ? ParkingAreas.area[0].toString() :
                                                selectedArea.get(0).toString(),
                                        new Parking.Callback() {
                                    @Override
                                    public void successResponse(Object object) {
                                        Log.e(TAG, (String) object);
                                        Toast.makeText(getActivity(), R.string.success_save,
                                                Toast.LENGTH_SHORT).show();
                                        violationList.clear();
                                        new executeTask().execute();
                                    }

                                    @Override
                                    public void errorResponse(Object object) {
                                        Log.e(TAG, (String) object);
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), R.string.error_empty_fields,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    class executeTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            parking.getViolations(new Parking.Callback() {

                @Override
                public void successResponse(Object object) {
                    String response = (String) object;
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() == 0) {
                            Toast.makeText(getActivity(), R.string.no_records,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Violation violation = new Violation();
                                violation.setPlateNumber(jsonObject.getString("plate_number"));
                                violation.setViolationType(jsonObject.getString("violation_type"));
                                violationList.add(violation);
                            }
                            violationAdapter = new ViolationAdapter(getActivity(), violationList);
                            violationAdapter.notifyDataSetChanged();
                            listView.setAdapter(violationAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void errorResponse(Object object) {
                    Log.e(TAG, (String) object);
                }
            });
            return null;
        }
    }
}
