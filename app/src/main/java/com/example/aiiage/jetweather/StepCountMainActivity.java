package com.example.aiiage.jetweather;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aiiage.jetweather.step.step.UpdateUiCallBack;
import com.example.aiiage.jetweather.step.step.service.StepService;
import com.example.aiiage.jetweather.step.step.utils.DbUtils;
import com.example.aiiage.jetweather.step.step.utils.SharedPreferencesUtils;
import com.example.aiiage.jetweather.util.SharePreUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import step.step.bean.StepData;
import step.view.StepArcView;


/**
 * 记步主页
 */
public class StepCountMainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_data;
    private StepArcView cc;
    private TextView tv_set;
    private TextView tv_isSupport;
    private TextView tv_cal;
    private TextView tv_reduce_fat;
    private Button btn_calculation;
    private SharedPreferencesUtils sp;
    private int default_weight = 60;

    private void assignViews() {
        tv_data = (TextView) findViewById(R.id.tv_data);
        cc = (StepArcView) findViewById(R.id.cc);
        tv_set = (TextView) findViewById(R.id.tv_set);
        tv_isSupport = (TextView) findViewById(R.id.tv_isSupport);
        btn_calculation = (Button) findViewById(R.id.btn_calculation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_step_count_main_activity);
        Log.d(StepCountMainActivity.class.getSimpleName(), "onCreate: StepCountMainActivity");
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        assignViews();
        initData();
        addListener();

        tv_cal = (TextView) findViewById(R.id.tv_cal);
        tv_reduce_fat = (TextView) findViewById(R.id.tv_reduce_fat);
    }


    private void addListener() {
        tv_set.setOnClickListener(this);
        tv_data.setOnClickListener(this);
        btn_calculation.setOnClickListener(this);
    }

    private void initData() {
        sp = new SharedPreferencesUtils(this);
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        String planWalk_QTY = (String) sp.getParam("planWalk" +
                "_QTY", "7000");
        //设置当前步数为0
        cc.setCurrentCount(Integer.parseInt(planWalk_QTY), 0);
        tv_isSupport.setText("计步中...");
        setupService();
    }


    private boolean isBind = false;

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService stepService = ((StepService.StepBinder) service).getService();
            //设置初始化数据
            String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
            cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepService.getStepCount());

            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    String planWalk_QTY = (String) sp.getParam("planWalk_QTY", "7000");
                    cc.setCurrentCount(Integer.parseInt(planWalk_QTY), stepCount);
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                startActivity(new Intent(this, SetPlanActivity.class));
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            case R.id.tv_data:
                startActivity(new Intent(this, HistoryActivity.class));
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            case R.id.btn_calculation:
                //int current_step = new StepService().getStepCount();
                //StepData stepData=new StepData();
                String CURRENT_DATE = getTodayDate();
                List<step.step.bean.StepData> list = DbUtils.getQueryByWhere(step.step.bean.StepData.class, "today", new String[]{CURRENT_DATE});
                if (list.isEmpty()){
                    Toast.makeText(this, "走路运动后才可以测哟", Toast.LENGTH_SHORT).show();
                }else {
                    int current_step = Integer.parseInt(list.get(0).getStep());
                    int weight = SharePreUtil.getInt(this, "weight", 60);
                    float  cal = weight / 2000.0f * current_step;
                    float reduce_fat = cal / 18;
                    if (current_step == 0) {
                        Toast.makeText(this, "走路运动后才可以测哟", Toast.LENGTH_SHORT).show();
                    } else {
                        tv_cal.setText("大约消耗了" + cal + "cal卡路里");
                        tv_reduce_fat.setText("大约消耗了   " + reduce_fat + "g脂肪");
                        Toast.makeText(this, "要坚持锻炼哦", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(StepCountMainActivity.class.getSimpleName(), "现有步数" + current_step + "卡路里" + cal + ",脂肪" + reduce_fat);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取当天日期
     *
     * @return
     */
    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(StepCountMainActivity.class.getSimpleName(), "onStart: StepCountMainActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(StepCountMainActivity.class.getSimpleName(), "onRestart: StepCountMainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(StepCountMainActivity.class.getSimpleName(), "onPause: StepCountMainActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(StepCountMainActivity.class.getSimpleName(), "onStop: StepCountMainActivity");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
        Log.d(StepCountMainActivity.class.getSimpleName(), "onDestroy: StepCountMainActivity");
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        Log.d(StepCountMainActivity.class.getSimpleName(), "finish: StepCountMainActivity");
    }
}
