package com.citparkingsystem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.citparkingsystem.adapters.ParkingHistoryAdapter;
import com.citparkingsystem.adapters.ViolationAdapter;
import com.citparkingsystem.encapsulate.ParkingArea;
import com.citparkingsystem.encapsulate.Violation;
import com.citparkingsystem.requests.Parking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dave Tolentin on 8/13/2017.
 */

public class ParkingHistoryFragment extends Fragment {

    private Parking parking;
    private ParkingHistoryAdapter parkingHistoryAdapter;
    private ArrayList<ParkingArea> parkingAreas = new ArrayList<ParkingArea>();
    private ListView listView;

    private final static String TAG = ParkingHistoryFragment.class.getSimpleName();

    public ParkingHistoryFragment() {

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
                                parkingArea.setArea(jsonObject.getString("area"));
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
