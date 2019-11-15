package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.ToastUtil;
import com.example.chen.ls4000.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2018/8/23.
 */

public class CoefficientActivity extends Activity implements View.OnClickListener{

    private SharedHelper sh;
    private EditText coeff,pwm,collection;
    private Button save;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coefficient);

        sh = new SharedHelper(getApplicationContext());

        initcoeff();
    }

    private void initcoeff(){
        coeff=(EditText)findViewById(R.id.id_coefficient_coeff);       //结果系数
        pwm=(EditText)findViewById(R.id.id_coefficient_pwm);            //pwm系数
        collection=(EditText)findViewById(R.id.id_coefficient_collection);  //采集点系数
        save=(Button)findViewById(R.id.id_coefficient_save);
        back=(ImageView)findViewById(R.id.id_coefficient_back);


        EventBus.getDefault().register(this);
        hideBottomUIMenu();

        back.setOnClickListener(this);
        save.setOnClickListener(this);

        sh = new SharedHelper(this);


        //结果系数
        if (sh.readLan().equals(" ")){
            coeff.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }
            coeff.setText(sh.readCoeff());

        //pwm系数
        if (sh.readLan().equals(" ")){
            pwm.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }
        pwm.setText(sh.readPwm());

        //采集点系数
        if (sh.readLan().equals(" ")){
            collection.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        }
        collection.setText(sh.readsaveCollec());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response) throws UnsupportedEncodingException  {
        int comm = response.getComm();
        String data = response.getMes();
        byte[] bys = response.getBys();
        if(comm == 0xC3){
            String str1 =data.substring(0,3);   //结果系数
            String str2 =data.substring(3,7);   //pwm系数
            String str3 =data.substring(7,11);   //采集点系数

            double ss1 = 0;
            String result2 ,result3;        //pwm系数,采集点系数

            if("0".equals(str1.substring(0,1))){
                ss1= Double.valueOf(str1.substring(1,3)) / 100;     //第一位为0
            }else{
                ss1= Double.valueOf(str1) / 100;                       //第一位不为0
            }


            if("0".equals(str2.substring(0,1))){
                result2=str2.substring(1,4);            //第一位为0
            }else{
                result2 = str2;                          //第一位不为0
            }

            if("0".equals(str3.substring(0,1))){
                result3= str3.substring(1,4);           //第一位为0
            }else{
                result3 = str3;                          //第一位不为0
            }


            //结果系数
            if(String.valueOf(ss1).equals(sh.readCoeff()) ){
                coeff.setText(sh.readCoeff());
                ToastUtil.showToast(this,"上下位机数相同",2000);
            }else{
                coeff.setText(String.valueOf(ss1));
                sh.saveCoeff(String.valueOf(ss1));
                ToastUtil.showToast(this,"上下位机数不同",2000);
            }

            //pwm系数
            if(result2.equals(sh.readPwm())){
                pwm.setText(sh.readPwm());
                ToastUtil.showToast(this,"上下位机数相同",2000);
            }else{
                pwm.setText(result2);
                sh.savePwm(result2);
                ToastUtil.showToast(this,"上下位机数不同",2000);
            }

            //采集点系数
            if(result3.equals(sh.readsaveCollec())){
                collection.setText(sh.readsaveCollec());
                ToastUtil.showToast(this,"上下位机数相同",2000);
            }else{
                collection.setText(result3);
                sh.saveCollec(result3);
                ToastUtil.showToast(this,"上下位机数不同",2000);
            }

        }

    }

    //点击空白处收起键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (CoefficientActivity.this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(CoefficientActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    public void showToast(final Activity activity, final String word,final long time){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Toast toast = Toast.makeText(activity,word,Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                },time);
            }
        });
    }

    @Override
    public void onClick(View v) {
        BleMethodCode methodCode;
        BleMethodCode met;
        BleMethodCode ble;
        int ipwm = 0;
        float icoeff = 0;
        int collec = 0;
        String coe= "" ;
        switch (v.getId()){
            case R.id.id_coefficient_save:
                try {
                    if(" ".equals(coeff.getText().toString()) && " ".equals(pwm.getText().toString()) &&
                            " ".equals(collection.getText().toString())){
                        showToast(this,getString(R.string.string268),1000);
                        return;
//                    }else if(" ".equals(coeff.getText().toString()) &&" ".equals(pwm.getText().toString())){
//                        showToast(this,"采集点偏移不能为空值",1000);
//                        return;
//                    }else if(" ".equals(coeff.getText().toString()) &&" ".equals(collection.getText().toString())){
//                        showToast(this,"紫外灯PWM不能为空值",1000);
//                        return;
//                    }
//                    else if(" ".equals(pwm.getText().toString()) && " ".equals(collection.getText().toString())){
//                        showToast(this,"紫外灯PWM和采集点偏移不能为空值",1000);
//                        return;
//                    }else if(" ".equals(collection.getText().toString())){
//                        showToast(this,"采集点偏移不能为空值",1000);
//                        return;
//                    }else if(" ".equals(coeff.getText().toString()) ){
//                        showToast(this,"结果系数不能为空值",1000);
//                        return;
//                    }else if(" ".equals(pwm.getText().toString())) {
//                        showToast(this, "紫外灯PWM不能为空值", 1000);
//                        return;
                    } else {
                        icoeff = Float.parseFloat(coeff.getText().toString());
                        if(icoeff > 0.5  && icoeff < 1.5){
                            sh.saveCoeff(coeff.getText().toString());
                            int cof = (int)( Float.parseFloat(coeff.getText().toString())*100);
                            if(cof<100){
                                coe+="0"+cof;
                            }else if(cof>=100){
                                coe+=cof;
                            }



                            //coe +=coeff.getText().toString();
                        }else{
                            showToast(this,getString(R.string.string269),1000);
                            return;
                        }


                        ipwm = (int)(Float.parseFloat(pwm.getText().toString()) );
                        if(ipwm >0 && ipwm <1500){
                            sh.savePwm(pwm.getText().toString());
                            //coe += (int)(Float.parseFloat(pwm.getText().toString()) );
                            for (int i = 0; i < 3 - pwm.getText().toString().length(); i++) {
                                coe += "0";
                            }
                            coe += pwm.getText().toString();
                        }else{
                            showToast(this,getString(R.string.string270),1000);
                            return;
                        }

                        collec = Integer.parseInt(collection.getText().toString());
                        if(collec >0 && collec<=999){
                            sh.saveCollec(collection.getText().toString());
                            coe += "0"+collec;
                        }else if(collec>999 && collec<=2000){
                            coe += collec;
                            sh.saveCollec(collection.getText().toString());
                        }else {
                            showToast(this,getString(R.string.string310),1000);
                            return;
                        }
//                        for (int i = 0; i < 3 - collection.getText().toString().length(); i++) {
//                            coe += "0";
//                        }



                        //int[] coe = {i,j,x};
                        methodCode = new BleMethodCode(3);
                        methodCode.setMes(coe);
                        methodCode.setComm((byte) 0x0B);
                        EventBus.getDefault().post(methodCode);
                        finish();
                    }

                }catch (Exception e){
                    showToast(this,getString(R.string.string271),1000);
                    e.printStackTrace();

                }

                break;
            case R.id.id_coefficient_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
