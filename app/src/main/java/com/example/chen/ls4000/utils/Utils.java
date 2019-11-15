package com.example.chen.ls4000.utils;

import android.app.Activity;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Administrator on 2017/6/6 0006.
 */

public class Utils {

    public static byte[] addBytes(byte[] data1,byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length+"\r\n".getBytes().length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        System.arraycopy("\r\n".getBytes(),0,data3,data2.length,"\r\n".length());
        return data3;
    }

    public static byte[] check_sum(byte[] data)
    {
        byte[] da = new byte[4];
        int sum = 0;
        int count = data.length;
        int i = 0;
        //MessageBox.Show("#" + data.Length + "#"+data[0]);
        while (count > 0)
        {
            //sum += data[i] % 0xffff;
            sum += data[i];
            count -= 1;
            i++;
            //MessageBox.Show("#"+sum+"#");
        }

        sum = (sum >> 16) + (sum & 0xffff);
        sum += (sum >> 16);
        sum = ~sum;

//        da[0] = (byte)((short)sum >> 8);
//        da[1] = (byte)((short)sum);

        //MessageBox.Show(sum + "#" + da[0] + "#" + da[1]);

        da = intToBytes(sum);

        return da;
    }

    public static byte[] check_suml(byte[] data)
    {
        byte[] da = new byte[4];
        int sum = 0;
        int count = data.length;
        int i = 0;
        //MessageBox.Show("#" + data.Length + "#"+data[0]);
        if(data.length>1000){
            System.arraycopy(data,18,data,0,data.length-18);
        }
        while (count > 0)
        {
            //sum += data[i] % 0xffff;
            sum += data[i];
            count -= 1;
            i++;
            //MessageBox.Show("#"+sum+"#");
        }

        sum = (sum >> 16) + (sum & 0xffff);
        sum += (sum >> 16);
        sum = ~sum;

//        da[0] = (byte)((short)sum >> 8);
//        da[1] = (byte)((short)sum);

        //MessageBox.Show(sum + "#" + da[0] + "#" + da[1]);

        //da = intToBytes(sum);

        byte[] b = new byte[4];
        b[0] = (byte) (sum & 0xff);
        b[1] = (byte) (sum >> 8 & 0xff);
        b[2] = (byte) (sum >> 16 & 0xff);
        b[3] = (byte) (sum >> 24 & 0xff);
        return b;

       // return sum;
    }
    public static int[] bytesToIntArray(byte[] bytes){
        int[] ints=new int[bytes.length/2 + 10];
//        boolean flag;
        for (int i=0;i<bytes.length/2;i++){
            byte[] b={bytes[2*i],bytes[2*i+1]};
            ints[i] = bytesToInt(b);
        }
        return ints;
    }

    /**
     * 将一个单字节的byte转换成32位的int
     * @param b
               byte
     * @return convert result
     */
    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    public static int byteArrayToInt(byte[] b) {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static int bytesToInt(byte[] src) {
        int value;
        value = (int) ((src[1] & 0xFF)
                | ((src[0] & 0xFF)<<8));
        return value;
    }


    public static byte[] intToBytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }

    public static byte[] shortToByteArray(short s) {
        byte[] src = new byte[2];
        src[1] =  (byte) ((s>>8) & 0xFF);
        src[0] =  (byte) (s & 0xFF);

        return src;
    }

    private static SimpleDateFormat sdf = null;
    public  static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }

    public  static void showToast(final Activity activity, final String word, final long time){
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

}
