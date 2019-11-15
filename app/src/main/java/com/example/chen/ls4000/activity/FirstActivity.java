package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.zyapi.CommonApi;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.event.bleResponse;
import com.example.chen.ls4000.service.BlueToothService;
import com.example.chen.ls4000.service.LisService;
import com.example.chen.ls4000.service.MyService;
import com.example.chen.ls4000.service.PortCardService;
import com.example.chen.ls4000.service.PortService;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.MyContextWrapper;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.ToastUtil;
import com.example.chen.ls4000.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FirstActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 2000; //延迟2秒
    private Button btn;
    private boolean flag = false;
    private BleMethodCode methodCode;
    private int num = 0;
    private SharedHelper sp;
    private ImageView logo;
    private TextView loading, version, company, first_hint;
    private MyApp myApp;
    private ImageView anim;
    private static final String ACTION_SET_HIDE_NAVIGATIONVIEW = "ismart.intent.action_hide_navigationview"; //隐藏虚拟键
    private static final String ACTION_LOCK_PANELBAR = "ismart.intent.action_lock_panelbar";    //禁用下拉


    private ExecutorService es = Executors.newScheduledThreadPool(30);
    private CommonApi commonApi = null;
    private int[] ios = new int[]{1, 2, 3, 4, 80, 79, 78, 86, 83, 82};
    private int GPIO_12V_EN = 44;       //12V电压

    private int GPIO_5V_EN = 7;
    private boolean isStop = true;
    private int LED_ON = 0;
    private int LED_OFF = 1;

    private int cur = 0;            //升级发送的包数
    private int sendLen = 1024;    //byte数据
    private boolean isOpen = true;

    private RelativeLayout firstpage;

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            num++;
            methodCode = new BleMethodCode(3);
            methodCode.setMes("");
            methodCode.setComm((byte) 0x04);            //获取温度
            EventBus.getDefault().post(methodCode);
//                methodCode = new BleMethodCode(3);
//                methodCode.setMes("LAN\r\n");
//                EventBus.getDefault().post(methodCode);
           // handler.postDelayed(this, 2000);
        }
    };

    Handler maxHandler = new Handler();
    Runnable maxRunnable = new Runnable() {
        @Override
        public void run() {
            //隐藏虚拟键
            Intent intent1 = new Intent("ismart.intent.action_hide_navigationview");
            intent1.putExtra("hide", true);
            sendBroadcast(intent1);

            //禁用状态栏下拉
            Intent intent2 = new Intent("ismart.intent.action_lock_panelbar");
            intent2.putExtra("state", true);
            sendBroadcast(intent2);
            sp.savevirtual_key("1");
        }
    };


    Handler mmHandler = new Handler();

    Runnable mmRunnable = new Runnable() {
        @Override
        public void run() {
            methodCode = new BleMethodCode(3);
            methodCode.setMes("");
            methodCode.setComm((byte) 0x0E);
            EventBus.getDefault().post(methodCode);
        }
    };


    public void showToast(final Activity activity, final String word, final long time) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_SHORT);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, time);
            }
        });
    }

    String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_first);
        hideBottomUIMenu();

        myApp = (MyApp) getApplication();
        myApp.setPowerBool(false);

        EventBus.getDefault().register(this);
        //EventBus.getDefault().post(new BleMethodCode(6));
        //jumpView("success");
        startService(new Intent(FirstActivity.this, PortService.class));
        startService(new Intent(FirstActivity.this, MyService.class));
        startService(new Intent(FirstActivity.this, PortCardService.class));
        startService(new Intent(FirstActivity.this, LisService.class));
        startService(new Intent(FirstActivity.this, BlueToothService.class));

        commonApi = new CommonApi();
        //连接蓝牙打印机
