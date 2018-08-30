package com.example.dobitnarae;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;

    private Store store;
    private Basket basket;

    public OrderFragmentManagementFragment2(Store store) {
        this.store = store;
        originItems = JSONTask.getInstance().getOrderAdminAll(store.getAdmin_id());
        this.items = new ArrayList<Order>();

        for (Order item:originItems) {
            if(item.getAcceptStatus()==1 || item.getAcceptStatus()==2)
                items.add(item);
        }

        this.basket = Basket.getInstance();
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

        mListView = (ListView) rootView.findViewById(R.id.listView);

        mAdapter = new ListViewAdapter(getContext());
        mListView.setAdapter(mAdapter);

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataUpdate();
                refresh();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        for (Order item:items) {
            mAdapter.addItem(item);
        }

        return rootView;
    }

    private class ViewHolder {
        public LinearLayout linearLayout;
        public String mNo;
        public ImageView iv_main;
        public TextView tv_basket;
        public TextView tv_date;
        public LinearLayout layout_accept;
        public TextView tv_accept;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<OrderInfoData> mListData = new ArrayList<OrderInfoData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.component_listview_order, null);

                holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.order_list_item);
                holder.iv_main = (ImageView) convertView.findViewById(R.id.order_list_img);
                holder.tv_basket = (TextView) convertView.findViewById(R.id.order_basket);
                holder.tv_date = (TextView) convertView.findViewById(R.id.order_date);
                holder.layout_accept = (LinearLayout) convertView.findViewById(R.id.order_accept_layout);
                holder.tv_accept = (TextView) convertView.findViewById(R.id.order_accept_tv);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            final OrderInfoData mData = mListData.get(position);

            // 서버에서 이미지 받아야함
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.gobchang);

            holder.mNo = mData.getOrderNo();
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OrderSpecificActivity.class);
                    intent.putExtra("order", position);
                    intent.putExtra("id", 1);
                    intent.putExtra("store", store);
                    mContext.startActivity(intent);
                }
            });
            holder.iv_main.setBackground(drawable);
            holder.tv_basket.setText(mData.getOrderBasket());
            holder.tv_date.setText(mData.getOrderDate());

            if(mData.getOrderAccept()==0) {
                holder.layout_accept.setBackgroundResource(R.drawable.border_all_layout_item_gray);
                holder.tv_accept.setText("승인 대기");
                holder.tv_accept.setTextColor(getResources().getColor(R.color.contentColor));
            } else if(mData.getOrderAccept()==1) {
                holder.layout_accept.setBackgroundResource(R.drawable.border_all_layout_item_green);
                holder.tv_accept.setText("승인");
                holder.tv_accept.setTextColor(getResources().getColor(R.color.storeOpeningColor));
            } else if(mData.getOrderAccept()==2) {
                holder.layout_accept.setBackgroundResource(R.drawable.border_all_layout_item_red);
                holder.tv_accept.setText("거절");
                holder.tv_accept.setTextColor(getResources().getColor(R.color.storeClosingColor));
            }

            return convertView;
        }

        public void addItem(Order item){
            OrderInfoData addInfo = null;

            int sum = 0;
            for(int i = 0; i < basket.getBasket().size(); i++){
                sum += basket.getBasket().get(i).getCnt() ;
            }
            sum -= 1;

            addInfo = new OrderInfoData();
            addInfo.setOrderNo(String.valueOf(item.getOrderNo()));
            // 고객 아이디가 아닌 고객 이름을 보여지게 해야함
            addInfo.setOrderName(item.getUserID());
            // 고객 주문데이터로 수정필요
            if(basket.getBasket().size()!=0 && sum != 0)
                addInfo.setOrderBasket(basket.getBasket().get(0).getClothes().getName() + " 외 " + sum + "벌");
            else if(sum==0)
                addInfo.setOrderBasket(basket.getBasket().get(0).getClothes().getName() + " 1벌");
            else
                addInfo.setOrderBasket("비어있음");
            addInfo.setOrderDate(item.getOrderDate());
            addInfo.setOrderAccept(item.getAcceptStatus());
            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }


        public void clear(){
            mListData.clear();
            dataChange();
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }
    }

    public void dataUpdate(){
        originItems = JSONTask.getInstance().getOrderAdminAll(store.getAdmin_id());
        this.items = new ArrayList<Order>();

        for (Order item:originItems) {
            if(item.getAcceptStatus()==1 || item.getAcceptStatus()==2)
                items.add(item);
        }

        mAdapter.clear();
        for (Order item:items)
            mAdapter.addItem(item);
        mAdapter.notifyDataSetChanged();
    }

    public void refresh(){
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
