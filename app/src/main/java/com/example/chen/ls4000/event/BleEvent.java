package com.example.chen.ls4000.event;

import com.example.chen.ls4000.bean.Instrument;
import com.example.chen.ls4000.bean.Sample;

/**
 * Created by Administrator on 2017/3/12.
 */

public class BleEvent {
    String mes;
    int comm;
    byte[] bys;
    Sample sample;
    Instrument instrument;

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

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

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public BleEvent() {
    }

    public BleEvent(String mes) {

        this.mes = mes;
    }

    public BleEvent(String mes, int comm) {
        this.mes = mes;
        this.comm = comm;
    }

    public BleEvent(String mes, int comm, byte[] bys) {
        this.mes = mes;
        this.comm = comm;
        this.bys = bys;
    }
}
