package com.example.dobitnarae;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemListRecyclerAdapter extends RecyclerView.Adapter<ItemListRecyclerAdapter.ViewHolder> {
    Context context;
    List<Clothes> clothes;
    int item_layout;
    Store store;

    public ItemListRecyclerAdapter(Context context, List<Clothes> items, Store store, int item_layout) {
        this.context = context;
        this.clothes = items;
        this.store = store;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Clothes item = clothes.get(position);
        // TODO  서버에서 이미지 받아야함
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.gobchang);
        holder.image.setBackground(drawable);
        holder.name.setText(item.getName());
        holder.price.setText("" + item.getPrice());
        holder.cardview.setId(item.getCloth_id());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClothesReservationActivity.class);
                intent.putExtra("clothes", item);
                intent.putExtra("store", store);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.clothes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.clothes_name);
            price = (TextView) itemView.findViewById(R.id.clothes_price);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }

    public void setClothes(List<Clothes> clothes) {
        this.clothes = clothes;
        this.notifyDataSetChanged();
    }
}