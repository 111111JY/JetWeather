package com.example.aiiage.jetweather;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;

import com.example.aiiage.jetweather.gson.Weather;
import com.example.aiiage.jetweather.util.TTSUtils;
import com.example.aiiage.jetweather.util.Utility;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.litepal.LitePal;

import java.lang.reflect.Field;

import static android.support.constraint.Constraints.TAG;


/**
 * Created by 危伟俊 on 2018/7/27.
 * 作用：提供一个全局Context以及沉浸式状态栏
 */
public class MyApplication extends Application {
    public static float mDensity;
    private static View mStatusBarView;
    private static Context context;
    private static VoiceWakeuper voiceWake;
    private SpeechUnderstander understander;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        context = getApplicationContext();
        SpeechUtility.createUtility(MyApplication.getContext(), SpeechConstant.APPID + "=5b62eb18");
        Setting.setShowLog(true);
        //初始化唤醒对象
        voiceWake = VoiceWakeuper.createWakeuper(MyApplication.this, null);

        //初始化讯飞语音
        // 初始化工具类
        //讯飞语音播报初始化
        TTSUtils.getInstance().init();
        initScreenSize();
        //获取唤醒词
        getResource();
        //初始化设置--语音理解对象，语音唤醒对象，语音合成对象
        initSet();
    }

    private static void initScreenSize() {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mDensity = dm.density;
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 获取唤醒词等唤醒资源
     */
    private String getResource() {
        /**
         * 加载唤醒资源
         * */
        StringBuffer param = new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(MyApplication.this,
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/5b62eb18.jet");
        param.append(SpeechConstant.IVW_RES_PATH + "=" + resPath);
        param.append("," + ResourceUtil.ENGINE_START + "=" + SpeechConstant.ENG_IVW);

        return ResourceUtil.generateResourcePath(MyApplication.this,
                ResourceUtil.RESOURCE_TYPE.assets, "ivw/5b62eb18.jet");
    }

    /**
     * 初始化设置--语音理解对象，语音唤醒对象，语音合成对象
     */
    private void initSet() {
        //语音理解设置
        understander=SpeechUnderstander.createUnderstander(MyApplication.getContext(),null);
        //understander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //语音合成设置
        //语音唤醒设置
        voiceWake.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
//		voiceWake.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarID);
        voiceWake.setParameter(SpeechConstant.IVW_THRESHOLD, "0:1650;1:1650;2:1650" );
        voiceWake.setParameter(SpeechConstant.ASR_THRESHOLD, "80");
        voiceWake.setParameter(SpeechConstant.IVW_SST, "wakeup");
        voiceWake.setParameter(SpeechConstant.KEEP_ALIVE, "1");
        voiceWake.setParameter(SpeechConstant.IVW_NET_MODE, "" + 0);
        voiceWake.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
        voiceWake.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "request_focus");
        voiceWake.startListening(mWakeuperListener);
    }

    /**
     * 销毁唤醒功能
     */
    public  static  void destroyWakeuper() {
        // 销毁合成对象
        voiceWake = VoiceWakeuper.getWakeuper();
        if (voiceWake != null) {
            voiceWake.destroy();
        }
    }

    /**
     * 停止唤醒
     */
    public static  void stopWakeuper() {
        voiceWake.stopListening();
    }

    /**
     * 唤醒监听器
     */
    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        @Override
        public void onResult(WakeuperResult results) {
            Log.d(MyApplication.getContext().getPackageName(), "onResult: ---------------------------------------程序被唤醒了");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
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
        }

        @Override
        public void onBeginOfSpeech() {
            Log.d(MyApplication.getContext().getPackageName(), "onBeginOfSpeech: -------------------------------------------正在录音");
            // TODO Auto-generated method stub
        }

        @Override
        public void onError(SpeechError arg0) {
            Log.d(MyApplication.getContext().getPackageName(), "onError: ---------------------------------------------------识别出错");
            // TODO Auto-generated method stub
        }

        @Override
        public void onVolumeChanged(int arg0) {
            // TODO Auto-generated method stub
        }
    };

    //=============沉侵式==(begin)=================

    /**
     * 设置全屏沉侵式效果
     */
    public static void setNoStatusBarFullMode(Activity activity) {
        // sdk 4.4
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            if (mStatusBarView != null) {
                ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
                root.removeView(mStatusBarView);
            }
            return;
        }

        // sdk 5.x
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setStatusBarColor(Color.TRANSPARENT);
            return;
        }
    }

    /**
     * 设置控件的paddingTop, 使它不被StatusBar覆盖
     */
    public static void setStatusBarPadding(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int marginTop = getStatusBarHeight(view.getContext());
            view.setPadding(view.getPaddingLeft(), marginTop,
                    view.getPaddingRight(), view.getPaddingBottom());
            return;
        }
    }


    public static void setStatusBarColor(Activity activity, int statusColor) {
        // sdk 4.4
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
                if (mStatusBarView == null) {
                    //为了适配一些特殊机型的状态栏颜色无法改变，同时高度和系统原生的高度区别，所以这里重新创建一个View用于覆盖状态栏来实现效果
                    mStatusBarView = new View(activity);
                    mStatusBarView.setBackgroundColor(statusColor);
                } else {
                    // 先解除父子控件关系，否则重复把一个控件多次
                    // 添加到其它父控件中会出错
                    ViewParent parent = mStatusBarView.getParent();
                    if (parent != null) {
                        ViewGroup viewGroup = (ViewGroup) parent;
                        if (viewGroup != null)
                            viewGroup.removeView(mStatusBarView);
                    }
                }
                ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        getStatusBarHeight(activity));
                root.addView(mStatusBarView, param);
            }
            return;
        }

        // sdk 5.x
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(statusColor);
            return;
        }
    }

    /**
     * 通过反射的方式获取状态栏高度，
     * 一般为24dp，有些可能较特殊，所以需要反射动态获取
     */
    private static int getStatusBarHeight(Context context) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object obj = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int id = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("-------无法获取到状态栏高度");
        }
        return dp2px(24);
    }

    public static int dp2px(int dp) {
        return (int) (dp * mDensity);
    }
    //=============沉侵式==(end)=================
}
