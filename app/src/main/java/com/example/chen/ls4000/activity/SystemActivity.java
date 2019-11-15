package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chen.ls4000.R;

public class SystemActivity extends Activity implements View.OnClickListener{

    private Button user,print,wifi,bluetooth,gps,time,back,promana,conmana;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        initviews();
    }

    private void initviews(){
        user = (Button)findViewById(R.id.id_system_user);
        print = (Button)findViewById(R.id.id_system_print);
        wifi = (Button)findViewById(R.id.id_system_wifi);
        bluetooth = (Button)findViewById(R.id.id_system_bluetooth);
        gps = (Button)findViewById(R.id.id_system_gps);
        time = (Button)findViewById(R.id.id_system_time);
        back = (Button)findViewById(R.id.id_system_back);
        promana = (Button)findViewById(R.id.id_system_promana);
        conmana = (Button)findViewById(R.id.id_system_conmana);

        hideBottomUIMenu();

        user.setOnClickListener(this);
        print.setOnClickListener(this);
        wifi.setOnClickListener(this);
        bluetooth.setOnClickListener(this);
        gps.setOnClickListener(this);
        time.setOnClickListener(this);
        back.setOnClickListener(this);
        promana.setOnClickListener(this);
        conmana.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.id_system_user:
                intent = new Intent(SystemActivity.this,UserActivity.class);
                startActivity(intent);
                break;
            case R.id.id_system_print:
                intent = new Intent(SystemActivity.this,PrintActivity.class);
                startActivity(intent);
                break;
            case R.id.id_system_wifi:
                intent = new Intent(SystemActivity.this,WIFIActivity.class);
                startActivity(intent);
                break;
            case R.id.id_system_bluetooth:
                intent = new Intent(SystemActivity.this,BlueToothActivity.class);
                startActivity(intent);
                break;
            case R.id.id_system_gps:
                intent = new Intent(SystemActivity.this,GPSActivity.class);
                startActivity(intent);
                break;
            case R.id.id_system_time:
                intent = new Intent(SystemActivity.this,TimeActivity.class);
                startActivity(intent);
                break;
            case R.id.id_system_back:
                finish();
                break;
            case R.id.id_system_promana:
                intent = new Intent(SystemActivity.this,PromanaActivity.class);
                startActivity(intent);
                break;
            case R.id.id_system_conmana:
                intent = new Intent(SystemActivity.this,ConmanaActivity.class);
                startActivity(intent);
                break;
        }
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
