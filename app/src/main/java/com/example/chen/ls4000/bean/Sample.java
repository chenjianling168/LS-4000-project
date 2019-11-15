package com.example.chen.ls4000.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/30 0030.
 */

public class Sample implements Serializable{
    String name,age,gender,IDNum,samNum,proName,proNum,samType,testPeo,
            concen,refer,unit,testTime,hos,depart,doc,nid,audPeo,refer2,unit2,refer3,unit3;
    boolean flagClick,flagSend,flagNull;
    Project project;
    String curveData;

    String cPosi;   //c位置
    String t1Posi;
    String t2Posi;
    String t3Posi;
    String cRefer;  //c线参考位置
    String t1Refer;
    String t2Refer;
    String t3Refer;
    String state;


    /**
     * @param name 姓名
     * @param age 年龄
     * @param gender 性别
     * @param IDNum 身份证号
     * @param samNum 样本号
     * @param proName 项目名称
     * @param proNum 项目号
     * @param samType 样本类型
     * @param testPeo 检测人
     * @param concen 浓度
     * @param refer 参考值
     * @param unit 单位
     * @param refer2 参考值
     * @param unit2 单位
     * @param refer3 参考值
     * @param unit3 单位
     * @param testTime 检测时间
     * @param hos 医院名称
     * @param depart 科室名称
     * @param audPeo 审核人
     * @param doc 医生名称
     * @param nid  唯一码
     * @param flagClick 是否选中
     * @param flagSend 是否发送
     * @param flagNull 是否为空
     * @param curveData 曲线数据
     * @param state 状态
     */

    public Sample(String name, String age, String gender, String IDNum, String samNum, String proName,
                  String proNum, String samType, String testPeo, String concen, String refer, String unit,
                  String testTime, String hos, String depart, String doc, String nid, String audPeo, String refer2,
                  String unit2, String refer3, String unit3, boolean flagClick, boolean flagSend,
                  boolean flagNull, Project project, String curveData, String cPosi, String t1Posi, String t2Posi,
                  String t3Posi, String cRefer, String t1Refer, String t2Refer, String t3Refer, String state) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.IDNum = IDNum;
        this.samNum = samNum;
        this.proName = proName;
        this.proNum = proNum;
        this.samType = samType;
        this.testPeo = testPeo;
        this.concen = concen;
        this.refer = refer;
        this.unit = unit;
        this.testTime = testTime;
        this.hos = hos;
        this.depart = depart;
        this.doc = doc;
        this.nid = nid;
        this.audPeo = audPeo;
        this.refer2 = refer2;
        this.unit2 = unit2;
        this.refer3 = refer3;
        this.unit3 = unit3;
        this.flagClick = flagClick;
        this.flagSend = flagSend;
        this.flagNull = flagNull;
        this.project = project;
        this.curveData = curveData;
        this.cPosi = cPosi;
        this.t1Posi = t1Posi;
        this.t2Posi = t2Posi;
        this.t3Posi = t3Posi;
        this.cRefer = cRefer;
        this.t1Refer = t1Refer;
        this.t2Refer = t2Refer;
        this.t3Refer = t3Refer;
        this.state = state;
    }



    public Sample() {
    }

    public String getcRefer() {
        return cRefer;
    }

    public void setcRefer(String cRefer) {
        this.cRefer = cRefer;
    }

    public String getT1Refer() {
        return t1Refer;
    }

    public void setT1Refer(String t1Refer) {
        this.t1Refer = t1Refer;
    }

    public String getT2Refer() {
        return t2Refer;
    }

    public void setT2Refer(String t2Refer) {
        this.t2Refer = t2Refer;
    }

    public String getT3Refer() {
        return t3Refer;
    }

    public void setT3Refer(String t3Refer) {
        this.t3Refer = t3Refer;
    }

    public String getcPosi() {
        return cPosi;
    }

    public void setcPosi(String cPosi) {
        this.cPosi = cPosi;
    }

    public String getT1Posi() {
        return t1Posi;
    }

    public void setT1Posi(String t1Posi) {
        this.t1Posi = t1Posi;
    }

    public String getT2Posi() {
        return t2Posi;
    }

    public void setT2Posi(String t2Posi) {
        this.t2Posi = t2Posi;
    }

    public String getT3Posi() {
        return t3Posi;
    }

    public void setT3Posi(String t3Posi) {
        this.t3Posi = t3Posi;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean isFlagClick() {
        return flagClick;
    }

    public void setFlagClick(boolean flagClick) {
        this.flagClick = flagClick;
    }

    public boolean isFlagSend() {
        return flagSend;
    }

    public void setFlagSend(boolean flagSend) {
        this.flagSend = flagSend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIDNum() {
        return IDNum;
    }

    public void setIDNum(String IDNum) {
        this.IDNum = IDNum;
    }

    public String getSamNum() {
        return samNum;
    }

    public void setSamNum(String samNum) {
        this.samNum = samNum;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProNum() {
        return proNum;
    }

    public void setProNum(String proNum) {
        this.proNum = proNum;
    }

    public String getSamType() {
        return samType;
    }

    public void setSamType(String samType) {
        this.samType = samType;
    }


    public String getAudPeo() {
        return audPeo;
    }

    public void setAudPeo(String audPeo) {
        this.audPeo = audPeo;
    }

    public String getTestPeo() {
        return testPeo;
    }

    public void setTestPeo(String testPeo) {
        this.testPeo = testPeo;
    }

    public String getConcen() {
        return concen;
    }

    public void setConcen(String concen) {
        this.concen = concen;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getUnit() {
        return unit;
    }

    public String getRefer2() {
        return refer2;
    }

    public void setRefer2(String refer2) {
        this.refer2 = refer2;
    }

    public String getUnit2() {
        return unit2;
    }

    public void setUnit2(String unit2) {
        this.unit2 = unit2;
    }

    public String getRefer3() {
        return refer3;
    }

    public void setRefer3(String refer3) {
        this.refer3 = refer3;
    }

    public String getUnit3() {
        return unit3;
    }

    public void setUnit3(String unit3) {
        this.unit3 = unit3;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public String getHos() {
        return hos;
    }

    public void setHos(String hos) {
        this.hos = hos;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public boolean isFlagNull() {
        return flagNull;
    }

    public void setFlagNull(boolean flagNull) {
        this.flagNull = flagNull;
    }

    public String getCurveData() {
        return curveData;
    }

    public void setCurveData(String curveData) {
        this.curveData = curveData;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
