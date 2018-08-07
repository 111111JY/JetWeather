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
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.aiiage.jetweather.AboutActivity;
import com.example.aiiage.jetweather.MyApplication;
import com.example.aiiage.jetweather.R;
import com.example.aiiage.jetweather.WeatherActivity;
import com.example.aiiage.jetweather.gson.Weather;
import com.example.aiiage.jetweather.util.HttpUtil;
import com.example.aiiage.jetweather.util.TTSUtils;
import com.example.aiiage.jetweather.util.Utility;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SpokenForecastService extends Service {
    public SpokenForecastService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int hour = prefs.getInt("hour", 0);
        int minute = prefs.getInt("minute", 0);
        //语音播报实现方法
//        speak_forecast();
        java.util.Calendar calendar = java.util.Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.setTimeInMillis(SystemClock.elapsedRealtime());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, SpokenForecastService.class);
        PendingIntent pi = PendingIntent.getService(MyApplication.getContext(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(pi);
//        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
//            // manager.cancel(pi);
//            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
//            speak_forecast();
//        } else {
//            //manager.cancel(pi);
//            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
//            speak_forecast();
//        }
        speak_forecast();

        /**
         * 参数说明
         * AlarmManager.RTC，硬件闹钟，不唤醒手机（也可能是其它设备）休眠；当手机休眠时不发射闹钟。
         AlarmManager.RTC_WAKEUP，硬件闹钟，当闹钟发躰时唤醒手机休眠；
         AlarmManager.ELAPSED_REALTIME，真实时间流逝闹钟，不唤醒手机休眠；当手机休眠时不发射闹钟。
         AlarmManager.ELAPSED_REALTIME_WAKEUP，真实时间流逝闹钟，当闹钟发躰时唤醒手机休眠；
         RTC闹钟和ELAPSED_REALTIME最大的差别就是前者可以通过修改手机时间触发闹钟事件，后者要通过真实时间的流逝，即使在休眠状态，时间也会被计算。
         */
        //manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_DAY, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 语音播报天气实现方法
     */
    private void speak_forecast() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        Weather weather = Utility.handleWeatherResponse(weatherString);
        String tdcityname = weather.basic.citylocation;
        String tdcityforecast = weather.now.cond_txt;
        String tdtmpnow = weather.now.temperature;
        String tdtmpmax = weather.forecastList.get(0).temperature.max;
        String tdtmpmin = weather.forecastList.get(0).temperature.min;
        String tdair = weather.aqi.city.qlty;
        String tdsuggestion = weather.suggestion.comfort.info;
        String weather_string = tdcityname + "，今天天气，" + tdcityforecast + "，现在温度，" + tdtmpnow + "摄氏度"
                + ",最高温度，" + tdtmpmax + "摄氏度，" + "最低温度，" + tdtmpmin + "摄氏度," + "空气总体质量," + tdair + "," + tdsuggestion;
        TTSUtils.getInstance().speak(weather_string);
        notifyUser();
    }
    /**
     * 发送一个通知到通知栏告诉用户简易天气信息
     */
    private void notifyUser(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        Weather weather = Utility.handleWeatherResponse(weatherString);
        String tdcityname = weather.basic.citylocation;
        String tdcityforecast = weather.now.cond_txt;
        String tdtmpmax = weather.forecastList.get(0).temperature.max;
        String tdtmpmin = weather.forecastList.get(0).temperature.min;
        String tdair=weather.aqi.city.qlty;

        String weather_string = tdcityname + "   " + tdcityforecast + "   " +"空气质量 "+tdair+ "   最高温度 " + tdtmpmax + "℃" + "  最低温度" + tdtmpmin + "℃";
        Intent intent = new Intent(this, WeatherActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification=new NotificationCompat.Builder(MyApplication.getContext())
                .setContentTitle("今天天气信息")
                .setContentText(weather_string)
                .setTicker("收到JetWeather的今天天气消息通知")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND| Notification.DEFAULT_VIBRATE)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.app_weather_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.weather_icon))
                .build();
        manager.notify(1,notification);
    }
}
