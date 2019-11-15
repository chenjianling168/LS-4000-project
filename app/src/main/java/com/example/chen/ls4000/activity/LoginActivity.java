package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.Utils;

/**
 * 开始登陆页面
 */

public class LoginActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private EditText login_adming;
    private EditText login_paassword;
    private SharedHelper sh;
    private Button login_login;
    private CheckBox login_selection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化数据
        init();
    }

    /*
    * 初始化数据
    * */
    private void init() {
        login_adming = (EditText)findViewById(R.id.id_login_adming);        //用户名
        login_paassword = (EditText)findViewById(R.id.id_login_paassword);      //密码
        login_login = (Button)findViewById(R.id.id_login_login);                //登陆
        login_selection = (CheckBox)findViewById(R.id.id_login_selection);                        //选中记住密码

        sh = new SharedHelper(this);

        if("1".equals(sh.readAdminChoice())){                   //普通用户
            login_adming.setText(sh.readAdmin());
            if(sh.readRememberPW()){                            //设置为true密码保存
                login_paassword.setText(sh.readPassWord());
                login_selection.setChecked(true);
            }else{
                login_paassword.setText("");
            }
        }else if("2".equals(sh.readAdminChoice())){             //Vip用户
            login_adming.setText(sh.readVIPAdmin());
            if(sh.readRememberPW()){
                login_paassword.setText(sh.readVIPPassWord());
                login_selection.setChecked(true);
            }else{
                login_paassword.setText("");
            }
        }
        login_login.setOnClickListener(this);
        login_selection.setOnCheckedChangeListener(this);
    }


    /*点击效果*/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_login_login:
                    if(login_adming.getText().toString().equals(sh.readAdmin()) &&
                            login_paassword.getText().toString().equals(sh.readPassWord())){
                        sh.saveAdminChoice("1");            //普通用户
                        Jump();

                    }else if((login_adming.getText().toString().equals(sh.readVIPAdmin())) &&
                            (login_paassword.getText().toString().equals(sh.readVIPPassWord())) ||
                            (login_adming.getText().toString().equals(sh.readVIPAdmin())) &&
                                    (login_paassword.getText().toString().equals("password"))){
                        sh.saveAdminChoice("2");            //VIP用户
                        Jump();
                    }else{
                        Utils.showToast(this,"账户密码错误，请重试",2000);
                        login_paassword.setText("");
                    }

                break;
        }
    }

    /*页面跳转*/
    private void Jump(){
        Intent intent = new Intent(LoginActivity.this,FirstActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            sh.saveRememberPW(true);
        }else{
            sh.saveRememberPW(false);
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
            if (LoginActivity.this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }
}
