package com.example.chen.ls4000.event;

import com.example.chen.ls4000.bean.Sample;

/**
 * Created by Administrator on 2017/3/12.
 */

public class CardEvent {
    String mes;
    byte comm;
    byte[] bys;
    Sample sample;

    public byte[] getBys() {
        return bys;
    }

    public void setBys(byte[] bys) {
        this.bys = bys;
    }

    public byte getComm() {
        return comm;
    }

    public void setComm(byte comm) {
        this.comm = comm;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public CardEvent() {
    }

    public CardEvent(String mes) {

        this.mes = mes;
    }

    public CardEvent(String mes, byte comm) {
        this.mes = mes;
        this.comm = comm;
    }

    public CardEvent(String mes, byte comm, byte[] bys) {
        this.mes = mes;
        this.comm = comm;
        this.bys = bys;
    }
}
