package com.example.chen.ls4000.bean;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class Pro {
    private String referLow;    //参考下限
    private String referHigh;   //参考上限
    private String testLow;     //检测下限
    private String testHigh;    //检测上限
    private String unit;        //检测单位号码
    private String tc;          //T/C临界值
    private String equa;        //拟合方程种类代号
    private String a1High;      //高于临界值四参数a
    private String b1High;      //高于临界值四参数b
    private String c1High;      //高于临界值四参数c
    private String d1High;      //高于临界值四参数d
    private String a1Low;       //低于临界值四参数a
    private String b1Low;       //低于临界值四参数b
    private String c1Low;       //低于临界值四参数c
    private String d1Low;       //低于临界值四参数d
    private String wholeBlood;      //全血
    private String serum;            //血清
    private String plasma;          //血浆
    private String urine;            //尿液
    private String other;           //其他

    public Pro(String referLow, String referHigh, String testLow, String testHigh, String unit, String tc, String equa,
               String a1High, String b1High, String c1High, String d1High, String a1Low,
               String b1Low, String c1Low, String d1Low, String wholeBlood, String serum, String plasma, String urine, String other) {
        this.referLow = referLow;
        this.referHigh = referHigh;
        this.testLow = testLow;
        this.testHigh = testHigh;
        this.unit = unit;
        this.tc = tc;
        this.equa = equa;
        this.a1High = a1High;
        this.b1High = b1High;
        this.c1High = c1High;
        this.d1High = d1High;
        this.a1Low = a1Low;
        this.b1Low = b1Low;
        this.c1Low = c1Low;
        this.d1Low = d1Low;
        this.wholeBlood = wholeBlood;
        this.serum = serum;
        this.plasma = plasma;
        this.urine = urine;
        this.other = other;
    }

    public Pro() {
    }

    public String getReferLow() {
        return referLow;
    }

    public void setReferLow(String referLow) {
        this.referLow = referLow;
    }

    public String getReferHigh() {
        return referHigh;
    }

    public void setReferHigh(String referHigh) {
        this.referHigh = referHigh;
    }

    public String getTestLow() {
        return testLow;
    }

    public void setTestLow(String testLow) {
        this.testLow = testLow;
    }

    public String getTestHigh() {
        return testHigh;
    }

    public void setTestHigh(String testHigh) {
        this.testHigh = testHigh;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getEqua() {
        return equa;
    }

    public void setEqua(String equa) {
        this.equa = equa;
    }

    public String getA1High() {
        return a1High;
    }

    public void setA1High(String a1High) {
        this.a1High = a1High;
    }

    public String getB1High() {
        return b1High;
    }

    public void setB1High(String b1High) {
        this.b1High = b1High;
    }

    public String getC1High() {
        return c1High;
    }

    public void setC1High(String c1High) {
        this.c1High = c1High;
    }

    public String getD1High() {
        return d1High;
    }

    public void setD1High(String d1High) {
        this.d1High = d1High;
    }

    public String getA1Low() {
        return a1Low;
    }

    public void setA1Low(String a1Low) {
        this.a1Low = a1Low;
    }

    public String getB1Low() {
        return b1Low;
    }

    public void setB1Low(String b1Low) {
        this.b1Low = b1Low;
    }

    public String getC1Low() {
        return c1Low;
    }

    public void setC1Low(String c1Low) {
        this.c1Low = c1Low;
    }

    public String getD1Low() {
        return d1Low;
    }

    public void setD1Low(String d1Low) {
        this.d1Low = d1Low;
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
