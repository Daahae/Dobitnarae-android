package com.example.dobitnarae.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dobitnarae.Clothes;
import com.example.dobitnarae.ClothesReservationActivity;
import com.example.dobitnarae.R;
import com.example.dobitnarae.Store;

import java.text.DecimalFormat;
import java.util.List;

public class ClothesRecommendationListRecyclerAdapter extends RecyclerView.Adapter<ClothesRecommendationListRecyclerAdapter.ViewHolder> {
    Context context;
    List<Clothes> clothes;

    public ClothesRecommendationListRecyclerAdapter(Context context, List<Clothes> items) {
        this.context = context;
        this.clothes = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_clothes_recommend_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Clothes item = clothes.get(position);

        // 상점 정보 가져오기
       final Store store = new Store(item.getStore_id(), "세종대학교" + item.getStore_id(), "신구", "02-3408-3114",
                "세종대학교는 대한민국 서울특별시 광진구 군자동에 위치한 사립 종합대학이다." +
                        " 세종대나 SJU의 약칭으로 불리기도 한다. 10개의 단과 대학, 1개의 교양 대학," +
                        " 1개의 독립학부, 1개의 일반대학원, 1개의 전문대학원, 5개의 특수대학원과 57개의 연구소," +
                        " 8개의 BK21사업팀으로 구성되어 있다. 학교법인 대양학원에 의해 운영된다. 현재 총장은 화학 박사 신구이다. ",
                "24시간 영업", "서울특별시 광진구 군자동 능동로 209", 0,
                37.550278, 127.073114, "09:00", "21:00");

        // TODO  서버에서 이미지 받아야함
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.gobchang);
        holder.image.setBackground(drawable);

        holder.storeName.setText("운선제");
        holder.clothesName.setText(item.getName());

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
        TextView storeName, clothesName;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.recommend_clothes_image);
            storeName = (TextView) itemView.findViewById(R.id.recommend_store_name);
            clothesName = (TextView) itemView.findViewById(R.id.recommend_clothes_name);
            cardview = (CardView) itemView.findViewById(R.id.recommend_cardview);
        }
    }

    public void setClothes(List<Clothes> clothes) {
        this.clothes = clothes;
        this.notifyDataSetChanged();
    }
}