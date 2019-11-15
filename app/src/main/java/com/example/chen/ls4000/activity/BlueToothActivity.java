package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.adapter.BlueListAdapter;
import com.example.chen.ls4000.bean.BlueDevice;
import com.example.chen.ls4000.service.BlueToothService;
import com.example.chen.ls4000.task.BlueAcceptTask;
import com.example.chen.ls4000.task.BlueConnectTask;
import com.example.chen.ls4000.task.BlueReceiveTask;
import com.example.chen.ls4000.utils.BluetoothUtil;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.widget.InputDialogFragment;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlueToothActivity extends Activity implements
        View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener,
        BlueConnectTask.BlueConnectListener, InputDialogFragment.InputCallbacks, BlueAcceptTask.BlueAcceptListener {
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothActivity";
    private CheckBox ck_bluetooth;
    private TextView tv_discovery;
    private ImageView back;
    private ListView lv_bluetooth;
    private BluetoothAdapter mBluetooth;
    private ArrayList<BlueDevice> mDeviceList =new ArrayList<BlueDevice>();
    private MyApp myApp;
    private boolean flag;
    private  BlueListAdapter adapter;
    private SharedHelper sh;
    private RelativeLayout availableLayout;
    private TextView available;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        bluetoothPermissions();
        sh  = new SharedHelper(this);
        ck_bluetooth = (CheckBox) findViewById(R.id.id_bluetooth_switch);
        tv_discovery = (TextView) findViewById(R.id.id_bluetooth_tv1);
        lv_bluetooth = (ListView) findViewById(R.id.lv_bluetooth);
        availableLayout = (RelativeLayout) findViewById(R.id.id_bluetooth_layout);
        available = (TextView) findViewById(R.id.id_bluetooth_available);
        back = (ImageView) findViewById(R.id.id_bluetooth_back);
        if (BluetoothUtil.getBlueToothStatus(this) ) {
            ck_bluetooth.setChecked(true);
        }
        ck_bluetooth.setOnCheckedChangeListener(this);
        tv_discovery.setOnClickListener(this);
        back.setOnClickListener(this);
        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        adapter=new BlueListAdapter(BlueToothActivity.this,mDeviceList);
        lv_bluetooth.setAdapter(adapter);
        if (mBluetooth == null) {
            showToast(this, getString(R.string.string14), 1000);
            return;
        }
        myApp = (MyApp) getApplication();

        if("0".equals(sh.readUpdateFlag())) {
            if (ck_bluetooth.isChecked()) {
                tv_discovery.setText(getString(R.string.string22));
                mBluetooth.startDiscovery();
            } else {
                tv_discovery.setText(getString(R.string.string23));
                mBluetooth.cancelDiscovery();
            }
            available.setVisibility(View.GONE);
            availableLayout.setVisibility(View.GONE);
        }else{
            //tv_discovery.setText("已连接到蓝牙打印机");
            available.setVisibility(View.VISIBLE);
            availableLayout.setVisibility(View.VISIBLE);
        }
//        if(myApp.getPrintState()>0){
//            ck_bluetooth.setChecked(true);
//        }else{
//            ck_bluetooth.setChecked(false);
//        }

//            ck_bluetooth.setChecked(sh.readBluetooth());

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


    // 定义获取基于地理位置的动态权限
    private void bluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    /**
     * 重写onRequestPermissionsResult方法
     * 获取动态权限请求的结果,再开启蓝牙
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (BluetoothUtil.getBlueToothStatus(this) ) {
                ck_bluetooth.setChecked(true);
            }else{
                ck_bluetooth.setChecked(false);
            }
            ck_bluetooth.setOnCheckedChangeListener(this);
            tv_discovery.setOnClickListener(this);
            mBluetooth = BluetoothAdapter.getDefaultAdapter();
            if (mBluetooth == null) {
                showToast(this, getString(R.string.string14), 1000);
                return;
            }
        } else {
            showToast(this, getString(R.string.string15), 1000);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.id_bluetooth_switch) {
            if (isChecked) {
                beginDiscovery();
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                startActivityForResult(intent, 1);
                tv_discovery.setText(getString(R.string.string22));
                // 下面这行代码为服务端需要，客户端不需要
                mHandler.postDelayed(mAccept, 1000);

            } else {

                cancelDiscovery();
                BluetoothUtil.setBlueToothStatus(this, false);
                tv_discovery.setText(getString(R.string.string23));
                mDeviceList.clear();
                adapter = new BlueListAdapter(this,mDeviceList);
                lv_bluetooth.setAdapter(adapter);
                sh.saveBluetoothState(false);
                //sh.saveUpdateFlag("0");
                available.setVisibility(View.GONE);
                availableLayout.setVisibility(View.GONE);
            }
            //ck_bluetooth.setChecked(BluetoothUtil.getBlueToothStatus(this));
        }
    }

    private Runnable mAccept = new Runnable() {
        @Override
        public void run() {
            if (mBluetooth.getState() == BluetoothAdapter.STATE_ON) {
                BlueAcceptTask acceptTask = new BlueAcceptTask(true);
                acceptTask.setBlueAcceptListener(BlueToothActivity.this);
                acceptTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_bluetooth_tv1) {
            beginDiscovery();
        }else if(v.getId() == R.id.id_bluetooth_back){
            sh.saveBluetooth(ck_bluetooth.isChecked());
            finish();
        }else if(v.getId() == R.id.id_bluetooth_switch){
            if(ck_bluetooth.isChecked()){
                beginDiscovery();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                showToast(this, getString(R.string.string20), 1000);
            } else if (resultCode == RESULT_CANCELED) {
                ck_bluetooth.setChecked(false);
                showToast(this, getString(R.string.string21), 1000);
            }
        }
    }

    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            beginDiscovery();
            mHandler.postDelayed(this, 2000);
        }
    };

    private void beginDiscovery() {
        if (!mBluetooth.isDiscovering()) {
            mDeviceList.clear();
            BlueListAdapter adapter=  new BlueListAdapter(BlueToothActivity.this, mDeviceList);
            lv_bluetooth.setAdapter(adapter);
            if(ck_bluetooth.isChecked()){
                tv_discovery.setText(getString(R.string.string22));
                mBluetooth.startDiscovery();
            }else{
                tv_discovery.setText(getString(R.string.string23));
                mBluetooth.cancelDiscovery();
            }

        }

    }


    private void cancelDiscovery() {
        mHandler.removeCallbacks(mRefresh);     //刷新
        tv_discovery.setText(getString(R.string.string23));
        if (mBluetooth.isDiscovering() ) {
            mBluetooth.cancelDiscovery();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(sh.readBluetoothState()) {
            mHandler.postDelayed(mRefresh, 5000);  // 刷新蓝牙设备列表时间
        }
        blueReceiver = new BluetoothReceiver();
        //需要过滤多个动作，则调用IntentFilter对象的addAction添加新动作
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter foundFilter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        foundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        foundFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        foundFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        if(!flag) {
            registerReceiver(blueReceiver, foundFilter);
            flag = true;
        }
        if(!flag) {
            registerReceiver(blueReceiver, foundFilter2);
            flag = true;
        }
        registerReceiver(blueReceiver, foundFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mBluetooth.isDiscovering()){     //如果正在处于扫描过程...
            mBluetooth.cancelDiscovery();      //取消扫描...
        }else{
            mDeviceList.clear();
            adapter.notifyDataSetChanged();

              /* 开始搜索 */
            mBluetooth.startDiscovery();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        cancelDiscovery();
        unregisterReceiver(blueReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(flag) {
//            unregisterReceiver(blueReceiver);
//            flag = false;
//        }
    }

    private BluetoothReceiver blueReceiver;

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent){
                return;
            }
            String action = intent.getAction();
            if(TextUtils.isEmpty(action)){  //如果字符串为null或0长度，则返回true。
                return;
            }
            Log.d(TAG, "onReceive action=" + action);
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BlueDevice item = new BlueDevice(device.getName(), device.getAddress(), device.getBondState()-10);
                mDeviceList.add(item);
                adapter = new BlueListAdapter(BlueToothActivity.this,mDeviceList);
                lv_bluetooth.setAdapter(adapter);
                lv_bluetooth.setOnItemClickListener(BlueToothActivity.this);
                adapter.notifyDataSetChanged();

            }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                tv_discovery.setText(getString(R.string.string24));
                cancelDiscovery();

            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    tv_discovery.setText(getString(R.string.string25) + device.getName());
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    mHandler.postDelayed(mRefresh, 5000);
                    //信号强度
                    short rssi =intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                    tv_discovery.setText(getString(R.string.string26) + device.getName());
                    lv_bluetooth.setAdapter(adapter);
                    beginDiscovery();

                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    tv_discovery.setText(getString(R.string.string27) + device.getName());
                }
            }else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
              //  boolean flag = sh.readBluetoothState();
