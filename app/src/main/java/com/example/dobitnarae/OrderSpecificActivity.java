package com.example.dobitnarae;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderSpecificActivity extends AppCompatActivity {
    private Order item;
    private Order item2;
    private ArrayList<Order> originItems, nConfirm, confirm;
    int index, id;

    private LinearLayout layout;

    private NestedScrollView mScrollView;

    private LinearLayout btnRegister;
    private LinearLayout btnReject;

    private ArrayList<BasketItem> basket;

    private Intent intent;
    private Store store;

    private OrderSpecificRecyclerAdapter mAdapter;
    private TextView totalClothesCnt, reservationCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_order2);

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

        intent = getIntent();
        index = (int) intent.getIntExtra("order", 0);
        id = (int) intent.getIntExtra("id", 0);
        store = (Store) intent.getSerializableExtra("store");

        this.originItems = JSONTask.getInstance().getOrderAdminAll(store.getAdmin_id());
        this.nConfirm = new ArrayList<Order>();
        this.confirm = new ArrayList<Order>();
        for (Order item:originItems) {
            if(item.getAcceptStatus()==0)
                nConfirm.add(item);
            else
                confirm.add(item);
        }

        layout = (LinearLayout) findViewById(R.id.layout_confirmornot);

        if(id==0) {
            this.item = nConfirm.get(index);
            this.basket = JSONTask.getInstance().getBascketCustomerAll(nConfirm.get(index).getOrderNo());

            // 승인 버튼 클릭 시
            btnRegister = (LinearLayout) findViewById(R.id.order_clothes_register);
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), item.getUserID() + "님의 주문이 승인되었습니다.", Toast.LENGTH_SHORT).show();
                    item.setAcceptStatus(1); // 승인
                    JSONTask.getInstance().updateOrderAccept(item.getOrderNo(), 1);
                    btnRegister.setEnabled(false);
                    btnReject.setEnabled(false);
                    btnRegister.setBackgroundResource(R.color.darkergrey);
                    btnReject.setBackgroundResource(R.color.darkergrey);
                }
            });

            // 거절 버튼 클릭 시
            btnReject = (LinearLayout) findViewById(R.id.order_clothes_reject);
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), item.getUserID() + "님의 주문이 거절되었습니다.", Toast.LENGTH_SHORT).show();
                    item.setAcceptStatus(2); // 거절
                    JSONTask.getInstance().updateOrderAccept(item.getOrderNo(), 2);
                    btnRegister.setEnabled(false);
                    btnReject.setEnabled(false);
                    btnRegister.setBackgroundResource(R.color.darkergrey);
                    btnReject.setBackgroundResource(R.color.darkergrey);
                }
            });
        }
        else if(id==1){
            this.item2 = confirm.get(index);
            this.basket = JSONTask.getInstance().getBascketCustomerAll(confirm.get(index).getOrderNo());
            if(this.item2.getAcceptStatus()!=0)
                layout.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.reservation_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new OrderSpecificRecyclerAdapter(this, basket);
        recyclerView.setAdapter(mAdapter);

        if(basket.size()==0){
            Toast.makeText(getApplicationContext(), "장바구니가 비었습니다.", Toast.LENGTH_SHORT).show();
        }

        reservationCost = findViewById(R.id.reservation_cost);
        setTotalCost();

        totalClothesCnt = findViewById(R.id.reservation_clothes_total_cnt);
        setTotalClothesCnt();

        /*
        // 스크롤뷰, 리스트뷰 중복 스크롤 허용
        mScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView_order);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        */
    }
    public void setTotalClothesCnt()
    {
        int cnt = 0;
        for(BasketItem item : basket)
            cnt += item.getCnt();
        totalClothesCnt.setText("" + cnt);
    }

    public void setTotalCost()
    {
        int price = 0;
        for(BasketItem item : basket)
            price += item.getCnt() * item.getClothes().getPrice();
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###");
        String str = decimalFormat.format(price) + " 원";
        reservationCost.setText(str);
    }
}
