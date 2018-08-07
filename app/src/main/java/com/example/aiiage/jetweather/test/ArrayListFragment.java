package com.example.aiiage.jetweather.test;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.example.aiiage.jetweather.MyApplication;
import com.example.aiiage.jetweather.R;
import com.example.aiiage.jetweather.WeatherActivity;
import com.example.aiiage.jetweather.gson.Forecast;
import com.example.aiiage.jetweather.gson.Hourly;
import com.example.aiiage.jetweather.gson.Weather;
import com.example.aiiage.jetweather.util.HttpUtil;
import com.example.aiiage.jetweather.util.TTSUtils;
import com.example.aiiage.jetweather.util.Utility;
import com.example.aiiage.jetweather.viewspread.helper.BaseViewHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 危伟俊 on 2018/8/3.
 */
public class ArrayListFragment extends ListFragment implements View.OnClickListener {
    int mNum;
    private Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.btn_nav)
    Button btn_nav;
    @BindView(R.id.btn_location)
    Button btn_location;
    @BindView(R.id.iv_weather_now)
    ImageView iv_weather_now;
    @BindView(R.id.iv_forecast_bg)
    ImageView iv_forecast_bg;
    @BindView(R.id.tv_update_time)
    TextView tv_update_time;
    @BindView(R.id.tv_degree)
    TextView tv_degree;
    @BindView(R.id.tv_weather_info)
    TextView tv_weather_info;
    @BindView(R.id.tv_aqi)
    TextView tv_aqi;
    @BindView(R.id.tv_pm25)
    TextView tv_pm25;
    @BindView(R.id.tv_comfort)
    TextView tv_comfort;
    @BindView(R.id.tv_car_wash)
    TextView tv_car_wash;
    @BindView(R.id.tv_sport)
    TextView tv_sport;
    @BindView(R.id.tv_qlty)
    TextView tv_qlty;
    @BindView(R.id.tv_comf_brf)
    TextView tv_comf_brf;
    @BindView(R.id.tv_car_wash_brf)
    TextView tv_car_wash_brf;
    @BindView(R.id.tv_sport_brf)
    TextView tv_sport_brf;
    @BindView(R.id.btn_delete)
    Button btn_delete;
    @BindView(R.id.btn_delete_layout_close)
    Button btn_delete_layout_close;
    @BindView(R.id.cl_delete)
    CoordinatorLayout cl_delete;
    @BindView(R.id.ll_forecast)
    LinearLayout forecast_layout;
    @BindView(R.id.layout_aqi)
    LinearLayout aqi_layout;
    @BindView(R.id.layout_suggestion)
    LinearLayout suggestion_layout;
    @BindView(R.id.ll_hourly)
    LinearLayout hourly_layout;
    @BindView(R.id.weather_layout)
    ScrollView weather_layout;

    private String mWeatherId;
    String cityName;

    BaseViewHelper helper;
    //初始化百度定位
    LocationClient mLocationClient;
    MyLocationListener myLocationListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_nav:
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                CoordinatorLayout cl_menu = (CoordinatorLayout) activity.findViewById(R.id.cl_menu);
                Button btn_float=(Button)activity.findViewById(R.id.btn_float);
                DrawerLayout drawerLayout=(DrawerLayout)activity.findViewById(R.id.drawer_layout);

                cl_menu.setVisibility(View.GONE);
                btn_float.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.btn_location:
                //绑定百度定位
                mLocationClient = new LocationClient(MyApplication.getContext());
                myLocationListener = new MyLocationListener(btn_location);
                mLocationClient.registerLocationListener(myLocationListener);
                //设置option
                LocationClientOption option = new LocationClientOption();
                option.setIsNeedAddress(true);
                option.setOpenGps(true);
                option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                option.setCoorType("bd09ll");
                option.setScanSpan(1000);
                mLocationClient.setLocOption(option);
                //开启定位
                mLocationClient.start();
                break;
            case R.id.btn_delete:
                cl_delete.setVisibility(View.GONE);
                new AlertDialog.Builder(MyApplication.getContext())
                        .setTitle("警告")
                        .setIcon(R.drawable.warming)
                        .setMessage("确定要删除该城市天气吗？")
                        .setCancelable(false)
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MyApplication.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MyApplication.getContext(), "取消", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
            case R.id.btn_delete_layout_close:
                cl_delete.setVisibility(View.GONE);
                break;
            default:
        }

    }

    /**
     * 百度定位SDK Api
     */
    class MyLocationListener implements BDLocationListener {
        MyLocationListener(Button btn_location) {
            btn_location.setText(cityName);
        }

        String cityName;

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            cityName = bdLocation.getCity();
            Log.d("Locate", cityName);
            final AlertDialog dialog = new AlertDialog.Builder(MyApplication.getContext())
                    .setTitle("定位服务")
                    .setIcon(R.drawable.location)
                    .setCancelable(false)
                    .setMessage("努力定位中，请耐心等待...")
                    .show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    new WeatherActivity().addCity(cityName);
                }
            }, 2000);

            mLocationClient.stop();
        }
    }

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static ArrayListFragment newInstance(int num) {

        ArrayListFragment f = new ArrayListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, 0));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }

    /**
     * 数据、图片初始化
     */
    private void init() {
        tv_title.setText("Fragment #" + mNum);
        btn_delete.setOnClickListener(this);
        btn_delete_layout_close.setOnClickListener(this);

        //讯飞语音播报初始化
        TTSUtils.getInstance().init();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        //背景图加载
        String forecast_bg = prefs.getString("bing_pic", null);
        if (forecast_bg != null) {
            Glide.with(this).load(forecast_bg).into(iv_forecast_bg);
        } else {
            loadForecastBg();
        }
        //天气数据的处理逻辑
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.cityId;
            showWeatherInfo(weather);
        }
