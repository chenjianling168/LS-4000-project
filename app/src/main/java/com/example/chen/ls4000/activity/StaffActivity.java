package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.utils.CurveChart;
import com.example.chen.ls4000.utils.CustomCurveChart;
import com.example.chen.ls4000.utils.DrawLineChart;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018-10-16.
 * 工程师调试
 */

public class StaffActivity extends Activity implements View.OnClickListener{

    private static final int REFRESH = 0;
    private ImageView back , teststaff;
    private EditText peakC,placeC,textC;
    private EditText peakT3,peakT2,peakT1;
    private EditText placeT3,placeT2,placeT1;
    private EditText textT1C,textT2C,textT3C;
    private EditText textT1,textT2,textT3;
    private CurveChart customCurveChart;
    private RelativeLayout linearlayout2;
    private SharedHelper sh;
    private int[] result ,resulted;
    private Sample sample;
    private float[] res;
    private float resMin,resMax;
    private EditText amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        hideBottomUIMenu();

        back=(ImageView)findViewById(R.id.id_staff_back);           //返回
        teststaff=(ImageView)findViewById(R.id.id_staff_testBtn);   //检测
        amount = (EditText)findViewById(R.id.id_staff_amount);               //连卡的选择
        customCurveChart = (CurveChart)findViewById(R.id.id_staff_customCurveChart);
        linearlayout2=(RelativeLayout)findViewById(R.id.id_staff_linearlayout3);

        textC=(EditText)findViewById(R.id.id_staff_textC);      //C值
        placeC=(EditText)findViewById(R.id.id_staff_placeC);    //C位置
        peakC=(EditText)findViewById(R.id.id_staff_peakC);      //C峰值

        peakT1=(EditText)findViewById(R.id.id_staff_peakT1);    //T1峰值
        peakT2=(EditText)findViewById(R.id.id_staff_peakT2);    //T2峰值
        peakT3=(EditText)findViewById(R.id.id_staff_peakT3);    //T3峰值

        placeT1=(EditText)findViewById(R.id.id_staff_placeT1);  //T1位置
        placeT2=(EditText)findViewById(R.id.id_staff_placeT2);  //T2位置
        placeT3=(EditText)findViewById(R.id.id_staff_placeT3);  //T3位置

        textT1C=(EditText)findViewById(R.id.id_staff_textT1C);  //T/1值
        textT2C=(EditText)findViewById(R.id.id_staff_textT2C);  //T/2值
        textT3C=(EditText)findViewById(R.id.id_staff_textT3C);  //T/3值

        textT1=(EditText)findViewById(R.id.id_staff_textT1);    //T1值
        textT2=(EditText)findViewById(R.id.id_staff_textT2);    //T2值
        textT3=(EditText)findViewById(R.id.id_staff_textT3);    //T3值

        back.setOnClickListener(this);
        teststaff.setOnClickListener(this);

        EventBus.getDefault().register(this);

        sh = new SharedHelper(this);

        amount.setText(sh.readAmount());

