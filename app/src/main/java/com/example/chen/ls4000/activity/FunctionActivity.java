package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.zyapi.CommonApi;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.utils.ProgressDialogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2019-1-23.
 */

public class FunctionActivity extends Activity implements View.OnClickListener{
    private ExecutorService es = Executors.newScheduledThreadPool(30);
    private CommonApi commonApi = null;

    private Button mBtnStartGPIO;
    private Button mBtnStopGPIO;

    private int[] ios = new int[]{1, 2, 3, 4, 80, 79, 78, 86, 83, 82};
    private int GPIO_12V_EN = 44;

    private int GPIO_5V_EN = 7;
    private boolean isStop = true;
    private int LED_ON = 0;
    private int LED_OFF = 1;

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            isTestStart = false;
//            ProgressDialogUtils.dismissProgressDialog();
//            switch (msg.what) {
//                case 0:
//
//                    showDialog("测试成功");
//                    break;
//                case -1:
//                    showDialog("测试失败");
//                    break;
//                case -2:
//                    showDialog("测试失败");
//                    break;
//
//            }
//        }
//    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_func);

        commonApi = new CommonApi();
        initViews();
    }

    private void initViews() {
        mBtnStartGPIO = (Button) this.findViewById(R.id.btn_start_io);
        mBtnStartGPIO.setOnClickListener(this);
        mBtnStopGPIO = (Button) this.findViewById(R.id.btn_stop_io);
        mBtnStopGPIO.setOnClickListener(this);
//
//        stopGpioTest();
//
//
//        try {
//            Thread.sleep(5000);
//            startGpioTest();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private void startGpioTest() {
        //first ,turn on GPIO 12V
        commonApi.setGpioDir(GPIO_12V_EN, 1);
        commonApi.setGpioOut(GPIO_12V_EN, 1);

        //first ,turn on GPIO 5V
//        commonApi.setGpioDir(GPIO_5V_EN, 1);
//        commonApi.setGpioOut(GPIO_5V_EN, 1);

        es.submit(new TaskTestI0());
    }

    private void stopGpioTest() {
        isStop = true;
        if (commonApi != null) {
            commonApi.setGpioOut(GPIO_12V_EN, 0);
            //commonApi.setGpioOut(GPIO_5V_EN, 0);
        }
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
        switch (v.getId()) {
            case R.id.btn_start_io:
               // startGpioTest();
                break;
            case R.id.btn_stop_io:
                //stopGpioTest();
                break;

        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStop = true;

        if (commonApi != null) {
            commonApi.setGpioOut(GPIO_12V_EN, 0);
            commonApi.setGpioOut(GPIO_5V_EN, 0);
        }
    }
}
