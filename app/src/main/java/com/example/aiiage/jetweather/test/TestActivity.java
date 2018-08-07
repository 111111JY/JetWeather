package com.example.aiiage.jetweather.test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.aiiage.jetweather.AboutActivity;
import com.example.aiiage.jetweather.R;
import com.example.aiiage.jetweather.SpokenForecastActivity;
import com.example.aiiage.jetweather.UpdateFrequentActivity;
import com.example.aiiage.jetweather.WeatherActivity;
import com.example.aiiage.jetweather.viewspread.helper.BaseViewHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 危伟俊 on 2018/8/3.
 */
public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refresh_layout;
    @BindView(R.id.btn_float)
    FloatingActionButton btn_float;
    @BindView(R.id.btn_close)
    Button btn_close;
    @BindView(R.id.btn_add)
    Button btn_add;
    @BindView(R.id.btn_about)
    Button btn_about;
    @BindView(R.id.btn_update_frequent)
    Button btn_update_frequent;
    @BindView(R.id.btn_spoken)
    Button btn_spoken;
    @BindView(R.id.goto_first)
    Button btn_add_test;
    @BindView(R.id.goto_last)
    Button btn_del_test;
    @BindView(R.id.cl_menu)
    CoordinatorLayout cl_menu;


    static final int NUM_ITEMS = 10;
    PageAdapter mAdapter;
    ViewPager mPager;
    private ArrayList<Fragment> fragments = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        btn_float.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_update_frequent.setOnClickListener(this);
        btn_spoken.setOnClickListener(this);
        btn_about.setOnClickListener(this);
        btn_add_test.setOnClickListener(this);
        btn_del_test.setOnClickListener(this);

        fragments = new ArrayList<>();
        fragments.add(ArrayListFragment.newInstance(1));
        fragments.add(ArrayListFragment.newInstance(2));
        fragments.add(ArrayListFragment.newInstance(3));

        mAdapter = new PageAdapter(getSupportFragmentManager(), fragments);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        /**
         * 下拉刷新手动更新天气信息
         */
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //requestWeather(mWeatherId);
                refresh_layout.finishRefresh(2000/*,false*/);
                refresh_layout.setEnableLoadMore(true);
            }
        });
        refresh_layout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                // cl_delete.setVisibility(View.VISIBLE);
                refresh_layout.finishLoadMore(2000);
                refresh_layout.setEnableLoadMore(true);
            }
        });
    }

    public void AddFragment() {
        fragments.add(ArrayListFragment.newInstance(4));
        mAdapter.notifyDataSetChanged();
    }

    public void DelFragment() {
        int po = mPager.getCurrentItem();
        fragments.remove(fragments.get(po));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goto_first:
                AddFragment();
                break;
            case R.id.goto_last:
                DelFragment();
                break;
            case R.id.btn_nav:
                cl_menu.setVisibility(View.GONE);
                btn_float.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.btn_float:
                btn_float.setVisibility(View.GONE);
                drawerLayout.closeDrawer(Gravity.START);
                cl_menu.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_close:
                cl_menu.setVisibility(View.GONE);
                btn_float.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_add:
                // showAddCityDialog("添加城市", null, "添加");
                break;
            case R.id.btn_update_frequent:
                Intent intent_update_frequent = new Intent(TestActivity.this, UpdateFrequentActivity.class);
                new BaseViewHelper
                        .Builder(TestActivity.this, btn_update_frequent)
                        .startActivity(intent_update_frequent);
                break;
            case R.id.btn_spoken:
                Intent intent_spoken_forecast = new Intent(TestActivity.this, SpokenForecastActivity.class);
                new BaseViewHelper
                        .Builder(TestActivity.this, btn_spoken)
                        .startActivity(intent_spoken_forecast);
                break;
            case R.id.btn_about:
                Intent intent_about = new Intent(TestActivity.this, AboutActivity.class);
                new BaseViewHelper
                        .Builder(TestActivity.this, btn_about)
                        .startActivity(intent_about);
                break;
            default:
        }
    }
}
