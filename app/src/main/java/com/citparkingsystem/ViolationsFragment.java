package com.citparkingsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.citparkingsystem.adapters.ViolationAdapter;
import com.citparkingsystem.encapsulate.Violation;
import com.citparkingsystem.lib.ParkingAreas;
import com.citparkingsystem.lib.StringHelper;
import com.citparkingsystem.requests.Parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Walter Ybanez on 7/27/2017.
 */

public class ViolationsFragment extends Fragment {
    private ProgressDialog pDialog; // Progress Dialog
    private Parking parking; // Handles all the send/receive through the server
    private ViolationAdapter violationAdapter;
    private ArrayList<Violation> violationList = new ArrayList<Violation>(); // List of all violations
    private ListView listView; // ListView of violations
    private AlertDialog.Builder builder; // Holds the alert dialog of edit violation
    private AlertDialog.Builder deleteBuilder; // Holds the alert dialog of delete violation
    private ActionMode mActionMode;
    private ArrayList selectedArea = new ArrayList(); // Contains the selected area

    // Inorder for us to know what position to display the current area to be edited
    private int index = 0;

    private final static String TAG = ViolationsFragment.class.getSimpleName();

    public ViolationsFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parking = new Parking(getActivity()); // Instantiate the parking class object

        // Instantiate the alert dialog object
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Alert dialog above or equal to lollipop
            builder = new AlertDialog.Builder(getActivity(),
                    android.R.style.Theme_Material_Light_Dialog_Alert);
            deleteBuilder = new AlertDialog.Builder(getActivity(),
                    android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            // Alert dialog lower than lollipop version
            builder = new AlertDialog.Builder(getActivity());
            deleteBuilder = new AlertDialog.Builder(getActivity());
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
        // Call the layout of violation
        View rootView = inflater.inflate(R.layout.fragment_violations, container, false);

        // Call the list view of violation
        listView = (ListView) rootView.findViewById(R.id.violations_list_view_id);

        // Call the async task to display the violation
        new executeTask().execute("");
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Toolbar menus
        inflater.inflate(R.menu.menu_violation, menu);
        // Search in toolbar
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search_violation).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter dynamically through the list view, not in database
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
        // Add Violation
        if (item.getItemId() == R.id.menu_add_violation) {
            ArrayList<String> u = new ArrayList<>();
            for (int i = 0; i < ParkingAreas.area.length; i++) {
                u.add(i, StringHelper.toTheUpperCaseSingle(ParkingAreas.area[i].toString().trim()));
            }
            CharSequence[] upperCaseArea = u.toArray(new CharSequence[u.size()]);
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);

            // Create dynamically the edit text
            final EditText txtPlateNum = new EditText(getActivity());
            final EditText txtViolationType = new EditText(getActivity());
            final EditText txtCarModel = new EditText(getActivity());
            final EditText txtCarColor = new EditText(getActivity());
            final EditText txtCarMake = new EditText(getActivity());
            final EditText txtAdditionalDetails = new EditText(getActivity());

            // Add hints in edit text
            txtPlateNum.setHint(R.string.enter_plate_num);
            txtViolationType.setHint(R.string.enter_violation);
            txtCarModel.setHint(R.string.enter_car_model);
            txtCarColor.setHint(R.string.enter_car_color);
            txtCarMake.setHint(R.string.enter_car_make);
            txtAdditionalDetails.setHint(R.string.enter_additional_details);

            // Attach the newly created edit texts in the alert dialog
            layout.addView(txtPlateNum);
            layout.addView(txtCarMake);
            layout.addView(txtCarModel);
            layout.addView(txtCarColor);
            layout.addView(txtViolationType);
            layout.addView(txtAdditionalDetails);

            // Create now the alert dialog
            builder.setTitle("Enter Violation")
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
                            // Get the text fields values
                            String plateNum = txtPlateNum.getText().toString();
                            String violation = txtViolationType.getText().toString();
                            String carModel = txtCarModel.getText().toString();
                            String carColor = txtCarColor.getText().toString();
                            String carMake = txtCarMake.getText().toString();
                            String additionalDetails = txtAdditionalDetails.getText().toString();

