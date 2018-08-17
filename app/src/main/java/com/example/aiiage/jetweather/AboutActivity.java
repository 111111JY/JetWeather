package com.example.aiiage.jetweather;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aiiage.jetweather.gson.Version;
import com.example.aiiage.jetweather.gson.Weather;
import com.example.aiiage.jetweather.service.DownloadService;
import com.example.aiiage.jetweather.util.HttpUtil;
import com.example.aiiage.jetweather.util.Utility;
import com.example.aiiage.jetweather.viewspread.helper.BaseViewHelper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 危伟俊 on 2018/8/1.
 */
public class AboutActivity extends AppCompatActivity {

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    BaseViewHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);//启动服务
        bindService(intent, connection, BIND_AUTO_CREATE);//绑定服务
        if (ContextCompat.checkSelfPermission(AboutActivity.this, Manifest
                .permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AboutActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }

        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        try {
            String Version_Name = getVersionName();
            tv_version.setText("版本号   " + Version_Name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button btn_check_version = (Button) findViewById(R.id.btn_check_version);
        btn_check_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AboutActivity.this, "版本检测中...", Toast.LENGTH_SHORT).show();
                /**
                 * 获取服务器apk的版本信息地址
                 */
                String version_check_url = "";
                requestVersionName(version_check_url);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int local_version_code = getVersionCode();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
                            //String apk_version_name=prefs.getString("Version_Name","1.0");
                            int apk_version_code = prefs.getInt("Version_Code", 1);
                            //判断是否需要更新
                            if (local_version_code < apk_version_code) {
                                new AlertDialog.Builder(AboutActivity.this)
                                        .setTitle("版本更新")
                                        .setMessage("检测到新的版本，是否下载新版本安装？")
                                        .setIcon(R.drawable.checkversion)
                                        .setCancelable(false)
                                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                /**
                                                 * 新的安装包所在地址
                                                 */
                                                // String url = "";
                                                //downloadBinder.startDownload(url);
                                                Toast.makeText(AboutActivity.this, "更新中", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                // downloadBinder.cancelDownload();
                                                Toast.makeText(AboutActivity.this, "取消", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .show();
                            } else {
                                new AlertDialog.Builder(AboutActivity.this)
                                        .setTitle("版本更新")
                                        .setMessage("已是最新版本，不需要更新了。")
                                        .setCancelable(true)
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1500);

            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in,R.anim.zoom_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用该功能", Toast.LENGTH_SHORT).show();
                }
            default:
        }
    }

    /**
     * 获取当前程序的版本名
     */
    private String getVersionName() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        Log.e("TAG", "版本号" + packInfo.versionCode);
        Log.e("TAG", "版本名" + packInfo.versionName);
        return packInfo.versionName;
    }

    /**
     * 获取当前程序的版本号
     */
    private int getVersionCode() throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        Log.e("TAG", "版本号" + packInfo.versionCode);
        Log.e("TAG", "版本名" + packInfo.versionName);
        return packInfo.versionCode;
    }

    /**
     * 获取服务器apk文件的版本名、版本号
     *
     * @param url
     */
    public void requestVersionName(String url) {
        String Url = "http://guolin.tech/api/weather?cityid=";
        Log.d(MyApplication.getContext().getPackageName(), "requestWeather: " + Url);
        HttpUtil.sendOkHttpRequest(Url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AboutActivity.this, "获取版本信息错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Version version = Utility.handleVersion(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (version != null) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(AboutActivity.this).edit();
                            editor.putString("version", responseText);
                            editor.apply();
                            Log.d(MyApplication.getContext().getPackageName(), "run: -----------------------" + version.toString());
                        }
                    }
                });
            }
        });
    }

}
