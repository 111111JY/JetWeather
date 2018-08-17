package com.example.aiiage.jetweather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.aiiage.jetweather.AddCityActivity;
import com.example.aiiage.jetweather.MainActivity;
import com.example.aiiage.jetweather.MyApplication;
import com.example.aiiage.jetweather.R;
import com.example.aiiage.jetweather.WeatherActivity;
import com.example.aiiage.jetweather.util.ConstantValue;
import com.example.aiiage.jetweather.util.SharePreUtil;

/**
 * Created by 危伟俊 on 2018/8/1.
 */
public class SplashActivity extends AppCompatActivity {
    private MyCountDownTimer mCountDownTimer;
    private Button btn_skip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_activity);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        btn_skip = (Button) findViewById(R.id.btn_skip);
        btn_skip.setText("3s 跳过");

        //创建倒计时类
        mCountDownTimer = new MyCountDownTimer(3000, 1000);
        mCountDownTimer.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Boolean isFirst = SharePreUtil.getBoolean(getApplicationContext(), ConstantValue.ISFIRST, true);
                if (isFirst) {
                    Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                    //将isFirst改为false,并且在本地持久化
                    SharePreUtil.saveBoolean(getApplicationContext(), ConstantValue.ISFIRST, false);
                } else {
                    //进入应用程序主界面
                    Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                }
                finish();
            }
        }, 3000);
    }

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以「 毫秒 」为单位倒计时的总数
         *                          例如 millisInFuture = 1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick()
         *                          例如: countDownInterval = 1000 ; 表示每 1000 毫秒调用一次 onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            btn_skip.setText("0S 跳过");
        }

        public void onTick(long millisUntilFinished) {
            btn_skip.setText(millisUntilFinished / 1000 + "S 跳过");
        }
    }

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onDestroy();
    }
}
