package com.example.dobitnarae;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class ReserveListRecyclerAdapter extends RecyclerView.Adapter<ReserveListRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<Reserve> reserves;
    SimpleDateFormat dateFormat;
    Drawable acceptFlg, pendingFlg, rejectFlg;
    ArrayList<Store> stores;
    int[] storeID;

    public ReserveListRecyclerAdapter(Context context, ArrayList<Reserve> reserves) {
        this.context = context;
        this.reserves = reserves;

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        acceptFlg = context.getResources().getDrawable(R.drawable.border_all_layout_item_gray);
        pendingFlg = context.getResources().getDrawable(R.drawable.border_all_layout_item_green);
        rejectFlg = context.getResources().getDrawable(R.drawable.border_all_layout_item_red);

        stores = new ArrayList<>();
        for(Reserve reserve : reserves){
            Store tmp = JSONTask.getInstance().getAdminStoreAll(reserve.getAdmin_id()).get(0);
            stores.add(tmp);
        }

        storeID = new int[reserves.size()];
        for(int i=0; i<storeID.length; i++){
            storeID[i] = JSONTask.getInstance().changeStoreID(reserves.get(i).getAdmin_id());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.component_reserve_list_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Reserve item = reserves.get(position);
        Store store = stores.get(position);

        ServerImg.getStoreImageGlide(context, storeID[position], holder.image);

        holder.name.setText(store.getName());

        String reserveTime = item.getRentalDate();
        holder.time.setText(reserveTime);
        holder.storeView.setId(item.getId());

        Drawable successLayoutDrawable;
        int successStatus = item.getAcceptStatus();

        if(successStatus == 0){
            holder.successText.setText("대기");
            holder.successText.setTextColor(Color.parseColor("#8f8f8f"));
            successLayoutDrawable = pendingFlg;
        }
        else if(successStatus == 1){
            holder.successText.setText("승인");
            holder.successText.setTextColor(Color.parseColor("#339738"));
            successLayoutDrawable = acceptFlg;
        }
        else {
            holder.successText.setText("거절");
            holder.successText.setTextColor(Color.parseColor("#f94c4c"));
            successLayoutDrawable = rejectFlg;
        }
        holder.reserveSuccessBorder.setBackground(successLayoutDrawable);

        holder.storeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReservationInfoActivity.class);
                intent.putExtra("reserveInfo", item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.reserves.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, time, successText;
        LinearLayout storeView, reserveSuccessBorder;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.reserve_list_img);
            name = (TextView) itemView.findViewById(R.id.reserve_name);
            time = (TextView) itemView.findViewById(R.id.reserve_time);
            storeView = (LinearLayout) itemView.findViewById(R.id.reserve_list_item);
            successText = (TextView) itemView.findViewById(R.id.reserve_success_text);
            reserveSuccessBorder = (LinearLayout)itemView.findViewById(R.id.reserve_success_layout);
        }
    }

    public void setReserves(ArrayList<Reserve> reserves) {
        this.reserves = reserves;
        this.notifyDataSetChanged();
    }
}