package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.Utils;

/**
 * 账户密码修改页面
 */

public class ModifyActivity extends Activity implements View.OnClickListener{
    private ImageView modify_back;
    private EditText modify_admin,modify_password,modify_newadmin,modify_newpassword,modify_confirmadmin;
    private Button modify_adminsave;
    private LinearLayout modify_admin_layout;

    private EditText modify_vipadmin,modify_vippassword,modify_new_vippassword,modify_confirmvippassword;
    private Button modify_vipsave;
    private LinearLayout modify_vipadmin_layout;

    private SharedHelper sh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        /* 初始化数据*/
        init();

        /*处理数据*/
        handleData();
    }

    /*
    * 处理数据
    * */
    private void handleData() {
        if("1".equals(sh.readAdminChoice())){           //普通用户
            modify_vipadmin_layout.setVisibility(View.GONE);
            modify_admin.setText(sh.readAdmin());
            modify_password.setText(sh.readPassWord());
        }else if("2".equals(sh.readAdminChoice())){     //VIP用户
            modify_admin.setText(sh.readAdmin());
            modify_password.setText(sh.readPassWord());

            modify_vipadmin.setText(sh.readVIPAdmin());
            modify_vippassword.setText(sh.readVIPPassWord());
        }





    }

    /*
        * 初始化数据
        * */
    private void init() {
        modify_back = (ImageView)findViewById(R.id.id_modify_back);         //返回

        /*普通用户*/
        modify_admin = (EditText)findViewById(R.id.id_modify_admin);         //原用户
        modify_password = (EditText)findViewById(R.id.id_modify_password);      //原密码
        modify_newadmin = (EditText)findViewById(R.id.id_modify_newadmin);      //新用户
        modify_newpassword =(EditText)findViewById(R.id.id_modify_newpassword);     //新密码
        modify_confirmadmin = (EditText)findViewById(R.id.id_modify_confirmadmin);      //确认密码
        modify_adminsave = (Button)findViewById(R.id.id_modify_adminsave);          //保存
        modify_admin_layout = (LinearLayout)findViewById(R.id.id_modify_admin_layout);  //普通用户布局

        /*VIP用户*/
        modify_vipadmin = (EditText)findViewById(R.id.id_modify_vipadmin);          //原VIP用户
        modify_vippassword = (EditText)findViewById(R.id.id_modify_vippassword);    //原VIP密码
        modify_new_vippassword = (EditText)findViewById(R.id.id_modify_new_vippassword);      //新密码
        modify_confirmvippassword = (EditText)findViewById(R.id.id_modify_confirmvippassword);      //确认密码
        modify_vipsave = (Button)findViewById(R.id.id_modify_vipsave);              //VIP保存
        modify_vipadmin_layout = (LinearLayout)findViewById(R.id.id_modify_vipadmin_layout);        //VIP用户


        modify_adminsave.setOnClickListener(this);
        modify_vipsave.setOnClickListener(this);
        modify_back.setOnClickListener(this);
        sh = new SharedHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_modify_adminsave:
                if(modify_admin.getText().toString().equals(sh.readAdmin()) &&
                        modify_password.getText().toString().equals(sh.readPassWord())){
                    if(modify_admin.getText().toString().length() >= 8){
                        if(modify_newpassword.getText().toString().length() >= 8 &&
                                modify_confirmadmin.getText().toString().length() >= 8){
                            if(modify_newpassword.getText().toString().equals(modify_confirmadmin.getText().toString())){
                                sh.savePassWord(modify_newpassword.getText().toString());
                                sh.saveAdmin(modify_admin.getText().toString());
                            }else{
                                Utils.showToast(this, "2个密码输入不一致", 2000);
                            }
                        }else{
                            Utils.showToast(this, "2个密码要大于8位且小于12位", 2000);
                        }
                    }else{
                        Utils.showToast(this, "新账户要大于8位且小于12位", 2000);
                    }
                }else{
                    Utils.showToast(this, "原账户和原密码输入有误", 2000);
                }

                break;


            case R.id.id_modify_vipsave:
                if(modify_vippassword.getText().toString().equals(sh.readVIPPassWord())) {
                    if (modify_new_vippassword.getText().toString().length() >= 8 &&
                            modify_confirmvippassword.getText().toString().length() >= 8) {
                        if (modify_new_vippassword.getText().toString().equals(modify_confirmvippassword.getText().toString())) {
                            sh.saveVIPPassWord(modify_new_vippassword.getText().toString());
                            Utils.showToast(this, "VIP密码保存成功", 2000);
                        } else {
                            Utils.showToast(this, "2次密码输入不一致", 2000);
                        }

                    } else {
                        Utils.showToast(this, "密码要大于8位且小于12位", 2000);
                    }
                }else{
                    Utils.showToast(this, "原密码输入错误", 2000);
                }
                break;

            case R.id.id_modify_back:
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

    //点击空白处收起键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (ModifyActivity.this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(ModifyActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }
}
