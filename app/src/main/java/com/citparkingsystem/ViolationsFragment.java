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
import com.citparkingsystem.encapsulate.ParkingArea;
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
    private ListView listView;
    private AlertDialog.Builder builder;
    private AlertDialog.Builder deleteBuilder;
    private ActionMode mActionMode;
    private ArrayList selectedArea = new ArrayList();
    private int index = 0;

    private final static String TAG = ViolationsFragment.class.getSimpleName();

    public ViolationsFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parking = new Parking(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(),
                    android.R.style.Theme_Material_Light_Dialog_Alert);
            deleteBuilder = new AlertDialog.Builder(getActivity(),
                    android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
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
        View rootView = inflater.inflate(R.layout.fragment_violations, container, false);
        listView = (ListView) rootView.findViewById(R.id.violations_list_view_id);
        new executeTask().execute("");
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
            ArrayList<String> u = new ArrayList<>();
            for (int i = 0; i < ParkingAreas.area.length; i++) {
                u.add(i, StringHelper.toTheUpperCaseSingle(ParkingAreas.area[i].toString().trim()));
            }
            CharSequence[] upperCaseArea = u.toArray(new CharSequence[u.size()]);
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText txtPlateNum = new EditText(getActivity());
            final EditText txtViolationType = new EditText(getActivity());
            final EditText txtCarModel = new EditText(getActivity());
            final EditText txtCarColor = new EditText(getActivity());
            final EditText txtCarMake = new EditText(getActivity());
            final EditText txtAdditionalDetails = new EditText(getActivity());

            txtPlateNum.setHint(R.string.enter_plate_num);
            txtViolationType.setHint(R.string.enter_violation);
            txtCarModel.setHint(R.string.enter_car_model);
            txtCarColor.setHint(R.string.enter_car_color);
            txtCarMake.setHint(R.string.enter_car_make);
            txtAdditionalDetails.setHint(R.string.enter_additional_details);

            layout.addView(txtPlateNum);
            layout.addView(txtCarMake);
            layout.addView(txtCarModel);
            layout.addView(txtCarColor);
            layout.addView(txtViolationType);
            layout.addView(txtAdditionalDetails);
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
                            String plateNum = txtPlateNum.getText().toString();
                            String violation = txtViolationType.getText().toString();
                            String carModel = txtCarModel.getText().toString();
                            String carColor = txtCarColor.getText().toString();
                            String carMake = txtCarMake.getText().toString();
                            String additionalDetails = txtAdditionalDetails.getText().toString();
                            if (plateNum.trim().length() > 0 || violation.trim().length() > 0) {
                                parking.addViolation(plateNum, violation,
                                        selectedArea.size() == 0 ? ParkingAreas.area[0].toString() :
                                                selectedArea.get(0).toString(), carModel, carColor,
                                        carMake, additionalDetails, new Parking.Callback() {
                                    @Override
                                    public void successResponse(Object object) {
                                        Log.e(TAG, (String) object);
                                        Toast.makeText(getActivity(), R.string.success_save,
                                                Toast.LENGTH_SHORT).show();
                                        new executeTask().execute("");
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
        } else if (item.getItemId() == R.id.menu_sort_by_violation) {
            new executeTask().execute("violation_type");
        } else if (item.getItemId() == R.id.menu_sort_by_area) {
            new executeTask().execute("area");
        }
        return super.onOptionsItemSelected(item);
    }

    class executeTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViolationsFragment.this.getActivity());
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String sortBy = strings[0];
            parking.getViolations(sortBy, new Parking.Callback() {

                @Override
                public void successResponse(Object object) {
                    violationList.clear();
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
                            violationAdapter = new ViolationAdapter(getActivity(), violationList);
                            violationAdapter.notifyDataSetChanged();
                            listView.setAdapter(violationAdapter);
                            listView.setOnItemLongClickListener(
                                    new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                               int position, long id) {
                                    Violation violation = violationList.get(position);
                                    Log.e(TAG, "Id: "+violation.getId());
                                    mActionMode = ((AppCompatActivity) getActivity()).
                                            startSupportActionMode(mActionModeCallback);
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
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Violation violation = violationList.get(position);
                                    Log.e(TAG, "Id: "+violation.getId());
                                    String tag[] = { violation.getViolationType(),
                                            violation.getPlateNumber(),
                                            violation.getParkingArea(),
                                            String.valueOf(violation.getId()),
                                            violation.getCarModel(),
                                            violation.getCarColor(),
                                            violation.getCarMake(),
                                            violation.getAdditionalDetails()
                                    };
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

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
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

                case R.id.edit:
                    LinearLayout layout = new LinearLayout(getActivity());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText txtPlateNum = new EditText(getActivity());
                    final EditText txtViolationType = new EditText(getActivity());
                    final EditText txtCarModel = new EditText(getActivity());
                    final EditText txtCarColor = new EditText(getActivity());
                    final EditText txtCarMake = new EditText(getActivity());
                    final EditText txtAdditionalDetails = new EditText(getActivity());

                    txtPlateNum.setHint(R.string.enter_plate_num);
                    txtViolationType.setHint(R.string.enter_violation);
                    txtCarModel.setHint(R.string.enter_car_model);
                    txtCarColor.setHint(R.string.enter_car_color);
                    txtCarMake.setHint(R.string.enter_car_make);
                    txtAdditionalDetails.setHint(R.string.enter_additional_details);

                    txtPlateNum.setText(tag[1]);
                    txtViolationType.setText(tag[0]);
                    txtCarModel.setText(tag[4]);
                    txtCarColor.setText(tag[5]);
                    txtCarMake.setText(tag[6]);
                    txtAdditionalDetails.setText(tag[7]);

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
                    builder.setTitle("Edit Violation").setView(layout).
                            setSingleChoiceItems(ParkingAreas.area, index, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    selectedArea.add(0, ParkingAreas.area[which]);
                                }
                            }).setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                case R.id.delete:
                    deleteBuilder.setTitle("Confirm").setMessage("Area you sure you want to delete this" +
                            " violation?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parking.deleteViolation(Integer.valueOf(tag[3]), new Parking.Callback() {
                                @Override
                                public void successResponse(Object object) {
                                    Toast.makeText(getActivity(), R.string.success_delete,
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
