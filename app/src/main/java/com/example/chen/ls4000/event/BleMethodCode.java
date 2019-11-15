package com.example.chen.ls4000.event;

/**
 * Created by Administrator on 2017/3/11.
 */

public class BleMethodCode {

    //-1 关闭蓝牙service
    //0 切断蓝牙链接
    //1 连接蓝牙
    //2 读
    //3 写
    //4 扫描设备
    //5 连接设备
    //6 获取连接信息

    int code;
    String mes;
    byte comm;
    byte[] bytes;

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public byte getComm() {
        return comm;
    }

    public void setComm(byte comm) {
        this.comm = comm;
    }

    public BleMethodCode(int code) {
        this.code = code;
    }

    public BleMethodCode(int code, String mes) {
        this.code = code;
        this.mes = mes;
    }

    public BleMethodCode(int code, String mes, byte comm) {
        this.code = code;
        this.mes = mes;
        this.comm = comm;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
