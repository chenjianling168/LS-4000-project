package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.utils.DBUtils;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;

public class AboutActivity extends Activity implements View.OnClickListener {

    private Button recover;
    private ImageView back;
    private DBUtils dbUtils;
    private SharedHelper sp;
    private TextView serial,flush;
    private MyApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initviews();
//        BleMethodCode methodCode;
//        methodCode = new BleMethodCode(3);
//        methodCode.setMes("");
//        methodCode.setComm((byte) 0xC1);
//        EventBus.getDefault().post(methodCode);
    }



    private void initviews() {
        /*恢复出厂设置*/
        recover = (Button) findViewById(R.id.id_about_recover);
        /*返回*/
        back = (ImageView) findViewById(R.id.id_about_back);
        /*输入序列号*/
        serial=(TextView)findViewById(R.id.id_about_serial);
        /*下位机软件版本*/
        flush = (TextView)findViewById(R.id.id_about_flush);
        hideBottomUIMenu();

        recover.setOnClickListener(this);
        back.setOnClickListener(this);
        serial.setOnClickListener(this);


        EventBus.getDefault().register(this);
        dbUtils = new DBUtils(this);
        sp = new SharedHelper(this);
        myApp = new MyApp();

        if (sp.readLan().equals(" ")){
            serial.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }
        serial.setText(sp.readserial());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        BleMethodCode methodCode;
        switch (v.getId()) {

            case R.id.id_about_recover:
//                methodCode = new BleMethodCode(3);
//                methodCode.setMes("");
//                methodCode.setComm((byte) 0x07);
//                EventBus.getDefault().post(methodCode);
                popTip();
                break;
            case R.id.id_about_back:        //返回
                //sp.saveserial(serial.getText().toString());
                finish();
                break;

            case  R.id.id_about_serial:
                final View view ;
                final TextView serialtext;

                Button ensure,cancel;

                view = View.inflate(this, R.layout.aboutserial, null);
                serialtext = (TextView) view.findViewById(R.id.id_prompt_serialtext);
                ensure = (Button) view.findViewById(R.id.id_prompt_ensure);
                cancel = (Button)view.findViewById(R.id.id_prompt_cancel);
                final android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(this, R.style.AlertDialog).create();
                dialog.setView(view);
                dialog.setCancelable(false);
                dialog.show();
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
                params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
                dialog.getWindow().setAttributes(params);

                ensure.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        serial.setText(serialtext.getText().toString());
                        sp.saveserial(serial.getText().toString());
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
        }
    }

    private void upUpgrade() {

    }
    String data;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response)  {
        int comm = response.getComm();
        data = response.getMes();
        if(comm == 0xC2){
            abhandler.postDelayed(abRunnable,1500);
        }

    }

    /*线程获取下位机版本号*/
    Handler abhandler = new Handler();
    Runnable abRunnable = new Runnable() {
        @Override
        public void run() {
            flush.setText("LS-4000" + data);

        }
    };

    private void popTip() {
        final View view;

//        if("English".equals(lan)){
//            view = View.inflate(this,R.layout.mydialog_eng,null);
//        }else{
        view = View.inflate(this, R.layout.tiplayout, null);
        //  }

        TextView tip;
        Button ensure, cancel;

        tip = (TextView) view.findViewById(R.id.id_tip_tip);
        ensure = (Button) view.findViewById(R.id.id_tip_ensure);
        cancel = (Button) view.findViewById(R.id.id_tip_cancel);
        tip.setText(getString(R.string.string1));

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialog).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        dialog.getWindow().setAttributes(params);

        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp myApp = (MyApp) getApplication();
                myApp.clearTestPeoples();
                dbUtils.dropSamples();
                dbUtils.dropProjects();
                dbUtils.dropInstruments();
                sp.saveUser("", "", "","","","","","");
                sp.saveStaff("","","","","","","","","","","","","","","");
                myApp.setPrintState(0);
                sp.saveWifi(false);
                sp.saveWifiState(false);
                sp.saveBluetooth(false);
                sp.savePrint(false, true, true, true, true,true, true, true, true);
//                sp.saveCoeff("0.97");
//                sp.savePwm("640");
//                sp.saveCollec("840");
                startActivity(new Intent(AboutActivity.this,MainActivity.class));
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
}
