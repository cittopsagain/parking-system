package com.citparkingsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.citparkingsystem.R;
import com.citparkingsystem.encapsulate.ParkingArea;

import java.util.ArrayList;

/**
 * Created by Dave Tolentin on 8/13/2017.
 */

public class ParkingHistoryAdapter extends BaseAdapter implements Filterable {

    private final static String TAG = ViolationAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ParkingArea> parkingAreas;
    private ArrayList<ParkingArea> mStringFilterList;
    private ValueFilter valueFilter;

    public ParkingHistoryAdapter(Context context, ArrayList<ParkingArea> parkingAreas) {
        super();
        this.context = context;
        this.parkingAreas = parkingAreas;
        mStringFilterList = parkingAreas;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return parkingAreas.size();
    }

    @Override
    public Object getItem(int i) {
        return parkingAreas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return parkingAreas.indexOf(getItem(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_parking_history, null);
        }

        ParkingArea parkingArea = parkingAreas.get(position);

        TextView txtArea = (TextView) convertView.findViewById(R.id.area_text_view_id);
        TextView txtDateTimePark = (TextView)
                convertView.findViewById(R.id.date_time_park_text_view_id);

        txtArea.setText(parkingArea.getArea());
        txtDateTimePark.setText(parkingArea.getDateTimePark());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
            valueFilter.context(this.context);
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {

        Context context;
        public Context context (Context context) {
            this.context = context;
            return null;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<ParkingArea> filterList = new ArrayList<ParkingArea>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getArea().toUpperCase())
                            .contains(constraint.toString().toUpperCase()) ||
                            (mStringFilterList.get(i).getDateTimePark().toUpperCase())
                                    .contains(constraint.toString().toUpperCase())) {

                        ParkingArea parkingArea = new ParkingArea();
                        parkingArea.setArea(mStringFilterList.get(i)
                                .getArea());
                        parkingArea.setDateTimePark(mStringFilterList.get(i)
                                .getDateTimePark());
                        filterList.add(parkingArea);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            parkingAreas = (ArrayList<ParkingArea>) results.values;
            if (parkingAreas.size() == 0) {
                Toast.makeText(this.context, "No results found for "+constraint+"!",
                        Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        }
    }
}
