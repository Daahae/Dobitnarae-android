package com.example.dobitnarae;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class OrderSpecificActivity extends AppCompatActivity {
    private Order item;
    private Order item2;
    private ArrayList<Order> originItems, nConfirm, confirm;
    int index, id;

    private LinearLayout layout;
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
        setContentView(R.layout.activity_specific_order);

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
            this.basket = JSONTask.getInstance().getBascketCustomerAll(nConfirm.get(index).getId());

            // 승인 버튼 클릭 시
            btnRegister = (LinearLayout) findViewById(R.id.order_clothes_register);
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), item.getUser_id() + "님의 주문이 승인되었습니다.", Toast.LENGTH_SHORT).show();
                    item.setAcceptStatus(1); // 승인
                    JSONTask.getInstance().updateOrderAccept(item.getId(), 1);
                    btnRegister.setEnabled(false);
                    btnReject.setEnabled(false);
                    btnRegister.setBackgroundResource(R.color.darkergrey);
                    btnReject.setBackgroundResource(R.color.darkergrey);
                    OrderFragmentManagementFragment.changeFlg = true;
                    JSONTask.getInstance().sendMsgByFCM(item.getUser_id(), item.getUser_id() + "님의 주문이 승인되었습니다.");
                }
            });

            // 거절 버튼 클릭 시
            btnReject = (LinearLayout) findViewById(R.id.order_clothes_reject);
            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), item.getUser_id() + "님의 주문이 거절되었습니다.", Toast.LENGTH_SHORT).show();
                    item.setAcceptStatus(2); // 거절
                    JSONTask.getInstance().updateOrderAccept(item.getId(), 2);
                    btnRegister.setEnabled(false);
                    btnReject.setEnabled(false);
                    btnRegister.setBackgroundResource(R.color.darkergrey);
                    btnReject.setBackgroundResource(R.color.darkergrey);
                    OrderFragmentManagementFragment.changeFlg = true;

                    for (BasketItem item : basket) {
                        Clothes temp = item.getClothes();
                        int tmpCnt = item.getClothes().getCount();
                        temp.setCount(tmpCnt + item.getCnt());
                        JSONTask.getInstance().updateCloth(temp);
                    }
                    JSONTask.getInstance().sendMsgByFCM(item.getUser_id(), item.getUser_id() + "님의 주문이 거절되었습니다.");
                }
            });
        }
        else if(id==1){
            this.item2 = confirm.get(index);
            this.basket = JSONTask.getInstance().getBascketCustomerAll(confirm.get(index).getId());
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
