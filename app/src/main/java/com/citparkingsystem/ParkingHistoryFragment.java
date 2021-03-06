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
import android.widget.ListView;
import android.widget.Toast;

import com.citparkingsystem.adapters.ParkingHistoryAdapter;
import com.citparkingsystem.encapsulate.ParkingArea;
import com.citparkingsystem.lib.StringHelper;
import com.citparkingsystem.requests.Parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Walter Ybanez on 8/13/2017.
 */

public class ParkingHistoryFragment extends Fragment {

    private Parking parking;
    private ParkingHistoryAdapter parkingHistoryAdapter;
    private ArrayList<ParkingArea> parkingAreas = new ArrayList<ParkingArea>();
    private ListView listView;
    private AlertDialog.Builder builder;

    private final static String TAG = ParkingHistoryFragment.class.getSimpleName();

    public ParkingHistoryFragment() {

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
        View rootView = inflater.inflate(R.layout.fragment_parking_history, container, false);
        listView = (ListView) rootView.findViewById(R.id.parking_history_list_view_id);
        new executeTask().execute();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_parking_history, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search_parking_history).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                parkingHistoryAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_advanced_search) {
            builder.setTitle("Advanced Search").setPositiveButton("Search",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    class executeTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            parking.getParkingHistory(new Parking.Callback() {

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
                                ParkingArea parkingArea = new ParkingArea();
                                parkingArea.setArea(StringHelper.toTheUpperCaseSingle(
                                        jsonObject.getString("area").trim()+" area"));
                                parkingArea.setDateTimePark(jsonObject.getString("date_time_park"));
                                parkingAreas.add(parkingArea);
                            }
                            parkingHistoryAdapter = new ParkingHistoryAdapter(getActivity(),
                                    parkingAreas);
                            parkingHistoryAdapter.notifyDataSetChanged();
                            listView.setAdapter(parkingHistoryAdapter);
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
