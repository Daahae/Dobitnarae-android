package com.example.dobitnarae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ClothesReservationActivity extends AppCompatActivity {
    private DecimalFormat dc;
    private Clothes item;
    private LinearLayout btnReduce, btnAdd;
    private TextView totalPrice, selectCnt;
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_reservation);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //뒤로가기
        ImageButton backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        Intent intent = getIntent();
        item = (Clothes) intent.getSerializableExtra("clothes");
        store = (Store) intent.getSerializableExtra("store");

        TextView titleName = (TextView)findViewById(R.id.toolbar_title);
        titleName.setText(store.getName());

        // 이미지
        // reserve_clothes_img

        // 옷 이름
        TextView name = findViewById(R.id.reserve_clothes_name);
        name.setText(item.getName());

        // 옷 설명
        TextView description = findViewById(R.id.reserve_clothes_introduction);
        description.setText(item.getIntro());

        // 옷 가격
        dc = new DecimalFormat("###,###,###,###");
        TextView price = findViewById(R.id.reserve_clothes_price);
        String str = dc.format(item.getPrice()) + " 원";
        price.setText(str);

        // 총 가격
        setTotalPrice(1);

        btnReduce = findViewById(R.id.counting_btn_reduce);
        btnAdd = findViewById(R.id.counting_btn_add);
        selectCnt = findViewById(R.id.reserve_clothes_cnt);

        // 수량 추가, 감소 버튼 이벤트
        btnReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = Integer.parseInt((String) selectCnt.getText()) - 1;
                if (cnt == 0)
                    btnReduce.setClickable(false);
                selectCnt.setText( "" + cnt);
                setTotalPrice(cnt);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt = Integer.parseInt((String) selectCnt.getText()) + 1;
                if (cnt == 1)
                    btnReduce.setClickable(true);
                selectCnt.setText("" + cnt);
                setTotalPrice(cnt);
            }
        });

        LinearLayout gotoBasket = (LinearLayout)findViewById(R.id.reserve_clothes_basket);
        gotoBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivityTmp.basket.addClothes(v.getContext(), new BasketItem(item, Integer.parseInt((String) selectCnt.getText())));
                Toast.makeText(getApplicationContext(), "장바구니",Toast.LENGTH_SHORT).show();

                ArrayList<BasketItem> tmp = MainActivityTmp.basket.getBasket();
                for(BasketItem a : tmp){
                    Log.e(a.getClothes().getName(), "" + a.getCnt());
                }
            }
        });

        LinearLayout gotoReserve = (LinearLayout)findViewById(R.id.reserve_clothes_reserve);
        gotoReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "예약하기",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setTotalPrice(int cnt){
        totalPrice = findViewById(R.id.reserve_clothes_total_price);
        String total = dc.format(item.getPrice() * cnt) + " 원";
        totalPrice.setText(total);
    }
}