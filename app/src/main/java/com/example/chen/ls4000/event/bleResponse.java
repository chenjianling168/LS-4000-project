package com.example.chen.ls4000.event;

/**
 * Created by Administrator on 2017/3/11.
 */

public class bleResponse {
    int code;
    // -1 蓝牙不可用
    //0 扫描关闭
    //1 连接成功
    //2 连接失败
    //3 找不到指定设备
    //6 rssi
    //4 写入成功
    //5 写入失败
    //7 通讯通道通畅
    //8 通讯通道找不到
    //9 蓝牙基本状态信息
    int rssi;
    boolean connect;
    boolean character;

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public boolean isCharacter() {
        return character;
    }

    public void setCharacter(boolean character) {
        this.character = character;
    }

    public bleResponse(int code, boolean connect, boolean character) {
        this.code=code;

        this.connect = connect;
        this.character = character;
    }

    public bleResponse(int code, int rssi) {
        this.rssi = rssi;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public bleResponse(int code) {
        this.code = code;
    }
}
