package com.example.chen.ls4000.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class Project implements Serializable{
//    private String proName;
//    private String batch;
//    private String bornTime;
//    private String untilTime;


    private String audit;//审核
    private String area;//区域代码
    private String batch;//项目批号
    private String proName;//项目名称
    private String bornTime;//生产日期
    private String shelfLife;//保质期
    private String incuTime;//温育时间
    private String addSample;//加样量
    private String ct1;//CT1/CT2/CT3间距
    private String ct2;
    private String ct3;
    private String cRefer; //参考值1
    private String cRefer2; //参考值2
    private String cRefer3; //参考值3
    private String backHigh;//本底最高值
    private String cMin;//C线最小值
    private String amount;
    private String data;
    private String proNum;//项目号
    private String addTime;//添加时间
    private boolean clickFlag;//是否选中
    private String unit;//样本单位1
    private String unit2;//样本单位2
    private String unit3;//样本单位3
    private  String cline;  //C线位置
    private String wholeBlood;      //全血
    private String serum;            //血清
    private String plasma;          //血浆
    private String urine;            //尿液
    private String other;           //其他


    public Project(String audit, String area, String batch, String proName, String bornTime, String shelfLife,
                   String incuTime, String addSample, String ct1, String ct2, String ct3, String cRefer,
                   String cRefer2, String cRefer3, String backHigh, String cMin, String amount, String data,
                   String proNum, String addTime, boolean clickFlag, String unit, String unit2, String unit3,
                   String cline, String wholeBlood, String serum, String plasma, String urine, String other) {
        this.audit = audit;
        this.area = area;
        this.batch = batch;
        this.proName = proName;
        this.bornTime = bornTime;
        this.shelfLife = shelfLife;
        this.incuTime = incuTime;
        this.addSample = addSample;
        this.ct1 = ct1;
        this.ct2 = ct2;
        this.ct3 = ct3;
        this.cRefer = cRefer;
        this.cRefer2 = cRefer2;
        this.cRefer3 = cRefer3;
        this.backHigh = backHigh;
        this.cMin = cMin;
        this.amount = amount;
        this.data = data;
        this.proNum = proNum;
        this.addTime = addTime;
        this.clickFlag = clickFlag;
        this.unit = unit;
        this.unit2 = unit2;
        this.unit3 = unit3;
        this.cline = cline;
        this.wholeBlood = wholeBlood;
        this.serum = serum;
        this.plasma = plasma;
        this.urine = urine;
        this.other = other;
    }

    public Project() {
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getBornTime() {
        return bornTime;
    }

    public void setBornTime(String bornTime) {
        this.bornTime = bornTime;
    }

    public String getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(String shelfLife) {
        this.shelfLife = shelfLife;
    }

    public String getIncuTime() {
        return incuTime;
    }

    public void setIncuTime(String incuTime) {
        this.incuTime = incuTime;
    }

    public String getAddSample() {
        return addSample;
    }

    public void setAddSample(String addSample) {
        this.addSample = addSample;
    }

    public String getCt1() {
        return ct1;
    }

    public void setCt1(String ct1) {
        this.ct1 = ct1;
    }

    public String getCt2() {
        return ct2;
    }

    public void setCt2(String ct2) {
        this.ct2 = ct2;
    }

    public String getCt3() {
        return ct3;
    }

    public void setCt3(String ct3) {
        this.ct3 = ct3;
    }

    public String getcRefer() {
        return cRefer;
    }

    public void setcRefer(String cRefer) {
        this.cRefer = cRefer;
    }

    public String getcRefer2() {
        return cRefer2;
    }

    public void setcRefer2(String cRefer2) {
        this.cRefer2 = cRefer2;
    }

    public String getcRefer3() {
        return cRefer3;
    }

    public void setcRefer3(String cRefer3) {
        this.cRefer3 = cRefer3;
    }

    public String getBackHigh() {
        return backHigh;
    }

    public void setBackHigh(String backHigh) {
        this.backHigh = backHigh;
    }

    public String getcMin() {
        return cMin;
    }

    public void setcMin(String cMin) {
        this.cMin = cMin;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getProNum() {
        return proNum;
    }

    public void setProNum(String proNum) {
        this.proNum = proNum;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public boolean isClickFlag() {
        return clickFlag;
    }

    public void setClickFlag(boolean clickFlag) {
        this.clickFlag = clickFlag;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit2() {
        return unit2;
    }

    public void setUnit2(String unit2) {
        this.unit2 = unit2;
    }

    public String getUnit3() {
        return unit3;
    }

    public void setUnit3(String unit3) {
        this.unit3 = unit3;
    }

    public String getCline() {
        return cline;
    }

    public void setCline(String cline) {
        this.cline = cline;
    }

    public String getWholeBlood() {
        return wholeBlood;
    }

    public void setWholeBlood(String wholeBlood) {
        this.wholeBlood = wholeBlood;
    }

    public String getSerum() {
        return serum;
    }

    public void setSerum(String serum) {
        this.serum = serum;
    }

    public String getPlasma() {
        return plasma;
    }

    public void setPlasma(String plasma) {
        this.plasma = plasma;
    }

    public String getUrine() {
        return urine;
    }

    public void setUrine(String urine) {
        this.urine = urine;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
