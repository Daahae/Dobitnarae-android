package com.example.dobitnarae;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import java.util.Objects;

public class SpecificOrderActivity extends AppCompatActivity {
    DecimalFormat dc;
    Order item;
    ArrayList<Clothes> basket;
    TextView totalPrice;

    private NestedScrollView mScrollView;

    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;

    private LinearLayout btnRegister;
    private LinearLayout btnReject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_specific);


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
        item = (Order) intent.getSerializableExtra("order");

        // 인텐트로 옷 리스트를 넘겨받음
        basket = intent.getParcelableArrayListExtra("cloth");

        mListView = (ListView) findViewById(R.id.listview_specific);

        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);

        for (Clothes citem:basket) {
            mAdapter.addItem(citem);
        }

        // 총 가격 설정
        setTotalPrice(basket);

        // 스크롤뷰, 리스트뷰 중복 스크롤 허용
        mScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView_order);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        // 승인 버튼 클릭 시
        btnRegister = (LinearLayout) findViewById(R.id.order_clothes_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, item.getUserID() + "님의 주문이 승인되었습니다.", Snackbar.LENGTH_LONG).show();
                item.setAcceptStatus(1); // 승인
                btnRegister.setEnabled(false);
                btnReject.setEnabled(false);
                btnRegister.setBackgroundResource(R.color.darkergrey);
                btnReject.setBackgroundResource(R.color.darkergrey);
                // 데이터 변경됨을 반영
                //mAdapter.dataChange();
            }
        });

        // 거절 버튼 클릭 시
        btnReject = (LinearLayout) findViewById(R.id.order_clothes_reject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, item.getUserID() + "고객의 주문이 거절되었습니다.", Snackbar.LENGTH_LONG).show();
                item.setAcceptStatus(2); // 거절
                btnRegister.setEnabled(false);
                btnReject.setEnabled(false);
                btnRegister.setBackgroundResource(R.color.darkergrey);
                btnReject.setBackgroundResource(R.color.darkergrey);
                //mAdapter.dataChange();
            }
        });
    }

    public void setTotalPrice(List<Clothes> citems){
        totalPrice = findViewById(R.id.reserve_clothes_total_price);
        String total;
        int sum = 0;

        for (Clothes citem: citems) {
            sum += citem.getPrice() * citem.getCount();
        }

        dc = new DecimalFormat("###,###,###,###");
        total = dc.format(sum) + " 원";
        totalPrice.setText(total);
    }

    private class ViewHolder {
        public ImageView imageView;
        public TextView mName;
        public TextView mIntro;
        public TextView mPrice;
        public TextView mCnt;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<Clothes> mListData = new ArrayList<Clothes>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_order_specific, null);

                holder.imageView = (ImageView) convertView.findViewById(R.id.order_clothes_img);
                holder.mName = (TextView) convertView.findViewById(R.id.order_clothes_name);
                holder.mIntro = (TextView) convertView.findViewById(R.id.order_clothes_introduction);
                holder.mPrice = (TextView) convertView.findViewById(R.id.order_clothes_price);
                holder.mCnt = (TextView) convertView.findViewById(R.id.order_clothes_cnt);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            Clothes mData = mListData.get(position);

            // 서버에서 이미지 받아야함
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.sample);

            holder.imageView.setBackground(drawable);
            holder.mName.setText(mData.getName());
            holder.mIntro.setText(mData.getIntro());
            // 옷 가격
            dc = new DecimalFormat("###,###,###,###");
            String str = dc.format(mData.getPrice()) + " 원";
            holder.mPrice.setText(str);

            holder.mCnt.setText(""+mData.getCount());

            return convertView;
        }

        public void addItem(Clothes item){
            Clothes addInfo = null;
            addInfo = new Clothes();
            // 이미지는 디렉토리에서 불러올예정
            addInfo.setName(item.getName());
            addInfo.setIntro(item.getIntro());
            addInfo.setPrice(item.getPrice());
            addInfo.setCount(item.getCount());
            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }


        public void clear(){
            mListData.clear();
            dataChange();
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }
    }
}
