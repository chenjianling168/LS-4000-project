package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.adapter.WifiListAdapter;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.thread.ConnectThread;
import com.example.chen.ls4000.thread.ListenerThread;
import com.example.chen.ls4000.utils.SharedHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class WIFIActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private ImageView back;
    private CheckBox switchWifi;
    private ListView lv;
    private Button last,next;
    private TextView text_state,textview;

    private WifiManager wifiManager;
    private WifiListAdapter wifiListAdapter;
    private WifiConfiguration config;
    private int wcgID;
    private String wrongNetWork;
    private RelativeLayout availableLayout;
    private TextView available;
    private TextView wifiName;

    /**
     * 热点名称
     */
    private static final String WIFI_HOTSPOT_SSID = "TEST";
    /**
     * 端口号
     */
    private static final int PORT = 54321;

    private static final int WIFICIPHER_NOPASS = 1;
    private static final int WIFICIPHER_WEP = 2;
    private static final int WIFICIPHER_WPA = 3;

    public static final int DEVICE_CONNECTING = 1;//有设备正在连接热点
    public static final int DEVICE_CONNECTED = 2;//有设备连上热点
    public static final int SEND_MSG_SUCCSEE = 3;//发送消息成功
    public static final int SEND_MSG_ERROR = 4;//发送消息失败
    public static final int GET_MSG = 6;//获取新消息


    /**
     * 连接线程
     */
    private ConnectThread connectThread;

    /**
     * 监听线程
     */
    private ListenerThread listenerThread;

    private SharedHelper sp;

    /**
     * 添加广播过滤器
     * @param savedInstanceState
     */
    private IntentFilter intentFilter;
    private WifiChangedReceiver wifiChangedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        initviews();
        initBroadcastReceiver();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        listenerThread = new ListenerThread(PORT, handler);
        listenerThread.start();
    }

    private void initviews(){
        switchWifi = (CheckBox) findViewById(R.id.id_wifi_switch);
        lv = (ListView)findViewById(R.id.id_wifi_lv);
        last = (Button)findViewById(R.id.id_wifi_last);
        next = (Button)findViewById(R.id.id_wifi_next);
        back = (ImageView)findViewById(R.id.id_wifi_back);
        text_state = (TextView) findViewById(R.id.id_wifi_text_state);
        textview = (TextView)findViewById(R.id.id_wifi_textview);
        available = (TextView)findViewById(R.id.id_wifi_available);
        availableLayout = (RelativeLayout)findViewById(R.id.id_wifi_layout);
        wifiName = (TextView)findViewById(R.id.id_wifi_name);

        hideBottomUIMenu();

        sp = new SharedHelper(this);
        switchWifi.setChecked(sp.readWifi());

        last.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);
        switchWifi.setOnCheckedChangeListener(this);


        wifiListAdapter = new WifiListAdapter(this, R.layout.wifi_list_item);
        lv.setAdapter(wifiListAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wifiManager.disconnect();
                final ScanResult scanResult = wifiListAdapter.getItem(position);
                String capabilities = scanResult.capabilities;
                int type = WIFICIPHER_WPA;
                if (!TextUtils.isEmpty(capabilities)) {
                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        type = WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        type = WIFICIPHER_WEP;
                    } else {
                        type = WIFICIPHER_NOPASS;
                    }
                }
                config = isExsits(scanResult.SSID);

                if (config == null) {
                    if (type != WIFICIPHER_NOPASS) {  //需要密码
                        final int finalType = type;



                        view = View.inflate(WIFIActivity.this, R.layout.wifilayout, null);

                        TextView tip;
                        Button ensure,cancel;
                        final EditText wifi_edit;
                        CheckBox wifi_check;

                        tip = (TextView) view.findViewById(R.id.id_wifi_title);     //上面提示按钮
                        wifi_edit = (EditText)view.findViewById(R.id.id_wifi_tip);  //输入框
                        wifi_check = (CheckBox)view.findViewById(R.id.id_wifi_check);   //单选按钮
                        ensure = (Button) view.findViewById(R.id.id_wifi_ensure);   //确定
                        cancel = (Button)view.findViewById(R.id.id_wifi_cancel);    //取消
                        tip.setText(getString(R.string.string88));

                        final android.app.AlertDialog dialog = new android.app.AlertDialog.
                                Builder(WIFIActivity.this, R.style.AlertDialog).create();
                        dialog.setView(view);
                        dialog.setCancelable(false);
                        dialog.show();
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        params.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
                        params.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
                        dialog.getWindow().setAttributes(params);

                        wifi_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked){
                                    //如果选中，显示密码
                                    wifi_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                }else{
                                    //否则隐藏密码
                                    wifi_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                }
                            }
                        });

                        ensure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                wrongNetWork = "\"" + scanResult.SSID + "\"";
                                config = createWifiInfo(scanResult.SSID, wifi_edit.getText().toString(),
                                        finalType);
                                removeWifiBySsid(wifiManager, "\"" + scanResult.SSID + "\"");
                                connect(config);
                                dialog.dismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                    } else {
                        config = createWifiInfo(scanResult.SSID, "", type);
                        connect(config);
                    }
                } else {
                    connect(config);
                }
            }
        });

    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.id_wifi_switch) {
            if(isChecked){
                search();
            }else{
                closeWifiHotspot();
                wifiListAdapter.clear();
            }
        }
    }




    private void connect(WifiConfiguration config) {
        text_state.setText(getString(R.string.string91));
        wcgID = wifiManager.addNetwork(config);
        wifiManager.enableNetwork(wcgID, true);
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

//        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        receiver = new Receiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //广播
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiChangedReceiver = new WifiChangedReceiver();
        registerReceiver(wifiChangedReceiver, intentFilter);

        initBroadcastReceiver();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_wifi_last:
                search();
                break;
            case R.id.id_wifi_next:
                break;
            case R.id.id_wifi_back:
                sp.saveWifi(switchWifi.isChecked());
                finish();
                break;
            case R.id.id_wifi_switch:
                if(switchWifi.isChecked()){
                    search();
                } else{
                    closeWifiHotspot();
                }
                if (wifiManager.isWifiEnabled()) {
                    //开启wifi
                    wifiManager.setWifiEnabled(false);
                }
                wifiListAdapter.clear();
//                if(sp.readWifiState()){
//                    switchWifi.setChecked(true);
//                }else{
//                    switchWifi.setChecked(false);
//                }
                break;
        }
    }

    /**
     * 创建Wifi热点
     */
    private void createWifiHotspot() {
        if (wifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            wifiManager.setWifiEnabled(false);
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = WIFI_HOTSPOT_SSID;
        config.preSharedKey = "123456789";
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        //通过反射调用设置热点
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, Boolean.TYPE);

            boolean enable = (Boolean) method.invoke(wifiManager, config, true);
            if (enable) {
                textview.setText(getString(R.string.string223) + WIFI_HOTSPOT_SSID + " password:123456789");
            } else {
                textview.setText(getString(R.string.string224));
            }
        } catch (Exception e) {
            e.printStackTrace();
            textview.setText(getString(R.string.string224));
        }
    }

    /**
     * 关闭WiFi热点
     */
    public void closeWifiHotspot() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
            Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(wifiManager, config, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        textview.setText(getString(R.string.string225));
        text_state.setText(getString(R.string.string226));
        available.setVisibility(View.GONE);
        availableLayout.setVisibility(View.GONE);
    }

    /**
     * 获取连接到热点上的手机ip
     *
     * @return
     */
    private ArrayList<String> getConnectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }

    /**
     * 搜索wifi热点
     */
    private void search() {
        if (!wifiManager.isWifiEnabled()) {
            //开启wifi
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // listenerThread
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);

        unregisterReceiver(wifiChangedReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DEVICE_CONNECTING:
                    connectThread = new ConnectThread(listenerThread.getSocket(),handler);
                    connectThread.start();
                    break;
                case DEVICE_CONNECTED:
                    textview.setText(getString(R.string.string92));
                    break;
                case SEND_MSG_SUCCSEE:
                    textview.setText(getString(R.string.string93) + msg.getData().getString("MSG"));
                    break;
                case SEND_MSG_ERROR:
                    textview.setText(getString(R.string.string94) + msg.getData().getString("MSG"));
                    break;
                case GET_MSG:
                    textview.setText(getString(R.string.string95) + msg.getData().getString("MSG"));
                    break;
            }
        }
    };

    private Receiver receiver;

    class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.w("BBB", "SCAN_RESULTS_AVAILABLE_ACTION");
                // wifi已成功扫描到可用wifi。
                List<ScanResult> scanResults = wifiManager.getScanResults();
                wifiListAdapter.clear();
                wifiListAdapter.addAll(scanResults);
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                Log.w("BBB", "WifiManager.WIFI_STATE_CHANGED_ACTION");
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        //获取到wifi开启的广播时，开始扫描
                        //switchWifi.setChecked(true);
                        wifiManager.startScan();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        //wifi关闭发出的广播
                        switchWifi.setChecked(false);
                        sp.saveWifiState(false);
                        text_state.setText(getString(R.string.string96));
                        available.setVisibility(View.GONE);
                        availableLayout.setVisibility(View.GONE);
                        break;
                }
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                Log.w("BBB", "WifiManager.NETWORK_STATE_CHANGED_ACTION");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    text_state.setText(getString(R.string.string96));
                    available.setVisibility(View.GONE);
                    availableLayout.setVisibility(View.GONE);

                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if(switchWifi.isChecked()==true){
                        text_state.setText(getString(R.string.string97) + wifiInfo.getSSID());
                        available.setVisibility(View.VISIBLE);
                        availableLayout.setVisibility(View.VISIBLE);
                        wifiName.setText(wifiInfo.getSSID()+"");
                    }else{
                        text_state.setText(getString(R.string.string226));
                        available.setVisibility(View.GONE);
                        availableLayout.setVisibility(View.GONE);
                    }
                    Log.w("AAA","wifiInfo.getSSID():"+wifiInfo.getSSID()+"  WIFI_HOTSPOT_SSID:"+WIFI_HOTSPOT_SSID);
                    if (wifiInfo.getSSID().equals(WIFI_HOTSPOT_SSID)) {
                        //如果当前连接到的wifi是热点,则开启连接线程
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ArrayList<String> connectedIP = getConnectedIP();
                                    for (String ip : connectedIP) {
                                        if (ip.contains(".")) {
                                            Log.w("AAA", "IP:" + ip);
                                            Socket socket = new Socket(ip, PORT);
                                            connectThread = new ConnectThread(socket, handler);
                                            connectThread.start();
                                        }
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                else {
                    NetworkInfo.DetailedState state = info.getDetailedState();
                    if (state == state.CONNECTING) {
                        text_state.setText(getString(R.string.string91));
                    } else if (state == state.AUTHENTICATING) {
                        text_state.setText(getString(R.string.string98));
                    } else if (state == state.OBTAINING_IPADDR) {
                        text_state.setText(getString(R.string.string99));
                    } else if (state == state.FAILED) {
                        text_state.setText(getString(R.string.string100));
                        available.setVisibility(View.GONE);
                        availableLayout.setVisibility(View.GONE);
                    }
                }

            }
            else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                //LogLazy.e("wifi密码错误广播");
                //Toast.makeText(WIFIActivity.this,"wifi密码错误广播",Toast.LENGTH_LONG).show();
               // System.out.print("wifi密码错误广播");
                int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {

                    //System.out.print("密码错误");
                    removeWifiBySsid(wifiManager,wrongNetWork);
                }
            }
           /* else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                    text_state.setText("连接已断开");
                    wifiManager.removeNetwork(wcgID);
                } else {
                    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    text_state.setText("已连接到网络:" + wifiInfo.getSSID());
                }
            }*/
        }
    }

