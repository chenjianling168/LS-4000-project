package com.example.chen.ls4000.ComAssistant;

import com.example.chen.ls4000.bean.ComBean;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

/**
 * @author benjaminwan
 *串口辅助工具类
 */
public abstract class SerialHelper{
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private SendThread mSendThread;
	private String sPort="/dev/s3c2410_serial0";
	private int iBaudRate=9600;
	private boolean _isOpen=false;
	private byte[] _bLoopData=new byte[]{0x30};
	private int iDelay=500;

	//----------------------------------------------------
	public SerialHelper(String sPort,int iBaudRate){
		this.sPort = sPort;
		this.iBaudRate=iBaudRate;
	}
	public SerialHelper(){
		this("/dev/s3c2410_serial0",9600);
	}
	public SerialHelper(String sPort){
		this(sPort,9600);
	}
	public SerialHelper(String sPort,String sBaudRate){
		this(sPort,Integer.parseInt(sBaudRate));
	}
	//----------------------------------------------------
	public void open() throws SecurityException, IOException,InvalidParameterException{
		mSerialPort =  new SerialPort(new File(sPort), iBaudRate, 0);
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();
		mReadThread = new ReadThread();
		mReadThread.start();
		mSendThread = new SendThread();
		mSendThread.setSuspendFlag();
		mSendThread.start();
		_isOpen=true;
	}
	//----------------------------------------------------
	public void close(){
		if (mReadThread != null)
			mReadThread.interrupt();
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
		_isOpen=false;
	}
	//----------------------------------------------------
	public void send(byte[] bOutArray){
		try
		{
			mOutputStream.write(bOutArray);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	//----------------------------------------------------
	public void sendHex(String sHex){
		byte[] bOutArray = MyFunc.HexToByteArr(sHex);
		send(bOutArray);
	}
	//----------------------------------------------------
	public void sendTxt(String sTxt,byte[] bytes,byte comm){
		try {
			if(comm == 0x32){
				byte[] bOutArray = sTxt.getBytes("gb2312");
				send(bOutArray);
			}else if(comm == 0x31){
				byte[] bytes1 = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00,
						0x03, 0x20, 0x01, 0x22};
				send(bytes1);
				byte[] bytes2 = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00,
						0x03, 0x20, 0x02, 0x21};
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				send(bytes2);
				byte[] bytes3 = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00,
						0x03, 0x30, 0x01, 0x32};
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				send(bytes3);
			}else if(comm == 0x33){
					byte[] bytes1 = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69,
							0x00, 0x03, 0x20, 0x01, 0x22};
					send(bytes1);
//					byte[] bytes2 = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x02, 0x21};
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					send(bytes2);
					byte[] bytes3 = new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69,
							0x00, 0x03, 0x30, 0x01, 0x32};
					try {

						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					send(bytes3);
			}else if(comm == 0x08){
				if("1".equals(sTxt)){
					byte[] bys = new byte[8];
					byte[] dataLen = new byte[2];
					byte[] checkSum = new byte[4];
					bys[0] = (byte) 0xAA;
					bys[1] = comm;
					dataLen = Utils.shortToByteArray((short) sTxt.length());
					checkSum = Utils.check_sum(new byte[]{0x01});
					System.arraycopy(dataLen, 0, bys, 2, 2);
					System.arraycopy(checkSum, 0, bys, 4, 4);
					byte[] bOutArray = new byte[]{0x01};
					send(bys);
					send(bOutArray);
				}else if("0".equals(sTxt)){
					byte[] bys = new byte[8];
					byte[] dataLen = new byte[2];
					byte[] checkSum = new byte[4];
					bys[0] = (byte) 0xAA;
					bys[1] = comm;
					dataLen = Utils.shortToByteArray((short) sTxt.length());
					checkSum = Utils.check_sum(new byte[]{0x00});
					System.arraycopy(dataLen, 0, bys, 2, 2);
					System.arraycopy(checkSum, 0, bys, 4, 4);
					byte[] bOutArray = new byte[]{0x00};
					send(bys);
					send(bOutArray);
				}

			}else {
				byte[] bys = new byte[8];
				byte[] dataLen = new byte[2];
				byte[] checkSum = new byte[4];
				bys[0] = (byte) 0xAA;
				bys[1] = comm;
				byte[] bOutArray = null;

                if(comm == 0){
                    dataLen = Utils.shortToByteArray((short) bytes.length);
					checkSum = Utils.check_sum(bytes);
					bOutArray = bytes;
                }else{
                    dataLen = Utils.shortToByteArray((short) sTxt.getBytes("gb2312").length);
					checkSum = Utils.check_sum(sTxt.getBytes("gb2312"));
					bOutArray = sTxt.getBytes("gb2312");
					System.arraycopy(dataLen, 0, bys, 2, 2);
					System.arraycopy(checkSum, 0, bys, 4, 4);
					send(bys);
                }


//				dataLen = Utils.shortToByteArray((short) sTxt.length());
//				checkSum = Utils.check_sum(sTxt.getBytes("gb2312"));

				send(bOutArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	//接受数据开启的线程
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) {
				try
				{
					if (mInputStream == null) return;
					byte[] buffer=new byte[1500];
					int size = mInputStream.read(buffer);
					if (size > 0){
						ComBean ComRecData = new ComBean(sPort,buffer,size);
						onDataReceived(ComRecData);
					}
					try
					{
						Thread.sleep(50);//延时50ms
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				} catch (Throwable e)
				{
					e.printStackTrace();
					return;
				}
			}
		}
	}
	//接受数据开启的线程
	private class SendThread extends Thread{
		public boolean suspendFlag = true;// 控制线程的执行
		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) {
				synchronized (this)
				{
					while (suspendFlag)
					{
						try
						{
							wait();
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
				send(getbLoopData());
				try
				{
					Thread.sleep(iDelay);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		//线程暂停
		public void setSuspendFlag() {
			this.suspendFlag = true;
		}

		//唤醒线程
		public synchronized void setResume() {
			this.suspendFlag = false;
			notify();
		}
	}
	//----------------------------------------------------
	public int getBaudRate()
	{
		return iBaudRate;
	}
	public boolean setBaudRate(int iBaud)
	{
		if (_isOpen)
		{
			return false;
		} else
		{
			iBaudRate = iBaud;
			return true;
		}
	}
	public boolean setBaudRate(String sBaud)
	{
		int iBaud = Integer.parseInt(sBaud);
		return setBaudRate(iBaud);
	}
	//----------------------------------------------------
	public String getPort()
	{
		return sPort;
	}
	public boolean setPort(String sPort)
	{
		if (_isOpen)
		{
			return false;
		} else
		{
			this.sPort = sPort;
			return true;
		}
	}
	//----------------------------------------------------
	public boolean isOpen()
	{
		return _isOpen;
	}
	//----------------------------------------------------
	public byte[] getbLoopData()
	{
		return _bLoopData;
	}
	//----------------------------------------------------
	public void setbLoopData(byte[] bLoopData)
	{
		this._bLoopData = bLoopData;
	}
	//----------------------------------------------------
	public void setTxtLoopData(String sTxt){
		this._bLoopData = sTxt.getBytes();
	}
	//----------------------------------------------------
	public void setHexLoopData(String sHex){
		this._bLoopData = MyFunc.HexToByteArr(sHex);
	}
	//----------------------------------------------------
	public int getiDelay()
	{
		return iDelay;
	}
	//----------------------------------------------------
	public void setiDelay(int iDelay)
	{
		this.iDelay = iDelay;
	}
	//----------------------------------------------------
	public void startSend()
	{
		if (mSendThread != null)
		{
			mSendThread.setResume();
		}
	}
	//----------------------------------------------------
	public void stopSend()
	{
		if (mSendThread != null)
		{
			mSendThread.setSuspendFlag();
		}
	}
	//----------------------------------------------------
	protected abstract void onDataReceived(ComBean ComRecData);
}