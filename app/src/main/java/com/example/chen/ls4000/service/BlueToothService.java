package com.example.chen.ls4000.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.Instrument;
import com.example.chen.ls4000.bean.Sample;
import com.example.chen.ls4000.event.BleEvent;
import com.example.chen.ls4000.utils.BluetoothUtil;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.PrintUtil;
import com.example.chen.ls4000.utils.SharedHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.reflect.Method;

public class BlueToothService extends Service {


    private BluetoothSocket mSocket;
    private BluetoothStateReceiver mBluetoothStateReceiver;
    private AsyncTask mConnectTask;
    private ProgressDialog mProgressDialog;
    private SharedHelper sp;
    private MyApp myApp;
    private BluetoothAdapter bluetoothAdapter;

    public BlueToothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initReceiver();
        EventBus.getDefault().register(this);
        sp = new SharedHelper(getApplicationContext());
        myApp = new MyApp();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        try {
//            Bundle bundle = intent.getExtras();
//            bundle.setClassLoader(BlueToothService.class.getClassLoader());
//            BluetoothDevice device = bundle.getParcelable("bluedevice");
//            connectDevice(device,1);
            boolean fl = sp.readBluetoothState();
            bluetoothAdapter.startDiscovery();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void closeSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                mSocket = null;
                e.printStackTrace();
            }
        }
    }

    protected void cancelConnectTask() {
        if (mConnectTask != null) {
            mConnectTask.cancel(true);
            mConnectTask = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelConnectTask();
        closeSocket();
        unregisterReceiver(mBluetoothStateReceiver);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleEvent response) {
        //这里改数据
        Sample sample = response.getSample();
        Instrument instrument = response.getInstrument();
        String ss = "";
        if(sp.readHosPrint()){  //医院名称
            ss += "1";
        }else{
            ss += "0";
        }
        if(sp.readDepartPrint()){   //科室名称
            ss += "1";
        }else{
            ss += "0";
        }

        if(sp.readDocPrint()){   //医生名称
            ss += "1";
        }else{
            ss += "0";
        }

        if(sp.readAutoPrint()){   //审核人
            ss += "1";
        }else{
            ss += "0";
        }
//        if(sp.readDatePrint()){
//            ss += "1";
//        }else{
//            ss += "0";
//        }
//        if(sp.readTimePrint()){
//            ss += "1";
//        }else{
//            ss += "0";
//        }
//
//        if(sp.readSamnumPrint()){
//            ss += "1";
//        }else{
//            ss += "0";
//        }
        PrintUtil.printTest(this,mSocket,sample,ss);
        //PrintUtil.printInstrument(this,mSocket,instrument,ss);
    }

    private void initReceiver() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothStateReceiver = new BluetoothStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(mBluetoothStateReceiver, filter);
    }

    /**
     * 检查蓝牙状态，如果已打开，则查找已绑定设备
     *
     * @return
     */
    public boolean checkBluetoothState() {
        if (BluetoothUtil.isBluetoothOn()) {
            return true;
        } else {
            //BluetoothUtil.openBluetooth(this);
            return false;
        }
    }

    public void connectDevice(BluetoothDevice device, int taskType) {
        if (checkBluetoothState() && device != null) {
            mConnectTask = new ConnectBluetoothTask(taskType).execute(device);
        }
    }


    class ConnectBluetoothTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {

        int mTaskType;

        public ConnectBluetoothTask(int taskType) {
            this.mTaskType = taskType;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog(getString(R.string.string218));
            super.onPreExecute();
        }

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... params) {
            if(mSocket != null){
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mSocket = BluetoothUtil.connectDevice(params[0]);
            //   onConnected(mSocket, mTaskType);
            // PrintUtil.printTest(mSocket);
            return mSocket;
        }

        @Override
        protected void onPostExecute(BluetoothSocket socket) {
            mProgressDialog.dismiss();
            if (socket == null || !socket.isConnected()) {
                //sp.saveBluetoothState(false);
                toast(getString(R.string.string216));
                sp.saveUpdateFlag("0");

            } else {
                toast(getString(R.string.string217));
                sp.saveUpdateFlag("1");
                bluetoothAdapter.cancelDiscovery();
                //sp.saveBluetoothState(true);
            }

            super.onPostExecute(socket);
        }
    }

//    private void connectDevice(int taskType){
//        if(mSelectedPosition >= 0){
//            BluetoothDevice device = mAdapter.getItem(mSelectedPosition);
//            if(device!= null)
//                super.connectDevice(device, taskType);
//        }else{
//            Toast.makeText(this, "还未选择打印设备", Toast.LENGTH_SHORT).show();
//        }
//    }

    protected void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(BlueToothService.this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mProgressDialog.show();
        }
    }

    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 监听蓝牙状态变化的系统广播
     */
    class BluetoothStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    //toast("蓝牙已开启");
                    if(!bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.startDiscovery();
                    }
                    sp.saveBluetoothState(true);
                    break;

                case BluetoothAdapter.STATE_TURNING_OFF:
                    //toast("蓝牙已关闭");
                    if(bluetoothAdapter.isDiscovering()){
                        bluetoothAdapter.cancelDiscovery();
                    }
                    sp.saveBluetoothState(false);
                    break;
                case BluetoothAdapter.STATE_OFF:
            }
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                //ViewUtils.showToast(name + "的连接被断开", getApplicationContext());
                //Toast.makeText(BlueToothService.this, "蓝牙连接123已断开！", Toast.LENGTH_LONG).show();
                bluetoothAdapter.startDiscovery();
                //sp.saveBluetoothState(false);
                sp.saveUpdateFlag("0");
            } else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                //Toast.makeText(BlueToothService.this, "蓝牙连接123已连接！", Toast.LENGTH_LONG).show();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获取查找到的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println(device.getName());
                // 如果查找到的设备符合要连接的设备，处理
                if ("T12 BT Printer".equalsIgnoreCase(device.getName())) {
                    // 搜索蓝牙设备的过程占用资源比较多，一旦找到需要连接的设备后需要及时关闭搜索
                    bluetoothAdapter.cancelDiscovery();
                    // 获取蓝牙设备的连接状态
                    int connectState = device.getBondState();
                    switch (connectState) {
                        // 未配对
                        case BluetoothDevice.BOND_NONE:
                            // 配对
//                            if(sp.readBluetoothState()) {
//                                try {
//                                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
//                                    createBondMethod.invoke(device);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
                            break;
                        // 已配对
                        case BluetoothDevice.BOND_BONDED:
                            //try {
                            // 连接
                            //if(sp.readBluetoothState()) {
                                connectDevice(device, 1);
                           // }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                            break;
                    }
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                // 状态改变的广播
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if ("T12 BT Printer".equalsIgnoreCase(device.getName())) {
                    int connectState = device.getBondState();
                    switch (connectState) {
                        case BluetoothDevice.BOND_NONE:
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            //try {
                            // 连接
                            connectDevice(device, 1);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                            break;
                    }
                }
            }

            //onBluetoothStateChanged(intent);
        }
    }
}
