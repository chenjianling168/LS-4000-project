package com.example.chen.ls4000.bean;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;

/**
 * 存储Characteristic，并没有多大用
 */

public class CharacteristicsList {

    private ArrayList<BluetoothGattCharacteristic> mCharacteristics;

    public CharacteristicsList() {
        mCharacteristics = new ArrayList<BluetoothGattCharacteristic>();
    }

    public void addBleGattCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mCharacteristics == null) mCharacteristics = new ArrayList<BluetoothGattCharacteristic>();

        mCharacteristics.add(characteristic);
    }

    public ArrayList<BluetoothGattCharacteristic> getmBTCharacteristic() {
        return mCharacteristics;
    }

    public void setmBTServices(ArrayList<BluetoothGattCharacteristic> mCharacteristics) {
        this.mCharacteristics = mCharacteristics;
    }
}
