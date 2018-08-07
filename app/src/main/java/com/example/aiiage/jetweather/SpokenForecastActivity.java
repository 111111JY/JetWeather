package com.example.aiiage.jetweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aiiage.jetweather.service.SpokenForecastService;
import com.example.aiiage.jetweather.util.TTSUtils;
import com.example.aiiage.jetweather.viewspread.helper.BaseViewHelper;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 危伟俊 on 2018/8/3.
 */
public class SpokenForecastActivity extends AppCompatActivity {

    @BindView(R.id.btn_start_spoken_service)
    TextView btn_start_spoken_service;
    @BindView(R.id.btn_stop_spoken_service)
    TextView btn_stop_spoken_service;
    @BindView(R.id.tp_calendarView)
    TimePicker tp_calendarView;
    @BindView(R.id.tv_set_time)
    TextView tv_set_time;
    BaseViewHelper helper;

    private int hour, minute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_spoken_forecast);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //读取上次设置的播报时间
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String time_has_set=prefs.getString("tv_time",null);
        tv_set_time.setText(time_has_set);

        //过度动画设置
        helper = new BaseViewHelper
                .Builder(SpokenForecastActivity.this)
                .isFullWindow(true)//是否全屏显示
                .isShowTransition(true)//是否显示过渡动画
                .setDimColor(Color.BLACK)//遮罩颜色
                .setDimAlpha(200)//遮罩透明度
                .create();//开始动画

        tp_calendarView.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int h, int m) {
                hour = h;
                minute = m;
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(SpokenForecastActivity.this).edit();
                editor.putInt("hour", hour);
                editor.putInt("minute", minute);
                editor.apply();
            }
        });

        btn_start_spoken_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SpokenForecastActivity.this, "已经为你设置好了每天" + hour + "时" + minute + "分" + "语音播报天气预报", Toast.LENGTH_LONG).show();
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(SpokenForecastActivity.this).edit();

                tv_set_time.setText("你设置的每天语音播报时间为： " + hour + " 时 " + minute + " 分 ");
                String tv_time = tv_set_time.getText().toString();
                editor.putString("tv_time", tv_time);
                editor.apply();

                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent i = new Intent(SpokenForecastActivity.this, SpokenForecastService.class);
                PendingIntent pi = PendingIntent.getService(MyApplication.getContext(), 0, i, 0);
                //如果设置时间小于当前时间，语音播报就设置在明天
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    // manager.cancel(pi);
                    manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
                } else {
                    //manager.cancel(pi);
                    manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                }
//                Intent intent_start_spoken_service =
//                        new Intent(SpokenForecastActivity.this, SpokenForecastService.class);
//                startService(intent_start_spoken_service);
            }
        });
        btn_stop_spoken_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SpokenForecastActivity.this, "已经为你取消语音播报功能", Toast.LENGTH_LONG).show();
                tv_set_time.setText("你还没有开启天气预报语音播报功能 ");
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(SpokenForecastActivity.this).edit();
                String tv_time = tv_set_time.getText().toString();
                editor.putString("tv_time", tv_time);
                editor.apply();

                Intent intent_stop_spoken_service =
                        new Intent(SpokenForecastActivity.this, SpokenForecastService.class);
                stopService(intent_stop_spoken_service);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (helper != null && helper.isShowing()) {
            helper.backActivity(this);
        } else {
            super.onBackPressed();
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
