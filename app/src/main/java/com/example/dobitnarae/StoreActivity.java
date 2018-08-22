package com.example.dobitnarae;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoreActivity extends AppCompatActivity {
    Store store;
    ArrayList<Store> storeList = new ArrayList<Store>();
    List<Clothes> items;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_clothes);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // TODO 스크롤 하고 탭 클릭시 스크롤 처음으로 돌아가게하기
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.store_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        LinearLayout gotoBasket = (LinearLayout) findViewById(R.id.store_basket);
        gotoBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "장바구니", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StoreActivity.this, BasketActivity.class);
                startActivity(intent);
            }
        });

        // TODO
        // 특정 인덴트에서 store 키값을 받아와
        // 서버로 통신 하여 `판매중인 옷` 데이터 받아와야함
        Intent intent = getIntent();
        store = (Store) intent.getSerializableExtra("store");

        TextView titleName = (TextView) findViewById(R.id.toolbar_title);
        titleName.setText(store.getName());


        // 특정 인덴트에서 store 키값을 받아와
        // 서버로 통신 하여 `가게정보, 판매중인 옷` 데이터 받아옴

        // 옷 정보들 가져와서 초기화
        int ITEM_SIZE = 8;
        items = new ArrayList<>();
        Clothes[] item = new Clothes[ITEM_SIZE];
        for(int i=0; i<ITEM_SIZE; i++){
            item[i] = new Clothes(i, store.getId(), i % Constant.category_cnt + 1,
                    "불곱창" + (i + 1), "이 곱창은 왕십리에서 시작하여...",
                    1000 * (i + 1), (i + 1) % ITEM_SIZE,  0);
            items.add(item[i]);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_store, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            // 액티비티 만들어서 케이스 문에다가 넣어주면 됩니다
            // 탭 이름은 values/string 에 들어있어요

            switch (position) {
                case 0:
                    return StoreInfoFragment.newInstance(0, store);
                case 1:
                    return StoreClothesFragment.newInstance(1, items, store);
            }
                return null;
        }


        public int getCount () {
            // Show 3 total pages.
            return 2;
        }
    }
}



