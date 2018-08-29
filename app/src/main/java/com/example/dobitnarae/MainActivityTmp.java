package com.example.dobitnarae;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dobitnarae.RecyclerViewAdapter.ClothesRecommendationListRecyclerAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivityTmp extends AppCompatActivity {
    private Account account;
    private ClothesRecommendationListRecyclerAdapter cAdapter;
    private ArrayList<Clothes> clothes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tmp);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        LinearLayout myPageBtn = (LinearLayout)findViewById(R.id.myPage);
        myPageBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivityTmp.this, MyPageActivity.class);
                startActivity(intent);
            }
        });

        View.OnClickListener gotoStoreList = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityTmp.this, StoreListActivity.class);
                int sector = Integer.parseInt((String) v.getTag());
                intent.putExtra("sector", sector);
                startActivity(intent);
            }
        };

        LinearLayout sector1 = (LinearLayout)findViewById(R.id.store_sector_1);
        sector1.setOnClickListener(gotoStoreList);

        LinearLayout sector2 = (LinearLayout)findViewById(R.id.store_sector_2);
        sector2.setOnClickListener(gotoStoreList);

        // 옷 추천 리스트
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.clothes_recommendation_recycler_view);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setNestedScrollingEnabled(false);

        // 랜덤으로 옷 추출
        // 더미
        int ITEM_SIZE = 8;
        clothes = new ArrayList<>();
        for(int i=0; i<ITEM_SIZE; i++){
            Clothes item = new Clothes(i, i, i % Constant.category_cnt + 1,
                    "불곱창" + (i + 1), "이 곱창은 왕십리에서 시작하여...",
                    1000 * (i + 1), (i + 1) % ITEM_SIZE,  0);
            clothes.add(item);
        }

        cAdapter = new ClothesRecommendationListRecyclerAdapter(this, clothes);
        recyclerView.setAdapter(cAdapter);

        // 고궁 알아보기
        setPalaceRedirection();

        // 지울것
        account = Account.getInstance();
    }

    private void setPalaceRedirection(){
        // 경복궁
        ImageView gyeongbok = (ImageView)findViewById(R.id.gyeongbokgung);
        gyeongbok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.royalpalace.go.kr/"));
                startActivity(intent);
            }
        });

        // 창경궁
        ImageView changgyeong = (ImageView)findViewById(R.id.changgyeonggung);
        changgyeong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cgg.cha.go.kr/"));
                startActivity(intent);
            }
        });

        // 창덕궁
        ImageView changdeok = (ImageView)findViewById(R.id.changdeokgung);
        changdeok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cdg.go.kr/"));
                startActivity(intent);
            }
        });

        // 종묘
        ImageView jongmyo = (ImageView)findViewById(R.id.jongmyo);
        jongmyo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jm.cha.go.kr/"));
                startActivity(intent);
            }
        });

        // 덕수궁
        ImageView deoksu = (ImageView)findViewById(R.id.deoksugung);
        deoksu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.deoksugung.go.kr/"));
                startActivity(intent);
            }
        });
    }

}
