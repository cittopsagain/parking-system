package com.citparkingsystem.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.citparkingsystem.R;
import com.citparkingsystem.encapsulate.Menu;

import java.util.List;

/**
 * Created by Dave Tolentin on 8/13/2017.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private Context mContext;
    private List<Menu> menuList;
    private static String TAG = MenuAdapter.class.getSimpleName();

    public interface CallFragment {
        void albumClick(int position);
    }

    private CallFragment callFragment = null;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            // overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public MenuAdapter(Context mContext, List<Menu> menuList, CallFragment callFragment) {
        this.mContext = mContext;
        this.menuList = menuList;
        this.callFragment = callFragment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Menu menu = menuList.get(position);
        holder.title.setText(menu.getName());
        holder.count.setText(menu.getSlots()+" available out of "+menu.getMaxSlots()+" slots");
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the condition if the client gives images of the remaining parking areas
                // and its slots

                // As of now only the assigned area of my client (Highschool Area)
                if (position == 3) {
                    callFragment.albumClick(position);
                }
            }
        });
        // Loading album cover using Glide library
        Glide.with(mContext).load(menu.getThumbnail()).into(holder.thumbnail);

        /*holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });*/
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        /*PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();*/
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }
}
