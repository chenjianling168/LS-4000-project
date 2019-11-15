package com.example.chen.ls4000.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.zyapi.CommonApi;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.activity.MainActivity;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.utils.DBUtils;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.TimeUtils;
import com.example.chen.ls4000.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class MyService extends Service  {

    private MyApp myApp;
    private DBUtils dbUtils;
    private Sample testPeople;
    private SharedHelper sp;
    private int time[] = new int[]{30,60,180,300,600,700,700,700};
    private String proStr[] = new String[]{"PCT","CRP","NT_proBNP","cTnI","CK_MB","D-DIMER","HBALC"};
    private String times[] = new String[]{"","","","","","","","",""};
    private String status[] = new String[]{"","","","","","","","",""};
    private String pros[] = new String[]{"","","","","","","","",""};
    private String lan;
    Map<String,Boolean> map;
    private int timeIncu;

    private ScreenReceiver receiver;
    private CommonApi commonApi = null;
    private int GPIO_12V_EN = 44;
    private int GPIO_5V_EN = 7;

    private BroadcastReceiver batteryLevelRcvr;
    private IntentFilter batteryLevelFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        myApp = (MyApp) getApplication();
        dbUtils = new DBUtils(this);
        Context context = getApplicationContext();
        sp = new SharedHelper(context);
        lan = sp.readLan();

        commonApi = new CommonApi();

        receiver = new ScreenReceiver();
        //2.创建intent-filter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        //3.注册广播接收者
        registerReceiver(receiver, filter);



    }

    Handler mmHandler = new Handler();
    Runnable mmRunnable = new Runnable() {
        @Override
        public void run() {
            monitorBatteryState();
        }
    };




    private void monitorBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent){
                StringBuilder sb = new StringBuilder();
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                int level = -1; // percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                sb.append("手机");
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
                    Toast.makeText(MyService.this,getString(R.string.string329),Toast.LENGTH_SHORT).show();
                } else {
                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            Toast.makeText(MyService.this,getString(R.string.string330),Toast.LENGTH_SHORT).show();
                            break;
                        case BatteryManager.BATTERY_STATUS_CHARGING:    //充电中
                            sb.append("电池");
                            if (level <= 5) {
                                myApp.setQuanFlap(1);
                                Toast.makeText(MyService.this,getString(R.string.string331),Toast.LENGTH_SHORT).show();
                            }else if (5<level &&level <= 100) {
                                myApp.setQuanFlap(0);
                                //mmHandler.postDelayed(mmRunnable, 1000);
                            }
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:     //放电中
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:    //未充电
                            if (level <= 5) {
                                myApp.setQuanFlap(1);
                                Toast.makeText(MyService.this, getString(R.string.string331), Toast.LENGTH_SHORT).show();
                            } else if (5<level &&level <= 100) {
                                myApp.setQuanFlap(0);
                                //mmHandler.postDelayed(mmRunnable, 1000);
                            }
                                //Toast.makeText(MyService.this,"当前电量" + "[" + level + "]",Toast.LENGTH_SHORT).show();
                            break;
                        case BatteryManager.BATTERY_STATUS_FULL:    //已充满
                            //sb.append(" 完全充电.");
                            break;
                        default:
                            //sb.append("电池是难以形容的!");
                            break;
                    }
                }
                sb.append(' ');


            }
        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelRcvr, batteryLevelFilter);

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mmHandler.postDelayed(mmRunnable,1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(receiver);
        unregisterReceiver(batteryLevelRcvr);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response) {
        //这里改数据

        String ss = response.getMes();
        int comm = response.getComm();
        System.out.println("数据serviceflag"+ss+"*"+ss.length());
        if(comm == 0x0B) {
            for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                if(myApp.getTestPeoples().get(i).isFlagSend()){
                    myApp.getTestPeoples().remove(i);
                    break;
                }
            }
        }else if(comm == 14 ){
          //  popDialog("项目不匹配，请更换项目卡！");
            System.out.println("数据项目不匹配，请更换项目卡！");
        }
        if(comm == 0x66){
            timeIncu = Integer.valueOf(ss);
            handler.postDelayed(runnable,1000);
        }
    }




    public class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            if(Intent.ACTION_SCREEN_OFF.equals(action)){
                    System.out.println("屏幕关闭");
                myApp.setScreenBool(false);
                try {
                    Thread.sleep(1000);
                    if(myApp.isPowerBool() && commonApi != null && !myApp.isScreenBool()){
                        commonApi.setGpioOut(GPIO_12V_EN,0);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(Intent.ACTION_SCREEN_ON.equals(action)){
                System.out.println("屏幕打开");
                myApp.setScreenBool(true);
                if(commonApi != null){
                    //commonApi.setGpioOut(GPIO_5V_EN,1);
                    commonApi.setGpioOut(GPIO_12V_EN, 1);
                }
            }
        }

    }


    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int time = timeIncu;
            if(time>0) {
                handler.postDelayed(this, 1000);
                time = time-1;
                timeIncu = time;
                sp.saveTime(time);
            }else{
                handler.removeCallbacks(runnable);
            }
        }
    };

}



