package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;

public class AgeingActivity extends Activity implements View.OnClickListener {

    private EditText et;
    private ImageView stop,back;
    private Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ageing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initviews();
    }



    private void initviews(){
        et = (EditText)findViewById(R.id.id_ageing_et);
        start = (Button)findViewById(R.id.id_ageing_starttest);
       // stop = (Button)findViewById(R.id.id_ageing_stop);
        back = (ImageView)findViewById(R.id.id_ageing_back);

        hideBottomUIMenu();

        start.setOnClickListener(this);
        //stop.setOnClickListener(this);
        back.setOnClickListener(this);

        EventBus.getDefault().register(this);
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
            case R.id.id_ageing_starttest:
                if(getString(R.string.string12).equals(start.getText().toString())) {
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("1");
                    methodCode.setComm((byte) 0x08);
                    EventBus.getDefault().post(methodCode);
                    start.setText(getString(R.string.string13));
                    back.setClickable(false);
                }else{
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("0");
                    methodCode.setComm((byte) 0xff);
                    EventBus.getDefault().post(methodCode);
                    start.setText(getString(R.string.string12));
                    back.setClickable(true);
                }
                break;
            case R.id.id_ageing_back:
                finish();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response) throws UnsupportedEncodingException {

        int comm = response.getComm();
        String data = response.getMes();
        byte[] bys = response.getBys();

        if (comm == 0x05) {//质控结果

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