//        List<BluetoothDevice> printerDevices = BluetoothUtil.getPairedDevices();
//        Intent intent = new Intent(FirstActivity.this, BlueToothService.class);
//        Bundle bundle = new Bundle();
//        for (int i = 0; i < printerDevices.size(); i++) {
//            if ("RG-MTP58B".equals(printerDevices.get(i).getName())) {
//                BluetoothDevice bluetoothDevice = printerDevices.get(i);
//                bundle.putParcelable("bluedevice", bluetoothDevice);
//                intent.putExtras(bundle);
//                startService(intent);
//                break;
//            }
//
//        }
        //BluetoothDevice device = bundle.getParcelable("bluedevice");
        //startService(new Intent(FirstActivity.this, BlueToothService.class));

        Context context = getApplicationContext();

        initviews();

        //下拉框的隐藏
        //maxHandler.postDelayed(maxRunnable, 3000);

        sp = new SharedHelper(context);
        //sp.saveBluetoothState(false);

        if(!sp.readLogo()){
            firstpage.setBackgroundResource(R.mipmap.firstpage1);
        }else{
            firstpage.setBackgroundResource(R.mipmap.firstpage);
        }

        myApp.setObject();
        String lan = sp.readLan();
        sp.saveTime(0);
        //if("中文".equals(lan)){
        logo.setImageResource(R.mipmap.logo);
        loading.setText(getString(R.string.string37));
        version.setText(getString(R.string.string38));
        company.setText(getString(R.string.string39));
