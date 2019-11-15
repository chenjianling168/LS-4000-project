package com.example.chen.ls4000.thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.chen.ls4000.activity.CoefficientActivity;
import com.example.chen.ls4000.activity.WIFIActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;

/**
 * 连接线程
 * Created by 坤 on 2016/9/7.
 */
public class ConnectThread extends Thread {

    private final Socket socket;
    private Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ConnectThread(Socket socket, Handler handler){
        setName("ConnectThread");
        Log.w("AAA","ConnectThread");
        this.socket = socket;
        this.handler = handler;
    }

    @Override
    public void run() {
/*        if(activeConnect){
//            socket.c
        }*/
        if(socket==null){
            return;
        }
        handler.sendEmptyMessage(WIFIActivity.DEVICE_CONNECTED);
        try {
            //获取数据流
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytes;
            while (true){
                //读取数据
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    final byte[] data = new byte[bytes];
                    System.arraycopy(buffer, 0, data, 0, bytes);

                    Message message = Message.obtain();
                    message.what = WIFIActivity.GET_MSG;
                    Bundle bundle = new Bundle();
                    bundle.putString("MSG",new String(data));
                    message.setData(bundle);
                    handler.sendMessage(message);

                    Log.w("AAA","读取到数据:"+new String(data));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //字符转换为16进制
    public static String str2HexStr(String str){
        byte[] bytes =str.getBytes();
        BigInteger bigInteger = new BigInteger(1,bytes);
        return bigInteger.toString(16);
    }

    /** 16进制的字符串转换成16进制字符串数组
     * @param src
     * @return
     */
    public static byte[] HexString2Bytes(String src){
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();
        for (int i = 0; i <len ; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return  ret;
    }

    public static byte uniteBytes(byte src0,byte src1){
        byte _b0 =Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte)(_b0 ^ _b1);
        return ret;
    }

    //字节数组转16进制字符串显示
    public String bytes2HexString(byte[] b,int length){
        String r="";
        for (int i = 0; i <length ; i++) {
            String hex =Integer.toHexString(b[i] & 0xFF);
            if(hex.length() == 1){
                hex = "0" + hex;
            }
            r += hex.toUpperCase();
        }
        return  r;
    }



    /**
     * 发送数据
     */
    public void sendData(String msg){
        Log.w("AAA","发送数据:"+(outputStream==null));
        if(outputStream!=null){
            try {
                outputStream.write(msg.getBytes());
                Log.w("AAA","发送消息："+msg);
                Message message = Message.obtain();
                //message.what = WIFIActivity.SEND_MSG_SUCCSEE;
                //message.what = CoefficientActivity.
                Bundle bundle = new Bundle();
                bundle.putString("MSG",new String(msg));
                message.setData(bundle);
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = WIFIActivity.SEND_MSG_ERROR;
                Bundle bundle = new Bundle();
                bundle.putString("MSG",new String(msg));
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }
    }
}
