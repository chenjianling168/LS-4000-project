package com.example.chen.ls4000.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class Instrument implements Serializable {
    private String seqNum;
    private String time;        //时间
    private String result;        //结果
    private boolean clickFlag;

    public Instrument(String seqNum, String time, String result, boolean clickFlag) {
        this.seqNum = seqNum;
        this.time = time;
        this.result = result;
        this.clickFlag = clickFlag;
    }

    public Instrument() {
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }

    public boolean isClickFlag() {
        return clickFlag;
    }

    public void setClickFlag(boolean clickFlag) {
        this.clickFlag = clickFlag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
