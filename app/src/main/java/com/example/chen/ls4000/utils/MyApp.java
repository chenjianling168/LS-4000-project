package com.example.chen.ls4000.utils;

import android.app.Application;

import com.example.chen.ls4000.bean.Sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rego.printlib.export.regoPrinter;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class MyApp extends Application {

    private regoPrinter printer;
    private int printState;
    private String printName="RG-MTP58B";
    private int x;

    public static Map<Integer,Boolean> selectMap;
    public static Map<Integer,Boolean> selectMapPro;
    public static Map<Integer,Boolean> selectMapInstr;
    private List<Sample> testPeoples,resultPeoples;
    private int testFlag;//0为未收到已满信息，1为已收到已满信息
    private int testFlap;//0为未点击开始测试，1为已点击开始检测
    private int conFlag;//0为未质控，1为正在质控
    private int quanFlap; //0为正常电量 1为低电量
    private String cardIDFlap; //1为省份证自动获取 ,0为手动输入
    private String amountFlap; //连卡判断 1为单联卡,2为双联卡,3为三联卡
    private String concenFlap; //浓度判断 1为1个结果,2为2个结果,3为3个结果
    private boolean insertBool;//false为样本正在进样，true为无样本进样
    private boolean deleteBool;//false为样本为清除，true为样本已清除
    private boolean scanBool;//false为扫码不正确，true为扫码正确
    private boolean screenBool;//屏幕状态
    private boolean powerBool;//是否可以断电
    private int peoNum;



    //清空 testPeoples
    public  void clearTestPeoples(){
        for (int i = 0;i<testPeoples.size();i++){
            testPeoples.remove(0);
        }
    }

    public int getPeoNum() {
        return peoNum;
    }

    public void setPeoNum(int peoNum) {
        this.peoNum = peoNum;
    }

    public regoPrinter getObject() {
        return printer;
    }

    public void setObject() {
        printer = new regoPrinter(this);
    }

    public String getPrintName() {
        return printName;
    }

    public void setPrintName(String name) {
        printName = name;
    }
    public void setPrintState(int state) {
        printState = state;

    }

    public String getCardIDFlap() {
        return cardIDFlap;
    }

    public void setCardIDFlap(String cardIDFlap) {
        this.cardIDFlap = cardIDFlap;
    }

    public int getPrintState() {
        return printState;
    }

    public String getConcenFlap() {
        return concenFlap;
    }

    public void setConcenFlap(String concenFlap) {
        this.concenFlap = concenFlap;
    }

    public boolean isScanBool() {
        return scanBool;
    }

    public void setScanBool(boolean scanBool) {
        this.scanBool = scanBool;
    }

    public List<Sample> getTestPeoples() {
        return testPeoples;
    }

    public void setTestPeoples(List<Sample> testPeoples) {
        this.testPeoples = testPeoples;
    }

    public int getTestFlag() {
        return testFlag;
    }

    public void setTestFlag(int testFlag) {
        this.testFlag = testFlag;
    }

    public int getTestFlap() {
        return testFlap;
    }

    public void setTestFlap(int testFlap) {
        this.testFlap = testFlap;
    }

    public int getQuanFlap() {
        return quanFlap;
    }

    public void setQuanFlap(int quanFlap) {
        this.quanFlap = quanFlap;
    }

    public String getAmountFlap() {
        return amountFlap;
    }

    public void setAmountFlap(String amountFlap) {
        this.amountFlap = amountFlap;
    }

    public List<Sample> getResultPeoples() {
        return resultPeoples;
    }

    public void setResultPeoples(List<Sample> resultPeoples) {
        this.resultPeoples = resultPeoples;
    }

    public int getConFlag() {
        return conFlag;
    }

    public void setConFlag(int conFlag) {
        this.conFlag = conFlag;
    }

    public boolean isInsertBool() {
        return insertBool;
    }

    public void setInsertBool(boolean insertBool) {
        this.insertBool = insertBool;
    }

    public boolean isDeleteBool() {
        return deleteBool;
    }

    public void setDeleteBool(boolean deleteBool) {
        this.deleteBool = deleteBool;
    }

    public boolean isScreenBool() {
        return screenBool;
    }

    public void setScreenBool(boolean screenBool) {
        this.screenBool = screenBool;
    }

    public boolean isPowerBool() {
        return powerBool;
    }

    public void setPowerBool(boolean powerBool) {
        this.powerBool = powerBool;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        testPeoples = new ArrayList<Sample>();
        Sample sample = new Sample();
        sample.setFlagNull(true);
        //testPeoples.add(sample);
        resultPeoples = new ArrayList<Sample>();
        testFlap = 0;
        testFlag = 0;
        conFlag = 0;
        quanFlap = 0;
        cardIDFlap = null;
        amountFlap = null;
        concenFlap = null;
        insertBool = false;
        deleteBool = false;
        printState = 0;
        screenBool = true;
        peoNum = 0;
        powerBool = true;
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(MyContextWrapper.wrap(base, "en"));
//    }
}