        Bundle bundle =getIntent().getExtras();
        byte[] bytes = bundle.getByteArray("result");
        if(bytes != null) {
            result = Utils.bytesToIntArray(bytes);
            float t1 = bundle.getFloat("text1");
            textT1.setText(t1 +"mv");
            double t1cp = bundle.getDouble("t1CPosi");
            textT1C.setText(t1cp+"");
            int t1p = bundle.getInt("t1Place");
            placeT1.setText(t1p+"");
            float t1k = bundle.getFloat("t1Peak");
            peakT1.setText(t1k+"mV");

            float c = bundle.getFloat("textC");
            textC.setText(c+"mV");
            int cp = bundle.getInt("placeC");
            placeC.setText(cp+"");
            float ck = bundle.getFloat("peakC");
            peakC.setText(ck+"mV");

            if("2".equals(sh.readAmount())){
                float t2 = bundle.getFloat("text2");
                textT2.setText(t2 +"mv");
                double t2cp = bundle.getDouble("t2CPosi");
                textT2C.setText(t2cp+"");
                int t2p = bundle.getInt("t2Place");
                placeT2.setText(t2p+"");
                float t2k = bundle.getFloat("t2Peak");
                peakT2.setText(t2k+"mV");

            }else if("3".equals(sh.readAmount())){
                float t2 = bundle.getFloat("text2");
                textT2.setText(t2 +"mv");
                double t2cp = bundle.getDouble("t2CPosi");
                textT2C.setText(t2cp+"");
                int t2p = bundle.getInt("t2Place");
                placeT2.setText(t2p+"");
                float t2k = bundle.getFloat("t2Peak");
                peakT2.setText(t2k+"mV");

                float t3 = bundle.getFloat("text3");
                textT3.setText(t3 +"mv");
                double t3cp = bundle.getDouble("t3CPosi");
                textT3C.setText(t3cp+"");
                int t3p = bundle.getInt("t3Place");
                placeT3.setText(t3p+"");
                float t3k = bundle.getFloat("t3Peak");
                peakT3.setText(t3k+"mV");
            }
            setDataed(customCurveChart);
        }



    }


    private void setDataed(final DrawLineChart chart){
        res = new float[result.length +10 ];
        resMin = 10000000;
        resMax = 0;
        for (int i = 0; i < res.length -10 ; i++) {     //原来的数值450
            res[i] = result[i];
            if(resMin>res[i]){
                resMin = res[i];
                }
            if(resMax<res[i]){
                resMax = res[i];
                }
            }

        chart.setBrokenLineLTRB(60,20,15,10);   //设置边框左上右下边距
        chart.setRadius(2.5f);              //圆的半径
        chart.setCircleWidth(1f);           //设置宽度
        chart.setBorderTextSize(10);           //边框文本大小
        chart.setBrokenLineTextSize(10);        //折线文本大小
        chart.setMaxVlaue(resMax);              //图表显示最大值
        chart.setMinValue(resMin);          //图表显示最小值
        chart.setNumberLine(5);             //图表横线数量
        chart.setBorderWidth(1f);           //边框线宽度
        chart.setBrokenLineWidth(1.5f);         //折线宽度
        chart.setBorderTransverseLineWidth(0.3f);       //边框横线宽度
        Random random = new Random();
//        float result[] = new float[res.length-10];
//        for (int i = 0; i < result.length; i++) {
//            result[i] = res[i];
//        }
        chart.setValue(res);
        chart.invalidate();
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
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    Bundle bundle;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response) throws UnsupportedEncodingException{
        int comm = response.getComm();
        String data = response.getMes();
        byte[] bys = response.getBys();

        if(comm == 0x12) {      //0346
            System.out.println("bytes数据"+bys.length);
            System.out.println("data数据"+ data);
            double[] ss = new double[9];
           if("1".equals(sh.readAmount())){
               String str1 =data.substring(0,6);
               byte[] byt = str1.getBytes();
               System.out.print("收到数据：" + str1 );
               byte byt1 = (byte) (byt[0]  - 0x30);

               System.out.print("看看解析数据：" + byt1);
               int len1 = byt1 / 10;
               int len2 = byt1 % 10;
               String str2 = len1 +""+ len2;
               String str3 = str1.substring(0,1);
               System.out.print("解析：" + str2 + "解析：" + len1 +  "解析：" +len2);
               if(Integer.parseInt(str2 ) > 9){
                   String res2 = str1.replace(str3,str2);
                   System.out.println(res2);
                   ss[0] = Double.valueOf(res2.substring(0,6)) / 10000;
               }else{
                   ss[0] = Double.valueOf(str1.substring(0,6)) / 100000;
               }
           }else if("2".equals(sh.readAmount())){
               String str1 =data.substring(0,6);
               String str2 =data.substring(6,12);
               byte[] byt = str1.getBytes();
               byte byt1 = (byte) (byt[0]  - 0x30);
               int len1 = byt1 / 10;
               int len2 = byt1 % 10;
               String num1 = len1 +""+ len2;

               byte[] byt2 = str2.getBytes();
               byte bytnum2 = (byte) (byt2[0]  - 0x30);
               int len3 = bytnum2 / 10;
               int len4 = bytnum2 % 10;
               String num2 = len3 +""+ len4;

               String pag1 = str1.substring(0,1);
               String pag2 = str2.substring(0,1);

               System.out.print("解析：" + num1 + "解析：" + len1 +  "解析：" +len2);
               if(Integer.parseInt(num1 ) > 9){
                   String res1 = str1.replace(pag1,num1);
                   System.out.println(res1);
                   ss[0] = Double.valueOf(res1.substring(0,6)) / 10000;
               }else{
                   ss[0] = Double.valueOf(str1.substring(0,6)) / 100000;
               }

               System.out.print("解析：" + num2 + "解析：" + len3 +  "解析：" +len4);
               if(Integer.parseInt(num2) > 9){
                   String res2 = str2.replace(pag2,num2);
                   ss[1] = Double.valueOf(res2.substring(0,6)) / 10000;
               }else{
                   ss[1] = Double.valueOf(str1.substring(0,6)) / 100000;
               }
            }else if("3".equals(sh.readAmount())){
               String str1 =data.substring(0,6);
               String str2 =data.substring(6,12);
               String str3 =data.substring(12,18);

               byte[] byt = str1.getBytes();
               byte byt1 = (byte) (byt[0]  - 0x30);
               int len1 = byt1 / 10;
               int len2 = byt1 % 10;
               String num1 = len1 +""+ len2;
               System.out.print("解析：" + num1 + len1 +len2);


               byte[] byt2 = str2.getBytes();
               byte bytnum2 = (byte) (byt2[0]  - 0x30);
               int len3 = bytnum2 / 10;
               int len4 = bytnum2 % 10;
               String num2 = len3 +""+ len4;

               byte[] byt3 = str3.getBytes();
               byte bytnum3 = (byte) (byt3[0]  - 0x30);
               int len5 = bytnum3 / 10;
               int len6 = bytnum3 % 10;
               String num3 = len5 +""+ len6;

               String pag1 = str1.substring(0,1);
               String pag2 = str2.substring(0,1);
               String pag3 = str3.substring(0,1);

               System.out.print("解析：" + num1 + "解析：" + len1 +  "解析：" +len2);
               if(Integer.parseInt(num1 ) > 9){
                   String res1 = str1.replace(pag1,num1);
                   System.out.println(res1);
                   ss[0] = Double.valueOf(res1.substring(0,6)) / 10000;
               }else{
                   ss[0] = Double.valueOf(str1.substring(0,6)) / 100000;
               }

               System.out.print("解析：" + num2 + "解析：" + len3 +  "解析：" +len4);
               if(Integer.parseInt(num2) > 9){
                   String res2 = str2.replace(pag2,num2);
                   ss[1] = Double.valueOf(res2.substring(0,6)) / 10000;
               }else{
                   ss[1] = Double.valueOf(str2.substring(0,6)) / 100000;
               }

               System.out.print("解析：" + num3 + "解析：" + len5 +  "解析：" +len6);
               if(Integer.parseInt(num3) > 9){
                   String res3 = str3.replace(pag3,num3);
                   ss[2] = Double.valueOf(res3.substring(0,6)) / 10000;
               }else{
                   ss[2] = Double.valueOf(str3.substring(0,6)) / 100000;
               }
            }

            byte[] t1Posi = new byte[4];        //T1值
            byte[] t2Posi = new byte[4];        //T2值
            byte[] t3Posi = new byte[4];        //T3值

            byte[] t1CPosi = new byte[6];       //T1/C值
            byte[] t2CPosi = new byte[6];       //T2/C值
            byte[] t3CPosi = new byte[6];       //T3/C值

            byte[] t1Place = new byte[2];       //T1位置
            byte[] t2Place = new byte[2];       //T2位置
            byte[] t3Place = new byte[2];       //T3位置

            byte[] t1Peak = new byte[4];        //T1峰值
            byte[] t2Peak = new byte[4];        //T2峰值
            byte[] t3Peak = new byte[4];        //T3峰值

            byte[] cPosi = new byte[4];        //C值
            byte[] cPlace = new byte[2];        //C位置
            byte[] cPeak = new byte[4];        //C峰值
            byte[] bytes = new byte[bys.length];      //图形

            System.arraycopy(bys, 34, t1Posi, 0, 4);         //T1值

            System.arraycopy(bys, 0, t1CPosi, 0, 6);       //T1/C值
            System.arraycopy(bys, 6, t2CPosi, 0, 6);       //T2/C值
            System.arraycopy(bys, 12, t3CPosi, 0, 6);       //T3/C值

            System.arraycopy(bys, 20, t1Place, 0, 2);       //T1位置

            System.arraycopy(bys, 26, t1Peak, 0, 4);        //T1峰值

            System.arraycopy(bys, 30, cPosi, 0, 4);         //C值
            System.arraycopy(bys, 18, cPlace, 0, 2);        //C位置
            System.arraycopy(bys, 22, cPeak, 0, 4);        //C峰值


            if("1".equals(amount.getText().toString())){
                System.arraycopy(bys,38,bytes,0,800);
            }else if("2".equals(amount.getText().toString())){
                System.arraycopy(bys, 38, t2Place, 0, 4);       //T2位置
                System.arraycopy(bys, 42, t2Posi, 0, 2);       //T2值
                System.arraycopy(bys, 44, t2Peak, 0, 4);        //T2峰值
                System.arraycopy(bys,48,bytes,0,790);
            }else if("3".equals(amount.getText().toString())){
                System.arraycopy(bys, 38, t2Place, 0, 2);          //T2位置
                System.arraycopy(bys, 40, t2Posi, 0, 4);      //T2值
                System.arraycopy(bys, 44, t2Peak, 0, 4);        //T2峰值

                System.arraycopy(bys, 48, t3Place, 0, 2);         //T3位置
                System.arraycopy(bys, 50, t3Posi, 0, 4);       //T3值
                System.arraycopy(bys, 54, t3Peak, 0, 4);        //T3峰值
                System.arraycopy(bys,58,bytes,0,780);
            }



            textT1.setText(((float)Utils.byteArrayToInt(t1Posi) / 1000) + "mV");   //T1值
            placeT1.setText(Utils.bytesToInt(t1Place) + "");  //T1位置
            peakT1.setText( ((float)Utils.byteArrayToInt(t1Peak) / 1000) + "mV");    //T1峰值

//            float tc = (float) Utils.bytesToInt(t1CPosi) /10000;
            textT1C.setText(ss[0] + "");  //T1/C值

            float c =  (float) Utils.byteArrayToInt(cPosi) /1000;
            textC.setText(c + "mV");    //C值
            placeC.setText(Utils.bytesToInt(cPlace) + "");   //C位置
            float cp = (float)  Utils.byteArrayToInt(cPeak) / 1000;
            peakC.setText( cp + "mV");    //C峰值

            if("2".equals(amount.getText().toString())){
                textT2.setText( ((float)Utils.byteArrayToInt(t2Posi) / 1000) + "mV");   //T2值
                placeT2.setText(Utils.bytesToInt(t2Place) + "");  //T2位置
                peakT2.setText( ((float)Utils.byteArrayToInt(t2Peak) / 1000) + "mV");    //T2峰值
                textT2C.setText(ss[1] + "");  //T2/C值
            }else if("3".equals(amount.getText().toString())){
                textT2.setText( ((float)Utils.byteArrayToInt(t2Posi) / 1000) + "mV");   //T2值
                placeT2.setText(Utils.bytesToInt(t2Place) + "");  //T2位置
                peakT2.setText( ((float)Utils.byteArrayToInt(t2Peak) / 1000) + "mV");    //T2峰值
                textT2C.setText(ss[1] + "");  //T2/C值

                textT3.setText(((float)Utils.byteArrayToInt(t3Posi) / 1000) + "mV");   //T3值
                placeT3.setText(Utils.bytesToInt(t3Place) + "");  //T3位置
                peakT3.setText( ((float)Utils.byteArrayToInt(t3Peak) / 1000) + "mV");    //T3峰值
                textT3C.setText(ss[2] + "");  //T2/C值

                System.out.print("T1值" + ((float)Utils.byteArrayToInt(t1Posi) / 1000) + "T2值"+ ((float)Utils.byteArrayToInt(t2Posi) / 1000) +"T3值" + ((float)Utils.byteArrayToInt(t3Posi) / 1000));
                System.out.print("T1位置" + Utils.bytesToInt(t1Place)
                        + "T2位置"+ Utils.bytesToInt(t2Place) +"T3位置" +
                        (Utils.bytesToInt(t3Place)));
                System.out.print( "T1值" + ((float)Utils.byteArrayToInt(t1Peak) / 1000) +
                        "T2峰值"+((float)Utils.byteArrayToInt(t2Peak) / 1000) + "T3峰值 "+
                        ((float)Utils.byteArrayToInt(t3Peak) / 1000));

            }


            result = Utils.bytesToIntArray(bytes);
            res = new float[bytes.length/2 +10 ];       //原来的数值450
            resMin = 10000000;
            resMax = 0;


            bundle = new Bundle();
            bundle.putByteArray("result",bytes);

            bundle.putFloat("text1",(float)  Utils.byteArrayToInt(t1Posi) / 1000 );
            bundle.putDouble("t1CPosi",ss[0]);
            bundle.putInt("t1Place",Utils.bytesToInt(t1Place));
            bundle.putFloat("t1Peak",(float)  Utils.byteArrayToInt(t1Peak) / 1000);

            bundle.putFloat("textC",c);
            bundle.putInt("placeC",Utils.bytesToInt(cPlace));
            bundle.putFloat("peakC",cp);

            if("2".equals(sh.readAmount())){
                bundle.putFloat("text2",(float)  Utils.byteArrayToInt(t2Posi) / 1000);
                bundle.putInt("t2Place",Utils.bytesToInt(t2Place));
                bundle.putFloat("t2Peak",(float)  Utils.byteArrayToInt(t2Peak) / 1000);
                bundle.putDouble("t2CPosi",ss[1]);
            }else if("3".equals(sh.readAmount())){

                bundle.putFloat("text2",(float)  Utils.byteArrayToInt(t2Posi) / 1000);
                bundle.putInt("t2Place",Utils.bytesToInt(t2Place));
                bundle.putFloat("t2Peak",(float)  Utils.byteArrayToInt(t2Peak) / 1000);
                bundle.putDouble("t2CPosi",ss[1]);

                bundle.putFloat("text3",(float)  Utils.byteArrayToInt(t3Posi) / 1000);
                bundle.putInt("t3Place",Utils.bytesToInt(t3Place));
                bundle.putFloat("t3Peak",(float)  Utils.byteArrayToInt(t3Peak) / 1000);
                bundle.putDouble("t3CPosi",ss[2]);
            }

            customCurveChart.setCircleColor(getResources().getColor(R.color.color13));
            customCurveChart.setBrokenLineTextColor(getResources().getColor(R.color.color13));
            customCurveChart.setBrokenLineColor(getResources().getColor(R.color.color13));

            //initCurveChart();

            teststaff.setImageDrawable(getResources().getDrawable(R.mipmap.testbtn));
            teststaff.setClickable(true);


            //mHandler.postDelayed(mRunnable,10);
            Intent intent = new Intent(StaffActivity.this,StaffActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();

        }else if (comm == 0x11) {
            popProTip(getString(R.string.string31));
        }else if(comm == 0xC4){
            popDialog("质检卡条错误");
        }
    }

    //确定的响应事件
    private void popDialog(String str) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.string196))//设置对话框标题
                .setMessage(str)//设置显示的内容
                .setCancelable(false)
                .setPositiveButton(getString(R.string.string89), new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        dialog.dismiss();
                    }
                }).show();//在按键响应事件中显示此对话框

    }

    private void initCurveChart() {
        String[] xLabel = new String[res.length/2+10];
        for (int i = 0; i < res.length/2; i++) {
            xLabel[i] = i+"";
        }

        String strMin = String.valueOf(resMin);
        String strcen = String.valueOf(resMax/2);
        String str03 = String.valueOf(resMax/3);
        String str04 = String.valueOf(resMax/4);
        String strMax = String.valueOf(resMax);
        String[] yLabel = {strMin, str04, str03, strcen, strMax};
        int[] data2 = {400, 600, 650, 600, 400, 800, 900, 850, 650, 700, 500, 400};
        List<int[]> data = new ArrayList<int[]>();
        List<Integer> color = new ArrayList<Integer>();

        System.out.println("曲线数据"+res.length);
        int result[] = new int[res.length-10];
        for (int i = 0; i < result.length; i++) {
            result[i] = (int) res[i];
        }
        data.add(result);

        color.add(R.color.color14);

        linearlayout2.addView(new CustomCurveChart(this, xLabel, yLabel, data, color, false));
    }





    @Override
    protected void onResume() {
        super.onResume();

    }

