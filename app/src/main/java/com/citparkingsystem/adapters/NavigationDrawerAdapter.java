package com.citparkingsystem.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citparkingsystem.R;
import com.citparkingsystem.encapsulate.NavigationDrawer;

import java.util.Collections;
import java.util.List;

/**
 * Created by Dave Tolentin on 7/24/2017.
 */

public class NavigationDrawerAdapter extends RecyclerView.Adapter
        <NavigationDrawerAdapter.MyViewHolder> {
    List<NavigationDrawer> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private int mIcons[];
    private String counter[];

    public NavigationDrawerAdapter(Context context, List<NavigationDrawer> data, int icons[]) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.mIcons = icons;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.navigation_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavigationDrawer current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.imageView.setImageResource(mIcons[position]);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
        }
    }
}