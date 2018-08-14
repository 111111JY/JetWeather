package com.example.aiiage.jetweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aiiage.jetweather.service.SpokenForecastService;
import com.example.aiiage.jetweather.util.HttpUtil;
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
        Log.d(SpokenForecastActivity.class.getSimpleName(), "onCreate: SpokenForecastActivity");
        setContentView(R.layout.layout_spoken_forecast);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //读取上次设置的播报时间
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String time_has_set=prefs.getString("tv_time",null);
        tv_set_time.setText(time_has_set);

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
                int NewWorkType = HttpUtil.getAPNType(MyApplication.getContext());
                if (NewWorkType == 0){
                    Toast.makeText(SpokenForecastActivity.this, "你不打开网络，臣妾办不到啊！", Toast.LENGTH_SHORT).show();
                }else {
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
            }
        });
        btn_stop_spoken_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int NewWorkType = HttpUtil.getAPNType(MyApplication.getContext());
                if (NewWorkType == 0){
                    Toast.makeText(SpokenForecastActivity.this, "你不打开网络，臣妾办不到啊！", Toast.LENGTH_SHORT).show();
                }else {
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
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(SpokenForecastActivity.class.getSimpleName(), "onRestart: SpokenForecastActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(SpokenForecastActivity.class.getSimpleName(), "onPause: SpokenForecastActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(SpokenForecastActivity.class.getSimpleName(), "onStart: SpokenForecastActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(StepCountMainActivity.class.getSimpleName(), "onResume: SpokenForecastActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(SpokenForecastActivity.class.getSimpleName(), "onStop: SpokenForecastActivity");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
        Log.d(SpokenForecastActivity.class.getSimpleName(), "finish: SpokenForecastActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(SpokenForecastActivity.class.getSimpleName(), "onDestroy: SpokenForecastActivity");
        finish();
    }
}
