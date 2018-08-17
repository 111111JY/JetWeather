package com.example.aiiage.jetweather;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.aiiage.jetweather.gson.Weather;
import com.example.aiiage.jetweather.util.HttpUtil;
import com.example.aiiage.jetweather.util.SharePreUtil;
import com.example.aiiage.jetweather.viewspread.helper.BaseViewHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 危伟俊 on 2018/7/31.
 */
public class AddCityActivity extends Activity {

    @BindView(R.id.tv_location_city)
    TextView tv_location_city;
    @BindView(R.id.tv_next_step)
    TextView tv_next_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;
    @BindView(R.id.btn_big_location)
    Button btn_big_location;
    private LocationClient mLocationClient;
    private MyLocationListener myLocationListener;
    String canNext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_locate);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ButterKnife.bind(this);
        mLocationClient = new LocationClient(this);
        myLocationListener = new MyLocationListener(tv_location_city);
        mLocationClient.registerLocationListener(myLocationListener);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(AddCityActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(AddCityActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(AddCityActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.INTERNET);
        }
        if (ContextCompat.checkSelfPermission(AddCityActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(AddCityActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(AddCityActivity.this, permissions, 1);
        } else {
            initLocation();
            /**
             * 判断打开软件时是否开启了网络
             * 如果没开启网络就设置下一步按钮不可用，点击定位按钮，再次判断网络状态，若开启了网络，就开启定位，否则提示用户
             * 开启网络状态，直接可以根据定位到的城市，点击下一步进入天气信息展示页
             */
            int NetWorkType = HttpUtil.getAPNType(MyApplication.getContext());
            if (NetWorkType == 0) {
                tv_location_city.setText("无法定位");
                tv_next_step.setClickable(false);
                btn_next_step.setClickable(false);
                new AlertDialog.Builder(AddCityActivity.this)
                        .setTitle("没有网路")
                        .setMessage("没有网络我们也帮不了你哦。")
                        .setIcon(R.drawable.wifi)
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(AddCityActivity.this, "现在就去打开网络吧", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                btn_big_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int NetWorkType1 = HttpUtil.getAPNType(MyApplication.getContext());
                        if (NetWorkType1 == 0) {
                            Toast.makeText(AddCityActivity.this, "请开启网络再重试！", Toast.LENGTH_SHORT).show();
                        } else {
                            mLocationClient.start();
                            Toast.makeText(AddCityActivity.this, "定位成功了", Toast.LENGTH_SHORT).show();
                            tv_next_step.setClickable(true);
                            btn_next_step.setClickable(true);
                            tv_next_step.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String cityName = tv_location_city.getText().toString();
                                    Intent intent = new Intent(AddCityActivity.this, WeatherActivity.class);
                                    intent.putExtra("cityName", cityName);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                                    //startActivity(intent);
                                    finish();
                                }
                            });
                            btn_next_step.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String cityName = tv_location_city.getText().toString();
                                    Intent intent = new Intent(AddCityActivity.this, WeatherActivity.class);
                                    intent.putExtra("cityName", cityName);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                                    //startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                });
            } else {
                btn_big_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TranslateAnimation translateAnimation = new
                                TranslateAnimation(0, 0, 0, 50);
                        translateAnimation.setDuration(200);
                        translateAnimation.setRepeatCount(5);
                        btn_big_location.startAnimation(translateAnimation);
                        mLocationClient.start();
                    }
                });

                tv_next_step.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        canNext = tv_location_city.getText().toString();
                        if (canNext.equals("请点击定位") || canNext.equals("无法定位")) {
                            Toast.makeText(getApplication(), "请定位后再下一步", Toast.LENGTH_SHORT).show();
                        } else {
                            String cityName = tv_location_city.getText().toString();
                            Intent intent = new Intent(AddCityActivity.this, WeatherActivity.class);
                            intent.putExtra("cityName", cityName);
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                            //startActivity(intent);
                            finish();
                        }
                    }
                });
                btn_next_step.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        canNext = tv_location_city.getText().toString();
                        if (canNext.equals("请点击定位") || canNext.equals("无法定位")) {
                            Toast.makeText(getApplication(), "请定位后再下一步", Toast.LENGTH_SHORT).show();
                        } else {
                            String cityName = tv_location_city.getText().toString();
                            Intent intent = new Intent(AddCityActivity.this, WeatherActivity.class);
                            intent.putExtra("cityName", cityName);
                            startActivity(intent);
                            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                            //startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }
    }

    void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
    }

    class MyLocationListener implements BDLocationListener {
        TextView tv_location_city;

        MyLocationListener(TextView cityName) {
            tv_location_city = cityName;
        }

        String cityName;

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            cityName = bdLocation.getCity();
            Log.d("Locate", cityName);
            tv_location_city.setText(cityName);
            mLocationClient.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Snackbar.make(btn_big_location, "必须同意所有权限才能使用本程序所有功能", Snackbar.LENGTH_LONG)
                                    .setAction("确认", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    })
                                    .show();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发送未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}

