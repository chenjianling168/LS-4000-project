package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.Pro;
import com.example.chen.ls4000.bean.Project;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.event.BleEvent;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.event.LisEvent;
import com.example.chen.ls4000.utils.CurveChart;
import com.example.chen.ls4000.utils.CustomCurveChart;
import com.example.chen.ls4000.utils.DBUtils;
import com.example.chen.ls4000.utils.DrawLineChart;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.TimeUtils;
import com.example.chen.ls4000.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView name,gender,age,samnum,samtype,proname1,proname2,proname3,
            concen1,concen2,concen3,
            time,testPerson,refer1,refer2,refer3,unit1,unit2,unit3,referLocation,realLocation,audperson;
    private String data;
    private LinearLayout customCurveChart;
    private int[] result;
    private ImageView back;
    private Button print,upload;
    private Sample sample;
    private Project project;
    private MyApp myApp;
    private SharedHelper sp;
    private CurveChart curveChart;
    private float[] res;
    private float resMin,resMax;
    private DBUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        try {
            initviews();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void showToast(final Activity activity, final String word, final long time){
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response) throws UnsupportedEncodingException {
        int  comm = response.getComm();
        String data = response.getMes();
        byte[] bys = response.getBys();
        if(comm == 0x0f){
            concen1.setText(data);
            System.out.println("bytes数据"+bys.length);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initviews() throws UnsupportedEncodingException {

        refer1 = (TextView) findViewById(R.id.id_result_resultinfofont1);    //参考值1
        refer2 = (TextView) findViewById(R.id.id_result_resultinfofont2);    //参考值2
        refer3 = (TextView) findViewById(R.id.id_result_resultinfofont3);    //参考值3


        unit1 = (TextView) findViewById(R.id.id_result1_unit1);  //单位1
        unit2 = (TextView) findViewById(R.id.id_result1_unit2);  //单位2
        unit3 = (TextView) findViewById(R.id.id_result1_unit3);  //单位3

        //真实峰位置
        realLocation = (TextView) findViewById(R.id.tv_realLocation);
        referLocation = (TextView)findViewById(R.id.id_result_referlocation);

        name = (TextView)findViewById(R.id.id_result_name);
        gender = (TextView)findViewById(R.id.id_result_gender);
        age = (TextView)findViewById(R.id.id_result_age);
        samnum = (TextView)findViewById(R.id.id_result_samnum);
        samtype = (TextView)findViewById(R.id.id_result_samtype);

        proname1 = (TextView)findViewById(R.id.id_result_proname1);//项目1
        proname2 = (TextView)findViewById(R.id.id_result_proname2);//项目2
        proname3 = (TextView)findViewById(R.id.id_result_proname3); //项目3
        concen1 = (TextView)findViewById(R.id.id_result_concen1); //浓度1
        concen2 = (TextView)findViewById(R.id.id_result_concen2); //浓度2
        concen3 = (TextView)findViewById(R.id.id_result_concen3); //浓度3


        back = (ImageView)findViewById(R.id.id_result_back);
        time = (TextView)findViewById(R.id.id_result_time);
        testPerson = (TextView)findViewById(R.id.id_result_testperson);
        audperson = (TextView)findViewById(R.id.id_result_audperson);
        customCurveChart = (LinearLayout) findViewById(R.id.id_layout_customCurveChart);
        curveChart = (CurveChart) findViewById(R.id.id_result_customCurveChart);
        print = (Button)findViewById(R.id.id_result_print);
        upload = (Button)findViewById(R.id.id_result_upload);

        EventBus.getDefault().register(this);
        myApp = (MyApp) getApplicationContext();
        sp = new SharedHelper(this);
        dbUtils = new DBUtils(this);

        sample = (Sample) getIntent().getExtras().getSerializable("sample");
        //result = getIntent().getExtras().getIntArray("result");
        result = Utils.bytesToIntArray(sample.getCurveData().getBytes("ISO-8859-1"));
        res = new float[sample.getCurveData().getBytes("ISO-8859-1").length/2 +10];       //原来的数值450
        resMin = 10000000;
        resMax = 0;


        hideBottomUIMenu();

        //真实峰位置

        if((sample.getT2Refer() == null || "".equals(sample.getT2Refer())) && (sample.getT3Refer() == null
                || "".equals(sample.getT3Refer()))){
            referLocation.setText("C:" + sample.getcRefer() + " /T:" + sample.getT1Refer());
        }else if((sample.getT2Refer() != null && !"".equals(sample.getT2Refer())) && (sample.getT3Refer() == null
                || "".equals(sample.getT3Refer()))){
            referLocation.setText("C:" + sample.getcRefer() + " /T1:" + sample.getT1Refer() + "  /T2:"
                    + sample.getT2Refer());
        }else{
            referLocation.setText("C:" + sample.getcRefer() + " /T1: " + sample.getT1Refer() + "  T2:"
                    + sample.getT2Refer() +
                    "  T3:" + sample.getT3Refer());
        }

        if("0".equals(sample.getT2Posi()) && "0".equals(sample.getT3Posi())){
            realLocation.setText("C:" + sample.getcPosi() + " /T:" + sample.getT1Posi());
        }else if(!"0".equals(sample.getT2Posi())&& "0".equals(sample.getT3Posi())){
            realLocation.setText("C:" + sample.getcPosi() + " /T1:" + sample.getT1Posi() + "  /T2:"
                    + sample.getT2Posi());
        }else{
            realLocation.setText("C:" + sample.getcPosi() + " /T1: " + sample.getT1Posi() + "  T2:"
                    + sample.getT2Posi() + "T3:" + sample.getT3Posi());
        }
//        if(("1").equals(sample.getProject().getAmount())){
//            realLocation.setText("C:" + sample.getcPosi() + " /T:" + sample.getT1Posi());
//        }else if(("2").equals(sample.getProject().getAmount())){
//            realLocation.setText("C:" + sample.getcPosi() + " /T1:" + sample.getT1Posi() + "  /T2:" + sample.getT2Posi());
//        }else if(("3").equals(sample.getProject().getAmount())){
//            realLocation.setText("C:" + sample.getcPosi() + " /T1: " + sample.getT1Posi() + "  T2:" + sample.getT2Posi() +
//                                "  T3:" + sample.getT3Posi());
//        }


//        try {
//            switch (Integer.valueOf(sample.getProject().getAmount())){
//                case 1:
//                        realLocation.setText("C:" + sample.getcPosi() + " /T:" + sample.getT1Posi());
//                        break;
//                case 2:
//                        realLocation.setText("C:" + sample.getcPosi() + " /T1:" + sample.getT1Posi() + "  /T2:" + sample.getT2Posi());
//                        break;
//                case 3:
//                        realLocation.setText("C:" + sample.getcPosi() + " /T1: " + sample.getT1Posi() + "  T2:" + sample.getT2Posi() +
//                                "  T3:" + sample.getT3Posi());
//                        break;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }




        name.setText(sample.getName());
        if(getString(R.string.string3).equals(sample.getGender())){
            gender.setText(getString(R.string.string3));
        }else if(getString(R.string.string4).equals(sample.getGender())){
            gender.setText(getString(R.string.string4));
        }else{
            gender.setText("");
        }

        age.setText(sample.getAge());
        samnum.setText(sample.getSamNum());

        if(getString(R.string.string8).equals(sample.getSamType())){
            samtype.setText(getString(R.string.string8));
        }else if(getString(R.string.string9).equals(sample.getSamType())){
            samtype.setText(getString(R.string.string9));
        }else if(getString(R.string.string10).equals(sample.getSamType())){
            samtype.setText(getString(R.string.string10));
        }else if(getString(R.string.string11).equals(sample.getSamType())){
            samtype.setText(getString(R.string.string11));
        }else{
            samtype.setText(getString(R.string.string7));
        }




        String[] proNames = sample.getProName().split("/"); //项目名
        String[] concens = sample.getConcen().split("/");   //结果
        switch (proNames.length){
            case 1: //单联
                proname1.setText(proNames[0]);
                concen1.setText(concens[0]);
                refer1.setText(sample.getRefer());
                unit1.setText(sample.getUnit());
                break;
            case 2: //2联卡
                proname1.setText(proNames[0]);
                concen1.setText(concens[0]);
                refer1.setText(sample.getRefer());
                unit1.setText(sample.getUnit());

                proname2.setText(proNames[1]);
                concen2.setText(concens[1]);
                refer2.setText(sample.getRefer2());
                unit2.setText(sample.getUnit2());

                break;
            case 3: //3联卡
                proname1.setText(proNames[0]);
                concen1.setText(concens[0]);
                refer1.setText(sample.getRefer());
                unit1.setText(sample.getUnit());

                proname2.setText(proNames[1]);
                concen2.setText(concens[1]);
                refer2.setText(sample.getRefer2());
                unit2.setText(sample.getUnit2());

                proname3.setText(proNames[2]);
                concen3.setText(concens[2]);
                refer3.setText(sample.getRefer3());
                unit3.setText(sample.getUnit3());
                break;
        }
//        proname1.setText(sample.getProName());
//        if(("1").equals(myApp.getConcenFlap())){
//            concen1.setText(sample.getConcen());
//        }else if(("2").equals(myApp.getConcenFlap())){
//            String conStr = sample.getConcen();
//            String[] conStr2 = conStr.split("[/]");
//            concen1.setText(conStr2[0]);
//            concen2.setText(conStr2[1]);
//        }else if(("3").equals(myApp.getConcenFlap())){
//            String conStr = sample.getConcen();
//            String[] conStr3 = conStr.split("[/]");
//            concen1.setText(conStr3[0]);
//            concen2.setText(conStr3[1]);
//            String conStr4 =conStr.substring(conStr.lastIndexOf("/")+1);
//            concen3.setText(conStr4);
//        }
        //浓度值分类解决,之后写项目分类


        time.setText(sample.getTestTime());
        testPerson.setText(sample.getTestPeo());
        audperson.setText(sample.getAudPeo());
        back.setOnClickListener(this);
        upload.setOnClickListener(this);
        print.setOnClickListener(this);

        //initCurveChart();

        setData(curveChart);
        //curveChart.setBrokenLineLTRB(60,10,0,5);
        //curveChart.setBorderLineColor(getResources().getColor(R.color.color13));
        curveChart.setCircleColor(getResources().getColor(R.color.color13));
        curveChart.setBrokenLineTextColor(getResources().getColor(R.color.color13));
        curveChart.setBrokenLineColor(getResources().getColor(R.color.color13));

//        String textTime = sample.getTestTime();
//        String minTime="yy.MM.dd";
//        String xxxTime=TimeUtils.getWantDate(textTime,minTime);
//
//        time.setText(xxxTime);
    }

    private void setData(DrawLineChart chart) throws UnsupportedEncodingException {
        for (int i = 0; i < sample.getCurveData().getBytes("ISO-8859-1").length/2 -10; i++) {     //原来的数值450
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

        chart.setValue(res);
    }

    private void initCurveChart() {
        String[] xLabel = new String[500];
        for (int i = 0; i < 500; i++) {
            xLabel[i] = i+"";
        }
       // String[] xLabel = {"0","25", "50","75","100","125", "150","175", "200","225", "250","275", "300",
        //        "325","350","375", "400","425", "450","475", "500","525", "550"};
//        String[] yLabel = {"0", "30000", "60000", "90000", "120000", "150000", "180000", "210000", "240000", "270000",
//                "300000", "330000","360000", "390000"};
        String[] yLabel = {"0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
//        int[] data1 = {300, 500, 550, 500, 300, 700, 800, 750, 550, 600, 400, 300};
        int[] data2 = {400, 600, 650, 600, 400, 800, 900, 850, 650, 700, 500, 400};
//        int[] data3 = {500, 700, 750, 700, 500, 900, 1000, 950, 750, 800, 600, 500};
        List<int[]> data = new ArrayList<int[]>();
        List<Integer> color = new ArrayList<Integer>();
        System.out.println("曲线数据"+result.length);
        data.add(result);
        color.add(R.color.color14);
//        data.add(data2);
//        color.add(R.color.color13);
//        data.add(data3);
//        color.add(R.color.color25);
        customCurveChart.addView(new CustomCurveChart(this, xLabel, yLabel, data, color, false));
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




    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.id_result_back:
                finish();
                break;
            case R.id.id_result_print://打印
                if(sp.readBluetoothState()) {
                   if( "1".equals(sp.readUpdateFlag())){
                       BleEvent bleEvent = new BleEvent();
                       bleEvent.setSample(sample);
                       EventBus.getDefault().post(bleEvent);
                   }else{
                       showToast(this,getString(R.string.string245),1000);
                   }

                }else {
                    showToast(this,getString(R.string.string82),1000);
                }

                break;


            case R.id.id_result_upload:
                //pushBool[0] = true;
                String timeStr = sample.getTestTime();
//                timeStr = timeStr.replaceAll("-", "");
//                timeStr = timeStr.replaceAll(" ", "");
//                timeStr = timeStr.replaceAll(":", "");
                timeStr = timeStr.replaceAll("." , "");
                System.out.println("数据timeStr" + timeStr);
                String textTime = timeStr;
                String minTime="yyyyMMdd";
                String resultTime = TimeUtils.getWantDate(textTime,minTime);


                project = dbUtils.searchProject(sample.getProNum());  //上传

                if(project==null){
                    showToast(this,getString(R.string.string221),1000);
                    return;
                }else{
                    showToast(this,getString(R.string.string222),1000);
                }
                sample.setProject(project);
                int amount = Integer.valueOf(sample.getProject().getAmount());
                String[] datas = new String[100];
                datas = sample.getProject().getData().split(",");
                Pro[] pros = new Pro[amount];
                for (int j = 0; j < amount; j++) {
                    pros[j] = new Pro();
                    pros[j].setReferLow(datas[0+20*j]);
                    pros[j].setReferHigh(datas[1+20*j]);
                    pros[j].setUnit(datas[4+20*j]);
                }

                String[] cons = sample.getConcen().split("/");

                for (int j = 0; j < amount; j++) {
                    String printString ="";
                    printString +="H|\\^&|||LS40000|||||Lis||P|E1394-97|"+resultTime+"\r\n" ;
                    printString +="P|1| |" + sample.getSamNum() + "||" + sample.getName() + "||" + sample.getAge() + "|";
                    if (getString(R.string.string3).equals(sample.getGender())) {
                        printString += "M";
                    } else if (getString(R.string.string4).equals(sample.getGender())) {
                        printString += "F";
                    } else {
                        printString += "U";
                    }
                    printString += "||||||" + sample.getDepart() + "||||甲状腺|||||||23" + "\r\n"
                            + "O|1|" + sample.getSamNum() + "||" + sample.getProName() + "\r\n";

                    printString += "R|1|^^^" + sample.getProName() + "|" + cons[j] + "|";
                    printString += pros[j].getUnit() + "|" + pros[j].getReferLow() + " to " + pros[j].getReferHigh() + "|"+ "||||||"+ timeStr + "\r\n";
                    printString += "L|1|N" + "\r\n";

                    final LisEvent methodCode;
                    methodCode = new LisEvent();
                    methodCode.setMes(printString);
                    methodCode.setComm((byte) 0x32);
                    EventBus.getDefault().post(methodCode);
                    showToast(this,getString(R.string.string222),1000);
                    //Toast.makeText(this,getString(R.string.string222),Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }




}
