package com.example.dobitnarae;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyReserveFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<Reserve> reserves;
    private ReserveListRecyclerAdapter mAdapter;

    public MyReserveFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_my_reserve, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_reserve_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        reserves = JSONTask.getInstance().getCustomerReservationList(Account.getInstance().getId());

        mAdapter = new ReserveListRecyclerAdapter(getContext(), reserves);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    // 옷
    public void cancelReservation(int reservationID){
        for(Reserve item : reserves){
            if(item.getId() == reservationID) {
                reserves.remove(item);
                break;
            }
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
