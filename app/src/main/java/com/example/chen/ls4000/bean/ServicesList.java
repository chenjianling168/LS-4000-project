package com.example.chen.ls4000.bean;

import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;

/**
 * 存储BluetoothGattService，并没有多大用
 */

public class ServicesList {

    private ArrayList<BluetoothGattService> mBTServices;

    public ServicesList() {
        mBTServices=new ArrayList<BluetoothGattService>();
    }

    public void addBleService(BluetoothGattService service){
        if(mBTServices==null) mBTServices=new ArrayList<BluetoothGattService>();

        mBTServices.add(service);
    }

    public ArrayList<BluetoothGattService> getmBTServices() {
        return mBTServices;
    }

    public void setmBTServices(ArrayList<BluetoothGattService> mBTServices) {
        this.mBTServices = mBTServices;
    }
}
