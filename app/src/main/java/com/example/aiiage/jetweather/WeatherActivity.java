package com.example.aiiage.jetweather;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.aiiage.jetweather.gson.Forecast;
import com.example.aiiage.jetweather.gson.Hourly;
import com.example.aiiage.jetweather.gson.Weather;
import com.example.aiiage.jetweather.util.ConstantValue;
import com.example.aiiage.jetweather.util.HttpUtil;
import com.example.aiiage.jetweather.util.SharePreUtil;
import com.example.aiiage.jetweather.util.TTSUtils;
import com.example.aiiage.jetweather.util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 危伟俊 on 2018/7/28.
 */
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private Unbinder unbinder;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.btn_nav)
    Button btn_nav;
    @BindView(R.id.btn_location)
    Button btn_location;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refresh_layout;
    @BindView(R.id.iv_weather_now)
    ImageView iv_weather_now;
    @BindView(R.id.iv_forecast_bg)
    ImageView iv_forecast_bg;
    @BindView(R.id.weather_layout)
    ScrollView weather_layout;
    @BindView(R.id.tv_title)
    TextView tv_title;
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
    @BindView(R.id.tv_weather_hum)
    TextView tv_weather_hum;
    @BindView(R.id.tv_weather_wind_dir)
    TextView tv_weather_wind_dir;
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
    @BindView(R.id.btn_step_count)
    Button btn_step_count;
    @BindView(R.id.btn_change_city)
    Button btn_change_city;
    @BindView(R.id.cl_menu)
    CoordinatorLayout cl_menu;
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

    private String mWeatherId;
    private String WeatherId;
    String cityName;

    Animation loadAnimation;
    //初始化百度定位
    LocationClient mLocationClient;
    MyLocationListener myLocationListener;
    //存储城市的字符串列表
    ArrayList<String> CitylistSave = new ArrayList<>();
    ArrayList<String> CitylistGet = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MyApplication.setStatusBarColor(this, Color.TRANSPARENT);

        setContentView(R.layout.layout_weather);
        unbinder = ButterKnife.bind(this);
        //给按键绑定点击事件监听
        btn_location.setOnClickListener(this);
        btn_nav.setOnClickListener(this);
        btn_float.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_update_frequent.setOnClickListener(this);
        btn_spoken.setOnClickListener(this);
        btn_step_count.setOnClickListener(this);
        btn_about.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_delete_layout_close.setOnClickListener(this);
        btn_change_city.setOnClickListener(this);

        int NewWorkType = HttpUtil.getAPNType(MyApplication.getContext());
        if (NewWorkType == 0) {
            Toast.makeText(this, "请开启网络后再城市列表查看城市天气信息", Toast.LENGTH_LONG).show();
        } else {
            init();
            Boolean isFirstLoading = SharePreUtil.getBoolean(getApplicationContext(), ConstantValue.ISFIRSTLOADING, true);
            if (isFirstLoading) {
                Intent intent = getIntent();
                String cityName = intent.getStringExtra("cityName");
                addCity(cityName);
                SharePreUtil.saveBoolean(getApplicationContext(), ConstantValue.ISFIRSTLOADING, false);
            }
        }
        /**
         * 下拉刷新手动更新天气信息
         */
        refresh_layout.setEnableAutoLoadMore(false);
        refresh_layout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestWeather(mWeatherId);
                refresh_layout.finishRefresh(2000/*,false*/);
                refresh_layout.setEnableLoadMore(true);
            }
        });
        refresh_layout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadAnimation = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.zoom_in);
                cl_delete.startAnimation(loadAnimation);
                cl_delete.setVisibility(View.VISIBLE);
                refresh_layout.finishLoadMore(2000);
                refresh_layout.setEnableLoadMore(true);

            }
        });
    }

    /**
     * 工具类、数据、图片初始化
     */
    private void init() {
        CitylistGet = getListFromLocal("CityList");
        if (CitylistGet != null) {
            CitylistSave.addAll(CitylistGet);
        }


        //讯飞语音播报初始化
        TTSUtils.getInstance().init();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
            mWeatherId = weather.basic.weatherId;
            if (mWeatherId==null||mWeatherId.equals("")){
                Weather weather6=Utility.handleWeather6Response(weatherString);
                WeatherId=weather6.basic.cityId;
                showWeatherInfo6(weather6);
            }else {
                showWeatherInfo(weather);
            }
        } else {
            //无缓存时区服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            weather_layout.setVisibility(View.VISIBLE);
            requestWeather(mWeatherId);
        }
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
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", forecast_bg);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(forecast_bg).into(iv_forecast_bg);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息错误", Toast.LENGTH_SHORT).show();
                        refresh_layout.setEnableRefresh(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                            Toast.makeText(WeatherActivity.this, "获取天气信息成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        refresh_layout.setDisableContentWhenRefresh(true);
                    }
                });
            }
        });
        loadForecastBg();
    }

    /**
     * 保存城市列表到SharedPreferences
     *
     * @param list
     * @param key
     */
    public void saveListInLocal(ArrayList<String> list, String key) {
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    /**
     * 从SharedPreferences中取出城市列表
     *
     * @param key
     */
    public ArrayList<String> getListFromLocal(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
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

        String hum = "相对湿度:" + weather.now.hum + "%";
        String wind_dir = weather.now.wind_dir;

        changeWeatherStatusPic(cond_code);
        tv_title.setText(cityName);
        tv_update_time.setText(updateTime);
        tv_degree.setText(degree);
        tv_weather_info.setText(weatherInfo);

        tv_weather_hum.setText(hum);
        tv_weather_wind_dir.setText(wind_dir);

        btn_float.setVisibility(View.VISIBLE);
        /**
         * 先移除逐小时天气列表view，再加载新数据到其中
         */
        hourly_layout.removeAllViews();
        for (Hourly hourly : weather.hourlyList) {
            View view = LayoutInflater.from(this).inflate(R.layout.hourly_item, hourly_layout, false);
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
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecast_layout, false);

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
        weather_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 处理并展示Weather6实体类中的数据
     */
    private void showWeatherInfo6(Weather weather) {
        String cityName = weather.basic.citylocation;
        String updateTime = weather.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.cond_txt;
        String cond_code = weather.now.cond_code;

        String hum = "相对湿度:" + weather.now.hum + "%";
        String wind_dir = weather.now.wind_dir;

        changeWeatherStatusPic(cond_code);
        tv_title.setText(cityName);
        tv_update_time.setText(updateTime);
        tv_degree.setText(degree);
        tv_weather_info.setText(weatherInfo);

        tv_weather_hum.setText(hum);
        tv_weather_wind_dir.setText(wind_dir);

        /**
         * 先移除逐小时天气列表view，再加载新数据到其中
         */
        hourly_layout.removeAllViews();
        for (Hourly hourly : weather.hourlyList) {
            View view = LayoutInflater.from(this).inflate(R.layout.hourly_item, hourly_layout, false);
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
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecast_layout, false);

            TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_date.setText(forecast.date);
            TextView tv_info = (TextView) view.findViewById(R.id.tv_info);
            tv_info.setText(forecast.cond_txt);
            TextView tv_max = (TextView) view.findViewById(R.id.tv_max);
            tv_max.setText(forecast.max + "℃");
            TextView tv_min = (TextView) view.findViewById(R.id.tv_min);
            tv_min.setText(forecast.min + "℃");
            forecast_layout.addView(view);
        }
        /**
         * 显示Weather6的空气质量
         */
        showAir6(cityName);
        mWeatherId = weather.basic.cityId;
        showSuggestion6(mWeatherId);

        weather_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示WEATHER6实体类中的空气质量
     */
    private void showAir6(String cityName) {
        this.cityName = cityName;
        final String requestAirUrl = "https://free-api.heweather.com/s6/air/now?location=" + cityName + "&key=0754508fbe0f44be915c65a0182b8aa9";
        Log.d(MyApplication.getContext().getPackageName(), "showAir6: " + requestAirUrl);
        HttpUtil.sendOkHttpRequest(requestAirUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeather6Response(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showAirInfo6(weather);
                        }
                    }
                });
            }
        });

    }

    /**
     * 显示添加城市时查询到的天气空气质量
     */
    private void showAirInfo6(Weather weather) {
        if (weather.air_now_city != null) {
            tv_aqi.setText(weather.air_now_city.aqi);
            tv_qlty.setText(weather.air_now_city.qlty);
            tv_pm25.setText(weather.air_now_city.pm25);
        }
    }

    /**
     * 显示WEATHER6实体类中的生活建议
     */
    private void showSuggestion6(String weatherId) {
        String requestSuggestionUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=0754508fbe0f44be915c65a0182b8aa9";
        Log.d(MyApplication.getContext().getPackageName(), "showSuggestion6: " + requestSuggestionUrl);
        HttpUtil.sendOkHttpRequest(requestSuggestionUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showSuggestionInfo6(weather);
                        }
                    }
                });
            }
        });

    }

    /**
     * 显示添加城市时查询到的天气生活建议
     */
    private void showSuggestionInfo6(Weather weather) {
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

    @SuppressWarnings("ConstantConditions")
    /**
     * 根据获取到的天气状态码动态更换天气状态图片
     */
    public void changeWeatherStatusPic(String code) {
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
            /**
             * 将获取到的城市名存储到列表中并写入SharedPreferences
             */
            CitylistSave.add(cityName);
            saveListInLocal(CitylistSave, "CityList");
            Log.e("saveArrayList:", "Save ArrayList success" + CitylistSave);
            Log.d("Locate", cityName);
            final AlertDialog dialog = new AlertDialog.Builder(WeatherActivity.this)
                    .setTitle("定位服务")
                    .setIcon(R.drawable.location)
                    .setCancelable(false)
                    .setMessage("努力定位中，请耐心等待...")
                    .show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    addCity(cityName);
                }
            }, 2000);

            mLocationClient.stop();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_nav:
                cl_menu.setVisibility(View.GONE);
                btn_float.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.btn_float:
                btn_float.setVisibility(View.GONE);
                drawerLayout.closeDrawer(Gravity.START);
                loadAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
                cl_menu.startAnimation(loadAnimation);
                cl_menu.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_change_city:
                /**
                 * 从SharedPreferences取出城市到列表中
                 */
                CitylistGet = getListFromLocal("CityList");
                //Log.e("getArrayList:", "Get ArrayList size" + CitylistGet.size());
                if (CitylistGet != null) {
                    View change_city_layout = View.inflate(this, R.layout.layout_change_city, null);
                    final ListView lv_change_city = change_city_layout.findViewById(R.id.lv_change_city);

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("城市列表")
                            .setView(change_city_layout)
                            .setAdapter(new ArrayAdapter<>(this,
                                    android.R.layout.simple_list_item_1, CitylistGet), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    addCity(CitylistGet.get(i));
                                    Toast.makeText(WeatherActivity.this, "已为你切换到" + CitylistGet.get(i), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(this, "你还没有添加其他城市呢", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_close:
                loadAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_close);
                cl_menu.startAnimation(loadAnimation);
                cl_menu.setVisibility(View.GONE);
                btn_float.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_add:
                showAddCityDialog("添加城市", null, "添加");
                break;
            case R.id.btn_delete_layout_close:
                loadAnimation = AnimationUtils.loadAnimation(WeatherActivity.this, R.anim.zoom_out);
                cl_delete.startAnimation(loadAnimation);
                cl_delete.setVisibility(View.GONE);
                break;
            case R.id.btn_update_frequent:
                Intent intent_update_frequent = new Intent(WeatherActivity.this, UpdateFrequentActivity.class);
                startActivity(intent_update_frequent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                cl_menu.setVisibility(View.GONE);
                break;
            case R.id.btn_step_count:
                Intent intent_step_count = new Intent(WeatherActivity.this, StepCountMainActivity.class);
                startActivity(intent_step_count);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                cl_menu.setVisibility(View.GONE);
                break;
            case R.id.btn_spoken:
                Intent intent_spoken_forecast = new Intent(WeatherActivity.this, SpokenForecastActivity.class);
                startActivity(intent_spoken_forecast);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                cl_menu.setVisibility(View.GONE);
                break;
            case R.id.btn_about:
                Intent intent_about = new Intent(WeatherActivity.this, AboutActivity.class);
                startActivity(intent_about);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                cl_menu.setVisibility(View.GONE);
                break;
            case R.id.btn_location:
                TranslateAnimation translateAnimation = new
                        TranslateAnimation(0, 0, 0, 20);
                translateAnimation.setDuration(300);
                translateAnimation.setRepeatCount(6);
                btn_location.startAnimation(translateAnimation);
                //绑定百度定位
                mLocationClient = new LocationClient(this);
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
                new AlertDialog.Builder(WeatherActivity.this)
                        .setTitle("警告")
                        .setIcon(R.drawable.warming)
                        .setMessage("确定要删除该城市天气吗？")
                        .setCancelable(false)
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String city_delete = tv_title.getText().toString();
                                CitylistGet = getListFromLocal("CityList");
                                CitylistGet.remove(city_delete);
                                saveListInLocal(CitylistGet, "CityList");
                                addCity(CitylistGet.get(0));
                                Toast.makeText(WeatherActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(WeatherActivity.this, "取消", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }

    /**
     * 显示添加城市弹窗
     */
    private String showAddCityDialog(String title, String massage, String bt1Text) {
        LayoutInflater inflater = (LayoutInflater) WeatherActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_add_dialog, null);
        new AlertDialog.Builder(WeatherActivity.this)
                .setTitle(title)
                .setIcon(R.drawable.introduction)
                .setMessage(massage)
                .setView(view)
                .setCancelable(true)
                .setPositiveButton(bt1Text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //先判断现在手机的网络状态
                        int NewWorkType = HttpUtil.getAPNType(MyApplication.getContext());
                        if (NewWorkType == 0) {
                            Toast.makeText(WeatherActivity.this, "没有网络，我什么都帮不了你哦！", Toast.LENGTH_SHORT).show();
                        } else {
                            final AlertDialog tip_dialog = new AlertDialog.Builder(WeatherActivity.this)
                                    .setTitle("提示")
                                    .setMessage("以后只能在这里添加你经常查看的城市哟")
                                    .show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tip_dialog.dismiss();
                                    EditText et_add_city = (EditText) view.findViewById(R.id.et_add_city);
                                    String cityText = et_add_city.getText().toString();
                                    /**
                                     * 先取到库中的城市列表，再将需要添加的城市名加进去再存储
                                     * 将获取到的城市名存储到列表中并写入SharedPreferences
                                     */
                                    if (getListFromLocal("CityList") == null) {
                                        CitylistSave.add(cityText);
                                        saveListInLocal(CitylistSave, "CityList");
                                    } else {
                                        CitylistGet = getListFromLocal("CityList");
                                        CitylistGet.add(cityText);
                                        saveListInLocal(CitylistGet, "CityList");
                                    }
                                    Log.e("saveArrayList:", "Save ArrayList success" + CitylistSave);

                                    addCity(cityText);
                                }
                            }, 1500);
                        }
                    }
                })
                .show();
        return null;
    }

    /**
     * 添加城市实现代码
     *
     * @param cityName
     */
    public void addCity(final String cityName) {
        final String requestUrl = "https://free-api.heweather.com/s6/weather?location=" + cityName + "&key=0754508fbe0f44be915c65a0182b8aa9";
        Log.d(MyApplication.getContext().getPackageName(), "addCity: " + requestUrl);
        HttpUtil.sendOkHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeather6Response(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            //mWeatherId = weather.basic.weatherId;

                            showWeatherInfo6(weather);
                            cl_menu.setVisibility(View.GONE);
                            btn_float.setVisibility(View.VISIBLE);
                            Toast.makeText(WeatherActivity.this, "获取天气信息成功", Toast.LENGTH_SHORT).show();
                        } else {
                            cl_menu.setVisibility(View.GONE);
                            btn_float.setVisibility(View.VISIBLE);
                            //将刚才加进来的错误城市名从城市列表中移除
                            CitylistGet = getListFromLocal("CityList");
                            int i = CitylistGet.size();
                            CitylistSave.remove(CitylistGet.get(i - 1));
                            saveListInLocal(CitylistSave, "CityList");
                            //提醒用户
                            Snackbar.make(btn_location, "查询不到该城市天气信息", Snackbar.LENGTH_LONG)
                                    .setAction("确认", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(WeatherActivity.this, "请确认后再重新输入", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show();
                        }
                        refresh_layout.setDisableContentWhenRefresh(true);
                    }
                });
            }
        });
        loadForecastBg();
    }

    /**
     * 改写返回按钮事件，单击提示，双击退出
     *
     * @param keyCode
     * @param event
     * @return
     */
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重写Activity生命周期的Restart()方法
     */
    @Override
    protected void onRestart() {
        init();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        //先停止再销毁
        MyApplication.stopWakeuper();
        MyApplication.destroyWakeuper();
        unbinder.unbind();
        super.onDestroy();
    }
}
