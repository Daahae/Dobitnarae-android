package com.example.dobitnarae;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class BasketActivity extends AppCompatActivity {
    BasketRecyclerAdapter bAdapter;
    DecimalFormat decimalFormat;
    TextView priceTextView, totalClothesCnt;

    DatePickerDialog dpd;
    TimePickerDialog tpd;
    Context context;

    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        this.context = this;
        // 예약 목록 레이아웃 설정
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_basket_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 290, this.getResources().getDisplayMetrics());
        recyclerView.setMinimumHeight(displayMetrics.heightPixels - px);
        recyclerView.setLayoutManager(layoutManager);

        //전체 옷 개수 설정
        totalClothesCnt = findViewById(R.id.reserve_clothes_total_cnt);
        setTotalClothesCnt();

        // 전체 가격 설정
        bAdapter = new BasketRecyclerAdapter(this);
        recyclerView.setAdapter(bAdapter);

        priceTextView = findViewById(R.id.basket_reserve_clothes_total_price);
        decimalFormat = new DecimalFormat("###,###,###,###");

        // 총 대여 가격 설정
        setTotalCost();

        // 날짜 및 시간 설정
        setDateCalendar();
        setTimePicker();

        // 대여하기 버튼 설정
        LinearLayout reserveBtn = (LinearLayout) findViewById(R.id.basket_reservation);
        reserveBtn.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Basket.getInstance().getClothesCnt() == 0)
                    Toast.makeText(v.getContext(), "담은 한복이 없습니다", Toast.LENGTH_SHORT).show();
                else {
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            }
        });
    }

    public void setTotalClothesCnt()
    {
        totalClothesCnt.setText("" + Basket.getInstance().getTotalClothesCnt());
    }

    public void setTotalCost()
    {
        int price = Basket.getInstance().getTotalPrice();
        String str = decimalFormat.format(price) + " 원";
        priceTextView.setText(str);
    }

    private void setDateCalendar()
    {
        Calendar now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                null,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setTitle("예약 날짜");
        dpd.setOkText("다음");
        dpd.setCancelText("취소");
        dpd.setMinDate(now);
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                date = "" + year + "-" + String.format("%02d", monthOfYear + 1) + "-" + String.format("%02d", dayOfMonth);
                tpd.show(getFragmentManager(), "TimePickerdialog");
            }
        });
    }

    private void setTimePicker()
    {
        Calendar now = Calendar.getInstance();
        tpd = TimePickerDialog.newInstance(null,
                Calendar.HOUR_OF_DAY,
                Calendar.MINUTE,
                false
        );
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.setTitle("예약 시간");
        tpd.setOkText("예약 완료");
        tpd.setCancelText("취소");
        tpd.setTimeInterval(1, 5);
        tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                date += " " + String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
                reservation();
            }
        });
        tpd.dismissOnPause(true);
    }

    private void reservation()
    {
        Basket basket = Basket.getInstance();
        Account account = Account.getInstance();
        String adminID = JSONTask.getInstance().changeToAdminID(basket.getSelectedStoreID());

        Log.e("" + adminID, " " + basket.getSelectedStoreID());

        Reserve reserve = new Reserve(1, account.getId(),
                adminID, 0, date);
        JSONTask.getInstance().insertReserve(reserve, basket.getBasket());
        basket.clearBasket();

        Toast toast = Toast.makeText(context, "대여 신청 완료", Toast.LENGTH_SHORT);
        JSONTask.getInstance().sendMsgByFCM(adminID, "주문이 접수되었습니다.");
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        finish();
    }
}
