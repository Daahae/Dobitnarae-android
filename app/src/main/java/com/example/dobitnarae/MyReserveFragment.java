package com.example.dobitnarae;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyReserveFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<Reserve> reserves = null;
    private ReserveListRecyclerAdapter mAdapter = null;
    public static boolean changeFlg = false;
    private View rootView;
    private RecyclerView recyclerView;
    private boolean isAlreadyLoad;

    public MyReserveFragment() {
        isAlreadyLoad = false;
    }

    public static MyReserveFragment newInstance(int sectionNumber) {
        MyReserveFragment fragment = new MyReserveFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_reserve, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_reserve_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if(reserves == null)
            reserves = new ArrayList<>();
        mAdapter = new ReserveListRecyclerAdapter(getContext(), reserves);
        recyclerView.setAdapter(mAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout_reserve_list);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reserves = getReserves();
                swipeRefreshLayout.setRefreshing(false);
                refresh();
            }
        });

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 현재 탭 일경우 로딩
        if (isVisibleToUser && !isAlreadyLoad) {
            isAlreadyLoad = true;

            reserves = getReserves();
            mAdapter.setReserves(reserves);
        }
    }

    private ArrayList<Reserve> getReserves() {
        return JSONTask.getInstance().getCustomerReservationList(Account.getInstance().getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (changeFlg) {
            refresh();
            changeFlg = false;
        }
    }

    private void refresh() {
        reserves = getReserves();
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

}
