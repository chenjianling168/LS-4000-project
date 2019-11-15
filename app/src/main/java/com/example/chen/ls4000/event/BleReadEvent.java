package com.example.chen.ls4000.event;

/**
 * Created by Administrator on 2017/3/12.
 */

public class BleReadEvent {
    String mes;
    int comm;
    byte[] bys;

    public byte[] getBys() {
        return bys;
    }

    public void setBys(byte[] bys) {
        this.bys = bys;
    }

    public int getComm() {
        return comm;
    }

    public void setComm(int comm) {
        this.comm = comm;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }


    public BleReadEvent() {
    }

    public BleReadEvent(String mes) {

        this.mes = mes;
    }


    public BleReadEvent(String mes, int comm) {
        this.mes = mes;
        this.comm = comm;
    }

    public BleReadEvent(String mes, int comm, byte[] bys) {
        this.mes = mes;
        this.comm = comm;
        this.bys = bys;
    }
}
