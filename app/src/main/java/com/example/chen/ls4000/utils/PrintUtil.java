package com.example.chen.ls4000.utils;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.bean.Instrument;
import com.example.chen.ls4000.bean.Sample;
import com.github.promeg.pinyinhelper.Pinyin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * 蓝牙打印工具类
 */
public class PrintUtil {
    private Context context;
    private OutputStreamWriter mWriter = null;
    private OutputStream mOutputStream = null;

    public final static int WIDTH_PIXEL = 384;
    public final static int IMAGE_SIZE = 320;

    /**
     * 打印纸一行最大的字节
     */
    private static final int LINE_BYTE_SIZE = 32;

    /**
     * 初始化Pos实例
     *
     * @param encoding 编码
     * @throws IOException
     */
    public PrintUtil(OutputStream outputStream, String encoding) throws IOException {
        mWriter = new OutputStreamWriter(outputStream, encoding);
        mOutputStream = outputStream;
        initPrinter();
    }


    public void print(byte[] bs) throws IOException {
        mOutputStream.write(bs);
    }

    public void printRawBytes(byte[] bytes) throws IOException {
        mOutputStream.write(bytes);
        mOutputStream.flush();
    }

    /**
     * 初始化打印机
     *
     * @throws IOException
     */
    public void initPrinter() throws IOException {
        mWriter.write(0x1B);
        mWriter.write(0x40);
        mWriter.flush();
    }

    /**
     * 打印换行
     *
     * @return length 需要打印的空行数
     * @throws IOException
     */
    public void printLine(int lineNum) throws IOException {
        for (int i = 0; i < lineNum; i++) {
            mWriter.write("\n");
        }
        mWriter.flush();
    }

