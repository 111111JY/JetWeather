package com.example.aiiage.jetweather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.aiiage.jetweather.MyApplication;
import com.example.aiiage.jetweather.R;
import com.example.aiiage.jetweather.WeatherActivity;
import com.example.aiiage.jetweather.gson.Weather;
import com.example.aiiage.jetweather.util.HttpUtil;
import com.example.aiiage.jetweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private static final String TAG="Update";
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences pref  = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isUpdateTime = pref.getBoolean("isUpdateTime", true);
        if (isUpdateTime){
            updateWeather();
            updateBingPic();
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            int autoUpdateTime = pref.getInt("autoUpdateTime", 60);
            int anHour = autoUpdateTime  * 60 * 1000;
            long triggerAtTime = System.currentTimeMillis() + anHour;
            //long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            Intent i = new Intent(this, AutoUpdateService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            manager.cancel(pi);
            //setRepeating():唤醒类型、第一次执行延迟时间、间隔时间、PendingIntent
            manager.setRepeating(AlarmManager.RTC_WAKEUP,triggerAtTime,triggerAtTime,pi);
            notifyUser();
            //manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=0754508fbe0f44be915c65a0182b8aa9";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this)
                                .edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        Log.d(TAG, "onResponse: "+weather.toString());
                    }
                }
            });
        }
    }

    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/.api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this)
                        .edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                Log.d(TAG, "onResponse: --------成功");
            }
        });

    }

    private void notifyUser(){
        Intent intent = new Intent(this, WeatherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification=new NotificationCompat.Builder(MyApplication.getContext())
                .setContentTitle("Tips")
                .setContentText("天气信息更新成功了，快去看看吧！")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND| Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.app_weather_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.weather_icon))
                .build();
        manager.notify(1,notification);
    }
}
