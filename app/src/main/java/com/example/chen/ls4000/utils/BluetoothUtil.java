package com.example.chen.ls4000.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothUtil {

	//	/**
//	 * 蓝牙是否打开
//	 */
	public static boolean isBluetoothOn() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null)
			// 蓝牙已打开
			if (mBluetoothAdapter.isEnabled())
				return true;

		return false;
	}

	public static int connect(MyApp myApp) {

//		if (myApp.getPrintState()) {
//			context.getObject().CON_CloseDevices(context.getState());
//
//			con.setText(R.string.button_btcon);// "连接"
//			mBconnect = false;
//		} else {
		if (myApp.getPrintState()>0) {
			myApp.getObject().CON_CloseDevices(myApp.getPrintState());

			//con.setText(R.string.button_btcon);// "连接"
			myApp.setPrintState(0);
		}
		//else {
		List<String> getbtName = new ArrayList<String>();
		List<String> getbtMac = new ArrayList<String>();
		int state = -1;
		if(myApp.getObject().CON_GetWirelessDevices(0)!=null) {
			List<String> getbtNM = (ArrayList<String>) myApp.getObject()
					.CON_GetWirelessDevices(0);
			for (int i = 0; i < getbtNM.size(); i++) {
				getbtName.add(getbtNM.get(i).split(",")[0]);
				getbtMac.add(getbtNM.get(i).split(",")[1].substring(0, 17));
			}
			for (int i = 0; i < getbtNM.size(); i++) {
				if (myApp.getPrintName().equals(getbtName.get(i))) {
					state = myApp.getObject().CON_ConnectDevices(myApp.getPrintName(), getbtMac.get(i), 200);

					if (state > 0) {
						//utils.DeviceControl	DevCtrl = new utils.DeviceControl(utils.DeviceControl.powerPathKT);
						//try {
						//	DevCtrl.PowerOnMTDevice();
						//} catch (IOException e) {
						//	e.printStackTrace();
						//}
						myApp.setPrintState(state);
						//con.setText(R.string.TextView_close);// "关闭"
//				Intent intent = new Intent(ConnectAvtivity.this,
//						PrintModeActivity.class);
//				context.setState(state);
//				context.setName(PrintName);
//				context.setPrintway(printway.getSelectedItemPosition());
//				startActivity(intent);
						break;
					} else {
//				Toast.makeText(ConnectAvtivity.this, R.string.mes_confail,
//						Toast.LENGTH_SHORT).show();
						//mBconnect = false;
						myApp.setPrintState(state);
						//con.setText(R.string.button_btcon);// "连接"
					}
				}
			}
			//}
			//}
		}
		return myApp.getPrintState();
	}

	/**
	 * 获取所有已配对的设备
	 */
	public static List<BluetoothDevice> getPairedDevices() {
		List deviceList = new ArrayList<>();
		Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				deviceList.add(device);
			}
		}
		return deviceList;
	}

	/**
	 * 获取所有已配对的打印类设备
	 */
	public static List<BluetoothDevice> getPairedPrinterDevices() {
		return getSpecificDevice(BluetoothClass.Device.Major.IMAGING);
	}

	/**
	 * 从已配对设配中，删选出某一特定类型的设备展示
	 * @param deviceClass
	 * @return
	 */
	public static List<BluetoothDevice> getSpecificDevice(int deviceClass){
		List<BluetoothDevice> devices = BluetoothUtil.getPairedDevices();
		List<BluetoothDevice> printerDevices = new ArrayList<>();

		for (BluetoothDevice device : devices) {
			BluetoothClass klass = device.getBluetoothClass();
			// 关于蓝牙设备分类参考 http://stackoverflow.com/q/23273355/4242112
			if (klass.getMajorDeviceClass() == deviceClass)
				printerDevices.add(device);
		}

		return printerDevices;
	}

	/**
	 * 弹出系统对话框，请求打开蓝牙
	 */
	public static void openBluetooth(Activity activity) {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(enableBtIntent, 666);
	}

	public static BluetoothSocket connectDevice(BluetoothDevice device) {
		BluetoothSocket socket = null;
		try {
			socket = device.createRfcommSocketToServiceRecord(
					UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			socket.connect();
		} catch (IOException e) {
			try {
				socket.close();
			} catch (IOException closeException) {
				return null;
			}
			return null;
		}
		return socket;
	}

	private final static String TAG = "BluetoothUtil";

	// 获取蓝牙的开关状态
	public static boolean getBlueToothStatus(Context context) {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		boolean enabled = false;
		switch (bluetoothAdapter.getState()) {
			case BluetoothAdapter.STATE_ON:
			case BluetoothAdapter.STATE_TURNING_ON:
				enabled = true;
				break;
			case BluetoothAdapter.STATE_OFF:
			case BluetoothAdapter.STATE_TURNING_OFF:
			default:
				enabled = false;
				break;
		}
		return enabled;
	}

	// 打开或关闭蓝牙
	public static void setBlueToothStatus(Context context, boolean enabled) {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (enabled ) {
			bluetoothAdapter.enable();
		} else {
				bluetoothAdapter.disable();
		}
	}

	public static String readInputStream(InputStream inStream) {
		String result = "";
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			byte[] data = outStream.toByteArray();
			outStream.close();
			inStream.close();
			result = new String(data, "utf8");
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		return result;
	}

	public static void writeOutputStream(BluetoothSocket socket, String message) {
		Log.d(TAG, "begin writeOutputStream message=" + message);
		try {
			OutputStream outStream = socket.getOutputStream();
			outStream.write(message.getBytes());
			//outStream.flush();
			//outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d(TAG, "end writeOutputStream");
	}



}