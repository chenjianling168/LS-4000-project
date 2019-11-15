package com.example.chen.ls4000.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.chen.ls4000.bean.CharacteristicsList;
import com.example.chen.ls4000.bean.DeviceList;
import com.example.chen.ls4000.bean.ServicesList;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.event.bleResponse;
import com.example.chen.ls4000.utils.BleWrapper;
import com.example.chen.ls4000.utils.BleWrapperUiCallbacks;
import com.example.chen.ls4000.utils.CrcOperateUtil;
import com.example.chen.ls4000.utils.SharedHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

public class BLEService extends Service implements BleWrapperUiCallbacks {
    //扫描时间
    private static final long SCANNING_TIMEOUT = 3 * 1000; /* 5 seconds */
    //扫描状态标识
    private boolean mScanning = false;
    //消息通知
    private Handler mHandler = new Handler();

    private DeviceList mDeviceList;
    private ServicesList servicesList;
    private CharacteristicsList characteristicsList;

    public static final String ACTION_REBOOT =
            "android.intent.action.REBOOT";
    public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";


    //ble工具集合
    private BleWrapper mBleWrapper = null;

    //每个Characteristic的value，也就是程序接受到的数据
    //通过扫描设备可以调用到uiCharacteristicsDetails，然后requestCharacteristicValue（请求数据）
    //用于通讯的Characteristic
    BluetoothGattCharacteristic RWbluetoothGattCharacteristic;

    boolean connected = false;
    boolean haveCharacteristic = false;
    private byte[] bytes;
    private Timer timer;

    public BLEService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        mBleWrapper = new BleWrapper(this, this);

        // 检查一下船上是否有BT和BLE
        if (mBleWrapper.checkBleHardwareAvailable() == false) {
            EventBus.getDefault().post(new bleResponse(-1));
            return;
        }

//        PowerConnectionReceiver batteryReceiver = new PowerConnectionReceiver();
//        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        Intent batteryStatus = this.registerReceiver(null, ifilter);
        this.registerReceiver(this.mBatteryReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        context = getApplicationContext();
        sp = new SharedHelper(context);

        timer = new Timer();
        //setTimerTask();
    }

    private SharedHelper sp;
    private Context context;

//    private void setTimerTask(){
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                for (int i = 1; i < 9; i++) {
//                    if(sp.readTime(i)>0) {
//                        sp.saveStatus("正在检测",sp.readTime(i)-1,i,sp.readPro(i));
//                    }
//                }
//            }
//        },0,1000);
//    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (mBleWrapper == null) mBleWrapper = new BleWrapper(this, this);
        // on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
        if (mBleWrapper.isBtEnabled() == false) {
            EventBus.getDefault().post(new bleResponse(-1));
            return;
        }

        // initialize BleWrapper object
        mBleWrapper.initialize();

        mDeviceList = new DeviceList();
        servicesList = new ServicesList();
        characteristicsList = new CharacteristicsList();
        // Automatically start scanning for devices
        mScanning = true;
        // remember to add timeout for scanning to not run it forever and drain the battery
        addScanningTimeout();
        mBleWrapper.startScanning();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //根据扫描结果更新设备列表
    /* add device to the current list of devices */
    private void handleFoundDevice(final BluetoothDevice device,
                                   final int rssi,
                                   final byte[] scanRecord) {
        System.out.println("数据根据扫描结果更新列表");
        mDeviceList.add(device, scanRecord, rssi);
        EventBus.getDefault().post(device);
        Log.e("get device", device.getAddress() + ":" + device.getName() + "\n rssi:" + rssi + "\n" + scanRecord.toString());
    }


    //超时关闭扫描
    private void addScanningTimeout() {
        Runnable timeout = new Runnable() {
            @Override
            public void run() {
                closeScan();
            }
        };
        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
    }

    private void closeScan() {

        //关闭扫描
        if (mBleWrapper == null) return;
        if (mScanning) {
            mScanning = false;
            mBleWrapper.stopScanning();
            EventBus.getDefault().post(new bleResponse(0));
        }
    }

    private String connectAddress;


    //链接蓝牙设备
    /* user has selected one of the device */
    protected void connectBLE(String address) {
        if ( !mBleWrapper.connect(address)) {
            System.out.println("连接蓝牙3。。。。。。。。。。。。。。。数据");
            EventBus.getDefault().post(new bleResponse(3));
        }
        System.out.println("连接蓝牙4。。。。。。。。。。。。。。。数据");
        closeScan();
    }


    @Override
    public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
        handleFoundDevice(device, rssi, record);//找到指定设备并连接
    }

