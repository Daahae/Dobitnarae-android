package com.example.dobitnarae;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import java.util.Objects;

public class AdminActivity extends AppCompatActivity {
     private ImageButton editButton;
     private Spinner spinner;
     private ImageButton refreshBtn;
     private StoreManagementFragment storeManagementFragment;
     private ItemManagementFragment itemManagementFragment;
     private OrderManagementFragment orderManagementFragment;
     private ArrayList<Store> storeList;
     private Store store;

     private Context context;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a/
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_edit);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    editButton.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                    refreshBtn.setVisibility(View.GONE);
                    storeManagementFragment.refresh();
                }
                else if (position == 1) {
                    editButton.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    refreshBtn.setVisibility(View.GONE);
                    itemManagementFragment.refresh();
                }
                else if(position == 2) {
                    editButton.setVisibility(View.GONE);
                    spinner.setVisibility(View.GONE);
                    refreshBtn.setVisibility(View.VISIBLE);
                    orderManagementFragment.refresh();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        this.storeList = JSONTask.getInstance().getAdminStoreAll("jong4876");// JSON형태의 store정보들을 분류하여 arrayList에 저장
        this.store = storeList.get(0);

        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(store.getName());

        editButton = findViewById(R.id.editButton);
        spinner = findViewById(R.id.edit_spinner);
        refreshBtn = findViewById(R.id.refreshButton);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);
        this.context = context;
        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 카메라 전환시 변경된 방향을 원래대로 바꿈
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            newConfig.orientation = Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_example_swipe_menu, menu);
        return true;
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
            switch(position) {
                case 0:
                    return storeManagementFragment = StoreManagementFragment.newInstance(0, store);
                case 1:
                    return itemManagementFragment = ItemManagementFragment.newInstance(1, store);
                case 2:
                    return orderManagementFragment = OrderManagementFragment.newInstance(2, store);
                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            // 탭 개수
            return 3;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (storeManagementFragment != null)
            ((StoreManagementFragment) storeManagementFragment).onActivityResult(requestCode, resultCode, data);
    }

    public ImageButton getEditButton(){
        return editButton;
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public ImageButton getRefreshBtn(){
        return refreshBtn;
    }
}
