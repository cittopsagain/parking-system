package com.citparkingsystem;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.citparkingsystem.adapters.ViolationAdapter;
import com.citparkingsystem.encapsulate.Violation;
import com.citparkingsystem.requests.Parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave Tolentin on 7/27/2017.
 */

public class ViolationsFragment extends Fragment {

    private Parking parking;
    private ViolationAdapter violationAdapter;
    private List<Violation> violationList = new ArrayList<Violation>();
    private ListView listView;
    private MenuItem searchAction;
    private String currentFilter;

    private final static String TAG = ViolationsFragment.class.getSimpleName();

    public ViolationsFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parking = new Parking(getActivity());
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
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentFilter = !TextUtils.isEmpty(newText) ? newText : null;
                violationAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // searchAction = menu.findItem(R.id.action_search);
        super.onPrepareOptionsMenu(menu);
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
                    Log.e(TAG, "Error: "+(String) object);
                }
            });
            return null;
        }
    }
}
