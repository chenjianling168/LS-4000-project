package com.example.chen.ls4000.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-4-18.
 */

public class AccessPoint {

    private String ssid;
    private String bssid;
    private String password;
    private float signalStrength;  // 0~100
    private String encryptionType;
    private int networkId;
    /**
     * aps are relative AccessPoints who share the same ssid while different bssid
     * we will treat them as one hotspot
     */
    private ArrayList<AccessPoint> relativeAPs;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public float getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(float signalStrength) {
        this.signalStrength = signalStrength;
    }

    public String getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(String encryptionType) {
        this.encryptionType = encryptionType;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public ArrayList<AccessPoint> getRelativeAPs() {
        return relativeAPs;
    }

    public void setRelativeAPs(ArrayList<AccessPoint> relativeAPs) {
        this.relativeAPs = relativeAPs;
    }
}
