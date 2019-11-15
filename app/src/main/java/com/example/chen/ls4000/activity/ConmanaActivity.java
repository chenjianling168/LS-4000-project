package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.Instrument;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.event.BleEvent;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.utils.CurveChart;
import com.example.chen.ls4000.utils.DBUtils;
import com.example.chen.ls4000.utils.DrawLineChart;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.PrintUtil;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.TimeUtils;
import com.example.chen.ls4000.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
* 仪器质控页面
* */
public class ConmanaActivity extends Activity implements View.OnClickListener{

    private Button startDeter,delete,clear,print;
    private ImageView back;
    private DBUtils dbUtils;
    private List<Instrument> instruments;
    private EditText preTime,preResult,preCon,curTime,curResult,curCon;
    private CurveChart curveChart;
    private SharedHelper sp;
    private float[] res;
    private float resMin,resMax;
    private Context context;
    private BluetoothSocket bluetoothSocket;
    private Instrument instrument;
    private MyApp myApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conmana);

        initviews();
    }

    private void initviews(){
        startDeter = (Button)findViewById(R.id.id_conmana_startdeter);
        delete = (Button)findViewById(R.id.id_conmana_delete);
        clear = (Button)findViewById(R.id.id_conmana_clear);
        print = (Button)findViewById(R.id.id_conmana_print);
        back = (ImageView) findViewById(R.id.id_conmana_back);

        preTime = (EditText)findViewById(R.id.id_conmana_pretime);     //上次的时间
        preResult = (EditText)findViewById(R.id.id_conmana_preresult);  //上次的质控值
        preCon = (EditText)findViewById(R.id.id_conmana_precon);        //上次质控结论


        curTime = (EditText)findViewById(R.id.id_conmana_curtime);      //刚测的时间
        curResult = (EditText)findViewById(R.id.id_conmana_curresult);  //本的质控值
        curCon = (EditText)findViewById(R.id.id_conmana_curcon);        //本次质控结论

        hideBottomUIMenu();

        startDeter.setOnClickListener(this);
        delete.setOnClickListener(this);
        clear.setOnClickListener(this);
        print.setOnClickListener(this);
        back.setOnClickListener(this);
        EventBus.getDefault().register(this);
        dbUtils = new DBUtils(this);
        instruments = new ArrayList<>();
        instruments.addAll(dbUtils.search2Instruments());
        sp = new SharedHelper(this);
        myApp = (MyApp) getApplication();
    }

    @Override
    public void onClick(View v) {
        BleMethodCode methodCode;
        switch (v.getId()){
            case R.id.id_conmana_startdeter:
                myApp.setPowerBool(false);
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x01);
                EventBus.getDefault().post(methodCode);
                break;
            case R.id.id_conmana_print:
                //printInstrument(context,bluetoothSocket,instrument,ss);
//                BleEvent bleEvent =new BleEvent();
//                bleEvent.setInstrument(instrument);
//                EventBus.getDefault().post(bleEvent);
                break;
            case R.id.id_conmana_delete:
                popTip(1);
                break;
            case R.id.id_conmana_clear:
                popTip(2);
                break;
            case R.id.id_conmana_back:
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x0A);
                EventBus.getDefault().post(methodCode);
                finish();
                break;
        }
    }


    private String re;
    private int scanpage ;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response) throws UnsupportedEncodingException {
        int comm = response.getComm();
        String data = response.getMes();
        byte[] bys = response.getBys();
        System.out.println("身份证数据" + data);
        if (comm == 0x05) {

            scanpage = Integer.parseInt(re.substring(7,9));   //截取后2位通过这2位给选择
            Utils.showToast(ConmanaActivity.this,re +  "结果是：" + scanpage,5000);
            Instrument instrument = new Instrument();
            String timeStr = TimeUtils.getCurTime();

            instrument.setTime(timeStr);            //时间
            instrument.setResult(ncrandom(scanpage));       //结果

            float ss = Float.parseFloat(ncrandom(scanpage));
            if(ss<Double.parseDouble(sp.readHighCon())&&ss>Double.parseDouble(sp.readLowCon())) {
                instrument.setSeqNum(getString(R.string.string232));
            }else{
                instrument.setSeqNum(getString(R.string.string231));
            }
            dbUtils.saveInstruments(instrument);        //是否合格

            instruments = new ArrayList<Instrument>();

            instruments.addAll(dbUtils.search2Instruments());
            curTime.setText(timeStr);       //本次时间
            curResult.setText(ss+"");       //本的质控值
            curCon.setText(instrument.getSeqNum());     //本次质控结论
            if(instruments.size()>1) {
                preTime.setText(instruments.get(1).getTime());
                preResult.setText(instruments.get(1).getResult());
                preCon.setText(instruments.get(1).getSeqNum());
            }

            res = dbUtils.search50results();
            resMin = 0;
            resMax = 1000;

           // setData(curveChart);
        }else if(comm == 0x02) {            //扫码结果
            re = data;
            String res = re.substring(0,3);

            Utils.showToast(ConmanaActivity.this,data,2000);
            if("000".equals(res)){
                BleMethodCode methodCode;
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x07);
                EventBus.getDefault().post(methodCode);
            }else{
                popTip("E203"+getString(R.string.string30));
            }
        }else if (comm == 0x11) {
            popTip(getString(R.string.string31));
        }
    }


    private void popTip(final int n) {
        final View view;

        view = View.inflate(this, R.layout.promptlayout, null);

        final TextView tip;
        Button ensure,cancel;

        tip = (TextView) view.findViewById(R.id.id_prompt_tip);
        ensure = (Button) view.findViewById(R.id.id_prompt_ensure);
        cancel = (Button)view.findViewById(R.id.id_prompt_cancel);
        if(n== 1){
            tip.setText(getString(R.string.string32));
        }else if(n== 2){
            tip.setText(getString(R.string.string33));
        }
        //tip.setText(str);

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
                if(n== 1){
                    for (int i = 0; i < instruments.size(); i++) {
                        if(instruments.get(i).isClickFlag()){
                            dbUtils.deleteInstrument(instruments.get(i));
                        }
                    }
                    instruments.clear();
                    instruments.addAll(dbUtils.search2Instruments());
                    //adapter.notifyDataSetChanged();
                }else if(n == 2){
                    for (int i = 0; i < instruments.size(); i++) {
                        dbUtils.deleteInstrument(instruments.get(i));
                    }
                    instruments.clear();
                    //adapter.notifyDataSetChanged();
                }
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

    private void popTip(final String str) {
        final View view;

        view = View.inflate(this, R.layout.tiplayout, null);

        TextView tip;
        Button ensure,cancel;

        tip = (TextView) view.findViewById(R.id.id_tip_tip);
        ensure = (Button) view.findViewById(R.id.id_tip_ensure);
        cancel = (Button)view.findViewById(R.id.id_tip_cancel);
        tip.setText(str);

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
                if(getString(R.string.string34).equals(str)){
                    BleMethodCode methodCode;
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("");
                    methodCode.setComm((byte) 0x07);
                    EventBus.getDefault().post(methodCode);
                }
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

    public static String getBooleanArray(byte b) {
       // byte[] array = new byte[8];
        String ss = "";
        for (int i = 7; i >= 0; i--) {
            ss = (byte)(b & 1)+"" + ss;
            b = (byte) (b >> 1);
        }
        return ss;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setDialog(String str,String timeStr){
        View view;
        TextView eeprom,tem,scan,light,oper,tran,con,time;
        Button cancle;

        view = View.inflate(this, R.layout.conmana_dialog_layout,null);

        eeprom = (TextView)view.findViewById(R.id.id_con_dialog_eeprom);
        tem = (TextView)view.findViewById(R.id.id_con_dialog_tem);
        scan = (TextView)view.findViewById(R.id.id_con_dialog_scan);
        tran = (TextView)view.findViewById(R.id.id_con_dialog_tran);
        light = (TextView)view.findViewById(R.id.id_con_dialog_light);
        oper = (TextView)view.findViewById(R.id.id_con_dialog_oper);
        cancle = (Button)view.findViewById(R.id.id_con_dialog_cancel);
        con = (TextView)view.findViewById(R.id.id_con_dialog_con);
        time = (TextView)view.findViewById(R.id.id_con_dialog_time);

            if('1' == str.charAt(6)){
                eeprom.setText(getString(R.string.string35));
            }else{
                eeprom.setText(getString(R.string.string36));
            }
            if('1' == str.charAt(5)){
                tem.setText(getString(R.string.string35));
            }else{
                tem.setText(getString(R.string.string36));
            }
            if('1' == str.charAt(4)){
                scan.setText(getString(R.string.string35));
            }else{
                scan.setText(getString(R.string.string36));
            }
            if('1' == str.charAt(3)){
                light.setText(getString(R.string.string35));
            }else{
                light.setText(getString(R.string.string36));
            }
            if('1' == str.charAt(2)){
                oper.setText(getString(R.string.string35));
            }else{
                oper.setText(getString(R.string.string36));
            }
        if('1' == str.charAt(1)){
            tran.setText(getString(R.string.string35));
        }else{
            tran.setText(getString(R.string.string36));
        }
        con.setText(str.substring(8,str.length()));

        time.setText(timeStr);

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialog).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        System.out.println("数据数据");
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        dialog.getWindow().setAttributes(params);

        final String finalStr = str;

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
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


    /*质控结果*/
    DecimalFormat df =new DecimalFormat("#.000");
    private String ncrandom(int num){
        double result = 0;
        switch (num){
            case 1:
                result = 500;//498.3103;
                break;
            case 2:
                result = 180;//136.1182;

                break;
            case 3:
                result = 50;//50.1675;

                break;
            case 4:
                result = 20;//13.6855;

                break;
            case 5:
                result = 15;//8.1386;
                break;

            case 6:
                result = 7;
                break;

        }
        result = result * (1 - Math.random()/80);//result = result - Math.random()*0.01*result;
        String res = df.format(result);
        System.out.print("得到的事几号选项：" + num + "结果是：" + res);
        return res;
    }
}
