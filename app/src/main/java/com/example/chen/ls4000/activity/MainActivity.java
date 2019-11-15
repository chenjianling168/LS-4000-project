package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.zyapi.CommonApi;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.chen.ls4000.R;
import com.example.chen.ls4000.service.MyService;
import com.example.chen.ls4000.service.PortCardService;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.ToastUtil;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity implements View.OnClickListener {

    private RelativeLayout test,query,settings;
    private SharedHelper sp;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private MyApp myApp;
    public  Button test_guanji;

    private ExecutorService es = Executors.newScheduledThreadPool(30);
    private CommonApi commonApi = null;
    private int[] ios = new int[]{1, 2, 3, 4, 80, 79, 78, 86, 83, 82};
    private int GPIO_12V_EN = 44;       //12V电压

    private int GPIO_5V_EN = 7;
    private boolean isStop = true;
    private int LED_ON = 0;
    private int LED_OFF = 1;
    private TextView copyRight;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, PortCardService.class));
        initviews();

        myApp.setPowerBool(true);

        if("1".equals(sp.readvirtual_key())){
            //关闭虚拟键
            Intent intent1 = new Intent("ismart.intent.action_hide_navigationview");
            intent1.putExtra("hide", true);
            sendBroadcast(intent1);

            //禁用状态栏下拉
            Intent intent2 = new Intent("ismart.intent.action_lock_panelbar");
            intent2.putExtra("state", true);
            sendBroadcast(intent2);
        }else if("0".equals(sp.readvirtual_key())){
            //打开虚拟键
            Intent intent1 = new Intent("ismart.intent.action_hide_navigationview");
            intent1.putExtra("hide",false);
            sendBroadcast(intent1);

            //启用状态栏下拉
            Intent intent2 = new Intent("ismart.intent.action_lock_panelbar");
            intent2.putExtra("state",false);
            sendBroadcast(intent2);
        }
    }

    private void initviews(){
        test = (RelativeLayout) findViewById(R.id.id_main_test);
        query = (RelativeLayout) findViewById(R.id.id_main_query);
        settings = (RelativeLayout) findViewById(R.id.id_main_settings);
        test_guanji = (Button)findViewById(R.id.id_main_test_guanji);
        logo = (ImageView)findViewById(R.id.id_main_logo);
        copyRight = (TextView)findViewById(R.id.id_main_copyright);

        test.setOnClickListener(this);
        query.setOnClickListener(this);
        settings.setOnClickListener(this);
        test_guanji.setOnClickListener(this);

        hideBottomUIMenu();

        sp = new SharedHelper(this);
        myApp = (MyApp) getApplication();


//        startGpioTest();

        if(!sp.readLogo()){
            logo.setVisibility(View.INVISIBLE);
            copyRight.setVisibility(View.INVISIBLE);
        }else{
            logo.setVisibility(View.VISIBLE);
            copyRight.setVisibility(View.VISIBLE);
        }


        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

            }
        };

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        AMapLocationClientOption option = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
//        if(null != locationClient){
//            locationClient.setLocationOption(option);
//            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//            locationClient.stopLocation();
//            locationClient.startLocation();
//        }

        //startService(new Intent(MainActivity.this, PortCardService.class));



//        if ("English".equals(sp.readLan())){
//            Locale.setDefault(Locale.ENGLISH);
//            Configuration config = getBaseContext().getResources().getConfiguration();
//            config.locale = Locale.ENGLISH;
//            getBaseContext().getResources().updateConfiguration(config
//                    , getBaseContext().getResources().getDisplayMetrics());
//            //refreshSelf();
//
//
//
//
//    }else {
//            Locale.setDefault(Locale.CHINESE);
//            Configuration config = getBaseContext().getResources().getConfiguration();
//            config.locale = Locale.CHINESE;
//            getBaseContext().getResources().updateConfiguration(config
//                    , getBaseContext().getResources().getDisplayMetrics());
//            //refreshSelf();
//        }
    }

    //关闭下位机断电
    private void stopGpioTest() {
        isStop = true;
        if (commonApi != null) {
            commonApi.setGpioOut(GPIO_12V_EN, 0);
            //commonApi.setGpioOut(GPIO_5V_EN, 0);
        }
    }

    //打开下位机
    private void startGpioTest() {
        //first ,turn on GPIO 12V
        commonApi.setGpioDir(GPIO_12V_EN, 1);
        commonApi.setGpioOut(GPIO_12V_EN, 1);

        //first ,turn on GPIO 5V
//        commonApi.setGpioDir(GPIO_5V_EN, 1);
//        commonApi.setGpioOut(GPIO_5V_EN, 1);

        es.submit(new TaskTestI0());
    }

    private class TaskTestI0 implements Runnable {

        @Override
        public void run() {
            isStop = false;
            while (!isStop) {
                for (int i = 0; i < ios.length; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioDir(ios[i], 1);
                    commonApi.setGpioOut(ios[i], LED_ON);
                }

                for (int i = 0; i < ios.length; i++) {

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioDir(ios[i], 1);
                    commonApi.setGpioOut(ios[i], LED_OFF);
                }

                for (int i = 0; i < ios.length; i++) {

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    commonApi.setGpioDir(ios[i], 1);
                    commonApi.setGpioOut(ios[i], LED_ON);
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioOut(ios[i], LED_OFF);
                }


                for (int i = ios.length - 1; i >= 0; i--) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioDir(ios[i], 1);
                    commonApi.setGpioOut(ios[i], LED_ON);
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioOut(ios[i], LED_OFF);
                }
            }

        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.id_main_test://检测
                if(myApp.getQuanFlap()==0){
                    intent = new Intent(MainActivity.this, TestActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else if(myApp.getQuanFlap()==1){
                    ToastUtil.showToast(this,getString(R.string.string321),2000);
                }

                break;
            case R.id.id_main_query://查询
                intent = new Intent(MainActivity.this,QueryActivity.class);
                startActivity(intent);
                break;
            case R.id.id_main_settings://系统设置
                intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.id_main_test_guanji:
                intent = new Intent(this,FunctionActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApp.setPrintState(0);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
