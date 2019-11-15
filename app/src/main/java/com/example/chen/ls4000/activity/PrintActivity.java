package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.utils.SharedHelper;

public class PrintActivity extends Activity implements View.OnClickListener {

    private CheckBox autoCb,hosCb,departCb,docCb,dateCb,timeCb,referCb,numCb,audCb;
    private Button save;
    private ImageView back;
    private SharedHelper sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        initviews();
    }

    private void initviews(){
        autoCb = (CheckBox) findViewById(R.id.id_print_autocb);     //自动打印
        hosCb = (CheckBox) findViewById(R.id.id_print_hoscb);       //医院名称
        departCb = (CheckBox) findViewById(R.id.id_print_departcb);     //科室名称
        docCb = (CheckBox) findViewById(R.id.id_print_doccb);       //医生名称
        audCb = (CheckBox)findViewById(R.id.id_print_audcb);            //审核人
        dateCb = (CheckBox) findViewById(R.id.id_print_datecb);
        timeCb = (CheckBox) findViewById(R.id.id_print_timecb);
        referCb = (CheckBox) findViewById(R.id.id_print_refercb);   //参考值
        numCb = (CheckBox) findViewById(R.id.id_print_numcb);
        save = (Button)findViewById(R.id.id_print_save);
        back = (ImageView) findViewById(R.id.id_print_back);

        hideBottomUIMenu();

        save.setOnClickListener(this);
        back.setOnClickListener(this);

        sh = new SharedHelper(this);

        autoCb.setChecked(sh.readAutoPrint());      //自动打印
        hosCb.setChecked(sh.readHosPrint());        //医院名称
        departCb.setChecked(sh.readDepartPrint());  //科室名称
        docCb.setChecked(sh.readDocPrint());         //医生名称
        audCb.setChecked(sh.readAudPrint());        //审核人
        dateCb.setChecked(sh.readDatePrint());
        timeCb.setChecked(sh.readTimePrint());
        //referCb.setChecked(sh.readReferPrint());    //参考值
        numCb.setChecked(sh.readSamnumPrint());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_print_save:
                sh.savePrint(autoCb.isChecked(),hosCb.isChecked(),
                        departCb.isChecked(),docCb.isChecked(),audCb.isChecked(),
                        dateCb.isChecked(),timeCb.isChecked(),
                        referCb.isChecked(),numCb.isChecked());
                finish();
                break;
            case R.id.id_print_back:
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
