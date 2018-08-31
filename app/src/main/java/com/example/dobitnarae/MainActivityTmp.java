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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dobitnarae.RecyclerViewAdapter.ClothesRecommendationListRecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivityTmp extends AppCompatActivity {
    private Account account;
    private ClothesRecommendationListRecyclerAdapter cAdapter;
    private ArrayList<Clothes> clothes;

    // 날씨
    private final String[] skyStatus = {"맑음", "구름조금", "구름많음", "흐림"};
    private final String[] precipitationType = {"", "비", "비/눈", "눈"};

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


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

        // 날씨 설정
        try {
            TextView temperature = (TextView) findViewById(R.id.weather_temperature);
            TextView weatherStatusMsg = (TextView)findViewById(R.id.weather_status_text);
            ImageView weatherImg = (ImageView)findViewById(R.id.weather_status_img);
            TextView weatherContext = (TextView)findViewById(R.id.weather_context);

            JSONObject weatherInfo = new WeatherTask().execute().get();

            temperature.setText(weatherInfo.getString("기온"));
            int sky = weatherInfo.getInt("하늘상태");
            int pty = weatherInfo.getInt("강수상태");

            if(pty == 0)
                weatherStatusMsg.setText(skyStatus[sky]);
            else
                weatherStatusMsg.setText(precipitationType[pty]);

            // 추천 문구
            weatherContext.setText(getWeatherMessage(sky, pty));

            // 날씨 이미지
            Drawable drawable = ContextCompat.getDrawable(this, getWeatherImg(sky, pty));
            weatherImg.setBackground(drawable);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout logout = (LinearLayout)findViewById(R.id.main_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.setLogOut();
                Intent intent = new Intent(MainActivityTmp.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 현재 액티비티 닫기
                startActivity(intent);
            }
        });

        // 지울것
        account = Account.getInstance();
    }

//    @Override
//    public void onBackPressed() {
//        long tempTime = System.currentTimeMillis();
//        long intervalTime = tempTime - backPressedTime;
//
//        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
//            super.onBackPressed();
//            finish();
//        }
//        else {
//            backPressedTime = tempTime;
//            Toast.makeText(getApplicationContext(), "뒤로 버튼을 한번더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
//        }
//    }

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

    private int getWeatherImg(int skyStatus, int precipitationType){
        if(precipitationType == 0){ // 비 or 눈 안올때
            switch (skyStatus){
                case 0: // 맑음
                    return R.drawable.sunny_day_weather_symbol;
                case 1: // 구름 조금
                    return R.drawable.cloudy_day;
                default:    // 구름많음, 흐림
                    return R.drawable.cloud_outline;
            }
        }
        else {  // 비 or 눈 올때
            if(skyStatus == 0){ // 맑음
                switch (precipitationType){
                    case 1: //비
                        return R.drawable.rainy_day;
                    case 2: //눈비
                        return R.drawable.hail;
                    case 3: //눈
                        return R.drawable.snowy_weather_symbol;
                }
            }
            else{
                switch (precipitationType){
                    case 1: //비
                        return R.drawable.cloud_with_rain_drops;
                    case 2: //눈비
                        return R.drawable.hail_crystals_falling_of_a_cloud;
                    case 3: //눈
                        return R.drawable.snow_weather_symbol;
                }
            }
        }
        return R.drawable.sunny_day_weather_symbol;
    }

    private String getWeatherMessage(int skyStatus, int precipitationType){
        if(precipitationType == 0){
            if(skyStatus == 0 || skyStatus == 1) { // 맑음, 구름 조금
                return "화창한 오늘, 한복입고 고궁을 거닐어 보세요";
            }
            else{   // 구름 많음, 흐림
                return "당신의 나래가 되어줄 한복 구경 어떠세요?";
            }
        }
        else if(precipitationType == 3){ // 눈
            return "다채로운 한복으로\n 하얀 고궁을 채워보는건 어떨까요?";
        }
        else { // 비, 눈비
            return "눈부신 날을 위해\n 아름다운 한복을 구경해보세요";
        }
    }
}
