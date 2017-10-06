package com.citparkingsystem.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.citparkingsystem.R;
import com.citparkingsystem.encapsulate.Violation;
import com.citparkingsystem.lib.StringHelper;

import java.util.ArrayList;

/**
 * Created by Walter Ybanez on 7/27/2017.
 */

public class ViolationAdapter extends BaseAdapter implements Filterable {

    private final static String TAG = ViolationAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Violation> violationList;
    private ArrayList<Violation> mStringFilterList;
    private ValueFilter valueFilter;

    public ViolationAdapter(Context context, ArrayList<Violation> violationList) {
        super();
        this.context = context;
        this.violationList = violationList;
        mStringFilterList = violationList;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
            valueFilter.context(this.context);
        }
        return valueFilter;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return violationList.size();
    }

    @Override
    public Object getItem(int i) {
        return violationList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return violationList.indexOf(getItem(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.activity_violations, null);
        }

        Violation violation = violationList.get(i);

        TextView txtPlateNo = (TextView) view.findViewById(R.id.plate_no_text_view_id);
        Typeface font = Typeface.createFromAsset(this.context.getAssets(), "fonts/Roboto-Thin.ttf");
        // txtPlateNo.setTypeface(font);
        TextView txtViolationType = (TextView) view.findViewById(R.id.violation_type_text_view_id);
        TextView txtParkingArea = (TextView) view.findViewById(R.id.parking_area_text_view_id);
        TextView txtCarModel = (TextView) view.findViewById(R.id.car_model_text_view_id);
        TextView txtCarColor = (TextView) view.findViewById(R.id.car_color_text_view_id);
        TextView txtCarMake = (TextView) view.findViewById(R.id.car_make_text_view_id);
        TextView txtAdditionalDetails = (TextView) view.findViewById(R.id.additional_details_text_view_id);
        TextView txtDateTimeViolation = (TextView) view.findViewById(
                R.id.date_time_violation_text_view_id);

        txtPlateNo.setText(violation.getPlateNumber());
        txtViolationType.setText(violation.getViolationType());
        txtParkingArea.setText(violation.getParkingArea());
        txtCarModel.setText(violation.getCarModel());
        txtCarColor.setText(violation.getCarColor());
        txtCarMake.setText(violation.getCarMake());
        txtAdditionalDetails.setText(violation.getAdditionalDetails());
        txtDateTimeViolation.setText(violation.getViolationDate());

        return view;
    }

    // Handles the live search in the list view
    private class ValueFilter extends Filter {
        Context context;
        public Context context (Context context) {
            this.context = context;
            return null;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults(); // Holds the result of your search

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Violation> filterList = new ArrayList<Violation>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getPlateNumber().toUpperCase())
                            .contains(constraint.toString().toUpperCase()) ||
                            (mStringFilterList.get(i).getViolationType().toUpperCase())
                            .contains(constraint.toString().toUpperCase()) ||
                            (mStringFilterList.get(i).getParkingArea().toUpperCase())
                                    .contains(constraint.toString().toUpperCase())
                            ) {
                        // Results
                        Violation violation = new Violation();
                        violation.setParkingArea(mStringFilterList.get(i).getParkingArea());
                        violation.setPlateNumber(mStringFilterList.get(i).getPlateNumber());
                        violation.setViolationType(mStringFilterList.get(i).getViolationType());
                        violation.setViolationDate(mStringFilterList.get(i).getViolationDate());
                        filterList.add(violation);
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
            violationList = (ArrayList<Violation>) results.values;
            if (violationList.size() == 0) {
                Toast.makeText(this.context, "No results found for "+constraint+"!",
                        Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        }
    }
}
