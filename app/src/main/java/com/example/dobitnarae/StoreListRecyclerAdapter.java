package com.example.dobitnarae;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class StoreListRecyclerAdapter extends RecyclerView.Adapter<StoreListRecyclerAdapter.ViewHolder> {
    Context context;
    ArrayList<Store> stores;

    public StoreListRecyclerAdapter(Context context, ArrayList<Store> stores) {
        this.context = context;
        this.stores = stores;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.component_store_list_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO 뷰 형태 바꿔야함
        final Store item = stores.get(position);

        ServerImg.getStoreImageGlide(context, item.getId(), holder.image);
        holder.name.setText(item.getName());
        holder.address.setText(item.getAddress());
        holder.storeView.setId(item.getId());
        holder.storeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StoreActivity.class);
                intent.putExtra("store", item);
                context.startActivity(intent);
            }
        });

        // 영업정보 설정
//        try {
//            Date startTime = new SimpleDateFormat("hh:mm").parse(item.getStartTime());
//            Date endTime = new SimpleDateFormat("hh:mm").parse(item.getStartTime());;
//            Date today = new Date();
//
//            if(startTime.after(today) && endTime.before(today)){
//                Log.e("ㅁㄴㅇㄻㄴ", "영업중");
//            }
//            else
//                Log.e("ㅁㄴㅇㄻㄴ", "영업종료");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public int getItemCount() {
        return this.stores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, address, storeInfoText;
        LinearLayout storeView, storeInfoLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.store_list_img);
            name = (TextView) itemView.findViewById(R.id.store_name);
            address = (TextView) itemView.findViewById(R.id.store_address);
            storeView = (LinearLayout) itemView.findViewById(R.id.store_list_item);
            storeInfoLayout = (LinearLayout) itemView.findViewById(R.id.store_opening_info_border);
            storeInfoText = (TextView) itemView.findViewById(R.id.store_opening_info_text);
        }
    }

    public void setStores(ArrayList<Store> stores) {
        this.stores = stores;
        this.notifyDataSetChanged();
    }
}