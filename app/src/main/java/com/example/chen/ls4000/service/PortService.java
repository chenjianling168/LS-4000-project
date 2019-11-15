package com.example.chen.ls4000.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.chen.ls4000.ComAssistant.SerialHelper;
import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.AssistBean;
import com.example.chen.ls4000.bean.ComBean;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.event.BleReadEvent;
import com.example.chen.ls4000.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class PortService extends Service  {

    private SerialControl myCom;
    private DispQueueThread dispQueue;
    private AssistBean assistBean;
    StringBuilder sMsg;
    byte[] bys;
    int flag;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        myCom = new SerialControl();
        dispQueue = new DispQueueThread();
        assistBean = new AssistBean();
        myCom.setPort("/dev/ttyMT1"); //myCom.setPort("/dev/ttyS3");
        myCom.setBaudRate(9600);
        openComPort(myCom);

        sMsg=new StringBuilder();
        bys = new byte[1500];
        flag = 0;

//        SerialPortFinder mSerialPortFinder= new SerialPortFinder();
//        String[] entryValues = mSerialPortFinder.getAllDevicesPath();
//        List<String> allDevices = new ArrayList<String>();
//        for (int i = 0; i < entryValues.length; i++) {
//            allDevices.add(entryValues[i]);
//            System.out.println("串口数据"+entryValues[i]);
//        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeComPort(myCom);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BleMethodCode response) {
        sendPortData(myCom,response.getMes(),response.getBytes(),response.getComm());
    }

    public void write(String mes) {
        Log.e("write", mes);
        System.out.println("读取到的数据是2"+mes);
        if (!myCom.isOpen()) return;
        // byte[] dataToWrite = parseHexStringToBytes(mes);
        //byte[] dataToWrite = parseHexStringToBytes("0x520d0a");  //昨天成功的是这一句
        byte[] dataToWrite = parseString2Bytes(mes);//改写的函数，将R\r\n 直接转换为0x52 0x0D 0x0A

        for (byte b : dataToWrite) {
            Log.e("write byte test", b + ":" + (char) b + ": " + byte2HexStr(dataToWrite));
        }
        System.out.println("数据是长度"+dataToWrite.length);

    }

    public static byte[] parseString2Bytes(String mes) {
        System.out.println("BLEService收到的数据是"+mes.length());
        //mes += "\r\n";//\r\n 返回 0d0a 也就是13 10

        byte[] bytes = new byte[1500];
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

    //----------------------------------------------------串口控制类
    private class SerialControl extends SerialHelper {

        //		public SerialControl(String sPort, String sBaudRate){
//			super(sPort, sBaudRate);
//		}
        public SerialControl(){
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData)
        {
            //数据接收量大或接收时弹出软键盘，界面会卡顿,可能和6410的显示性能有关
            //直接刷新显示，接收数据量大时，卡顿明显，但接收与显示同步。
            //用线程定时刷新显示可以获得较流畅的显示效果，但是接收数据速度快于显示速度时，显示会滞后。
            //最终效果差不多-_-，线程定时刷新稍好一些。
            // dispQueue.AddQueue(ComRecData);//线程定时刷新显示(推荐)
            dispRecData(ComRecData);

        }
    }
    //----------------------------------------------------刷新显示线程
    private class DispQueueThread extends Thread{
        private Queue<ComBean> QueueList = new LinkedList<ComBean>();
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                final ComBean ComData;
                while((ComData=QueueList.poll())!=null)
                {
//                    runOnUiThread(new Runnable()
//                    {
//                        public void run()
//                        {
//                            DispRecData(ComData);
//                        }
//                    });
                    try
                    {
                        Thread.sleep(100);//显示性能高的话，可以把此数值调小。
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        public synchronized void AddQueue(ComBean ComData){
            QueueList.add(ComData);
        }
    }

    //----------------------------------------------------
    private AssistBean getAssistData() {
        SharedPreferences msharedPreferences = getSharedPreferences("ComAssistant", Context.MODE_PRIVATE);
        AssistBean AssistData =	new AssistBean();
        try {
            String personBase64 = msharedPreferences.getString("AssistData", "");
            byte[] base64Bytes = Base64.decode(personBase64.getBytes(),0);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            AssistData = (AssistBean) ois.readObject();
            return AssistData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AssistData;
    }
    //----------------------------------------------------显示接收数据
    private void dispRecData(ComBean ComRecData){
        //sMsg.append(ComRecData.sRecTime);
        //sMsg.append("[");
        //sMsg.append(ComRecData.sComPort);
        //sMsg.append("]");

        //sMsg.delete(0,sMsg.length());
        //sMsg.append(new String(ComRecData.bRec));
        //System.out.println("接收数据"+sMsg.toString());
        byte[] ss = ComRecData.bRec;
        System.arraycopy(ss,0,bys,flag,ss.length);
        flag = ss.length;

        int head = 0;
        int command = 0;
        int dataLen = 0;
        byte[] checkSum = new byte[4];
        int index = -1;
        byte[] data;

        //System.out.print(bys[1]&0x0FF);
        for (int i = 0; i < flag; i++) {
            head = bys[i]&0x0FF;
            if(head == 0xAA){
                index = i;
                break;
            }
        }
        System.out.println("接收数据"+ss.length);
        while(index != -1 && flag >= 8){
            command = bys[index + 1]&0x0FF;//命令
            dataLen = ((bys[index + 3] << 8) | bys[index + 2] & 0xff)&0x0FFFF;//数据长度
//            checkSum = ((((bys[index + 7] & 0xff) << 24)
//                    | ((bys[index + 6] & 0xff) << 16)
//                    | ((bys[index + 5] & 0xff) << 8) | ((bys[index + 4] & 0xff) << 0)))&0x0FFFFFFFFL;//校验码
            System.arraycopy(bys,index + 4,checkSum,0,4);
            if(dataLen <= flag - 8){
                data = new byte[dataLen];
                System.arraycopy(bys,8,data,0,dataLen);
                String msg = null;
                try {
                    msg = new String(data,"GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
//                if(Arrays.equals(Utils.check_suml(data),checkSum)) {
                    System.out.println("接收数据校验正确" + Utils.check_suml(data) + "*" + checkSum);
                    //}
                    System.out.println("接收数据校验2正确" + Utils.check_suml(data) + "*" + checkSum);
                    EventBus.getDefault().post(new BleReadEvent(msg, command, data));
                    System.out.println("接收数据2" + msg + "*" + command + "*" + data.length);
                    byte[] cache = new byte[flag - 8 - dataLen];
                    System.arraycopy(bys, 8 + dataLen, cache, 0, flag - 8 - dataLen);
                    System.arraycopy(cache, 0, bys, 0, cache.length);
                    flag = flag - 8 - dataLen;
                    if (flag > 0) {
                        for (int i = 0; i < flag; i++) {
                            head = bys[i] & 0x0FF;
                            if (head == 0xAA) {
                                index = i;
                                break;
                            }
                        }
                    }
      //          }
            }else{
                break;
            }

        }
//        while(sMsg.toString().contains("\r\n")){
//            int index = sMsg.toString().indexOf("\r");
//            while( '\n' != sMsg.toString().charAt(index+1)){
//                index = sMsg.toString().indexOf("\r",index + 1);
//            }
//            String msg = sMsg.toString().substring(0,index);
//            EventBus.getDefault().post(new BleReadEvent(msg));
//            System.out.println("接收数据2"+msg);
//            sMsg.delete(0,msg.length()+2);
//        }
    }

    public static void printComplementCode(int a)
    {
        for (int i = 0; i < 32; i++)
        {
            // 0x80000000 是一个首位为1，其余位数为0的整数
            int t = (a & 0x80000000 >>> i) >>> (31 - i);
            System.out.print(t);
        }
        System.out.println();
    }

    public int getUnsignedByte(byte data){      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        //data = -86;
        return data&0x0FF; // 部分编译器会把最高位当做符号位，因此写成0x0FF.
    }
    //----------------------------------------------------串口发送
    private void sendPortData(SerialHelper ComPort,String sOut,byte[] bytes,byte comm){
        System.out.println("PortService发送数据String"+sOut);
        System.out.println("PortService发送数据byte"+bytes);
        if (ComPort!=null && ComPort.isOpen())
        {
//            if (radioButtonTxt.isChecked())
//            {
            ComPort.sendTxt(sOut,bytes,comm);
//            }else if (radioButtonHex.isChecked()) {
//                ComPort.sendHex(sOut);
//            }
        }
    }
    //----------------------------------------------------关闭串口
    private void closeComPort(SerialHelper ComPort){
        if (ComPort!=null){
            ComPort.stopSend();
            ComPort.close();
        }
    }
    //----------------------------------------------------打开串口
    private void openComPort(SerialHelper ComPort){
        try
        {
            ComPort.open();
        } catch (SecurityException e) {
            showMessage(getString(R.string.string326));
        } catch (IOException e) {
            showMessage(getString(R.string.string327));
        } catch (InvalidParameterException e) {
            showMessage(getString(R.string.string328));
        }
    }
    //------------------------------------------显示消息
    private void showMessage(String sMsg)
    {
        Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
    }
}