    @Override
    public void uiDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {
        connected = true;
        EventBus.getDefault().post(new bleResponse(1));

    }

    @Override
    public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {
        connected = false;
        EventBus.getDefault().post(new bleResponse(2));
    }

    //连接设备后调用，可用的服务
    @Override
    public void uiAvailableServices(BluetoothGatt gatt, BluetoothDevice device, List<BluetoothGattService> services) {
        for (BluetoothGattService service : mBleWrapper.getCachedServices()) {
            servicesList.addBleService(service);
            //mBleWrapper.getCharacteristicsForService(mBleWrapper.getCachedService());
            mBleWrapper.getCharacteristicsForService(service);
            Log.e("bluetooth gatt", service.getUuid().toString());
        }

    }

    //每个服务可用的Characteristic
    @Override
    public void uiCharacteristicForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, List<BluetoothGattCharacteristic> chars) {
        for (BluetoothGattCharacteristic ch : chars) {
            characteristicsList.addBleGattCharacteristic(ch);
            uiCharacteristicsDetails(mBleWrapper.getGatt(), mBleWrapper.getDevice(), mBleWrapper.getCachedService(), ch);
            Log.e("characteristic list", ch.getUuid().toString());
        }
        if (haveCharacteristic) {
            EventBus.getDefault().post(new bleResponse(7));
        } else {
            EventBus.getDefault().post(new bleResponse(8));
        }
    }

    @Override
    public void uiGotNotification(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        Log.e("uiGotNotification", characteristic.getUuid().toString());
    }

    //获取RSSI，本程序里不需要
    @Override
    public void uiNewRssiAvailable(BluetoothGatt gatt, BluetoothDevice device, int rssi) {
        Log.e("rssi", device.getName() + ":" + device.getAddress() + "::" + rssi);
        EventBus.getDefault().post(new bleResponse(6, rssi));
    }




    @Override
    public void uiCharacteristicsDetails(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {
        //characteristic用于通讯
        Log.e("characteristic detail", characteristic.getUuid().toString());
        UUID uuid = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
        //00001543-0000-3512-2118-0009af100700
        //UUID uuid2 = UUID.fromString("00001543-0000-3512-2118-0009af100700");
        if (characteristic.getUuid().equals(uuid)) {
            RWbluetoothGattCharacteristic = characteristic;
            mBleWrapper.requestCharacteristicValue(characteristic);
            mBleWrapper.setNotificationForCharacteristic(characteristic, true);
            haveCharacteristic = true;
        }
    }

    //写操作 成功
    @Override
    public void uiSuccessfulWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String description) {
        Log.e("write success", ch.getUuid().toString() + description);
        EventBus.getDefault().post(new bleResponse(4));
    }

    //写操作 失败
    @Override
    public void uiFailedWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String description) {
        Log.e("write failed", ch.getUuid().toString() + description);
        System.out.println("数据写操作失败");
        EventBus.getDefault().post(new bleResponse(5));
    }

    /*
    *
    * 这里的strValue就是读取的数据
    *
     */
    @Override
    public void uiNewValueForCharacteristic(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String strValue, int intValue, byte[] rawValue, String timestamp) {
        Log.e("value", strValue);
       // Toast.makeText(getApplicationContext(),"get: "+strValue,Toast.LENGTH_SHORT).show();
        bytes = ch.getValue();
        //float fl = ArryToFloat(bytes,8);
        String res = new String(bytes);
//        if(res.contains("R=")){
//            res = res.substring(0,res.length()-6)+"."+res.substring(res.length()-6,res.length());
//        }
        System.out.println("读取到的数据是1#"+new String(bytes));
        //System.out.println("十六进制数是"+bytesToHexString(bytes));
        if(testCheckCRC(bytes)){
            //校验成功，截取数据
            System.out.println("校验成功");
        }else{
            //校验失败
            System.out.println("校验失败");
        }
       // System.out.println("读取到的数据是"+strValue);
        EventBus.getDefault().post(new BleReadEvent(res));
    }

    public static float getFloat(byte[] b) {
        System.out.println("到1");
        int accum = 0;
        System.out.println("到1");
        accum = accum|(b[0] & 0xff) << 0;
        System.out.println("到1");
        accum = accum|(b[1] & 0xff) << 8;
        System.out.println("到1");
        accum = accum|(b[2] & 0xff) << 16;
        System.out.println("到1");
        accum = accum|(b[3] & 0xff) << 24;
        System.out.println("到1");
        System.out.println(accum+"到");
        System.out.println("到1");
        return Float.intBitsToFloat(accum);
    }



    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public void write(String mes) {
        Log.e("write", mes);
        System.out.println("读取到的数据是2"+mes);
        if (mBleWrapper == null || !connected || RWbluetoothGattCharacteristic == null) return;
        // byte[] dataToWrite = parseHexStringToBytes(mes);
        //byte[] dataToWrite = parseHexStringToBytes("0x520d0a");  //昨天成功的是这一句
        byte[] dataToWrite = parseString2Bytes(mes);//改写的函数，将R\r\n 直接转换为0x52 0x0D 0x0A
//        String ss = "李四";
//        byte[] bys = null;
//        try {
//             bys = ss.getBytes("gb2312");
//            for(int i = 0;i<bys.length;i++){
//                System.out.println("数据是"+Integer.toHexString(bys[i]));
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        for(int i = 0;i<3;i++){
//            byte[] bys = null;
//            System.arraycopy(dataToWrite,0+i*16,bys,0+i*16,16);
//            mBleWrapper.writeDataToCharacteristic(RWbluetoothGattCharacteristic, dataToWrite);
//        }
        for (byte b : dataToWrite) {
            Log.e("write byte test", b + ":" + (char) b + ": " + byte2HexStr(dataToWrite));
        }
        System.out.println("数据是长度"+dataToWrite.length);
      //  Toast.makeText(getApplicationContext(),"send:"+byte2HexStr(dataToWrite),Toast.LENGTH_SHORT);
//        mBleWrapper.writeDataToCharacteristic(RWbluetoothGattCharacteristic, dataToWrite);
//        for (byte b : bys) {
//            Log.e("write byte test", b + ":" + (char) b + ": " + byte2HexStr(bys));
//        }
//        //  Toast.makeText(getApplicationContext(),"send:"+byte2HexStr(dataToWrite),Toast.LENGTH_SHORT);
//        int i = dataToWrite.length / 20;
//        fori
//        while (dataToWrite.length >= 20){
//            byte[] data = new byte[20];
//            System.arraycopy(dataToWrite,(i*20),data,0,20);
//            System.out.println("数据data是长度"+data.length);
//            mBleWrapper.writeDataToCharacteristic(RWbluetoothGattCharacteristic, data);
////            System.arraycopy(dataToWrite,20,data,0,dataToWrite.length-20);
////            System.arraycopy(data,0,dataToWrite,0,data.length);
//           // System.arraycopy(dataToWrite,20,data,0,dataToWrite.length-20);
//            byte[] data1 = new byte[dataToWrite.length-20];
//            System.arraycopy(dataToWrite,20,data1,0,dataToWrite.length-20);
//            dataToWrite = new byte[]{};
//            System.arraycopy(data1,20,dataToWrite,0,data1.length-20);
//            System.out.println("数据datatowrite是长度"+dataToWrite.length);
//        }
       mBleWrapper.writeDataToCharacteristic(RWbluetoothGattCharacteristic, dataToWrite);

    }


    public static byte[] parseString2Bytes(String mes) {
        System.out.println("BLEService收到的数据是"+mes.length());
        //mes += "\r\n";//\r\n 返回 0d0a 也就是13 10

        byte[] bytes = new byte[1000];
        try {
            bytes = mes.getBytes("gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println("shuju数据"+bytes.length);
        for (byte b : bytes) {
            Log.e("parseString2Bytes", b + ":" + (char) b + ": " + byte2HexStr(bytes));
        }
        return bytes;
    }

    //将string转换为16进制字符串
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    //将十六进制字符串转换为bytes
    public byte[] parseHexStringToBytes(final String hex) {
        String tmp = hex.substring(2).replaceAll("[^[0-9][a-f]]", "");
        byte[] bytes = new byte[tmp.length() / 2]; // every two letters in the string are one byte finally

        String part = "";

        for (int i = 0; i < bytes.length; ++i) {
            part = "0x" + tmp.substring(i * 2, i * 2 + 2);
            bytes[i] = Long.decode(part).byteValue();
        }

        return bytes;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleMethodCode response) {
        switch (response.getCode()) {
            case -1:
                //mBleWrapper.diconnect();
                mBleWrapper.close();
                Log.e("do here", "close service");
                System.out.println("断开蓝牙数据");
                this.stopSelf();
                break;
            case 0://断开蓝牙
                if (mBleWrapper != null && connected) {
                    mBleWrapper.diconnect();
                    Toast.makeText(getApplicationContext(), "断开连接" + connected, Toast.LENGTH_SHORT).show();
                }
                break;
            case 1://连接蓝牙
                if (mBleWrapper != null && !connected && connectAddress != null)
                    mBleWrapper.connect(connectAddress);
                break;
            case 2://读

                break;
            case 3://写
                write(response.getMes());
                break;
            case 4://扫描蓝牙设备
                if (mScanning) closeScan();
                // Automatically start scanning for devices
                mScanning = true;
                // remember to add timeout for scanning to not run it forever and drain the battery
                addScanningTimeout();
                mBleWrapper.startScanning();
                break;
            case 5://连接指定设备
                System.out.println("连接蓝牙。。。。。。。。。。。。。。。数据");
                if (connected) {
                    System.out.println("连接蓝牙1。。。。。。。。。。。。。。。数据");
                    mBleWrapper.diconnect();
                }
                System.out.println("连接蓝牙2。。。。。。。。。。。。。。。数据");
                connectBLE(response.getMes());

                break;
            case 6://获取连接信息
                EventBus.getDefault().post(new bleResponse(9,connected,haveCharacteristic));
                break;
        }
    }

    /***
     * 测试为数组添加CRC校验
     */
    public byte[] testAddCRC(byte[] data)
    {
//        byte[] id = HexCompressWrap.packHexCompress("123456", 3);
//
//        byte[] number = HexCompressWrap.packHexCompress("12345678", 4);
//
//        byte[] seqID = HexCompressWrap.packHexCompress("2016040506", 5);

        // 最终提交至服务器的byte数据，未添加crc校验
        //byte[] submit = CrcOperateUtil.concatAll(id, number, seqID);
        byte[] submit = {};
        System.arraycopy(data,0,submit,0,data.length-2);

        // 给submit数组添加两位CRC校验
        submit = CrcOperateUtil.setParamCRC(submit);

        return submit;
    }

    /**
     * 对传输后数组进行CRC校验
     */
    public boolean testCheckCRC(byte[] data)
    {
        // 获取接收到的crc校验数组
        //byte[] reByte = testAddCRC(data);

        if (CrcOperateUtil.isPassCRC(data, 2))
        {
            return true;
        }
            return false;
    }

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int voltage = arg1.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            int level = arg1.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = arg1.getIntExtra(BatteryManager.EXTRA_SCALE, 0);

            int status = arg1.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            String strStatus = "";
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    strStatus = "充电中……";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    strStatus = "放电中……";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    strStatus = "未充电";

//                    Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
//                    intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);

                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    strStatus = "充电完成";
                    break;
            }
            //Toast.makeText(arg0,strStatus+"",Toast.LENGTH_LONG).show();
        }
    };

}
