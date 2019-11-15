package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chen.ls4000.R;

public class ElecActivity extends Activity implements View.OnClickListener{

    private EditText scan,test,speed;
    private Button save;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elec);

        initviews();
    }

    private void initviews(){
        scan = (EditText)findViewById(R.id.id_elec_scan);
        test = (EditText)findViewById(R.id.id_elec_test);
        speed = (EditText)findViewById(R.id.id_elec_speed);
        save = (Button)findViewById(R.id.id_elec_save);
        back = (ImageView)findViewById(R.id.id_elec_back);

        hideBottomUIMenu();

        save.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_elec_save:
                break;
            case R.id.id_elec_back:
                finish();
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
