package com.example.chen.ls4000.bean;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

/**
 * 存储BluetoothDevice，并没有多大用
 */

public class DeviceList {

    private ArrayList<BluetoothDevice> mDevices;
    private ArrayList<byte[]> mRecords;
    private ArrayList<Integer> mRSSIs;

    public void add(BluetoothDevice device, byte[] record, Integer rssi){
        addDevice(device);
        addRecords(record);
        addRssi(rssi);
    }
    public void addDevice(BluetoothDevice device){
        if(mDevices==null){
            mDevices=new ArrayList<BluetoothDevice>();
        }
        mDevices.add(device);
    }

    public void addRecords(byte[] record){
        if(mRecords==null){
            mRecords=new ArrayList<byte[]>();
        }
        mRecords.add(record);
    }

    public void addRssi(Integer rssi){
        if(mRSSIs==null){
            mRSSIs=new ArrayList<Integer>();
        }
        mRSSIs.add(rssi);
    }
    public ArrayList<BluetoothDevice> getmDevices() {
        return mDevices;
    }

    public void setmDevices(ArrayList<BluetoothDevice> mDevices) {
        this.mDevices = mDevices;
    }

    public ArrayList<byte[]> getmRecords() {
        return mRecords;
    }

    public void setmRecords(ArrayList<byte[]> mRecords) {
        this.mRecords = mRecords;
    }

    public ArrayList<Integer> getmRSSIs() {
        return mRSSIs;
    }

    public void setmRSSIs(ArrayList<Integer> mRSSIs) {
        this.mRSSIs = mRSSIs;
    }
}
