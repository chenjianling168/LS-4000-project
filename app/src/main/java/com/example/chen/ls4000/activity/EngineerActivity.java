package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.utils.SharedHelper;

import org.greenrobot.eventbus.EventBus;

/*
* 工程师调试页面
* */
public class EngineerActivity extends Activity implements View.OnClickListener ,CompoundButton.OnCheckedChangeListener{

    private ImageView about,oper,ageing,elec,back,coeffcient,staff;
    private CheckBox reagentQual,sim,virtual_key,logo_key;
    private RelativeLayout operLayout,aboutLayout,ageingLayout,elecLayout,simLayout,coefflayout,stafflayout;
    private SharedHelper sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer);

        initviews();
    }

    private void initviews(){
        reagentQual = (CheckBox)findViewById(R.id.id_engineer_reagentqual);
        oper = (ImageView) findViewById(R.id.id_engineer_oper);
        about = (ImageView) findViewById(R.id.id_engineer_about);
        sim = (CheckBox) findViewById(R.id.id_engineer_sim);
        ageing = (ImageView) findViewById(R.id.id_engineer_ageing);
        coeffcient = (ImageView) findViewById(R.id.id_engineer_coeffcient);
        elec = (ImageView) findViewById(R.id.id_engineer_elec);
        staff = (ImageView) findViewById(R.id.id_engineer_staff);
        back = (ImageView) findViewById(R.id.id_engineer_back);
        operLayout = (RelativeLayout)findViewById(R.id.id_engineer_operlayout);
        aboutLayout = (RelativeLayout)findViewById(R.id.id_engineer_aboutlayout);
        ageingLayout = (RelativeLayout)findViewById(R.id.id_engineer_ageinglayout);
        elecLayout = (RelativeLayout)findViewById(R.id.id_engineer_eleclayout);
        coefflayout = (RelativeLayout)findViewById(R.id.id_coefficient_coefflayout);
        stafflayout = (RelativeLayout)findViewById(R.id.id_coefficient_stafflayout);
        virtual_key = (CheckBox)findViewById(R.id.id_engineer_virtual_key);
        logo_key = (CheckBox)findViewById(R.id.id_engineer_logo_key);

        hideBottomUIMenu();

        reagentQual.setOnClickListener(this);
        oper.setOnClickListener(this);
        about.setOnClickListener(this);
        sim.setOnClickListener(this);
        ageing.setOnClickListener(this);
        coeffcient.setOnClickListener(this);
        elec.setOnClickListener(this);
        back.setOnClickListener(this);
        staff.setOnClickListener(this);
        operLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);
        ageingLayout.setOnClickListener(this);
        elecLayout.setOnClickListener(this);
        coefflayout.setOnClickListener(this);
        stafflayout.setOnClickListener(this);
        virtual_key.setOnCheckedChangeListener(this);
        logo_key.setOnCheckedChangeListener(this);

        sh = new SharedHelper(this);

        if("1".equals(sh.readvirtual_key())){
            virtual_key.setChecked(false);

        }else if("0".equals(sh.readvirtual_key())){
            virtual_key.setChecked(true);

        }
        logo_key.setChecked(sh.readLogo());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        BleMethodCode methodCode;
        switch (v.getId()){
            case R.id.id_engineer_oper:
            case R.id.id_engineer_operlayout:
                intent = new Intent(EngineerActivity.this,OperActivity.class);
                startActivity(intent);
                break;
            /*向下位机发送指令，要下位机的软件版本号*/
            case R.id.id_engineer_about:
            case R.id.id_engineer_aboutlayout:
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0xC1);
                EventBus.getDefault().post(methodCode);
                intent = new Intent(EngineerActivity.this,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.id_engineer_sim:
                if(sim.isChecked()==true){
                    final View view ;
                    final TextView tip;
                    final Button ensure,cancel;

                    view = View.inflate(this, R.layout.promptlayout, null);

                    tip = (TextView) view.findViewById(R.id.id_prompt_tip);
                    ensure = (Button) view.findViewById(R.id.id_prompt_ensure);
                    cancel = (Button)view.findViewById(R.id.id_prompt_cancel);

                    tip.setText("确定显示SI卡信息");

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

                            dialog.dismiss();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sim.isClickable();
                            dialog.dismiss();
                        }
                    });
                }else{

                }



//                intent = new Intent(EngineerActivity.this,SIMActivity.class);
//                startActivity(intent);
                break;
            case R.id.id_engineer_ageing:
            case R.id.id_engineer_ageinglayout:
                intent = new Intent(EngineerActivity.this,AgeingActivity.class);
                startActivity(intent);
                break;
            case R.id.id_engineer_elec:
            case R.id.id_engineer_eleclayout:
//                intent = new Intent(EngineerActivity.this,ElecActivity.class);
//                startActivity(intent);
                break;

            case R.id.id_engineer_coeffcient:
             case  R.id.id_coefficient_coefflayout:

                 methodCode = new BleMethodCode(3);
                 methodCode.setMes("");
                 methodCode.setComm((byte) 0xC2);
                 EventBus.getDefault().post(methodCode);

                 intent = new Intent(EngineerActivity.this,CoefficientActivity.class);
                 startActivity(intent);
                 break;

            case R.id.id_engineer_staff:
            case R.id.id_coefficient_stafflayout:
                intent = new Intent(EngineerActivity.this,StaffActivity.class);
                Bundle bundle = new Bundle();
                byte[] result = null;
                bundle.putByteArray("result",result);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

//            case R.id.id_engineer_virtual_key:
//                if(virtual_key.isChecked() ){
//                    //隐藏虚拟键
//                    Intent intent1 = new Intent("ismart.intent.action_hide_navigationview");
//                    intent1.putExtra("hide",false);
//                    sendBroadcast(intent1);
//
//                    //禁用状态栏下拉
//                    Intent intent2 = new Intent("ismart.intent.action_lock_panelbar");
//                    intent2.putExtra("state",false);
//                    sendBroadcast(intent2);
//
//                    sh.savevirtual_key(false);
//                }else{
//                    //打开虚拟键
//                    Intent intent1 = new Intent("ismart.intent.action_hide_navigationview");
//                    intent1.putExtra("hide",true);
//                    sendBroadcast(intent1);
//
//                    //启用状态栏下拉
//                    Intent intent2 = new Intent("ismart.intent.action_lock_panelbar");
//                    intent2.putExtra("state",true);
//                    sendBroadcast(intent2);
//                    sh.savevirtual_key(true);
//                }
//                break;
            case R.id.id_engineer_back:
                intent = new Intent(EngineerActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView.getId() == R.id.id_engineer_virtual_key){
                if(isChecked){
                    //打开虚拟键
                    Intent intent1 = new Intent("ismart.intent.action_hide_navigationview");
                    intent1.putExtra("hide",false);
                    sendBroadcast(intent1);

                    //启用状态栏下拉
                    Intent intent2 = new Intent("ismart.intent.action_lock_panelbar");
                    intent2.putExtra("state",false);
                    sendBroadcast(intent2);
                    sh.savevirtual_key("0");
                }else{
                    //关闭虚拟键
                    Intent intent1 = new Intent("ismart.intent.action_hide_navigationview");
                    intent1.putExtra("hide", true);
                    sendBroadcast(intent1);

                    //禁用状态栏下拉
                    Intent intent2 = new Intent("ismart.intent.action_lock_panelbar");
                    intent2.putExtra("state", true);
                    sendBroadcast(intent2);
                    sh.savevirtual_key("1");
                }
            }
        if(buttonView.getId() == R.id.id_engineer_logo_key){
            sh.saveLogo(logo_key.isChecked());
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
