package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.adapter.SamAdapter;
import com.example.chen.ls4000.bean.Pro;
import com.example.chen.ls4000.bean.Project;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.event.BleEvent;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.event.CardEvent;
import com.example.chen.ls4000.utils.DBUtils;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.TimeUtils;
import com.example.chen.ls4000.utils.ToastUtil;
import com.example.chen.ls4000.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class TestActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private ImageView scanAdd, back,instTest, incuTest,delete;
    private ListView lv;
    private List<Sample> samples,addData;
    private MyApp myApp;
    private SamAdapter samAdapter;
    private TextView allChoose;
    private long mLastTime = 0;
    private long mCurTime = 0;
    private int onclickTimes = 0;
    private RelativeLayout timeLayout;
    private TextView time;
    private int seconds;
    private Sample sample;
    private DBUtils dbUtils;
    private int curPosition;
    private SharedHelper sp;


    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            BleMethodCode bleMethodCode;
            bleMethodCode = new BleMethodCode(3);
            bleMethodCode.setMes("");
            bleMethodCode.setComm((byte) 0x0E);     //查询卡是否插入
            EventBus.getDefault().post(bleMethodCode);
        }
    };


    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int timeIncu = sp.readTime();
            if (timeIncu > 0) {
                handler.postDelayed(this, 1000);
                tip.setText(timeIncu + "s");
                if(checkedPosition == flag1){
                    sp.saveFlap("2");
                    if(sample != null) {
                        sample.setState(getString(R.string.string323));
                    }
                }
                samAdapter.notifyDataSetChanged();
            } else if (timeIncu == 0) {
                dialogTime.dismiss();
                mHandler.removeCallbacks(mRunnable);
                if(checkedPosition == flag1){
                    sp.saveFlap("2");
                    sample.setState(getString(R.string.string323));
                }
                samAdapter.notifyDataSetChanged();
                handler.removeCallbacks(runnable);
                tip.setText("0s");
                timeLayout.setVisibility(View.GONE);
                sample = new Sample();
                flag = -1;
                flag1 = -1;
                for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                    if (myApp.getTestPeoples().get(i).isFlagClick()) {
                        sample = myApp.getTestPeoples().get(i);
                        sp.saveFlap("1");
                        flag = i;
                        break;
                    }
                }
                if (flag == -1) {
                    for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                        if (!myApp.getTestPeoples().get(i).isFlagNull()) {
                            sample = myApp.getTestPeoples().get(i);
                            sp.saveFlap("1");
                            flag1 = i;
                            break;
                        }
                    }
                    if (flag1 == -1) {
                        //sample = myApp.getTestPeoples().get(0);
                        showToast(TestActivity.this, getString(R.string.string275), 1000);
                        return;
                    }
                }
                String ss = "";
                if (sample.getProject() != null) {
                    Project myProject = sample.getProject();
                    if (0 == TimeUtils.overdueBool(myProject.getBornTime(), myProject.getShelfLife())) {
                        ss += "0" + sample.getProject().getAmount();//+"0190";
                        myApp.setAmountFlap(sample.getProject().getAmount());
                        sample.setcRefer(sample.getProject().getCline());
                        switch (Integer.valueOf(sample.getProject().getAmount())) {
                            case 1:
                                //3位
                                for (int i = 0; i < 4 - sample.getProject().getCt1().length(); i++) {
                                    ss += "0";
                                }
                                ss += sample.getProject().getCt1();
                                sample.setT1Refer(sample.getProject().getCt1());
                                break;

                            case 2:
                                //3位
                                for (int i = 0; i < 4 - sample.getProject().getCt1().length(); i++) {
                                    ss += "0";
                                }
                                ss += sample.getProject().getCt1();
                                for (int i = 0; i < 4 - sample.getProject().getCt2().length(); i++) {
                                    ss += "0";
                                }
                                ss += sample.getProject().getCt2();
                                sample.setT1Refer(sample.getProject().getCt1());
                                sample.setT2Refer(sample.getProject().getCt2());
                                break;

                            case 3:
                                for (int i = 0; i < 4 - sample.getProject().getCt1().length(); i++) {
                                    ss += "0";
                                }
                                ss += sample.getProject().getCt1();
                                for (int i = 0; i < 4 - sample.getProject().getCt2().length(); i++) {
                                    ss += "0";
                                }
                                ss += sample.getProject().getCt2();
                                for (int i = 0; i < 4 - sample.getProject().getCt3().length(); i++) {
                                    ss += "0";
                                }
                                ss += sample.getProject().getCt3();
                                sample.setT1Refer(sample.getProject().getCt1());
                                sample.setT2Refer(sample.getProject().getCt2());
                                sample.setT3Refer(sample.getProject().getCt3());
                                break;
                        }
                        //3位
                        for (int i = 0; i < 4 - sample.getProject().getCline().length(); i++) {
                            ss += "0";
                        }
                        ss += sample.getProject().getCline();
                        //String sd = "0123456789";

                        sample.setTestTime(TimeUtils.getCurTime());
                        //sample.setTestTime(TimeUtils.getCur2Time());


                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        BleMethodCode methodCode;
                        methodCode = new BleMethodCode(3);
                        methodCode.setMes(ss);
                        methodCode.setComm((byte) 0x02);
                        EventBus.getDefault().post(methodCode);



                        scanAdd.setClickable(false);
                        if (flag == -1) {
                            if (flag1 == -1) {
//                        myApp.getTestPeoples().remove(0);
//                        samples.remove(0);
                                sp.saveFlap("1");
                                myApp.getTestPeoples().get(0).setFlagSend(true);
                            } else {
//                        myApp.getTestPeoples().remove(flag1);
//                        samples.remove(flag1);
                                sp.saveFlap("1");
                                myApp.getTestPeoples().get(flag1).setFlagSend(true);
                            }
                        } else {
//                    myApp.getTestPeoples().remove(flag);
//                    samples.remove(flag);
                            sp.saveFlap("1");
                            myApp.getTestPeoples().get(flag).setFlagSend(true);
                        }
                        samAdapter.notifyDataSetChanged();
                    }
                }
            }

        }
    };

    Handler minHandler = new Handler();
    Runnable minRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };
    private TimerTask task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initLocaleLanguage();
        setContentView(R.layout.activity_test);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initviews();

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

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(MyContextWrapper.wrap(newBase,"en"));
//    }

    private void initLocaleLanguage() {
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale("en","");
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());//更新配置
    }

    private void initviews() {
        scanAdd = (ImageView) findViewById(R.id.id_test_scanadd);
        instTest = (ImageView) findViewById(R.id.id_test_insttest);
        incuTest = (ImageView) findViewById(R.id.id_test_incutest);
        back = (ImageView) findViewById(R.id.id_test_back);
        lv = (ListView) findViewById(R.id.id_test_lv);
        delete = (ImageView) findViewById(R.id.id_test_delete);
        allChoose = (TextView) findViewById(R.id.id_test_allchoose);
        timeLayout = (RelativeLayout) findViewById(R.id.id_test_timelayout);
        time = (TextView) findViewById(R.id.id_test_time);

        scanAdd.setOnClickListener(this);
        instTest.setOnClickListener(this);
        incuTest.setOnClickListener(this);
        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        allChoose.setOnClickListener(this);

        samples = new ArrayList<>();
        addData = new ArrayList<>();
        myApp = (MyApp) getApplication();
//        if (myApp.getTestPeoples().size() == 0) {
//            for (int i = 0; i < 7; i++) {
//                Sample sample1 = new Sample();
//                sample1.setFlagNull(true);
        //myApp.getTestPeoples().add(sample1);
//            }
//        }
        samples.addAll(myApp.getTestPeoples());


        samAdapter = new SamAdapter(this,samples);
        lv.setAdapter(samAdapter);

        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        hideBottomUIMenu();

        EventBus.getDefault().register(this);
        sp = new SharedHelper(this);

//        if ("English".equals(sp.readLan())){
//            Locale.setDefault(Locale.ENGLISH);
//            Configuration config = getBaseContext().getResources().getConfiguration();
//            config.locale = Locale.ENGLISH;
//            getBaseContext().getResources().updateConfiguration(config
//                    , getBaseContext().getResources().getDisplayMetrics());
//            refreshSelf();
//
//        }else {
//            Locale.setDefault(Locale.CHINESE);
//            Configuration config = getBaseContext().getResources().getConfiguration();
//            config.locale = Locale.CHINESE;
//            getBaseContext().getResources().updateConfiguration(config
//                    , getBaseContext().getResources().getDisplayMetrics());
//            refreshSelf();
//        }

        dbUtils = new DBUtils(getApplicationContext());


    }

    @Override
    protected void onStart() {
        super.onStart();

//        if ( (myApp.getTestPeoples().size() > 0 &&
//                !myApp.getTestPeoples().get(samples.size() - 1).isFlagNull()) ||
//            myApp.getTestPeoples().size() == 0 ) {
//            Sample sample = new Sample();
//            sample.setFlagNull(true);
//            myApp.getTestPeoples().add(sample);
//
//        }
        samples.clear();
        samples.addAll(myApp.getTestPeoples());


//        if((samples.size()<8&&myApp.getTestPeoples().size()>0)||
//                (myApp.getTestPeoples().size()-samples.size()<2)){
//            //检测完成添加新的空样本
//            putNullSample();
//        }
        samAdapter.notifyDataSetChanged();
        if(sp.readTime()!=0) {
            //handler.removeCallbacks(runnable);
            sp.saveFlap("2");
            //timeLayout.setVisibility(View.VISIBLE);
            //handler.postDelayed(runnable, 1000);
            samAdapter.notifyDataSetChanged();
        }
    }

    Bundle bundle;
    int seat1, inFlap;  //inFlap 0为初始值 1为即使检测,2为孵育检测
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response) throws UnsupportedEncodingException {

        int comm = response.getComm();
        String data = response.getMes();
        byte[] bys = response.getBys();
        System.out.println(comm+"身份证数据" + data+"*");
        sp.saveFlap("0");
        myApp.setCardIDFlap("0");
        if(comm == 0x0B){   //if (comm == 0x0f) {
            scanAdd.setClickable(true);
            if (flag == -1) {
                if (flag1 == -1) {
                    samples.remove(0);
                } else {
                    samples.remove(flag1);
                }
            } else {
                samples.remove(flag);
            }

            samAdapter.notifyDataSetChanged();

            System.out.println("bytes数据"+bys.length);
            incuTest.setClickable(true);
            instTest.setClickable(true);
            back.setClickable(true);
            scanAdd.setClickable(true);

            double[] ss = new double[3];
            String str1 =data.substring(0,6);
            byte[] byt = str1.getBytes();
            byte byt1 = (byte) (byt[0]  - 0x30);
            int len1 = byt1 / 10;
            int len2 = byt1 % 10;
            String str2 = len1 +""+ len2;
            String str3 = str1.substring(0,1);
            if(Integer.parseInt(str2 ) > 9){
                String res2 = str1.replace(str3,str2);
                System.out.println(res2);

                ss[0] = Double.valueOf(res2.substring(0,6)) / 10000;
            }else{
                ss[0] = Double.valueOf(str1.substring(0,6)) / 100000;
            }



            ss[1] = Double.valueOf(data.substring(6,12)) / 100000;
            ss[2] = Double.valueOf(data.substring(12,18)) / 100000;

            byte[] cPosi = new byte[3];
            byte[] t1Posi = new byte[3];
            byte[] t2Posi = new byte[3];
            byte[] t3Posi = new byte[3];
            byte[] bytes = new byte[800];//1500
            System.arraycopy(bys,18,cPosi,0,2);
            System.arraycopy(bys,20,t1Posi,0,2);
            System.arraycopy(bys,22,t2Posi,0,2);
            System.arraycopy(bys,24,t3Posi,0,2);
            System.arraycopy(bys,26,bytes,0,800);  //900

            sample.setcPosi(Utils.bytesToInt(cPosi)+"");
            sample.setT1Posi(Utils.bytesToInt(t1Posi)+"");
            sample.setT2Posi(Utils.bytesToInt(t2Posi)+"");
            sample.setT3Posi(Utils.bytesToInt(t3Posi)+"");
            //sample 是发送数据的

            sample.setConcen(calc(ss,sample));



            /**
             添加 参考值，单位
             **/
            switch (Integer.valueOf(sample.getProject().getAmount())){
                case 1:
                    sample.setRefer(DBUtils.searchProject(sample.getProNum()).getcRefer());

                    sample.setUnit(DBUtils.searchProject(sample.getProNum()).getUnit());
                    break;

                case 2:
                    sample.setRefer(DBUtils.searchProject(sample.getProNum()).getcRefer());
                    sample.setRefer2(DBUtils.searchProject(sample.getProNum()).getcRefer2());

                    sample.setUnit(DBUtils.searchProject(sample.getProNum()).getUnit());
                    sample.setUnit2(DBUtils.searchProject(sample.getProNum()).getUnit2());
                    break;

                case 3:
                    sample.setRefer(DBUtils.searchProject(sample.getProNum()).getcRefer());
                    sample.setRefer2(DBUtils.searchProject(sample.getProNum()).getcRefer2());
                    sample.setRefer3(DBUtils.searchProject(sample.getProNum()).getcRefer3());

                    sample.setUnit(DBUtils.searchProject(sample.getProNum()).getUnit());
                    sample.setUnit2(DBUtils.searchProject(sample.getProNum()).getUnit2());
                    sample.setUnit3(DBUtils.searchProject(sample.getProNum()).getUnit3());
                    break;
            }








            String time = TimeUtils.getCurTime();
            //String time = TimeUtils.getCur2Time();
            sample.setTestTime(time);

            sample.setCurveData(new String(bytes,"ISO-8859-1"));
            if(dbUtils.searchSampleNum()>=50000){
                dbUtils.saveSamples(sample, true);
            }else{
                dbUtils.saveSamples(sample, false);
            }

            bundle = new Bundle();
            bundle.putSerializable("sample", sample);

            if(sp.readAutoPrint()){
                BleEvent bleEvent = new BleEvent();
                bleEvent.setSample(sample);
                EventBus.getDefault().post(bleEvent);
            }

            Intent intent = new Intent(TestActivity.this, ResultActivity.class);
            // bundle.putIntArray("result",result);
            intent.putExtras(bundle);
            startActivity(intent);
            delete.setClickable(true);
            allChoose.setClickable(true);
            instTest.setImageDrawable(getResources().getDrawable(R.mipmap.btn_poct));
            myApp.setTestFlap(0);
        }else if (comm == 0x11) {
            if(inFlap==1){
                popProTip1("E200:"+getString(R.string.string31));
            }else if(inFlap==2){
                popProTip2("E200:"+getString(R.string.string31));
            }


        } else if(comm == 0x13){
            if(seat1 == 1){
                BleReadEvent bleReadEvent = new BleReadEvent();
                bleReadEvent.setComm(0x66);     //
                bleReadEvent.setMes(""+seconds);
                bleReadEvent.setBys(new byte[0]);
                EventBus.getDefault().post(bleReadEvent);
                seat1 = 0;
            }

            mHandler.postDelayed(mRunnable, 1000);



        } else if(comm == 0x32 && longClick){
            final String ss = unicodeToString(bys);
            final String nameStr = getName(bys);

            sample = myApp.getTestPeoples().get(curPosition);
            sample.setName(nameStr);
            sample.setIDNum(ss.substring(5,ss.length()));
            Calendar a=Calendar.getInstance();
            System.out.println(a.get(Calendar.YEAR));//得到年
            sample.setAge(a.get(Calendar.YEAR)-Integer.valueOf(ss.substring(1,5))+"");
            if("2".equals(ss.substring(0,1))){
                sample.setGender(getString(R.string.string4));
            }else{
                sample.setGender(getString(R.string.string3));
            }
            sample.setFlagNull(false);
            //   samples.clear();
            myApp.getTestPeoples().set(curPosition,sample);
            sample = new Sample();
            sample.setFlagNull(true);
            myApp.getTestPeoples().add(sample);
            samples.clear();
            samples.addAll(myApp.getTestPeoples());
            samAdapter.notifyDataSetChanged();

            longClick = false;

//            Message message = mHandler.obtainMessage();
//            message.what = 1;
//            Bundle bundle = new Bundle();
//            bundle.putString("ss",ss);
//            bundle.putString("name",nameStr);
//            message.setData(bundle);
//            mHandler.sendMessage(message);

        }else if(comm == 0X14){
            popProTip3("E201:"+getString(R.string.string279));
        }

    }


    private int flag,flag1,checkedPosition;
    String n;
    @Override
    public void onClick(View v) {
        BleMethodCode methodCode;

        switch (v.getId()) {
            case R.id.id_test_scanadd://扫码加样
                try {
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("");
                    methodCode.setComm((byte) 0x01);
                    EventBus.getDefault().post(methodCode);
//                try {
//                    Toast.makeText(this,"正在扫码，请稍候！",Toast.LENGTH_LONG).show();
//                    scanAdd.setClickable(false);
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                    Intent intent = new Intent(TestActivity.this, AddTestActivity.class);
                    startActivity(intent);
                    showToast(this,getString(R.string.string239),1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.id_test_insttest://即时检测
                if(myApp.getQuanFlap()==1){
                    ToastUtil.showToast(this,"电量过低无法检测",2000);
                    return;
                }
                if(myApp.getTestPeoples().size() == 0){
                    showToast(this,getString(R.string.string276),1000);
                    return;
                }
                sample = new Sample();
                flag = -1;
                flag1 = -1;
                for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                    if (myApp.getTestPeoples().get(i).isFlagClick()) {
                        sample = myApp.getTestPeoples().get(i);
                        flag = i;
                        checkedPosition = i;
                        break;
                    }
                }
                if (flag == -1) {
                    for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                        if (!myApp.getTestPeoples().get(i).isFlagNull()) {
                            sample = myApp.getTestPeoples().get(i);
                            flag1 = i;
                            checkedPosition = i;
                            break;
                        }
                    }
                }
                String textTime = TimeUtils.getCurTime();
                String minTime = "yyMMdd";
                String resultTime = TimeUtils.getWantDate(textTime, minTime);
                System.out.println("时间" + resultTime);


                if (myApp.getPeoNum() < 1000) {
                    if (0 <= myApp.getPeoNum() && myApp.getPeoNum() < 10) {
                        n = "000" + myApp.getPeoNum();
                    } else if (10 <= myApp.getPeoNum() && myApp.getPeoNum() < 100) {
                        n = "00" + myApp.getPeoNum();
                    } else if (100 <= myApp.getPeoNum() && myApp.getPeoNum() < 1000) {
                        n = "0" + myApp.getPeoNum();
                    }

                    if ((myApp.getPeoNum() + 1) % 10 != 0) {
                        System.out.println("asadasdas||||" + n);
                    } else {
                        System.out.println("asadasdas::::::" + n);
                    }
                }

                myApp.setPeoNum(myApp.getPeoNum() + 1);

                if ("".equals(sample.getSamNum())) {
                    sample.setSamNum(resultTime + n);
                }

                System.out.println("asadasdas" + sample.getSamNum());

                if (checkedPosition == flag1 || checkedPosition == flag) {
                    sp.saveFlap("1");
                    sample.setState(getString(R.string.string289));
                }

//                    if (flag1 == -1) {
//                        sample = myApp.getTestPeoples().get(0);
//                        showToast(TestActivity.this, getString(R.string.string275), 1000);
//                        return;
//                    }

                if (sample.isFlagNull()) {
                    showToast(this, getString(R.string.string83), 1000);
                } else {
                    try {
                        instTest.setClickable(false);
                        incuTest.setClickable(false);
                        back.setClickable(false);
                        scanAdd.setClickable(false);
                        delete.setClickable(false);
                        allChoose.setClickable(false);


                        String ss = "";//"000";
                        if (sample.getProject() != null) {
                            Project myProject = sample.getProject();
                            if (0 == TimeUtils.overdueBool(myProject.getBornTime(), myProject.getShelfLife())) {
                                ss += "0" + sample.getProject().getAmount();//+"0190";
                                myApp.setAmountFlap(sample.getProject().getAmount());
                                sample.setcRefer(sample.getProject().getCline());
                                switch (Integer.valueOf(sample.getProject().getAmount())) {

                                    case 1:
                                        //3位
                                        for (int i = 0; i < 4 - sample.getProject().getCt1().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt1();
                                        sample.setT1Refer(sample.getProject().getCt1());
                                        System.out.print("1111111111111111");
                                        break;
                                    case 2:
                                        for (int i = 0; i < 4 - sample.getProject().getCt1().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt1();
                                        for (int i = 0; i < 4 - sample.getProject().getCt2().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt2();
                                        sample.setT1Refer(sample.getProject().getCt1());
                                        sample.setT2Refer(sample.getProject().getCt2());
                                        System.out.print("2222222222222222");
                                        break;
                                    case 3:
                                        for (int i = 0; i < 4 - sample.getProject().getCt1().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt1();
                                        for (int i = 0; i < 4 - sample.getProject().getCt2().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt2();
                                        for (int i = 0; i < 4 - sample.getProject().getCt3().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt3();
                                        sample.setT1Refer(sample.getProject().getCt1());
                                        sample.setT2Refer(sample.getProject().getCt2());
                                        sample.setT3Refer(sample.getProject().getCt3());
                                        System.out.print("33333333333333333333333");
                                        break;
                                }
                                //3位
                                for (int i = 0; i < 4 - sample.getProject().getCline().length(); i++) {
                                    ss += "0";
                                }
                                ss += sample.getProject().getCline();
                                //String sd = "0123456789";
                                sample.setTestTime(TimeUtils.getCurTime());
                                inFlap = 1;

                                //sample.setTestTime(TimeUtils.getCur2Time());
                                methodCode = new BleMethodCode(3);
                                methodCode.setMes(ss);
                                //methodCode.setMes("");
                                methodCode.setComm((byte) 0x02);
                                EventBus.getDefault().post(methodCode);
                                if (flag == -1) {
                                    if (flag1 == -1) {
//                                myApp.getTestPeoples().remove(0);
//                                samples.remove(0);

                                        myApp.getTestPeoples().get(0).setFlagSend(true);
                                    } else {
//                                myApp.getTestPeoples().remove(flag1);
//                                samples.remove(flag1);

                                        myApp.getTestPeoples().get(flag1).setFlagSend(true);
                                    }
                                } else {
//                            myApp.getTestPeoples().remove(flag);
//                            samples.remove(flag);

                                    myApp.getTestPeoples().get(flag).setFlagSend(true);
                                }

                                samAdapter.notifyDataSetChanged();
                            } else {
                                showToast(this, "E202"+getString(R.string.string5), 1000);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }
                break;
            case R.id.id_test_incutest://孵育检测
                if(myApp.getQuanFlap()==1){
                    ToastUtil.showToast(this,getString(R.string.string321),2000);
                    return;
                }
                if(myApp.getTestPeoples().size() == 0){
                    showToast(this,getString(R.string.string276),1000);
                    return;
                }
                sample = new Sample();
                flag = -1;
                flag1 = -1;
                checkedPosition=-1;
                myApp.setTestFlap(1);
                sp.saveFlap("2");
                samAdapter.notifyDataSetChanged();
                for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                    if (myApp.getTestPeoples().get(i).isFlagClick()) {
                        sample = myApp.getTestPeoples().get(i);
                        sp.saveFlap("1");
                        flag = i;
                        checkedPosition = i;
                        break;
                    }
                }
                if (flag == -1) {
                    for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                        if (!myApp.getTestPeoples().get(i).isFlagNull()) {
                            sample = myApp.getTestPeoples().get(i);
                            sp.saveFlap("1");
                            flag1 = i;
                            checkedPosition = i;
                            break;
                        }
                    }
                }

                String textTime1 =TimeUtils.getCurTime();
                String minTime1="yyMMdd";
                String resultTime1=TimeUtils.getWantDate(textTime1,minTime1);
                System.out.println("时间"+resultTime1);

                if(myApp.getPeoNum()<1000) {
                    if (0 <= myApp.getPeoNum() && myApp.getPeoNum() < 10) {
                        n = "000" + myApp.getPeoNum();
                    } else if(10 <= myApp.getPeoNum() && myApp.getPeoNum() < 100){
                        n = "00"+myApp.getPeoNum();
                    }else if(100<= myApp.getPeoNum() && myApp.getPeoNum()<1000){
                        n = "0" + myApp.getPeoNum() ;
                    }

                    if ((myApp.getPeoNum() + 1) % 10 != 0) {
                        System.out.println("asadasdas||||" + n);
                    } else {
                        System.out.println("asadasdas::::::" + n);
                    }
                }

                myApp.setPeoNum(myApp.getPeoNum() + 1);

                if("".equals(sample.getSamNum())){
                    sample.setSamNum(resultTime1+n);
                }

                System.out.println("asadasdas"+sample.getSamNum());

                if(checkedPosition == flag1 || checkedPosition == flag){
                    sp.saveFlap("2");
                    sample.setState(getString(R.string.string323));
                }


//                    if(flag1 == -1) {
//                       // sample = myApp.getTestPeoples().get(0);
//                        instTest.setImageDrawable(getResources().getDrawable(R.mipmap.btn_poct));
//                        instTest.setClickable(true);
//                        incuTest.setClickable(true);
//                        back.setClickable(true);
//                        scanAdd.setClickable(true);
//                        allChoose.setClickable(true);
//                        showToast(TestActivity.this,getString(R.string.string275),1000);
//                        return;
//                    }

                if (sample.isFlagNull()) {
                    showToast(this, getString(R.string.string83), 1000);
                } else {

                    try {

                        boolean isChanged = false;
                        if(v == incuTest){
                            if(isChanged){
                                instTest.setImageDrawable(getResources().getDrawable(R.mipmap.btn_poct));
                            }else{
                                instTest.setImageDrawable(getResources().getDrawable(R.mipmap.btn_poct_1));
                            }
                            isChanged = !isChanged;

                        }

                        instTest.setClickable(false);
                        incuTest.setClickable(false);
                        back.setClickable(false);
                        scanAdd.setClickable(false);
                        //allChoose.setClickable(false);    //全选

                        //timeLayout.setVisibility(View.VISIBLE);
                        seconds = (int)(Float.parseFloat(sample.getProject().getIncuTime()) * 60);
                        //time.setText(seconds + "s");
                        sp.saveTime(seconds);
                        //handler.postDelayed(runnable, 100);
                        popProTip4(getString(R.string.string324));
//                        BleReadEvent bleReadEvent = new BleReadEvent();
//                        bleReadEvent.setComm(0x66);
//                        bleReadEvent.setMes(""+seconds);
//                        bleReadEvent.setBys(new byte[0]);
//                        EventBus.getDefault().post(bleReadEvent);
                        seat1 = 1;
                        inFlap = 2;
                        methodCode = new BleMethodCode(3);
                        methodCode.setMes("");
                        methodCode.setComm((byte) 0x0E);
                        EventBus.getDefault().post(methodCode);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.id_test_back://返回
//                Locale myLocale = Locale.ENGLISH;
//                Resources res = getResources();
//                DisplayMetrics dm = res.getDisplayMetrics();
//                Configuration conf = res.getConfiguration();
//                conf.locale = myLocale;
//                res.updateConfiguration(conf, dm);

                finish();
                break;
            case R.id.id_test_allchoose://全选、反选
                if(myApp.getTestPeoples().size() == 0){
                    showToast(this,getString(R.string.string276),500);
                }
                for (int i = 0; i < samples.size(); i++) {
                    //if(isChecked) {
                    if (getString(R.string.string84).equals(allChoose.getText().toString())) {
                        samples.get(i).setFlagClick(true);
                        samAdapter.notifyDataSetChanged();
                    } else {
                        samples.get(i).setFlagClick(false);
                        samAdapter.notifyDataSetChanged();
                    }
                    // }
                }
                if (getString(R.string.string84).equals(allChoose.getText().toString())) {
                    allChoose.setText(getString(R.string.string85));
                } else {
                    allChoose.setText(getString(R.string.string84));
                }
                break;

            case R.id.id_test_delete://删除

                for (int i = 0; i < samples.size(); i++) {
                    if (samples.get(i).isFlagClick()) {
                        if(myApp.getTestFlap() == 1){
                            if(checkedPosition == i){
                                showToast(this, getString(R.string.string277), 1000);
                                return;
                            }else {

                                if (!samples.get(i).isFlagNull()) {
                                    samples.remove(i);
                                    myApp.getTestPeoples().remove(i);
                                    i = i - 1;
                                    if (checkedPosition != 0) {
                                        checkedPosition = checkedPosition - 1;
                                    }

                                }
                            }
                        }else{
                            if (!samples.get(i).isFlagNull()) {
                                samples.remove(i);
                                myApp.getTestPeoples().remove(i);
                                i = i - 1;
                                if (checkedPosition != 0) {
                                    checkedPosition = checkedPosition - 1;
                                }

                            }
                        }

                    }
                }
                for (int i = 0; i < samples.size(); i++) {
                    samples.get(i).setFlagClick(false);
                    myApp.getTestPeoples().get(i).setFlagClick(false);
                    allChoose.setText(getString(R.string.string84));
                }
                samAdapter.notifyDataSetChanged();
                break;
        }
    }


    TextView tip,title;
    AlertDialog dialogTime;
    private void popProTip4(String str) {
        final View view;


        view = View.inflate(this, R.layout.test_time_layout, null);
        dialogTime = new AlertDialog.Builder(this, R.style.AlertDialog).create();

        title = (TextView) view.findViewById(R.id.id_test_time__title);
        tip = (TextView) view.findViewById(R.id.id_test_time_tip);
        title.setText(str);



        dialogTime.setView(view);
        dialogTime.setCancelable(false);
        dialogTime.show();
        WindowManager.LayoutParams params = dialogTime.getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        dialogTime.getWindow().setAttributes(params);
        int timeIncu = sp.readTime();

//        maxhandler.removeMessages(0);
//        Message message = maxhandler.obtainMessage(1);
//        maxhandler.sendMessageDelayed(message,1000);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,1000);


    }

    final Handler maxhandler = new Handler(){
        public void handleMessage(Message msg){
            int timeIncu = sp.readTime();
            switch (msg.what){
                case 1:
                    //timeIncu--;
                    if(timeIncu > 0){
                        sp.saveFlap("2");
                        sample.setState(getString(R.string.string323));
                        tip.setText(timeIncu + "s");
                        Message message = maxhandler.obtainMessage(1);
                        maxhandler.sendMessageDelayed(message, 1000);
                    }
                    else if(timeIncu == 0){

                        if(checkedPosition == flag1){
                            sp.saveFlap("2");
                            sample.setState(getString(R.string.string323));
                        }
                        tip.setText(0 + "s");
                        samAdapter.notifyDataSetChanged();
                        //handler.removeCallbacks(runnable);
                        maxhandler.removeMessages(1);
                        time.setText("0s");
                        timeLayout.setVisibility(View.GONE);
                        sample = new Sample();
                        flag = -1;
                        flag1 = -1;
                        for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                            if (myApp.getTestPeoples().get(i).isFlagClick()) {
                                sample = myApp.getTestPeoples().get(i);
                                sp.saveFlap("1");
                                flag = i;
                                break;
                            }
                        }
                        if (flag == -1) {
                            for (int i = 0; i < myApp.getTestPeoples().size(); i++) {
                                if (!myApp.getTestPeoples().get(i).isFlagNull()) {
                                    sample = myApp.getTestPeoples().get(i);
                                    sp.saveFlap("1");
                                    flag1 = i;
                                    break;
                                }
                            }
                            if (flag1 == -1) {
                                //sample = myApp.getTestPeoples().get(0);
                                showToast(TestActivity.this, getString(R.string.string275), 1000);
                                return;
                            }
                        }
                        String ss = "";
                        if (sample.getProject() != null) {
                            Project myProject = sample.getProject();
                            if (0 == TimeUtils.overdueBool(myProject.getBornTime(), myProject.getShelfLife())) {
                                ss += "0" + sample.getProject().getAmount();//+"0190";
                                myApp.setAmountFlap(sample.getProject().getAmount());
                                sample.setcRefer(sample.getProject().getCline());
                                switch (Integer.valueOf(sample.getProject().getAmount())) {
                                    case 1:
                                        //3位
                                        for (int i = 0; i < 4 - sample.getProject().getCt1().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt1();
                                        sample.setT1Refer(sample.getProject().getCt1());
                                        break;

                                    case 2:
                                        //3位
                                        for (int i = 0; i < 4 - sample.getProject().getCt1().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt1();
                                        for (int i = 0; i < 4 - sample.getProject().getCt2().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt2();
                                        sample.setT1Refer(sample.getProject().getCt1());
                                        sample.setT2Refer(sample.getProject().getCt2());
                                        break;

                                    case 3:
                                        for (int i = 0; i < 4 - sample.getProject().getCt1().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt1();
                                        for (int i = 0; i < 4 - sample.getProject().getCt2().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt2();
                                        for (int i = 0; i < 4 - sample.getProject().getCt3().length(); i++) {
                                            ss += "0";
                                        }
                                        ss += sample.getProject().getCt3();
                                        sample.setT1Refer(sample.getProject().getCt1());
                                        sample.setT2Refer(sample.getProject().getCt2());
                                        sample.setT3Refer(sample.getProject().getCt3());
                                        break;
                                }
                                //3位
                                for (int i = 0; i < 4 - sample.getProject().getCline().length(); i++) {
                                    ss += "0";
                                }
                                ss += sample.getProject().getCline();
                                //String sd = "0123456789";

                                sample.setTestTime(TimeUtils.getCurTime());
                                //sample.setTestTime(TimeUtils.getCur2Time());
                                BleMethodCode methodCode;
                                methodCode = new BleMethodCode(3);
                                methodCode.setMes(ss);
                                methodCode.setComm((byte) 0x02);
                                EventBus.getDefault().post(methodCode);
                                scanAdd.setClickable(false);
                                if (flag == -1) {
                                    if (flag1 == -1) {
//                        myApp.getTestPeoples().remove(0);
//                        samples.remove(0);
                                        sp.saveFlap("1");
                                        myApp.getTestPeoples().get(0).setFlagSend(true);
                                    } else {
//                        myApp.getTestPeoples().remove(flag1);
//                        samples.remove(flag1);
                                        sp.saveFlap("1");
                                        myApp.getTestPeoples().get(flag1).setFlagSend(true);
                                    }
                                } else {
//                    myApp.getTestPeoples().remove(flag);
//                    samples.remove(flag);
                                    sp.saveFlap("1");
                                    myApp.getTestPeoples().get(flag).setFlagSend(true);
                                }
                                samAdapter.notifyDataSetChanged();
                            }
                        }
                        dialogTime.dismiss();
                    }

                    break;
                case 2:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        //onclickTimes ++;
        //发送鼠标左键功能
        long mCurTime = System.currentTimeMillis();

//        SamAdapter.ViewHolder1 viewHolder1 = (SamAdapter.ViewHolder1) view.getTag();
//        if(position == 0){
//            viewHolder1.choose.setChecked(true);
//        }


        //判断是否发生了双击
        if (mLastTime != 0 && mCurTime - mLastTime < 500) {
            System.out.println("************双击数据************onclickTimes************" + onclickTimes + "第" + "条");
            if(samples.get(position).isFlagClick()){
                if(myApp.getTestFlap() == 1){
                    if(checkedPosition == position){
//                        SamAdapter.ViewHolder1 viewHolder1 = (SamAdapter.ViewHolder1) view.getTag();
//                        viewHolder1.choose.setClickable(false);
                        showToast(this,getString(R.string.string86),1000);
                        return;
                    }
                }else{
                    try {
                        if(samples.get(position).isFlagNull()){
                            BleMethodCode methodCode;
                            methodCode = new BleMethodCode(3);
                            methodCode.setMes("");
                            methodCode.setComm((byte) 0x01);
                            EventBus.getDefault().post(methodCode);
                            Intent intent = new Intent(TestActivity.this, UpdateTestActivity.class);
                            Sample sample = samples.get(position);
                            sample.setNid(System.currentTimeMillis() + "");
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("sample", sample);
                            intent.putExtras(bundle);
                            startActivity(intent);

                            mLastTime = mCurTime - 500;
                            showToast(this,getString(R.string.string239),1000);
                        }else{
                            Intent intent = new Intent(TestActivity.this, UpdateTestActivity.class);
                            Sample sample = samples.get(position);
                            sample.setNid(System.currentTimeMillis() + "");
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("sample", sample);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            mLastTime = mCurTime - 500;
                            showToast(this,getString(R.string.string278),1000);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }else {
                try {
                    if(samples.get(position).isFlagNull()){
                        BleMethodCode methodCode;
                        methodCode = new BleMethodCode(3);
                        methodCode.setMes("");
                        methodCode.setComm((byte) 0x01);
                        EventBus.getDefault().post(methodCode);
                        Intent intent = new Intent(TestActivity.this, UpdateTestActivity.class);
                        Sample sample = samples.get(position);
                        sample.setNid(System.currentTimeMillis() + "");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("sample", sample);
                        intent.putExtras(bundle);
                        startActivity(intent);

                        mLastTime = mCurTime - 500;
                        showToast(this,getString(R.string.string239),1000);
                    }else{
                        Intent intent = new Intent(TestActivity.this, UpdateTestActivity.class);
                        Sample sample = samples.get(position);
                        sample.setNid(System.currentTimeMillis() + "");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("sample", sample);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        mLastTime = mCurTime - 500;
                        showToast(this,getString(R.string.string278),1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                //Toast.makeText(this,getString(R.string.string239),Toast.LENGTH_SHORT).show();
            }
        } else {
            isSingClick(onclickTimes,position);
            mLastTime = mCurTime;
        }

    }

    boolean longClick = false;

    //判断是否是单击
    private void isSingClick(final long times, final int position) {

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (onclickTimes == times) {
                    System.out.println("************isSingClick************" + onclickTimes + "第" + "条");
//                    CardEvent cardEvent = new CardEvent();
//                    cardEvent.setBys(new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22});
//                    cardEvent.setComm((byte) 0x31);
//                    EventBus.getDefault().post(cardEvent);
//                    curPosition = position;
                    //hideBottomUIMenu();
                }

            }
        }.start();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(TestActivity.this, UpdateTestActivity.class);
//        Sample sample = samples.get(position);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("sample", sample);
//        intent.putExtras(bundle);
//        startActivity(intent);
        CardEvent cardEvent = new CardEvent();
        cardEvent.setBys(new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22});
        cardEvent.setComm((byte) 0x31);
        EventBus.getDefault().post(cardEvent);
        curPosition = position;
        longClick = true;
        return true;
    }

    private void popProTip3(String str) {
        final View view;


        view = View.inflate(this, R.layout.testlayout, null);

        TextView tip;
        Button ensure;

        tip = (TextView) view.findViewById(R.id.id_test_title);
        ensure = (Button) view.findViewById(R.id.id_test_ensure);
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
                samples.remove(checkedPosition);
                myApp.getTestPeoples().remove(checkedPosition);
                dialog.dismiss();
                finish();
                Intent intent = new Intent(TestActivity.this,TestActivity.class);
                startActivity(intent);

            }
        });

    }

    private void popProTip2(String str) {
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
                BleMethodCode bleMethodCode;
                bleMethodCode = new BleMethodCode(3);
                bleMethodCode.setMes("");
                bleMethodCode.setComm((byte) 0x0E);
                EventBus.getDefault().post(bleMethodCode);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                samples.remove(checkedPosition);
                myApp.getTestPeoples().remove(checkedPosition);
                samAdapter.notifyDataSetChanged();
                timeLayout.setVisibility(View.GONE);
                time.setText("0s");
                //maxhandler.removeMessages(1);
                handler.removeCallbacks(runnable);
                instTest.setImageDrawable(getResources().getDrawable(R.mipmap.btn_poct));
                instTest.setClickable(true);
                scanAdd.setClickable(true);
                incuTest.setClickable(true);
                dialog.dismiss();
                dialogTime.dismiss();
                finish();
                startActivity(new Intent(TestActivity.this,TestActivity.class));

//                finish();
//                Intent intent = new Intent(TestActivity.this,TestActivity.class);
//                startActivity(intent);

//                handler.removeCallbacks(runnable);
//                time.setText("0s");
//                timeLayout.setVisibility(View.GONE);
//                instTest.setImageDrawable(getResources().getDrawable(R.mipmap.btn_poct));
                //dialog.dismiss();
            }
        });
    }

    private void popProTip1(String str) {
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
                BleMethodCode methodCode;
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x02);
                EventBus.getDefault().post(methodCode);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                samples.remove(checkedPosition);
                myApp.getTestPeoples().remove(checkedPosition);
                samAdapter.notifyDataSetChanged();
                timeLayout.setVisibility(View.GONE);
                time.setText("0s");
                //maxhandler.removeMessages(1);
                handler.removeCallbacks(runnable);
                sp.saveTime(0);
                incuTest.setImageDrawable(getResources().getDrawable(R.mipmap.btn_poct));
                incuTest.setClickable(true);
                instTest.setClickable(true);
                scanAdd.setClickable(true);
                dialog.dismiss();
                if(dialogTime != null){
                    dialogTime.dismiss();
                }

                finish();
                startActivity(new Intent(TestActivity.this,TestActivity.class));

//                finish();
//                Intent intent = new Intent(TestActivity.this,TestActivity.class);
//                startActivity(intent);
//                incuTest.setClickable(true);
//                instTest.setClickable(true);
//                back.setClickable(true);
//                lv.setClickable(true);
//                scanAdd.setClickable(true);
//                delete.setClickable(true);
//                allChoose.setClickable(true);
//                dialog.dismiss();
            }
        });
    }

    public String unicodeToString(byte[] bys) {

        String age = "";
        String gender = "";
        String cardId = "";
        String ss = "";
        byte[] bytes = new byte[bys.length];

        int flag = -1;
        for (int i = 0; i < bys.length; i++) {
            if(bys[i] == 32 && bys[i+1] == 0 && bys[i+2] != 32){
                flag = i+2;
                break;
            }
        }
        for (int i = flag; i < flag + 2; i+=2) {
            gender += "&#"+bys[i+1]+bys[i]+";";
        }
        flag += 6;
        for (int i = flag; i < flag + 8; i+=2) {
            age += "&#"+bys[i+1]+bys[i]+";";
        }
        System.arraycopy(bys, flag + 8, bytes, 0, bys.length-(flag + 8));
        for (int i = 0; i < bytes.length; i++) {
            if(bytes[i] == 32 && bytes[i+1] == 0 && bytes[i+2] != 32){
                flag = i+2;
                break;
            }
        }
        for (int i = flag; i < flag + 36; i+=2) {
            cardId += "&#"+bytes[i+1]+bytes[i]+";";
        }

        ss = unicodeToascii(gender)+unicodeToascii(age)+unicodeToascii(cardId);
        return ss.toString();
    }

    public static String getName(byte[] bys) {

        String str = "";//unicode.replace("0x", "\\");
        int flag = -1;
        for (int i = 0; i < bys.length; i++) {
            if(bys[i] == 32){
                flag = i;
                break;
            }
        }
        String[] str1 = new String[flag - 14];
        int k = 0;
        for (int i = 14; i < flag; i++) {
            str1[k] = Integer.toHexString(bys[i]);
            if(str1[k].length()>2) {
                str1[k] = str1[k].substring(str1[k].length() - 2, str1[k].length());
            }else{
                for (int j = 0; j < 2-str1[k].length(); j++) {
                    str1[k] = "0"+str1[k];
                }
            }
            k++;
        }
        for (int i = 0; i < str1.length/2; i++) {
            //short s = (short)((bys[i*2+1]<<8)|(bys[i*2]&0xff));
            str += "\\u"+str1[i*2+1]+str1[i*2];
        }

        StringBuffer string = new StringBuffer();
        String[] hex = str.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        System.out.println("身份证信息数据"+string.toString());
        return string.toString();
    }
    private static Pattern pattern = Pattern.compile("\\&\\#(\\d+)");
    private String unicodeToascii(String str){
        StringBuilder sb = new StringBuilder();
        Matcher m =pattern.matcher(str);

        while (m.find()){
            sb.append((char)Integer.valueOf(m.group(1)).intValue());
        }

        return sb.toString();
    }

    //高位在前，低位在后
    public static int[] bytesToint(byte[] bytes){
        int result[] = new int[400];
        int a = 0;
        int b = 0;
        int c = 0;
        for (int i = 0; i < result.length; i++) {
            a = (bytes[i*3] & 0xff) << 16;//说明二
            b = (bytes[i*3+1] & 0xff) << 8;
            c = (bytes[i*3+2] & 0xff);
            result[i] = a | b | c;
        }
        return result;
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

    String referString = "";
    private String calc(double[] ss,Sample sample){
        int amount = Integer.valueOf(sample.getProject().getAmount());
        String[] datas = new String[100];
        datas = sample.getProject().getData().split(",");
        referString = "";
        DecimalFormat df=new DecimalFormat("0.00");
        String[] result = {"","",""};
        Pro[] pros = new Pro[amount];
        for (int i = 0; i < amount; i++) {
            pros[i] = new Pro();
            Project project =new Project();
            pros[i].setReferLow(datas[0+20*i]);
            pros[i].setReferHigh(datas[1+20*i]);
            pros[i].setTestLow(datas[2+20*i]);
            pros[i].setTestHigh(datas[3+20*i]);
            pros[i].setUnit(datas[4+20*i]);
            pros[i].setTc(datas[5+20*i]);
            pros[i].setA1High(datas[6+20*i]);
            pros[i].setB1High(datas[7+20*i]);
            pros[i].setC1High(datas[8+20*i]);
            pros[i].setD1High(datas[9+20*i]);
            pros[i].setA1Low(datas[10+20*i]);
            pros[i].setB1Low(datas[11+20*i]);
            pros[i].setC1Low(datas[12+20*i]);
            pros[i].setD1Low(datas[13+20*i]);
            pros[i].setWholeBlood(datas[14+20*i]);
            pros[i].setSerum(datas[15+20*i]);
            pros[i].setPlasma(datas[16+20*i]);
            pros[i].setUrine(datas[17+20*i]);
            pros[i].setOther(datas[18+20*i]);
            pros[i].setEqua(datas[19+20*i]);

//            switch (i){
//                case 0:
//                    pros[i].setTc(sample.getProject().getCt1());
//                    break;
//                case 1:
//                    pros[i].setTc(datas[5+20*i]);
//                    break;
//                case 2:
//                    pros[i].setTc(datas[5+20*i]);
//                    break;
//            }

//            if(i == 0){
//                referString += pros[i].getReferLow() + "-" +pros[i].getReferHigh();
//            }else{
//                referString += pros[i].getReferLow() +"-" +pros[i].getReferHigh();
//            }

        }
        double[] ca = new double[3];
        for (int i = 0; i < amount; i++) {
//            if("".equals(pros[i].getTc()) || ss[i]>=Double.valueOf(pros[i].getTc())){//ss[i]大于TC临界值
//                ca[i] = calculate(Integer.valueOf(pros[i].getEqua()),pros[i].getA1High(),pros[i].getB1High(),
//                        pros[i].getC1High(),pros[i].getD1High(),ss[i],sample);
//            }else{
//                ca[i] = calculate(Integer.valueOf(pros[i].getEqua()),pros[i].getA1Low(),pros[i].getB1Low(),
//                        pros[i].getC1Low(),pros[i].getD1Low(),ss[i],sample);
//            }

            if(!"".equals(pros[i].getTc())) {
                if (ss[i] > Double.valueOf(pros[i].getTc())) {
                    ca[i] = calculate(Integer.valueOf(pros[i].getEqua()), pros[i].getA1High(), pros[i].getB1High(),
                            pros[i].getC1High(), pros[i].getD1High(), ss[i],sample);
                } else if(ss[i] <= Double.valueOf(pros[i].getTc())){
                    ca[i] = calculate(Integer.valueOf(pros[i].getEqua()), pros[i].getA1Low(), pros[i].getB1Low(),
                            pros[i].getC1Low(), pros[i].getD1Low(), ss[i],sample);
                }
            }else{
                ca[i] = calculate(Integer.valueOf(pros[i].getEqua()), pros[i].getA1High(),pros[i].getB1High(),
                        pros[i].getC1High(), pros[i].getD1High(), ss[i],sample);
            }

            if(Double.isNaN(ca[i])){
                ca[i] = ss[i];
            }
            //检测下线<ca[i]<检测上线
            if(ca[i]<Double.valueOf(pros[i].getTestHigh()) && ca[i]>Double.valueOf(pros[i].getTestLow())){
                result[i] +=df.format(ca[i]);//ca;
            }   //ca[i]>=检测上线
            else if(ca[i]>=Double.valueOf(pros[i].getTestHigh())){
                result[i] += ">"+ Double.valueOf(pros[i].getTestHigh());     //>=
                // result[i] += " "+ df.format(ca[i]);
            }   //ca[i]<=检测下线
            else if(ca[i] <= Double.valueOf(pros[i].getTestLow())){
                result[i] += "<" + Double.valueOf(pros[i].getTestLow());     //<=
                //result[i] += " "+ df.format(ca[i]);
            }
            // 参考下线 < ca[i] < 参考上线
            if(ca[i] < Double.valueOf(pros[i].getReferHigh()) && ca[i] > Double.valueOf(pros[i].getReferLow())){

            }       //ca[i] >= 参考上线
            else if(ca[i] >= Double.valueOf(pros[i].getReferHigh())){
                result[i] += "↑";
            }       //ca[i] <= 参考下线
            else if(ca[i] <= Double.valueOf(pros[i].getReferLow())){
                result[i] += "↓";
            }
        }

        // return  result[0]+"/"+result[1]+"/"+result[2];
        if(amount == 2){
            myApp.setConcenFlap("2");
            return result[0]+"/"+result[1];
        }else if(amount == 3){
            myApp.setConcenFlap("3");
            return result[0] + "/" + result[1] + "/" + result[2];
        }else{
            myApp.setConcenFlap("1");
            return result[0];
        }
        //return result[0]+"/"+result[1]+"/"+result[2];
    }

    private double calculate(int kind, String a, String b, String c, String d, double TC,Sample sample)
    //根据T/C值求取浓度,拟合曲线:y=(A-D)/[1+(x/C)^B]+D
    {
        //double result ;
//         result =  C*pow(((A-D)/(TC-D)-1),(1/B));
//        //result = A + B * TC + C * pow(TC, 2.0f);
//        if (kind == 1)
//            return result*1.0f;
//        if (kind == 2)
//            return result*1.0f;
//        if (kind == 3)
//            return result*1.0f;

        //TC = TC * Double.valueOf(sp.readCoeff());

        double A = 0;
        double B = 0;
        double C = 0;
        double D = 0;
        if(!"".equals(a)){
            A = Double.valueOf(a);
        }
        if(!"".equals(b)){
            B = Double.valueOf(b);
        }
        if(!"".equals(c)){
            C = Double.valueOf(c);
        }
        if(!"".equals(d)){
            D = Double.valueOf(d);
        }

        double result = 0;
        double delta,temp1,temp2;
//        result = C*1.0*pow(((A-D)/(TC-D)-1),(1/B));
        switch (kind) {
            case 0:             // 直线回归
            case 3:
                result = A + B * TC;
                break;
            case 1:             // 二次曲线回归
                result = A + B * TC + C * pow(TC, 2.0f);
                break;
            case 2:             // 三次曲线回归
                result = A + B * TC + C * pow(TC, 2.0f) + D * pow(TC, 3.0f);
                break;
            case 4:             // Logistic 曲线拟合1
                result = A + (B - A) / (1 + pow(exp(1), -C * TC + D));
                break;
            case 5:             // Logistic 曲线拟合2(四参数)
                result = (A - D) / (1.0f + pow(TC / C, B)) + D;
                break;
            case 6:             // Hill曲线拟合
                result = A * pow(TC, C) / (pow(B, C) + pow(TC, C));
                break;
            case 7:             // 指数拟合曲线
                result = A + B * (1 - pow(exp(1), -C * TC));
                break;

//            case 0:
//                result = A + B * TC;
//                break;
//            case 1:
//                result = A + B * TC + C * TC * TC;
//                break;
//            case 2:
//                result = A + B * TC + C * TC * TC + D * TC * TC * TC;
//                break;
//            case 5:
//                result = C * pow(((A/D)/(TC-D)-1),(1/B));
//                break;
        }

        try {
            //校准因子
            String type=sample.getSamType();
            Project project2 = new Project();


            project2=DBUtils.searchProject(sample.getProNum());


            if(type.equals("全血")|| type.equals("Whole blood")){
                result=result*Double.valueOf(project2.getWholeBlood());
            }else if(type.equals("血清")|| type.equals("Serum")){
                result=result*Double.valueOf(project2.getSerum());
            }else if(type.equals("血浆")|| type.equals("Plasma")){
                result=result*Double.valueOf(project2.getPlasma());
            }else if(type.equals("尿液")|| type.equals("Urine")){
                result=result*Double.valueOf(project2.getUrine());
            }else if(type.equals("其他")|| type.equals("Others")){
                result=result*Double.valueOf(project2.getOther());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result*1.0f;
    }



    private double Cubic(double a, double b, double c, double d){
        double i = 0,k,p,q;
        int o,j,h;
        double r,s,t,u;
        int w,y;
        double m,n,v;
        double root = 0;

//	memset(&root,0,sizeof(root));
        if(a<0)
        {
            a = -a;
            b = -b;
            c = -c;
            d = -d;
        }

        if(b*b <= 3*a*c)
        {
            if(b*b == 3*a*c)
            {
                k = -b/(3*a);
                p = a*k*k*k + b*k*k + c*k +d;
                o = 1;
                if(p>0)
                {
                    for(j=0;;++j)
                    {
                        i = -b/(3*a) - o;
                        q = a*i*i*i + b*i*i + c*i +d;
                        o = o*2;
                        if(q<=0)
                        {
                            break;
                        }
                    }
                }
                if(p<0)
                {
                    for(j=0;;++j)
                    {
                        i = -b/(3*a) + o;
                        q = a*i*i*i + b*i*i + c*i +d;
                        o = o*2;
                        if(q>=0)
                        {
                            break;
                        }
                    }
                }
                for(h=0;h<50;++h)
                {
                    i = i - (a*i*i*i + b*i*i + c*i +d) / (3*a*i*i + 2*b*i + c);
                }
                if(p==0)
                {
                    i = -b/(3*a);
                }
            }

            if(b*b < 3*a*c)
            {
                i = -b/(3*a);
                for(h=0;h<100;++h)
                {
                    i = i-(a*i*i*i + b*i*i +c*i + d)/(3*a*i*i + 2*b*i + c);
                }
            }
            root = i;
        }

        if(b*b > 3*a*c)
        {
            r=(-b-sqrt(b*b-3*a*c))/(3*a);
            s=(-b+sqrt(b*b-3*a*c))/(3*a);
            t=a*r*r*r+b*r*r+c*r+d;
            u=a*s*s*s+b*s*s+c*s+d;
            if(t*u>=0)
            {
                if(d>0)
                {
                    o = 1;
                    for(w=0;;++w)
                    {
                        i = r-o;
                        o = o*2;
                        if(a*i*i*i+b*i*i+c*i+d<=0)
                        {
                            break;
                        }
                    }
                }
                if(d<0)
                {
                    o = 1;
                    for(w=0;;++w)
                    {
                        i = s+o;
                        o = o*2;
                        if(a*i*i*i+b*i*i+c*i+d<=0)
                        {
                            break;
                        }
                    }
                }
                for(h=0;h<100;++h)
                {
                    i = i-(a*i*i*i + b*i*i +c*i + d)/(3*a*i*i + 2*b*i + c);
                }

            }

            if(t*u>0)
            {
                root = i;
            }

            if(t*u==0)
            {
                if(t==0)
                {
                    root = r;
                    root = i;
                }

                if(u==0)
                {
                    root = i;
                    root = s;
                }
            }

            if(t*u<0)
            {
                o=1;
                for(w=0;;++w)
                {
                    m = r - o;
                    o = o*2;
                    if(a*m*m*m+b*m*m+c*m+d<=0)
                    {
                        break;
                    }
                }
                for(h=0;h<100;++h)
                {
                    m = m - (a*m*m*m + b*m*m +c*m + d)/(3*a*m*m + 2*b*m + c);
                }
                o = 1;
                for(w=0;;++w)
                {
                    n = s + o;
                    o = o*2;
                    if(a*n*n*n + b*n*n + c*n + d >=0)
                    {
                        break;
                    }
                }
                for(h=0;h<100;++h)
                {
                    n = n - (a*n*n*n + b*n*n +c*n + d)/(3*a*n*n + 2*b*n + c);
                }
                v = (r+s) / 2;
                for(h=0;h<100;++h)
                {
                    if(a*v*v*v+b*v*v+c*v+d>0)
                    {
                        r = v;
                        v = (r + s)/2;
                    }
                    if(a*v*v*v+b*v*v+c*v+d<0)
                    {
                        s = v;
                        v = (r + s)/2;
                    }
                    if(a*v*v*v+b*v*v+c*v+d==0)
                    {
                        break;
                    }
                }

                root = m;
                root = v;
                root = n;

            }

        }
        return root;
    }


    //添加一个空的sample
    private void putNullSample(){
        Sample sample2 = new Sample();
        sample2.setFlagNull(true);
        myApp.getTestPeoples().add(sample2);
    }




}
