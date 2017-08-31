package com.citparkingsystem;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.citparkingsystem.adapters.MenuAdapter;
import com.citparkingsystem.encapsulate.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave Tolentin on 7/24/2017.
 */

public class MenuFragment extends Fragment implements View.OnClickListener,
        MenuAdapter.CallFragment {

    private final static String TAG = MenuFragment.class.getSimpleName();
    private List<Menu> albumList;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;

    public MenuFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        adapter = new MenuAdapter(getActivity(), albumList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareAlbums();

        return rootView;
    }

    @Override
    public void albumClick(int position) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.academic_area);
        ParkingAreaFragment parkingAreaFragment = new ParkingAreaFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("keyParkingArea", position);
        parkingAreaFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.container_body_frame_layout_id, parkingAreaFragment);
        fragmentTransaction.commit();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * Adding few albums for testing
     */
    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.ic_academic_area,
                R.drawable.ic_academic_area,
                R.drawable.ic_academic_area,
                R.drawable.ic_academic_area,
                R.drawable.ic_academic_area
        };

        Menu a = new Menu("Academic Area", 13, covers[0]);
        albumList.add(a);

        a = new Menu("Area 1", 8, covers[1]);
        albumList.add(a);

        a = new Menu("Area 2", 11, covers[2]);
        albumList.add(a);

        a = new Menu("Area 3", 12, covers[3]);
        albumList.add(a);

        a = new Menu("Area 4", 14, covers[4]);
        albumList.add(a);

        adapter.notifyDataSetChanged();
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics()));
    }
}