                            if (plateNum.trim().length() > 0 || violation.trim().length() > 0) {
                                // If input fields has values, insert the records
                                parking.addViolation(plateNum, violation,
                                        selectedArea.size() == 0 ? ParkingAreas.area[0].toString() :
                                                selectedArea.get(0).toString(), carModel, carColor,
                                        carMake, additionalDetails, new Parking.Callback() {
                                    @Override
                                    public void successResponse(Object object) {
                                        Log.e(TAG, (String) object);
                                        Toast.makeText(getActivity(), R.string.success_save,
                                                Toast.LENGTH_SHORT).show();
                                        // Call again the async task to load the newly added violations
                                        new executeTask().execute("");
                                    }

                                    @Override
                                    public void errorResponse(Object object) {
                                        Log.e(TAG, (String) object);
                                    }
                                });
                            } else {
                                // Show notification if input fields has missing values
                                Toast.makeText(getActivity(), R.string.error_empty_fields,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        } else if (item.getItemId() == R.id.menu_sort_by_violation) {
            // Sort by violation
            new executeTask().execute("violation_type");
        } else if (item.getItemId() == R.id.menu_sort_by_area) {
            // Sort by area
            new executeTask().execute("area");
        }

        return super.onOptionsItemSelected(item);
    }

    // Will handle the display of all violations
    class executeTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress dialog before loading the records
            pDialog = new ProgressDialog(ViolationsFragment.this.getActivity());
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            // Get the parameter passed, sort by(area, violation)
            String sortBy = strings[0];
            parking.getViolations(sortBy, new Parking.Callback() {

                @Override
                public void successResponse(Object object) {
                    // Clear the array list before populating the records, inorder not to stack the
                    // the previous records
                    violationList.clear();

                    // Holds the array of violations
                    String response = (String) object;
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() == 0) {
                            // Display notification if no records in violations table
                            Toast.makeText(getActivity(), R.string.no_records,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // Loop all the violations
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Violation violation = new Violation();
                                violation.setId(Integer.valueOf(jsonObject.getString("id")));
                                violation.setPlateNumber(jsonObject.getString("plate_number"));
                                violation.setViolationType(jsonObject.getString("violation_type"));
                                violation.setParkingArea(jsonObject.getString("area"));
                                violation.setViolationDate(jsonObject.getString("violation_date"));
                                violation.setCarModel(jsonObject.getString("car_model"));
                                violation.setCarColor(jsonObject.getString("car_color"));
                                violation.setCarMake(jsonObject.getString("car_make"));
                                violation.setAdditionalDetails(jsonObject.getString("additional_details"));
                                violationList.add(violation);
                            }

                            // Attach now the list of violation in our custom adapter
                            // Violation adapter class handles the displaying of the newly fetched
                            // records and also for the live searching of violation
                            violationAdapter = new ViolationAdapter(getActivity(), violationList);
                            violationAdapter.notifyDataSetChanged();
                            listView.setAdapter(violationAdapter);

                            // Attach long press event
                            listView.setOnItemLongClickListener(
                                    new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                               int position, long id) {
                                    Violation violation = violationList.get(position);
                                    Log.e(TAG, "Id: "+violation.getId());
                                    mActionMode = ((AppCompatActivity) getActivity()).
                                            startSupportActionMode(mActionModeCallback);

                                    // This is the selected record to be passed in the
                                    // toolbar menus edit or delete
                                    String tag[] = { violation.getViolationType(),
                                                    violation.getPlateNumber(),
                                                    violation.getParkingArea(),
                                                    String.valueOf(violation.getId()),
                                                    violation.getCarModel(),
                                                    violation.getCarColor(),
                                                    violation.getCarMake(),
                                                    violation.getAdditionalDetails()
                                                };
                                    mActionMode.setTag(tag);
                                    view.setSelected(true);
                                    return true;
                                }
                            });

                            // Attach single click listener
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Violation violation = violationList.get(position);
                                    Log.e(TAG, "Id: "+violation.getId());
                                    // Get all the selected record and store it in the array
                                    // This is the selected record to be passed in the other view
                                    String tag[] = { violation.getViolationType(),
                                            violation.getPlateNumber(),
                                            violation.getParkingArea(),
                                            String.valueOf(violation.getId()),
                                            violation.getCarModel(),
                                            violation.getCarColor(),
                                            violation.getCarMake(),
                                            violation.getAdditionalDetails()
                                    };

                                    // Display another view for viewing the record
                                    // This class ables you to edit directly
                                    Intent i = new Intent(getActivity(), EditViolation.class);
                                    i.putExtra("values", tag);
                                    startActivity(i);
                                }
                            });
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

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
        }
    }

    // Menu of edit, delete
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            // Call the menu which holds the edit, delete icon
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final String tag[] = (String[])mode.getTag();
            switch (item.getItemId()) {

                // Hey! You tap the edit icon
                case R.id.edit:
                    // Display the dialog for editing the violation
                    // Build a layout programmatically
                    LinearLayout layout = new LinearLayout(getActivity());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    // Create dynamically the edit text
                    final EditText txtPlateNum = new EditText(getActivity());
                    final EditText txtViolationType = new EditText(getActivity());
                    final EditText txtCarModel = new EditText(getActivity());
                    final EditText txtCarColor = new EditText(getActivity());
                    final EditText txtCarMake = new EditText(getActivity());
                    final EditText txtAdditionalDetails = new EditText(getActivity());

                    // Add hints in edit text
                    txtPlateNum.setHint(R.string.enter_plate_num);
                    txtViolationType.setHint(R.string.enter_violation);
                    txtCarModel.setHint(R.string.enter_car_model);
                    txtCarColor.setHint(R.string.enter_car_color);
                    txtCarMake.setHint(R.string.enter_car_make);
                    txtAdditionalDetails.setHint(R.string.enter_additional_details);

                    // Set selected record to the text fields
                    txtPlateNum.setText(tag[1]);
                    txtViolationType.setText(tag[0]);
                    txtCarModel.setText(tag[4]);
                    txtCarColor.setText(tag[5]);
                    txtCarMake.setText(tag[6]);
                    txtAdditionalDetails.setText(tag[7]);

                    // Attach the newly created edit texts in the alert dialog
                    layout.addView(txtPlateNum);
                    layout.addView(txtCarMake);
                    layout.addView(txtCarModel);
                    layout.addView(txtCarColor);
                    layout.addView(txtViolationType);
                    layout.addView(txtAdditionalDetails);

                    for (int i = 0; i < ParkingAreas.area.length; i++) {
                        if (ParkingAreas.area[i].equals(tag[2].trim())) {
                            index = i;
                            break;
                        }
                    }

                    // Create now the alert dialog
                    builder.setTitle("Edit Violation").setView(layout).
                            setSingleChoiceItems(ParkingAreas.area, index, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedArea.add(0, ParkingAreas.area[which]);
                                }
                            }).setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Update now the violation
                            parking.updateViolation(Integer.valueOf(tag[3]),
                                    txtPlateNum.getText().toString().trim(),
                                    txtViolationType.getText().toString().trim(),
                                    selectedArea.size() == 0 ? ParkingAreas.area[index].toString() :
                                            selectedArea.get(0).toString(),
                                            txtCarModel.getText().toString(), txtCarColor.getText().toString(),
                                            txtCarMake.getText().toString(),
                                            txtAdditionalDetails.getText().toString(),
                                            new Parking.Callback() {
                                        @Override
                                        public void successResponse(Object object) {
                                            Log.e(TAG, (String) object);
                                            Toast.makeText(getActivity(), R.string.success_edit,
                                                    Toast.LENGTH_SHORT).show();
                                            new executeTask().execute("");
                                        }

                                        @Override
                                        public void errorResponse(Object object) {
                                            Log.e(TAG, (String) object);
                                        }
                                    });
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                    mode.finish();
                    return true;

                // Hey! You tap the delete icon
                case R.id.delete:
                    // Create now the alert dialog
                    deleteBuilder.setTitle("Confirm").setMessage("Area you sure you want to delete this" +
                            " violation?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Delete the selected record
                            parking.deleteViolation(Integer.valueOf(tag[3]), new Parking.Callback() {
                                @Override
                                public void successResponse(Object object) {
                                    Toast.makeText(getActivity(), R.string.success_delete,
                                            Toast.LENGTH_SHORT).show();
                                    // If deletion of the selected record, reload the async task
                                    // to display the new records
                                    new executeTask().execute("");
                                }

                                @Override
                                public void errorResponse(Object object) {
                                    Log.e(TAG, (String) object);
                                }
                            });
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing if you click cancel
                        }
                    }).show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
}
