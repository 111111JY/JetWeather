package com.example.aiiage.jetweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.aiiage.jetweather.service.AutoUpdateService;
import com.example.aiiage.jetweather.service.AutoUpdateService6;
import com.example.aiiage.jetweather.viewspread.helper.BaseViewHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 危伟俊 on 2018/8/2.
 */
public class UpdateFrequentActivity extends AppCompatActivity {
    @BindView(R.id.rg_frequent_list)
    RadioGroup rg_frequent_list;
    @BindView(R.id.rb_frequent_never)
    RadioButton rb_frequent_never;
    @BindView(R.id.rb_frequent_2hour)
    RadioButton rb_frequent_2hour;
    @BindView(R.id.rb_frequent_4hour)
    RadioButton rb_frequent_4hour;
    @BindView(R.id.rb_frequent_8hour)
    RadioButton rb_frequent_8hour;
    @BindView(R.id.rb_frequent_24hour)
    RadioButton rb_frequent_24hour;

    BaseViewHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.setStatusBarColor(UpdateFrequentActivity.this, Color.TRANSPARENT);
        setContentView(R.layout.layout_update_frequent);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        //过度动画设置
        helper = new BaseViewHelper
                .Builder(UpdateFrequentActivity.this)
                .isFullWindow(true)//是否全屏显示
                .isShowTransition(true)//是否显示过渡动画
                .setDimColor(Color.BLACK)//遮罩颜色
                .setDimAlpha(200)//遮罩透明度
                .create();//开始动画
        //用于选中radio button的保存
        rb_frequent_never.setId(R.id.rb_frequent_never);
        rb_frequent_2hour.setId(R.id.rb_frequent_2hour);
        rb_frequent_4hour.setId(R.id.rb_frequent_4hour);
        rb_frequent_8hour.setId(R.id.rb_frequent_8hour);
        rb_frequent_24hour.setId(R.id.rb_frequent_24hour);
        //从SharedPreferences中读出选中的radio button
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UpdateFrequentActivity.this);
        //读取选中radio button
        int choose_button_id = prefs.getInt("choose_button_id",0);
        rg_frequent_list.check(choose_button_id);

        rg_frequent_list.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit();
                switch (i) {
                    case R.id.rb_frequent_never:
                        editor.putBoolean("isUpdateTime", false);
                        editor.putInt("choose_button_id",R.id.rb_frequent_never);
                        rb_frequent_never.setChecked(true);
                            break;
                    case R.id.rb_frequent_2hour:
                        editor.putBoolean("isUpdateTime",true);
                        editor.putInt("autoUpdateTime", 120);
                        editor.putInt("choose_button_id",R.id.rb_frequent_2hour);
                        rb_frequent_2hour.setChecked(true);
                        break;
                    case R.id.rb_frequent_4hour:
                        editor.putBoolean("isUpdateTime",true);
                        editor.putInt("autoUpdateTime", 240);
                        editor.putInt("choose_button_id",R.id.rb_frequent_4hour);
                        rb_frequent_4hour.setChecked(true);
                        break;
                    case R.id.rb_frequent_8hour:
                        editor.putBoolean("isUpdateTime",true);
                        editor.putInt("autoUpdateTime", 480);
                        editor.putInt("choose_button_id",R.id.rb_frequent_8hour);
                        rb_frequent_8hour.setChecked(true);
                        break;
                    case R.id.rb_frequent_24hour:
                        editor.putBoolean("isUpdateTime",true);
                        editor.putInt("autoUpdateTime", 1440);
                        editor.putInt("choose_button_id",R.id.rb_frequent_24hour);
                        rb_frequent_24hour.setChecked(true);
                        break;
                    default:
                }
                editor.apply();
                Intent intent = new Intent(UpdateFrequentActivity.this, AutoUpdateService.class);
                //Intent intent6 = new Intent(UpdateFrequentActivity.this, AutoUpdateService6.class);
                startService(intent);
                Toast.makeText(UpdateFrequentActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
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