//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (switchWifi.isChecked() && action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//                Log.w("BBB", "SCAN_RESULTS_AVAILABLE_ACTION");
//                // wifi已成功扫描到可用wifi。
//                List<ScanResult> scanResults = wifiManager.getScanResults();
//                wifiListAdapter.clear();
//                wifiListAdapter.addAll(scanResults);
//            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
//                Log.w("BBB", "WifiManager.WIFI_STATE_CHANGED_ACTION");
//                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
//                switch (wifiState) {
//                    case WifiManager.WIFI_STATE_ENABLED:
//                        //获取到wifi开启的广播时，开始扫描
//                        //switchWifi.setChecked(true);
//                        wifiManager.startScan();
//                        break;
//                    case WifiManager.WIFI_STATE_DISABLED:
//                        //wifi关闭发出的广播
//                        switchWifi.setChecked(false);
//                        sp.saveWifiState(false);
//                        text_state.setText(getString(R.string.string96));
//                        break;
//                }
//            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
//                Log.w("BBB", "WifiManager.NETWORK_STATE_CHANGED_ACTION");
//                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
//                    text_state.setText(getString(R.string.string96));
//                    removeWifiBySsid(wifiManager,wrongNetWork);
//                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
//                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//                    final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                    if(switchWifi.isChecked()==true){
//                        text_state.setText(getString(R.string.string97) + wifiInfo.getSSID());
//                    }else{
//                        text_state.setText(getString(R.string.string226));
//                    }
//                    Log.w("AAA","wifiInfo.getSSID():"+wifiInfo.getSSID()+"  WIFI_HOTSPOT_SSID:"+WIFI_HOTSPOT_SSID);
//                    if (wifiInfo.getSSID().equals(WIFI_HOTSPOT_SSID)) {
//                        //如果当前连接到的wifi是热点,则开启连接线程
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    ArrayList<String> connectedIP = getConnectedIP();
//                                    for (String ip : connectedIP) {
//                                        if (ip.contains(".")) {
//                                            Log.w("AAA", "IP:" + ip);
//                                            Socket socket = new Socket(ip, PORT);
//                                            connectThread = new ConnectThread(socket, handler);
//                                            connectThread.start();
//                                        }
//                                    }
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start();
//                    }
//                } else if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
//                    //LogLazy.e("wifi密码错误广播");
//                    //Toast.makeText(this,"wifi密码错误广播",Toast.LENGTH_LONG).show();
//                    System.out.print("wifi密码错误广播");
//                    int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
//                    if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
//
//                        System.out.print("密码错误");
//                    }
//                }
//                else {
//                    NetworkInfo.DetailedState state = info.getDetailedState();
//                    if (state == state.CONNECTING) {
//                        text_state.setText(getString(R.string.string91));
//                    } else if (state == state.AUTHENTICATING) {
//                        text_state.setText(getString(R.string.string98));
//                    } else if (state == state.OBTAINING_IPADDR) {
//                        text_state.setText(getString(R.string.string99));
//                    } else if (state == state.FAILED) {
//                        text_state.setText(getString(R.string.string100));
//                    }
//                }
//
//            }
//           /* else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//                if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
//                    text_state.setText("连接已断开");
//                    wifiManager.removeNetwork(wcgID);
//                } else {
//                    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                    text_state.setText("已连接到网络:" + wifiInfo.getSSID());
//                }
//            }*/
//        }
//    };

    public static void removeWifiBySsid(WifiManager wifiManager, String targetSsid) {
        Log.d(TAG, "try to removeWifiBySsid, targetSsid=" + targetSsid);
        List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfig : wifiConfigs) {
            String ssid = wifiConfig.SSID;
            Log.d(TAG, "removeWifiBySsid ssid=" + ssid);
            if (ssid.equals(targetSsid)) {
                Log.d(TAG, "removeWifiBySsid success, SSID = " + wifiConfig.SSID + " netId = " + String.valueOf(wifiConfig.networkId));
                wifiManager.removeNetwork(wifiConfig.networkId);
                wifiManager.saveConfiguration();
            }
        }
    }




    /**
     * 判断当前wifi是否有保存
     *
     * @param SSID
     * @return
     */
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public WifiConfiguration createWifiInfo(String SSID, String password,
                                            int type) {
        Log.w("AAA", "SSID = " + SSID + "password " + password + "type ="
                + type);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (type == WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
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




    //广播
    class WifiChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiManager = (WifiManager) getApplicationContext().
                    getSystemService(Context.WIFI_SERVICE);
            System.out.print("你好啊   我把WiFi状态改变了");
            int state = wifiManager.getWifiState();
            if(state == WifiManager.WIFI_STATE_ENABLED || state==WifiManager.WIFI_STATE_ENABLING){
                sp.saveWifiState(true);
            }else {
                sp.saveWifiState(false);
            }

        }
    }
}
