package com.citparkingsystem.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.citparkingsystem.R;
import com.citparkingsystem.encapsulate.Violation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dave Tolentin on 7/27/2017.
 */

public class ViolationAdapter extends BaseAdapter implements Filterable {

    private final static String TAG = ViolationAdapter.class.getSimpleName();
    private Context context;
    private LayoutInflater inflater;
    private List<Violation> violationList;

    public ViolationAdapter(Context context, List<Violation> violationList) {
        this.context = context;
        this.violationList = violationList;
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
        return getItem(i).hashCode();
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
        TextView txtViolationType = (TextView) view.findViewById(R.id.violation_type_text_view_id);

        txtPlateNo.setText(violation.getPlateNumber());
        txtViolationType.setText(violation.getViolationType());

        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results=new FilterResults();
                if(charSequence.toString()!=null && charSequence.toString().length()>0){
                    ArrayList<Violation> filterList=new ArrayList<Violation>();
                    for(int i=0;i<violationList.size();i++){
                        if((violationList.get(i).getPlateNumber().toUpperCase())
                                .contains(charSequence.toString().toUpperCase())) {
                            Violation contacts = new Violation();
                            contacts.setViolationType(violationList.get(i).getViolationType());
                            contacts.setPlateNumber(violationList.get(i).getPlateNumber());
                            filterList.add(contacts);
                        }
                    }
                    results.count=filterList.size();
                    results.values=filterList;
                }else{
                    results.count=violationList.size();
                    results.values=violationList;
                }
                return results;
            }

            //Invoked in the UI thread to publish the filtering results in the user interface.
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                violationList = (ArrayList<Violation>) filterResults.values;
                notifyDataSetChanged();
                Log.e(TAG, "Filter: "+filterResults);
            }
        };
    }
}