//        else {
//            //无缓存时区服务器查询天气
//            mWeatherId = getIntent().getStringExtra("weather_id");
//            weather_layout.setVisibility(View.VISIBLE);
//            requestWeather(mWeatherId);
//        }
        loadForecastBg();
    }

    /**
     * 从服务器加载必应每日图作为背景图
     */
    public void loadForecastBg() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String forecast_bg = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
                editor.putString("bing_pic", forecast_bg);
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MyApplication.getContext()).load(forecast_bg).into(iv_forecast_bg);
                    }
                });
            }
        });

    }

    /**
     * 根据天气Id请求城市天气信息
     *
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=0754508fbe0f44be915c65a0182b8aa9";
        Log.d(MyApplication.getContext().getPackageName(), "requestWeather: " + weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyApplication.getContext(), "获取天气信息错误", Toast.LENGTH_SHORT).show();
                        //refresh_layout.setEnableRefresh(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(MyApplication.getContext()).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                            Toast.makeText(MyApplication.getContext(), "获取天气信息成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        //refresh_layout.setDisableContentWhenRefresh(true);
                    }
                });
            }
        });
        loadForecastBg();
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        String cond_code = weather.now.more.cond_code;
        changeWeatherStatusPic(cond_code);
        //tv_title.setText(cityName);
        tv_update_time.setText(updateTime);
        tv_degree.setText(degree);
        tv_weather_info.setText(weatherInfo);

        //btn_float.setVisibility(View.VISIBLE);
        /**
         * 先移除逐小时天气列表view，再加载新数据到其中
         */
        hourly_layout.removeAllViews();
        for (Hourly hourly : weather.hourlyList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.hourly_item, hourly_layout, false);
            TextView tv_day_time = (TextView) view.findViewById(R.id.tv_day_time);
            TextView tv_day_tmp = (TextView) view.findViewById(R.id.tv_day_tmp);
            TextView tv_day_cond_txt = (TextView) view.findViewById(R.id.tv_day_cond_txt);
            TextView tv_day_wind_dir = (TextView) view.findViewById(R.id.tv_day_wind_dir);
            TextView tv_day_wind_sc = (TextView) view.findViewById(R.id.tv_day_wind_sc);
            tv_day_time.setText(hourly.time);
            tv_day_tmp.setText(hourly.tmp + "℃");
            tv_day_cond_txt.setText(hourly.cond_txt);
            tv_day_wind_dir.setText(hourly.wind_dir);
            tv_day_wind_sc.setText("风力：" + hourly.wind_sc + "级");
            hourly_layout.addView(view);
        }
        /**
         * 先移除未来一周天气列表view，再加载新数据到其中
         */
        forecast_layout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item, forecast_layout, false);

            TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_date.setText(forecast.date);
            TextView tv_info = (TextView) view.findViewById(R.id.tv_info);
            tv_info.setText(forecast.more.info);
            TextView tv_max = (TextView) view.findViewById(R.id.tv_max);
            tv_max.setText(forecast.temperature.max + "℃");
            TextView tv_min = (TextView) view.findViewById(R.id.tv_min);
            tv_min.setText(forecast.temperature.min + "℃");
            forecast_layout.addView(view);
        }
        if (weather.aqi != null) {
            tv_aqi.setText(weather.aqi.city.aqi);
            tv_qlty.setText(weather.aqi.city.qlty);
            tv_pm25.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.brf;
        String car_wash = "洗车指数：" + weather.suggestion.carWash.brf;
        String sport = "运动建议：" + weather.suggestion.sport.brf;
        String comfort_text = weather.suggestion.comfort.info;
        String car_wash_text = weather.suggestion.carWash.info;
        String sport_text = weather.suggestion.sport.info;
        tv_comf_brf.setText(comfort);
        tv_comfort.setText(comfort_text);
        tv_car_wash_brf.setText(car_wash);
        tv_car_wash.setText(car_wash_text);
        tv_sport_brf.setText(sport);
        tv_sport.setText(sport_text);
    }

    /**
     * 根据获取到的天气状态码动态更换天气状态图片
     */
    private void changeWeatherStatusPic(String code) {
        String cond_code = code;
        switch (cond_code) {
            case ("100"):
                iv_weather_now.setBackgroundResource(R.drawable.c100);
                break;
            case ("101"):
                iv_weather_now.setBackgroundResource(R.drawable.c101);
                break;
            case ("102"):
                iv_weather_now.setBackgroundResource(R.drawable.c102);
                break;
            case ("103"):
                iv_weather_now.setBackgroundResource(R.drawable.c103);
                break;
            case ("104"):
                iv_weather_now.setBackgroundResource(R.drawable.c104);
                break;
            case ("200"):
                iv_weather_now.setBackgroundResource(R.drawable.c200);
                break;
            case ("201"):
                iv_weather_now.setBackgroundResource(R.drawable.c201);
                break;
            case ("202"):
                iv_weather_now.setBackgroundResource(R.drawable.c202);
                break;
            case ("203"):
                iv_weather_now.setBackgroundResource(R.drawable.c203);
                break;
            case ("204"):
                iv_weather_now.setBackgroundResource(R.drawable.c204);
                break;
            case ("205"):
                iv_weather_now.setBackgroundResource(R.drawable.c205);
                break;
            case ("206"):
                iv_weather_now.setBackgroundResource(R.drawable.c206);
                break;
            case ("207"):
                iv_weather_now.setBackgroundResource(R.drawable.c207);
                break;
            case ("208"):
                iv_weather_now.setBackgroundResource(R.drawable.c208);
                break;
            case ("209"):
                iv_weather_now.setBackgroundResource(R.drawable.c209);
                break;
            case ("210"):
                iv_weather_now.setBackgroundResource(R.drawable.c210);
                break;
            case ("211"):
                iv_weather_now.setBackgroundResource(R.drawable.c211);
                break;
            case ("212"):
                iv_weather_now.setBackgroundResource(R.drawable.c212);
                break;
            case ("213"):
                iv_weather_now.setBackgroundResource(R.drawable.c213);
                break;
            case ("300"):
                iv_weather_now.setBackgroundResource(R.drawable.c300);
                break;
            case ("301"):
                iv_weather_now.setBackgroundResource(R.drawable.c301);
                break;
            case ("302"):
                iv_weather_now.setBackgroundResource(R.drawable.c302);
                break;
            case ("303"):
                iv_weather_now.setBackgroundResource(R.drawable.c303);
                break;
            case ("304"):
                iv_weather_now.setBackgroundResource(R.drawable.c304);
                break;
            case ("305"):
                iv_weather_now.setBackgroundResource(R.drawable.c305);
                break;
            case ("306"):
                iv_weather_now.setBackgroundResource(R.drawable.c306);
                break;
            case ("307"):
                iv_weather_now.setBackgroundResource(R.drawable.c307);
                break;
            case ("309"):
                iv_weather_now.setBackgroundResource(R.drawable.c309);
                break;
            case ("310"):
                iv_weather_now.setBackgroundResource(R.drawable.c310);
                break;
            case ("311"):
                iv_weather_now.setBackgroundResource(R.drawable.c311);
                break;
            case ("312"):
                iv_weather_now.setBackgroundResource(R.drawable.c312);
                break;
            case ("313"):
                iv_weather_now.setBackgroundResource(R.drawable.c313);
                break;
            case ("314"):
                iv_weather_now.setBackgroundResource(R.drawable.c314);
                break;
            case ("315"):
                iv_weather_now.setBackgroundResource(R.drawable.c315);
                break;
            case ("316"):
                iv_weather_now.setBackgroundResource(R.drawable.c316);
                break;
            case ("317"):
                iv_weather_now.setBackgroundResource(R.drawable.c317);
                break;
            case ("318"):
                iv_weather_now.setBackgroundResource(R.drawable.c318);
                break;
            case ("399"):
                iv_weather_now.setBackgroundResource(R.drawable.c399);
                break;
            case ("400"):
                iv_weather_now.setBackgroundResource(R.drawable.c400);
                break;
            case ("401"):
                iv_weather_now.setBackgroundResource(R.drawable.c401);
                break;
            case ("402"):
                iv_weather_now.setBackgroundResource(R.drawable.c402);
                break;
            case ("403"):
                iv_weather_now.setBackgroundResource(R.drawable.c403);
                break;
            case ("404"):
                iv_weather_now.setBackgroundResource(R.drawable.c404);
                break;
            case ("405"):
                iv_weather_now.setBackgroundResource(R.drawable.c405);
                break;
            case ("406"):
                iv_weather_now.setBackgroundResource(R.drawable.c406);
                break;
            case ("407"):
                iv_weather_now.setBackgroundResource(R.drawable.c407);
                break;
            case ("408"):
                iv_weather_now.setBackgroundResource(R.drawable.c408);
                break;
            case ("409"):
                iv_weather_now.setBackgroundResource(R.drawable.c409);
                break;
            case ("410"):
                iv_weather_now.setBackgroundResource(R.drawable.c410);
                break;
            case ("499"):
                iv_weather_now.setBackgroundResource(R.drawable.c499);
                break;
            case ("500"):
                iv_weather_now.setBackgroundResource(R.drawable.c500);
                break;
            case ("501"):
                iv_weather_now.setBackgroundResource(R.drawable.c501);
                break;
            case ("502"):
                iv_weather_now.setBackgroundResource(R.drawable.c502);
                break;
            case ("503"):
                iv_weather_now.setBackgroundResource(R.drawable.c503);
                break;
            case ("504"):
                iv_weather_now.setBackgroundResource(R.drawable.c504);
                break;
            case ("507"):
                iv_weather_now.setBackgroundResource(R.drawable.c507);
                break;
            case ("508"):
                iv_weather_now.setBackgroundResource(R.drawable.c508);
                break;
            case ("509"):
                iv_weather_now.setBackgroundResource(R.drawable.c509);
                break;
            case ("510"):
                iv_weather_now.setBackgroundResource(R.drawable.c510);
                break;
            case ("511"):
                iv_weather_now.setBackgroundResource(R.drawable.c511);
                break;
            case ("512"):
                iv_weather_now.setBackgroundResource(R.drawable.c512);
                break;
            case ("513"):
                iv_weather_now.setBackgroundResource(R.drawable.c513);
                break;
            case ("514"):
                iv_weather_now.setBackgroundResource(R.drawable.c514);
                break;
            case ("515"):
                iv_weather_now.setBackgroundResource(R.drawable.c515);
                break;
            case ("900"):
                iv_weather_now.setBackgroundResource(R.drawable.c900);
                break;
            case ("901"):
                iv_weather_now.setBackgroundResource(R.drawable.c901);
                break;
            case ("999"):
                iv_weather_now.setBackgroundResource(R.drawable.c999);
                break;
            default:
                break;
        }
    }
}


