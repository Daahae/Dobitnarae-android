package com.example.dobitnarae;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class OrderFragmentManagementFragment2 extends Fragment{
    private ArrayList<Order> originItems, items;
    private OrderListRecyclerAdapter2 mAdapter = null;

    private Store store;

    public OrderFragmentManagementFragment2(Store store) {
        this.store = store;
        originItems = JSONTask.getInstance().getOrderAdminAll(store.getAdmin_id());
        this.items = new ArrayList<Order>();

        for (Order item:originItems) {
            if(item.getAcceptStatus()==1 || item.getAcceptStatus()==2)
                items.add(item);
        }
        for (Order item:items)
            item.setBasket(JSONTask.getInstance().getBascketCustomerAll(item.getOrderNo()));
    }

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static OrderFragmentManagementFragment2 newInstance(int sectionNumber, Store store) {
        OrderFragmentManagementFragment2 fragment = new OrderFragmentManagementFragment2(store);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_management_fragment_order, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_order_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new OrderListRecyclerAdapter2(getContext(), items, store);
        recyclerView.setAdapter(mAdapter);

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataUpdate();
                refresh();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    public void dataUpdate(){
        originItems = JSONTask.getInstance().getOrderAdminAll(store.getAdmin_id());
        this.items = new ArrayList<Order>();

        for (Order item:originItems) {
            if(item.getAcceptStatus()==1 || item.getAcceptStatus()==2)
                items.add(item);
        }
        for (Order item:items)
            item.setBasket(JSONTask.getInstance().getBascketCustomerAll(item.getOrderNo()));

        mAdapter.setOrders(items);
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
