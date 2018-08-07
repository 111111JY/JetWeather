package com.example.aiiage.jetweather;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.aiiage.jetweather.viewspread.view.CustomVideoView;

import static android.graphics.Color.TRANSPARENT;

/**
 * Created by 危伟俊 on 2018/8/3.
 */
public class VideoActivity extends AppCompatActivity {

    CustomVideoView videoview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_video);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        MyApplication.setNoStatusBarFullMode(VideoActivity.this);
        init();
    }

    private void init() {
         videoview = (CustomVideoView) findViewById(R.id.videoview);
        //设置播放加载路径
        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.weather));
        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });
        Button btn_next_activity = (Button) findViewById(R.id.btn_next_step);
        btn_next_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 记得在销毁的时候让播放的视频终止
                 */
                if (videoview != null) {
                    videoview.stopPlayback();
                }
                Intent intent = new Intent(VideoActivity.this, AddCityActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