//                if(sh.readBluetoothState()){
                cancelDiscovery();
                //tv_discovery.setText("已连接到蓝牙打印机");
                available.setVisibility(View.VISIBLE);
                availableLayout.setVisibility(View.VISIBLE);
                sh.saveUpdateFlag("1");

            //    }
            }else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                tv_discovery.setText(getString(R.string.string22));
                //ViewUtils.showToast(name + "的连接被断开", getApplicationContext());
                //Toast.makeText(BlueToothService.this, "蓝牙连接123已断开！", Toast.LENGTH_LONG).show();
                mBluetooth.startDiscovery();
                //sh.saveBluetoothState(false);
                sh.saveUpdateFlag("0");
                available.setVisibility(View.GONE);
                availableLayout.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cancelDiscovery();
        BlueDevice item = mDeviceList.get(position);
        BluetoothDevice device = mBluetooth.getRemoteDevice(item.address);
        int connectState = device.getBondState();
        int deviceType = device.getBluetoothClass().getMajorDeviceClass();
        try {
            if(connectState == BluetoothDevice.BOND_NONE) {
                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                Boolean result = (Boolean) createBondMethod.invoke(device);
            }else {
                //progressDialog = ProgressDialog.show(BlueToothActivity.this, "请稍等...", "蓝牙设备连接中...", true);
                if (deviceType == 7936 || deviceType == 1536) {
                    //stopService(new Intent(BlueToothActivity.this, BlueToothService.class));
                    List<BluetoothDevice> printerDevices = BluetoothUtil.getPairedDevices();
                    if (printerDevices.size() > 0) {
                        //连接蓝牙打印机
                        Intent intent = new Intent(BlueToothActivity.this, BlueToothService.class);
                        Bundle bundle = new Bundle();
                        item = new BlueDevice(device.getName(), device.getAddress(), device.getBondState() - 10);
                        mDeviceList.add(item);
                        lv_bluetooth.setAdapter(adapter);
                        mDeviceList.remove(position);
                        adapter.notifyDataSetChanged();
                        for (int i = 0; i < printerDevices.size(); i++) {
                            if ("T12 BT Printer".equals(printerDevices.get(i).getName())) {//("RG-MTP58B".equals(printerDevices.get(i).getName())) {
                                BluetoothDevice bluetoothDevice = printerDevices.get(i);
                                bundle.putParcelable("bluedevice", bluetoothDevice);
                                intent.putExtras(bundle);
                                //startService(intent);
                                break;
                            }
                        }
                    } else {
                        item = new BlueDevice(device.getName(), device.getAddress(), device.getBondState() - 9);
                        mDeviceList.add(item);
                        lv_bluetooth.setAdapter(adapter);
                        mDeviceList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            tv_discovery.setText(getString(R.string.string28) + e.getMessage());
        }
    }

    //向对方发送消息
    @Override
    public void onInput(String title, String message, int type) {
        Log.d(TAG, "onInput message=" + message);
        Log.d(TAG, "mBlueSocket is " + (mBlueSocket == null ? "null" : "not null"));
        BluetoothUtil.writeOutputStream(mBlueSocket, message);
    }

    private BluetoothSocket mBlueSocket;

    //客户端主动连接
    @Override
    public void onBlueConnect(String address, BluetoothSocket socket) {
        mBlueSocket = socket;
        tv_discovery.setText(getString(R.string.string29));
        refreshAddress(address);
    }

    //刷新已连接的状态
    private void refreshAddress(String address) {
        for (int i = 0; i < mDeviceList.size(); i++) {
            BlueDevice item = mDeviceList.get(i);
            if (item.address.equals(address) == true) {
                item.state = BlueListAdapter.CONNECTED;
                mDeviceList.set(i, item);
            }
        }
        adapter = new BlueListAdapter(this, mDeviceList);
        lv_bluetooth.setAdapter(adapter);
    }

    //服务端侦听到连接
    @Override
    public void onBlueAccept(BluetoothSocket socket) {
        Log.d(TAG, "onBlueAccept socket is " + (socket == null ? "null" : "not null"));
        if (socket != null) {
            mBlueSocket = socket;
            BluetoothDevice device = mBlueSocket.getRemoteDevice();
            refreshAddress(device.getAddress());
            BlueReceiveTask receive = new BlueReceiveTask(mBlueSocket, mHandler);
            receive.start();
        }
    }

    //收到对方发来的消息
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, "handleMessage readMessage=" + readMessage);
                AlertDialog.Builder builder = new AlertDialog.Builder(BlueToothActivity.this);
                builder.setTitle("我收到消息啦").setMessage(readMessage).setPositiveButton("确定", null);
                builder.create().show();

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(flag) {
//            unregisterReceiver(blueReceiver);
//            flag = false;
//        }
        if (mBlueSocket != null) {
            try {
                mBlueSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