//    public void postInvalidate() {
//        postInvalidateDelayed(0);
//    }
//    public void postInvalidateDelayed(long delayMilliseconds){
//        StaffActivity mAttachInfo = new StaffActivity();
//        if (mAttachInfo != null) {
//            Message msg = Message.obtain();
//            msg.what = StaffActivity.INVALIDATE_MSG;
//            msg.obj = this;
//        }
//
//    }


    private void popProTip(String str) {
        final View view;

//        if("English".equals(lan)){
//            view = View.inflate(this,R.layout.mydialog_eng,null);
//        }else{
        view = View.inflate(this, R.layout.tiplayout, null);
        //  }

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
                BleMethodCode methodCode;
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x0C);
                EventBus.getDefault().post(methodCode);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teststaff.setImageDrawable(getResources().getDrawable(R.mipmap.testbtn));
                teststaff.setClickable(true);
                back.setClickable(true);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        BleMethodCode bleMethodCode;
        switch (v.getId()){
            case R.id.id_staff_back:

                sh.saveStaff(textT1.getText().toString(),textT2.getText().toString(),textT3.getText().toString(),
                        placeT1.getText().toString(),placeT2.getText().toString(),placeT3.getText().toString(),
                        textT1C.getText().toString(),textT2C.getText().toString(),textT3C.getText().toString(),
                        peakT1.getText().toString(),peakT2.getText().toString(),peakT3.getText().toString(),
                        textC.getText().toString(),placeC.getText().toString(),peakC.getText().toString());

                intent = new Intent(StaffActivity.this,EngineerActivity.class);
                startActivity(intent);
                break;

            case R.id.id_staff_testBtn:
                bleMethodCode = new BleMethodCode(3);
                if(!"".equals(amount.getText().toString())){
                    bleMethodCode.setMes(amount.getText().toString());
                }else{
                    Utils.showToast(StaffActivity.this,"请输入连卡号",2000);
                }

                bleMethodCode.setComm((byte) 0x0C);
                EventBus.getDefault().post(bleMethodCode);

                sh.saveAmount(amount.getText().toString());
                teststaff.setClickable(false);

                textT1.setText("");
                textT2.setText("");
                textT3.setText("");

                placeT1.setText("");
                placeT2.setText("");
                placeT3.setText("");


                textT1C.setText("");
                textT2C.setText("");
                textT3C.setText("");

                peakT1.setText("");
                peakT2.setText("");
                peakT3.setText("");

                textC.setText("");
                placeC.setText("");
                peakC.setText("");


                boolean isChaned = false;
                if(v == teststaff){
                    if(isChaned){
                        teststaff.setImageDrawable(getResources().getDrawable(R.mipmap.testbtn));
                    }else{
                        teststaff.setImageDrawable(getResources().getDrawable(R.mipmap.testbtn_1));
                    }
                }
                teststaff.setClickable(false);
                break;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