//        }else if("English".equals(lan)){
//            logo.setImageResource(R.mipmap.logo);
//            loading.setText("System Self-checking...");
//            version.setText("version number:1.1.0");
//            company.setText("Suzhou Lansion Biotechnology Co.,Ltd © 2017-2020");
//        }




        startGpioTest();


        byte[] b = {(byte)0x01};
         str = new String(b);


       // 升级判断
        if("1".equals(sp.readUpgradeFlag())){
            first_hint.setVisibility(View.VISIBLE);
            first_hint.setText(getString(R.string.string300));
            upgHandler.postDelayed(upgRunnable,100);
        }else{
            mmHandler.postDelayed(mmRunnable, 100);
        }
    }

    Handler upgHandler = new Handler();
    Runnable upgRunnable = new Runnable() {
        @Override
        public void run() {
            BleMethodCode methodCode;
            methodCode = new BleMethodCode(3);
            methodCode.setMes("");
            methodCode.setComm((byte) 0x10);
            EventBus.getDefault().post(methodCode);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }



    //确定的响应事件
    private void popDialog(String str) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.string196))//设置对话框标题
                .setMessage(str)//设置显示的内容
                .setCancelable(false)
                .setPositiveButton(getString(R.string.string338), new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        dialog.dismiss();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        jumpView("success");

                    }
                }).show();//在按键响应事件中显示此对话框

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MyContextWrapper.wrap(base, "en"));
    }

    private void initviews() {
        logo = (ImageView) findViewById(R.id.id_first_logo);
        loading = (TextView) findViewById(R.id.id_first_loading);
        version = (TextView) findViewById(R.id.id_first_version);
        company = (TextView) findViewById(R.id.id_first_company);
        first_hint = (TextView) findViewById(R.id.id_first_hint);
        firstpage = (RelativeLayout)findViewById(R.id.id_first_firstpage);


//        jumpView("success");

    }


    public int getUnsignedByte(byte data) {      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        data = -86;
        return data & 0x0FF; // 部分编译器会把最高位当做符号位，因此写成0x0FF.
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    private void jumpView(final String mes) {

        System.out.println("收到的信息是" + mes);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent mainIntent = new Intent(FirstActivity.this, MainActivity.class);
                mainIntent.putExtra("value", "success");
                startActivity(mainIntent);
                finish();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(bleResponse response) {
        BleMethodCode methodCode;
        System.out.println("数据###########" + response.getCode());
        switch (response.getCode()) {
            case 4:
                System.out.println("数据first4 写入成功");
                break;
            case 5:
                System.out.println("数据first5 写入失败");
                jumpView("failed");
                break;
            case 6:
                System.out.println("数据first6 rssi");
                break;
//            case 7:
//                Log.e("test", "可以通讯");
//                Log.e("connected", "characteristic");
//                //连接成功后发送字符
//                methodCode = new BleMethodCode(3);
//                methodCode.setMes("R");
//                EventBus.getDefault().post(methodCode);
//                break;
            case 8:
                System.out.println("数据first8 通讯通道找不到");
                jumpView("failed");
                break;
            case 9:
                System.out.println("数据first9 蓝牙基本状态信息");
                if (response.isConnect() && response.isCharacter()) {
                    System.out.println("数据9链接成功");
                    //mHandler.postDelayed(r,2000);
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes("LAN\r\n");
                    EventBus.getDefault().post(methodCode);

                } else {
                    System.out.println("数据9链接失败");
                    showToast(this, getString(R.string.string272), 1000);
                    //Toast.makeText(getApplicationContext(), "设备连接失败", Toast.LENGTH_SHORT).show();
                    jumpView("failed");
                }
                break;
        }
    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//        hideBottomUIMenu();
//
//    }
    int biao = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleReadEvent response) throws UnsupportedEncodingException {
        System.out.println("收到数据" + response.getMes());
        //System.out.println("收到"+asciiToString(response.getMes()));
        int comm = response.getComm();
        byte[] bys = response.getBys();
        byte[] strByte = response.getMes().getBytes();

//        if(comm == 0x00){//退出窗口
//            Toast.makeText(getApplicationContext(), "设备自检失败，请退出重试", Toast.LENGTH_SHORT).show();
//        }else
        if (comm == 0x01) {  //自检成功    温度需要根据范围判断？
            flag = true;
            handler.removeCallbacks(runnable);
            double ter = Double.parseDouble(response.getMes().substring(1, 4));
            int n = Integer.parseInt(response.getMes().substring(0, 1));
            ter = ter / 10;
            if (n == 1) {
                ter = -ter;
            }
            showToast(this, getString(R.string.string40) + ter, 1000);
            //Toast.makeText(this,getString(R.string.string40)+ter,Toast.LENGTH_LONG).show();
            if (ter <= 40 && ter >= 10) {
                jumpView("success");
                biao = 0;
            } else {
                popTip(ter);
            }
        } else if (comm == 0x00) {
            System.out.println("321321" + bys.length);
            byte[] bytes = Utils.getBooleanArray(bys[0]);
            System.out.println("大市地税局：" + bytes);

            if (bytes[6] == 0) {
                biao = 1;
                popDialog(getString(R.string.string301));

            } else if (bytes[5] == 0) {
                popDialog(getString(R.string.string302));
                biao = 1;

            } else if (bytes[4] == 0) {
                popDialog(getString(R.string.string303));

                biao = 1;

            } else if (bytes[3] == 0) {
                popDialog(getString(R.string.string304));
                biao = 1;

            } else if (bytes[2] == 0) {
                popDialog(getString(R.string.string305));
                biao = 1;
            }
            if (biao == 0) {
                handler.postDelayed(runnable, 1000);
            }
        } else if (comm == 0x13) {      //有卡条就先把出来
            popProTip(getString(R.string.string306));

        } else if (comm == 0x11) {

            mxHandler.postDelayed(mxRunnable, 1500);

        } else if (comm == 0x15) {      //升级下位机
            try {
                Thread.sleep(3000);
                stopGpioTest();     //关闭下位机
                Thread.sleep(3000);
                startGpioTest();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            downBys = getFile();
            byte[] by = new byte[sendLen];

            while (isOpen){

                if(cur < downBys.length / sendLen){
                    System.arraycopy(downBys,cur * sendLen,by,0,sendLen);

                    String s = null;
                    try {
                        s = new String(by,"ISO-8859-1");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    BleMethodCode methodCode;
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes(s);
                    methodCode.setBytes(by);
                    EventBus.getDefault().post(methodCode);
                    cur++;


                }else if(cur == downBys.length / sendLen){  //最后一包数据
                    by = new byte[downBys.length % sendLen];
                    System.arraycopy(downBys, cur * sendLen, by, 0, downBys.length % sendLen);
                    String s = null;
                    try {
                        s = new String(by,"ISO-8859-1");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    BleMethodCode methodCode;
                    methodCode = new BleMethodCode(3);
                    methodCode.setMes(s);
                    methodCode.setBytes(by);
                    EventBus.getDefault().post(methodCode);
                    cur++;


                    isOpen = false;
                }

            }



        } else if (comm == 0x16) {          //升级是否成功指令
            System.out.println("321321" + bys.length);
            System.out.println("wwww:" + strByte);
            if(bys[0] == 0){
                first_hint.setText(getString(R.string.string307));

            }else if(bys[0] == 1){
                //判断是否有卡条
                first_hint.setText(getString(R.string.string308));
                mmHandler.postDelayed(mmRunnable, 100);
                sp.saveUpgradeFlag("0");

            }




        }
//        else if(comm == 0x02){//自检成功    温度需要根据范围判断？
//            flag = true;
//            handler.removeCallbacks(runnable);
//            double ter = Double.parseDouble(response.getMes().substring(1,4));
//            int n = Integer.parseInt(response.getMes().substring(0,1));
//            ter = ter/10;
//            if(n==1){
//                ter = -ter;
//            }
//            Toast.makeText(this,"当前的温度是"+ter,Toast.LENGTH_LONG).show();
//            jumpView("success");
//        }
        if (response.getMes().contains("OK")) {
            flag = true;
            handler.removeCallbacks(runnable);
            //Toast.makeText(getApplicationContext(), "收到OK", Toast.LENGTH_SHORT).show();
            System.out.println("收到" + response.getMes());
            //  Toast.makeText(getApplicationContext(), "设备连接成功", Toast.LENGTH_SHORT).show();
            //jumpView("success");
        } else {
            //jumpView("failed");
//            BleMethodCode methodCode;
//            methodCode = new BleMethodCode(3);
//            methodCode.setMes("LAN\r\n");
//            EventBus.getDefault().post(methodCode);
        }
        if (response.getMes().contains("AT")) {
            flag = true;
            handler.removeCallbacks(runnable);
        }

//        for (int i = 0; i < 5; i++) {
//            BleMethodCode methodCode;
//            methodCode = new BleMethodCode(3);
//            methodCode.setMes("LAN\r\n");
//            EventBus.getDefault().post(methodCode);
//        }
    }

    Handler mxHandler = new Handler();
    Runnable mxRunnable = new Runnable() {
        @Override
        public void run() {
            first_hint.setVisibility(View.GONE);
            methodCode = new BleMethodCode(3);
            methodCode.setMes("");
            methodCode.setComm((byte) 0x0F);
            EventBus.getDefault().post(methodCode);
        }
    };



    //关闭下位机断电
    private void stopGpioTest() {
        isStop = true;
        if (commonApi != null) {
            commonApi.setGpioOut(GPIO_12V_EN, 0);
            //commonApi.setGpioOut(GPIO_5V_EN, 0);
        }
    }

    //打开下位机
    private void startGpioTest() {
        //first ,turn on GPIO 12V
        commonApi.setGpioDir(GPIO_12V_EN, 1);
        commonApi.setGpioOut(GPIO_12V_EN, 1);

        //first ,turn on GPIO 5V
//        commonApi.setGpioDir(GPIO_5V_EN, 1);
//        commonApi.setGpioOut(GPIO_5V_EN, 1);

        es.submit(new TaskTestI0());
    }


    private class TaskTestI0 implements Runnable {

        @Override
        public void run() {
            isStop = false;
            while (!isStop) {
                for (int i = 0; i < ios.length; i++) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioDir(ios[i], 1);
                    commonApi.setGpioOut(ios[i], LED_ON);
                }

                for (int i = 0; i < ios.length; i++) {

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioDir(ios[i], 1);
                    commonApi.setGpioOut(ios[i], LED_OFF);
                }

                for (int i = 0; i < ios.length; i++) {

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    commonApi.setGpioDir(ios[i], 1);
                    commonApi.setGpioOut(ios[i], LED_ON);
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioOut(ios[i], LED_OFF);
                }


                for (int i = ios.length - 1; i >= 0; i--) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioDir(ios[i], 1);
                    commonApi.setGpioOut(ios[i], LED_ON);
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    commonApi.setGpioOut(ios[i], LED_OFF);
                }
            }

        }
    }

    private void popProTip(String str) {
        final View view;


        view = View.inflate(this, R.layout.firstlayout, null);

        TextView tip;
        Button ensure;

        tip = (TextView) view.findViewById(R.id.id_first_tip);
        ensure = (Button) view.findViewById(R.id.id_first_ensure);
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
                methodCode = new BleMethodCode(3);
                methodCode.setMes("");
                methodCode.setComm((byte) 0x0E);
                EventBus.getDefault().post(methodCode);
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        isStop = true;
//
//        if (commonApi != null) {
//            commonApi.setGpioOut(GPIO_12V_EN, 0);
//            commonApi.setGpioOut(GPIO_5V_EN, 0);
//        }

        EventBus.getDefault().unregister(this);
    }

    private void popTip(double tem) {
        View view;
//        if("English".equals(lan)){
//            view = View.inflate(this,R.layout.mydialog_eng,null);
//        }else{
        view = View.inflate(this, R.layout.temtiplayout, null);
        //  }

        TextView tip;
        Button ignore, turnOff;
        ImageView img;

        tip = (TextView) view.findViewById(R.id.id_temtip_tip);
        ignore = (Button) view.findViewById(R.id.id_temtip_ignore);
        turnOff = (Button) view.findViewById(R.id.id_temtip_turnoff);
        img = (ImageView) view.findViewById(R.id.id_temtip_img);

        if (tem > 35) {
            img.setImageResource(R.mipmap.img_hightemperature);
            tip.setText(getString(R.string.string45) + tem + getString(R.string.string46));
        } else if (tem < 10) {
            img.setImageResource(R.mipmap.img_lowtemperature);
            tip.setText(getString(R.string.string45) + tem + getString(R.string.string47));
        }

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialog).create();
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        dialog.getWindow().setAttributes(params);
        ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpView("success");
            }
        });

        turnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent("ismart.intent.action_poweroff");
                sendBroadcast(mIntent);
            }
        });
    }

    public static int bytesint(byte[] bytes) {
        int result = 0;
        if (bytes.length == 2) {
            int a = (bytes[0] & 0xff) << 8;//说明二
            int b = (bytes[1] & 0xff);
            result = a | b;
        }
        return result;
    }


    public static String asciiToString(String value) {

        StringBuffer sbu = new StringBuffer();
        //for (int i = 0; i < chars.length; i++) {
        sbu.append((char) Integer.parseInt(value));
        //}
        return sbu.toString();
    }


    private byte[] downBys;     //接受bin文件转化的byte数据
    private String encoding = "gb2312"; //转化成类型

    public byte[] getFile() {
        BufferedOutputStream bos = null;       //新建一个输出流
        FileOutputStream fos = null;          //文件包装输出流
        File file = null;           //新建文件

        String apkPath = "/storage/emulated/0/LS4000/bin";  //本地路径
        String fileName = "MtControl.bin";      //本地的bin文件ID

        FileInputStream fis = null;    //文件的输入了

        try {
            File dir = new File(apkPath);
            if (!dir.exists() && dir.isDirectory()) { //判断文件是否存在
                ToastUtil.showToast(this,"bin文件不存在",1500);
                return null;
            }

            file = new File(apkPath + "/" + fileName);  //新建file类
            System.out.println("数据文件长度" + file.length());
            FileInputStream in = new FileInputStream(file);     //文件输入流

            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

            System.out.println("bytes available:" + in.available());

            byte[] temp = new byte[1024];

            int size = 0;

            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }

            in.close();

            byte[] filea = out.toByteArray();
            out.close();
            String encoding = "ISO-8859-1";
            String fileaString = new String(filea, encoding);
            System.out.println(fileaString);

            return fileaString.getBytes(encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();        //关闭资源
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                fos.close();        //关闭资源
            }
        catch(IOException e1){
            e1.printStackTrace();
        }
    }
}
        return null;
    }
}
