package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.utils.SharedHelper;

public class SettingsActivity extends Activity implements View.OnClickListener {

    private ImageView user,print,wifi,bluetooth,gps,language,
            time,promana,instrqual,reagentqual,engineer,back,instrment;
    private RelativeLayout userlayout,printlayout,wifilayout,
            bluetoothlayout,gpslayout,languagelayout,
            timelayout,promanalayout,instrquallayout,
            reagentquallayout,engineerlayout,instrumentlayout,modifylayout;
    private SharedHelper sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /*初始化数据*/
        initviews();

        /*判断用户是普通用户还是VIP用户*/
        initChioce();
    }

    /*
    * 判断用户是普通用户还是VIP用户
    * */
    private void initChioce() {
        if("1".equals(sh.readAdminChoice())){           //普通用户
            engineerlayout.setVisibility(View.GONE);
        }else if("2".equals(sh.readAdminChoice())){     //VIP用户

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.id_settings_user:
            case R.id.id_settings_userlayout:                            //跳转到用户信息设置页面
                intent = new Intent(SettingsActivity.this,UserActivity.class);
                startActivity(intent);
                break;
            case R.id.id_settings_print:
            case R.id.id_settings_printlayout:                          //跳转到打印设置页面
                intent = new Intent(SettingsActivity.this,PrintActivity.class);
                startActivity(intent);
                break;
            case R.id.id_settings_wifi:
            case R.id.id_settings_wifilayout:                            //跳转到WIFI设置页面
                intent = new Intent(SettingsActivity.this,WIFIActivity.class);
                startActivity(intent);
                break;
            case R.id.id_settings_bluetooth:
            case R.id.id_settings_bluetoothlayout:                       //跳转到蓝牙设置页面
                intent = new Intent(SettingsActivity.this,BlueToothActivity.class);
                startActivity(intent);
                break;
            case R.id.id_settings_gps:
            case R.id.id_settings_gpslayout:                             //跳转到GPS设置页面
                intent = new Intent(SettingsActivity.this,GPSActivity.class);
                startActivity(intent);
                break;
            case R.id.id_settings_language:
            case R.id.id_settings_languagelayout:                        //跳转到语言设置页面
                intent = new Intent(SettingsActivity.this,LanguageActivity.class);
                startActivity(intent);
                break;

            case R.id.id_settings_time:
            case R.id.id_settings_timelayout:                               //跳转到时间设置页面
                intent = new Intent(SettingsActivity.this,TimeActivity.class);
                startActivity(intent);
                break;

            case R.id.id_settings_promana:
            case R.id.id_settings_promanalayout:                            //跳转到项目管理页面
                intent = new Intent(SettingsActivity.this,PromanaActivity.class);
                startActivity(intent);
                break;

            case R.id.id_settings_instrqual:
            case R.id.id_settings_instrquallayout:                              //跳转到仪器质控页面
                intent = new Intent(SettingsActivity.this,ConmanaActivity.class);
                startActivity(intent);

                break;
            case R.id.id_settings_reagentqual:
            case R.id.id_settings_reagentquallayout:
                break;
            case R.id.id_settings_engineer:
            case R.id.id_settings_engineerlayout:                               //跳转工程师调试页面
                intent = new Intent(SettingsActivity.this,EngineerActivity.class);
                startActivity(intent);

                break;

            case R.id.id_settings_instrment:
            case R.id.id_settings_instrumentlayout:                             //跳转关于仪器页面
                intent = new Intent(SettingsActivity.this,InstruActivity.class);
                startActivity(intent);

                break;

            case R.id.id_settings_back:
                intent = new Intent(SettingsActivity.this,MainActivity.class);      //返回主界面
                startActivity(intent);
                break;

            case R.id.id_settings_modifylayout:                         //跳转账户修改页面
                intent = new Intent(SettingsActivity.this,ModifyActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void initviews(){
        user = (ImageView)findViewById(R.id.id_settings_user);
        print = (ImageView)findViewById(R.id.id_settings_print);
        wifi = (ImageView)findViewById(R.id.id_settings_wifi);
        bluetooth = (ImageView)findViewById(R.id.id_settings_bluetooth);
        gps = (ImageView)findViewById(R.id.id_settings_gps);
        language = (ImageView)findViewById(R.id.id_settings_language);
        time = (ImageView)findViewById(R.id.id_settings_time);
        promana = (ImageView)findViewById(R.id.id_settings_promana);
        instrqual = (ImageView)findViewById(R.id.id_settings_instrqual);
        reagentqual = (ImageView)findViewById(R.id.id_settings_reagentqual);
        engineer = (ImageView)findViewById(R.id.id_settings_engineer);
        back = (ImageView)findViewById(R.id.id_settings_back);
        userlayout = (RelativeLayout)findViewById(R.id.id_settings_userlayout);
        printlayout = (RelativeLayout)findViewById(R.id.id_settings_printlayout);
        wifilayout = (RelativeLayout)findViewById(R.id.id_settings_wifilayout);
        bluetoothlayout = (RelativeLayout)findViewById(R.id.id_settings_bluetoothlayout);
        gpslayout = (RelativeLayout)findViewById(R.id.id_settings_gpslayout);
        languagelayout = (RelativeLayout)findViewById(R.id.id_settings_languagelayout);
        timelayout = (RelativeLayout)findViewById(R.id.id_settings_timelayout);
        promanalayout = (RelativeLayout)findViewById(R.id.id_settings_promanalayout);
        instrquallayout = (RelativeLayout)findViewById(R.id.id_settings_instrquallayout);
        reagentquallayout = (RelativeLayout)findViewById(R.id.id_settings_reagentquallayout);
        engineerlayout = (RelativeLayout)findViewById(R.id.id_settings_engineerlayout);

        modifylayout = (RelativeLayout)findViewById(R.id.id_settings_modifylayout);

        instrumentlayout = (RelativeLayout)findViewById(R.id.id_settings_instrumentlayout);
        instrment = (ImageView)findViewById(R.id.id_settings_instrment);


        sh = new SharedHelper(this);
        hideBottomUIMenu();

        user.setOnClickListener(this);
        print.setOnClickListener(this);
        wifi.setOnClickListener(this);
        bluetooth.setOnClickListener(this);
        gps.setOnClickListener(this);
        language.setOnClickListener(this);
        time.setOnClickListener(this);
        promana.setOnClickListener(this);
        instrqual.setOnClickListener(this);
        reagentqual.setOnClickListener(this);
        engineer.setOnClickListener(this);
        userlayout.setOnClickListener(this);
        printlayout.setOnClickListener(this);
        wifilayout.setOnClickListener(this);
        bluetoothlayout.setOnClickListener(this);
        gpslayout.setOnClickListener(this);
        languagelayout.setOnClickListener(this);
        timelayout.setOnClickListener(this);
        promanalayout.setOnClickListener(this);
        instrquallayout.setOnClickListener(this);
        reagentquallayout.setOnClickListener(this);
        engineerlayout.setOnClickListener(this);
        instrumentlayout.setOnClickListener(this);
        instrment.setOnClickListener(this);
        back.setOnClickListener(this);
        modifylayout.setOnClickListener(this);
    }


    private void popTip() {
        final View view;


        view = View.inflate(this, R.layout.enginpsdlayout, null);
        //  }

        final TextView tip;
        Button ensure,cancel;
        final EditText et;
        RelativeLayout click;

        tip = (TextView) view.findViewById(R.id.id_enginpsd_tip);
        ensure = (Button) view.findViewById(R.id.id_enginpsd_ensure);
        cancel = (Button)view.findViewById(R.id.id_enginpsd_cancel);
        click = (RelativeLayout)view.findViewById(R.id.id_settings_click);
        et = (EditText)view.findViewById(R.id.id_enginpsd_et);
        tip.setVisibility(View.GONE);
        //tip.setText(str);

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialog).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.height = 1280;//getWindow().getWindowManager().getDefaultDisplay().getHeight();
        dialog.getWindow().setAttributes(params);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("58775705".equals(et.getText().toString())){

                    dialog.dismiss();
                }else{
                    tip.setVisibility(View.VISIBLE);
                    et.setText("");
                }
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