    /**
     * 打印换行(只换一行)
     *
     * @throws IOException
     */
    public void printLine() throws IOException {
        printLine(1);
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printTabSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            mWriter.write("\t");
        }
        mWriter.flush();
    }

    /**
     * 绝对打印位置
     *
     * @return
     * @throws IOException
     */
    public byte[] setLocation(int offset) throws IOException {
        byte[] bs = new byte[4];
        bs[0] = 0x1B;
        bs[1] = 0x24;
        bs[2] = (byte) (offset % 256);
        bs[3] = (byte) (offset / 256);
        return bs;
    }

    public byte[] getGbk(String stText) throws IOException {
        byte[] returnText = stText.getBytes("GBK"); // 必须放在try内才可以
        return returnText;
    }

    private int getStringPixLength(String str) {
        int pixLength = 0;
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (Pinyin.isChinese(c)) {
                pixLength += 24;
            } else {
                pixLength += 12;
            }
        }
        return pixLength;
    }

    public int getOffset(String str) {
        return WIDTH_PIXEL - getStringPixLength(str);
    }

    /**
     * 打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printText(String text) throws IOException {
        mWriter.write(text);
        mWriter.flush();
    }

    /**
         * 对齐0:左对齐，1：居中，2：右对齐
     */
    public void printAlignment(int alignment) throws IOException {
        mWriter.write(0x1b);
        mWriter.write(0x61);
        mWriter.write(alignment);
    }

    public void printLargeText(String text) throws IOException {

        mWriter.write(0x1b);
        mWriter.write(0x21);
        mWriter.write(14);

        mWriter.write(text);

        mWriter.write(0x1b);
        mWriter.write(0x21);
        mWriter.write(0);

        mWriter.flush();
    }

    public void printTwoColumn(String title, String content) throws IOException {
        int iNum = 0;
        byte[] byteBuffer = new byte[100];
        byte[] tmp;

        tmp = getGbk(title);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = setLocation(getOffset(content));
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = getGbk(content);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);

        print(byteBuffer);
    }

    public void printThreeColumn(String left, String middle, String right) throws IOException {
        int iNum = 0;
        byte[] byteBuffer = new byte[200];
        byte[] tmp = new byte[0];

        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = getGbk(left);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        int pixLength = getStringPixLength(left) % WIDTH_PIXEL;
        if (pixLength > WIDTH_PIXEL / 2 || pixLength == 0) {
            middle = "\n\t\t\t" + middle;
        }

        tmp = setLocation(192);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = getGbk(middle);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = setLocation(getOffset(right));
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);
        iNum += tmp.length;

        tmp = getGbk(right);
        System.arraycopy(tmp, 0, byteBuffer, iNum, tmp.length);

        print(byteBuffer);
    }

    public void printDashLine() throws IOException {
        printText("--------------------------------");
    }

    public void printBitmap(Bitmap bmp) throws IOException {
        bmp = compressPic(bmp);
        byte[] bmpByteArray = draw2PxPoint(bmp);
            printRawBytes(bmpByteArray);
    }

    /*************************************************************************
     * 假设一个360*360的图片，分辨率设为24, 共分15行打印 每一行,是一个 360 * 24 的点阵,y轴有24个点,存储在3个byte里面。
     * 即每个byte存储8个像素点信息。因为只有黑白两色，所以对应为1的位是黑色，对应为0的位是白色
     **************************************************************************/
    private byte[] draw2PxPoint(Bitmap bmp) {
        //先设置一个足够大的size，最后在用数组拷贝复制到一个精确大小的byte数组中
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
        byte[] tmp = new byte[size];
        int k = 0;
        // 设置行距为0
        tmp[k++] = 0x1B;
        tmp[k++] = 0x33;
        tmp[k++] = 0x00;
        // 居中打印
        tmp[k++] = 0x1B;
        tmp[k++] = 0x61;
        tmp[k++] = 1;
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            tmp[k++] = 0x1B;
            tmp[k++] = 0x2A;// 0x1B 2A 表示图片打印指令
            tmp[k++] = 33; // m=33时，选择24点密度打印
            tmp[k++] = (byte) (bmp.getWidth() % 256); // nL
            tmp[k++] = (byte) (bmp.getWidth() / 256); // nH
            for (int i = 0; i < bmp.getWidth(); i++) {
                for (int m = 0; m < 3; m++) {
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        tmp[k] += tmp[k] + b;
                    }
                    k++;
                }
            }
            tmp[k++] = 10;// 换行
        }
        // 恢复默认行距
        tmp[k++] = 0x1B;
        tmp[k++] = 0x32;

        byte[] result = new byte[k];
        System.arraycopy(tmp, 0, result, 0, k);
        return result;
    }

    /**
     * 图片二值化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bit 位图
     * @return
     */
    private byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     */
    private int RGB2Gray(int r, int g, int b) {
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // 灰度转化公式
        return gray;
    }

    /**
     * 对图片进行压缩（去除透明度）
     *
     * @param bitmapOrg
     */
    private Bitmap compressPic(Bitmap bitmapOrg) {
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        // 定义预转换成的图片的宽度和高度
        int newWidth = IMAGE_SIZE;
        int newHeight = IMAGE_SIZE;
        Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBmp);
        targetCanvas.drawColor(0xffffffff);
        targetCanvas.drawBitmap(bitmapOrg, new Rect(0, 0, width, height), new Rect(0, 0, newWidth, newHeight), null);
        return targetBmp;
    }

    private static int getBytesLength(String msg) {
        return msg.getBytes(Charset.forName("GB2312")).length;
    }

    private  void printTwoData(String leftText, String rightText) throws IOException{
        StringBuilder sb = new StringBuilder();
        int leftTextLength = getBytesLength(leftText);
        int rightTextLength = getBytesLength(rightText);
        sb.append(leftText);

        // 计算两侧文字中间的空格
        int marginBetweenMiddleAndRight = LINE_BYTE_SIZE - leftTextLength - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        sb.append(rightText);

    }


    public static void  printTest(Context context,BluetoothSocket bluetoothSocket, Sample sample, String ss) {

        try {
            PrintUtil pUtil = new PrintUtil(bluetoothSocket.getOutputStream(), "GBK");
            // 店铺名 居中 放大

            // 分隔线
            pUtil.printDashLine();
            pUtil.printLine();

            pUtil.printAlignment(1);
            pUtil.printLargeText(context.getString(R.string.string72));
            pUtil.printLine();
            pUtil.printAlignment(0);
            pUtil.printLine();

            // 分隔线
            pUtil.printDashLine();
            pUtil.printLine();

            pUtil.printTwoColumn(context.getString(R.string.string76)+ sample.getName(),"");

            pUtil.printLine();

           pUtil. printTwoColumn(context.getString(R.string.string263)+sample.getAge(),context.getString(R.string.string264)+sample.getGender());
            pUtil.printLine();

            pUtil.printTwoColumn(context.getString(R.string.string77)+sample.getSamNum(),"");
            pUtil.printLine();


            String[] proNames = sample.getProName().split(" "); //项目名
            String[] concens = sample.getConcen().split(" ");   //结果
            switch (proNames.length){
                case 1:
                    // 分隔线  1
                    pUtil.printDashLine();
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string78)+proNames[0],"");//项目
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string80)+ sample.getRefer()+sample.getUnit(),"");//参考值
                    pUtil.printLine();

                    if(concens[0].contains("↑")||concens[0].contains("↓")){
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[0].substring(0,concens[0].length()-1) + sample.getUnit() + concens[0].substring(concens[0].length()-1,concens[0].length()), "");
                    }else {
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[0] + sample.getUnit(), "");  //结果
                    }
                    pUtil.printLine();
                    break;

                case 2:
                    pUtil.printDashLine();
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string78)+proNames[0],"");//项目
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string80)+ sample.getRefer()+sample.getUnit(),"");//参考值
                    pUtil.printLine();

                    if(concens[0].contains("↑")||concens[0].contains("↓")){
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[0].substring(0,concens[0].length()-1) + sample.getUnit() + concens[0].substring(concens[0].length()-1,concens[0].length()), "");
                    }else {
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[0] + sample.getUnit(), "");  //结果
                    }
                    //pUtil.printTwoColumn(context.getString(R.string.string79)+concens[0]+sample.getUnit(),"");  //结果
                    pUtil.printLine();

                    pUtil.printDashLine();
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string78)+proNames[1],"");//项目
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string80)+ sample.getRefer2()+sample.getUnit2(),"");//参考值
                    pUtil.printLine();

                    if(concens[1].contains("↑")||concens[1].contains("↓")){
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[1].substring(0,concens[1].length()-1) + sample.getUnit2() + concens[1].substring(concens[1].length()-1,concens[1].length()), "");
                    }else {
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[1] + sample.getUnit2(), "");  //结果
                    }
                    //pUtil.printTwoColumn(context.getString(R.string.string79)+concens[1]+sample.getUnit2(),"");  //结果
                    pUtil.printLine();

                    break;

                case 3:

                    pUtil.printDashLine();
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string78)+proNames[0],"");//项目
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string80)+ sample.getRefer()+sample.getUnit(),"");//参考值
                    pUtil.printLine();

                    if(concens[0].contains("↑")||concens[0].contains("↓")){
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[0].substring(0,concens[0].length()-1) + sample.getUnit() + concens[0].substring(concens[0].length()-1,concens[0].length()), "");
                    }else {
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[0] + sample.getUnit(), "");  //结果
                    }
                    //pUtil.printTwoColumn(context.getString(R.string.string79)+concens[0]+sample.getUnit(),"");  //结果
                    pUtil.printLine();

                    pUtil.printDashLine();
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string78)+proNames[1],"");//项目
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string80)+ sample.getRefer2()+sample.getUnit2(),"");//参考值
                    pUtil.printLine();

                    if(concens[1].contains("↑")||concens[1].contains("↓")){
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[1].substring(0,concens[1].length()-1) + sample.getUnit2() + concens[1].substring(concens[1].length()-1,concens[1].length()), "");
                    }else {
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[1] + sample.getUnit2(), "");  //结果
                    }
                    //pUtil.printTwoColumn(context.getString(R.string.string79)+concens[1]+sample.getUnit2(),"");  //结果
                    pUtil.printLine();

                    pUtil.printDashLine();
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string78)+proNames[2],"");//项目
                    pUtil.printLine();

                    pUtil.printTwoColumn(context.getString(R.string.string80)+ sample.getRefer3()+sample.getUnit3(),"");//参考值
                    pUtil.printLine();

                    if(concens[2].contains("↑")||concens[2].contains("↓")){
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[2].substring(0,concens[2].length()-1) + sample.getUnit3() + concens[2].substring(concens[2].length()-1,concens[2].length()), "");
                    }else {
                        pUtil.printTwoColumn(context.getString(R.string.string79) + concens[2] + sample.getUnit3(), "");  //结果
                    }
                    //pUtil.printTwoColumn(context.getString(R.string.string79)+concens[2]+sample.getUnit3(),"");  //结果
                    pUtil.printLine();

                    break;
            }



            // 分隔线
            pUtil.printDashLine();
            pUtil.printLine();

            pUtil.printTwoColumn(context.getString(R.string.string81)+sample.getTestTime(),"");
            pUtil.printLine();


            // 分隔线
            pUtil.printDashLine();
            pUtil.printLine();

            if('1' == ss.charAt(1)) {
                pUtil.printTwoColumn(context.getString(R.string.string73)+sample.getHos(),"");
                pUtil.printLine();
            }
            if('1' == ss.charAt(1)) {
                pUtil.printTwoColumn(context.getString(R.string.string74)+sample.getDepart(),"");
                pUtil.printLine();
            }
            if('1' == ss.charAt(1)) {
                pUtil.printTwoColumn(context.getString(R.string.string75)+sample.getTestPeo(),"");
                pUtil.printLine();
            }

            if('1' == ss.charAt(1)) {
                pUtil.printTwoColumn(context.getString(R.string.string267)+sample.getAudPeo(),"");
                pUtil.printLine();

                // 分隔线
                pUtil.printDashLine();
                pUtil.printLine();
            }





            pUtil.printLine(4);

        } catch (IOException e) {

        }

    }

    public static void printInstrument(Context context, BluetoothSocket bluetoothSocket, Instrument instrument,String ss){
        try{
            PrintUtil pInstrument = new PrintUtil(bluetoothSocket.getOutputStream(), "GBK");
            //报告单
            pInstrument.printAlignment(1);
            pInstrument.printLargeText(context.getString(R.string.string233));
            pInstrument.printLine();
            pInstrument.printAlignment(0);
            //pInstrument.printLine();

            // 分隔线
            pInstrument.printDashLine();
            pInstrument.printLine();

            pInstrument.printTwoColumn(context.getString(R.string.string234),context.getString(R.string.string235));
            pInstrument.printLine();


            pInstrument.printTwoColumn(context.getString(R.string.string236),context.getString(R.string.string237));
            pInstrument.printLine();


            pInstrument.printTwoColumn(context.getString(R.string.string238),instrument.getResult());
            pInstrument.printLine();

//            pInstrument.printTwoColumn(context.getString(R.string.string239),instrument.getSeqNum());
//            pInstrument.printLine();

//            if('1' == ss.charAt(3) && '1'== ss.charAt(4)) {
//                pInstrument.printTwoColumn(context.getString(R.string.string240), instrument.getTime());
//                pInstrument.printLine();
//            }else if('1' == ss.charAt(3)){
//                pInstrument.printTwoColumn(context.getString(R.string.string240), instrument.getTime().substring(0,instrument.getTime().indexOf(" ")));
//                pInstrument.printLine();
//            }else if('1' == ss.charAt(4)){
//                pInstrument.printTwoColumn(context.getString(R.string.string240), instrument.getTime().substring((instrument.getTime().indexOf(" ")+1),instrument.getTime().length()));
//                pInstrument.printLine();
//            }

//            pInstrument.printTwoColumn(context.getString(R.string.string240),instrument.getTime());
//            pInstrument.printLine();

            // 分隔线
            pInstrument.printDashLine();
            pInstrument.printLine();
            pInstrument.printLine(4);

        }catch (IOException e){

        }
    }
}