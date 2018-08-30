package com.example.dobitnarae;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class ItemManagementFragment extends Fragment{
    private ArrayList<Clothes> originItems, items;
    private ItemListRecyclerAdapter mAdapter;
    private ItemCategoryListRecyclerAdapter cAdapter;
    private Store store;
    public ArrayList<Clothes> deleteList;

    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> dataList;

    public ItemManagementFragment(Store store) {
        this.store = store;
        this.originItems = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
        this.items = getClothesList(0);
        deleteList = new ArrayList<Clothes>();
    }

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static ItemManagementFragment newInstance(int sectionNumber, Store store) {
        ItemManagementFragment fragment = new ItemManagementFragment(store);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_management_item, container, false);

        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_clothes);
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ItemListRecyclerAdapter(getActivity(), items, store, R.layout.fragment_store_clothes_list){
            @Override
            public void onBindViewHolder(final ViewHolder holder, final int position) {
                super.onBindViewHolder(holder, position);

                holder.cardview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.clicked == 0) {
                            holder.clicked = 1;
                            if(!deleteList.contains(items.get(position)))
                                deleteList.add(items.get(position));
                            holder.layout_cardview.setBackgroundResource(R.drawable.cardview_border);
                        }
                        else {
                            holder.clicked = 0;
                            if(deleteList.contains(items.get(position)))
                                deleteList.remove(items.get(position));
                            holder.layout_cardview.setBackgroundResource(R.drawable.cardview_bordernone);
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(mAdapter);

        RecyclerView recyclerViewCategory = (RecyclerView) rootView.findViewById(R.id.clothes_category);
        LinearLayoutManager layoutManagerCategory = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategory.setLayoutManager(layoutManagerCategory);

        cAdapter = new ItemCategoryListRecyclerAdapter(getContext(), originItems, mAdapter);
        recyclerViewCategory.setAdapter(cAdapter);

        // 당겨서 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataRefresh();

                // 새로고침 완료
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        // 스피너 드롭다운
        dataList = new ArrayList<String>();
        dataList.add("메       뉴");
        dataList.add("추       가");
        dataList.add("삭       제");
        dataList.add("새로고침");

        spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, dataList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v =  super.getView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(getActivity().getAssets(), "font/NanumSquareR.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(18);

                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v =  super.getDropDownView(position, convertView, parent);

                Typeface externalFont = Typeface.createFromAsset(getActivity().getAssets(), "font/NanumSquareR.ttf");
                ((TextView) v).setTypeface(externalFont);
                v.setBackgroundColor(Color.WHITE);
                ((TextView) v).setTextColor(Color.BLACK);
                ((TextView) v).setGravity(Gravity.CENTER);

                return v;
            }
        };
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        final Spinner spinner = ((AdminActivity)getActivity()).getSpinner();
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinner.getItemIdAtPosition(position) == 1){
                    // 추가
                    Intent intent = new Intent(getContext(), ItemAddActivity.class);
                    intent.putExtra("store", store);
                    startActivity(intent);
                    refresh();
                } else if(spinner.getItemIdAtPosition(position) == 2){
                    // 삭제
                    if(deleteList.size()!=0) {
                        originItems = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
                        for (Clothes tmp : deleteList) {
                            originItems.remove(tmp);
                            JSONTask.getInstance().deleteCloth(tmp.getCloth_id());
                        }
                        items = originItems;
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), deleteList.size() + "개 항목이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        deleteList.clear();
                        refresh();
                    } else {
                        Toast.makeText(getActivity(), "삭제할 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else if(spinner.getItemIdAtPosition(position) == 3) {
                    // 새로고침
                    dataRefresh();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    public ArrayList<Clothes> getClothesList(int category){
        ArrayList<Clothes> tmp = new ArrayList<>();
        if(category == 0)
            return originItems;

        for(int i=0; i<originItems.size(); i++){
            Clothes item = originItems.get(i);
            if(item.getCategory() == category)
                tmp.add(item);
        }
        return tmp;
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    public void dataRefresh(){
        // 새로고침
        originItems = JSONTask.getInstance().getClothesAll(store.getAdmin_id());
        items = getClothesList(0);
        mAdapter.setClothes(items);
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "새로고침 되었습니다.", Toast.LENGTH_SHORT).show();
        refresh();
    }
}
