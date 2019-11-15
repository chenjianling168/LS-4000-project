package com.example.chen.ls4000.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Spinner;

/**
 * Created by Administrator on 2017/7/4 0004.
 */

public class SharedHelper {

    private Context context;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public SharedHelper() {
    }

    public SharedHelper(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        editor = sp.edit();
    }


    public void savePrint(boolean autoPrint, boolean hosPrint, boolean departPrint,
                          boolean docPrint,boolean audiPrint, boolean datePrint, boolean timePrint,
                          boolean referPrint, boolean samplePrint) {
        editor.putBoolean("autoprint", autoPrint);
        //editor.putBoolean("pushprint",pushPrint);
        editor.putBoolean("hosprint", hosPrint);
        editor.putBoolean("audprint", audiPrint);
        editor.putBoolean("departprint", departPrint);
        editor.putBoolean("docprint", docPrint);
        editor.putBoolean("dateprint", datePrint);
        editor.putBoolean("timeprint", timePrint);
        editor.putBoolean("referprint", referPrint);
        editor.putBoolean("samnumprint", samplePrint);
        editor.commit();
    }

    public boolean readAutoPrint() {
        return sp.getBoolean("autoprint", true);
    }

    public boolean readHosPrint() {
        return sp.getBoolean("hosprint", true);
    }

    public boolean readAudPrint() {
        return sp.getBoolean("audprint", true);
    }

    public boolean readDepartPrint() {
        return sp.getBoolean("departprint", true);
    }

    public boolean readDocPrint() {
        return sp.getBoolean("docprint", true);
    }

    public boolean readDatePrint() {
        return sp.getBoolean("dateprint", true);
    }

    public boolean readTimePrint() {
        return sp.getBoolean("timeprint", true);
    }

    public boolean readReferPrint() {
        return sp.getBoolean("referprint", false);
    }

    public boolean readSamnumPrint() {
        return sp.getBoolean("samnumprint", false);
    }

//    public Map<String,Boolean> readPrint(){
//        Map<String,Boolean> data = new HashMap<String,Boolean>();
//        data.put("autoprint",sp.getBoolean("autoprint", true));
//      //  data.put("pushprint",sp.getBoolean("pushprint", false));
//        data.put("hosprint",sp.getBoolean("hosprint",true));
//        data.put("departprint",sp.getBoolean("departprint",true));
//        data.put("docprint",sp.getBoolean("docprint",true));
//        data.put("dateprint",sp.getBoolean("dateprint",true));
//        data.put("timeprint",sp.getBoolean("timeprint",true));
//        data.put("referprint",sp.getBoolean("referprint",false));
//        data.put("sampleprint",sp.getBoolean("sampleprint",false));
//        return data;
//    }


    /**
     * 储存结果系数
     */
    public void saveCoeff(String coeff) {
        editor.putString("Coefficient", coeff);
        editor.commit();
    }

    public String readCoeff() {
        return sp.getString("Coefficient", "0.97");
    }

    /**
     * 储存pwm系数
     */
    public void savePwm(String pwm) {
        editor.putString("pwm", pwm);
        editor.commit();
    }

    public String readPwm() {
        return sp.getString("pwm", "640");
    }

    /**
     * 储存采集点系数
     */
    public void saveCollec(String saveCollec) {
        editor.putString("collection", saveCollec);
        editor.commit();
    }

    public String readsaveCollec() {
        return sp.getString("collection", "840");
    }

    /**
    * 储存序列号
     */
    public void saveserial(String serial) {
        editor.putString("serial", serial);
        editor.commit();
    }

    public String readserial() {
        return sp.getString("serial", "");
    }

    public void saveUser(String hos, String depart, String doc1,String doc2,String doc3,
                         String aud1,String aud2,String type) {
        editor.putString("hosuser", hos);
        editor.putString("departuser", depart);
        editor.putString("docuser1", doc1);
        editor.putString("docuser2", doc2);
        editor.putString("docuser3", doc3);
        editor.putString("auditor1", aud1);
        editor.putString("auditor2", aud2);
        editor.putString("type" , type);
        editor.commit();
    }

    public String readHosUser() {
        return sp.getString("hosuser", "");
    }

    public String readDepartUser() {
        return sp.getString("departuser", "");
    }

    public String readDocUser1() {
        return sp.getString("docuser1", "");
    }

    public String readDocUser2() {
        return sp.getString("docuser2", "");
    }

    public String readDocUser3() {
        return sp.getString("docuser3", "");
    }

    public String readAuditor1(){
        return sp.getString("auditor1","");
    }

    public String readAuditor2(){
        return sp.getString("auditor2","");
    }

    public String readType(){
        return sp.getString("type" , "");
    }

    public void saveStaff(String textT1,String textT2,String textT3,String textT1C,String textT2C,String textT3C
    ,String placeT1,String placeT2,String placeT3,String peakT1,String peakT2,String peakT3,
                          String textC,String placeC,String peakC){
        editor.putString("textT1",textT1);
        editor.putString("textT2",textT2);
        editor.putString("textT3",textT3);

        editor.putString("textT1C",textT1C);
        editor.putString("textT2C",textT2C);
        editor.putString("textT3C",textT3C);

        editor.putString("placeT1",placeT1);
        editor.putString("placeT2",placeT2);
        editor.putString("placeT3",placeT3);

        editor.putString("peakT1",peakT1);
        editor.putString("peakT2",peakT2);
        editor.putString("peakT3",peakT3);

        editor.putString("textC",textC);
        editor.putString("placeC",placeC);
        editor.putString("peakC",peakC);
        editor.commit();
    }

    public String readtextT1(){
        return sp.getString("textT1","");
    }

    public String readtextT2(){
        return sp.getString("textT2","");
    }

    public String readtextT3(){
        return sp.getString("textT3","");
    }

    public String readtextT1C(){
        return sp.getString("textT1C","");
    }

    public String readtextT2C(){
        return sp.getString("textT2C","");
    }

    public String readtextT3C(){
        return sp.getString("textT3C","");
    }

    public String readplaceT1(){
        return sp.getString("placeT1","");
    }

    public String readplaceT2(){
        return sp.getString("placeT2","");
    }

    public String readplaceT3(){
        return sp.getString("placeT3","");
    }

    public String readpeakT1(){
        return sp.getString("peakT1","");
    }

    public String readpeakT2(){
        return sp.getString("peakT2","");
    }

    public String readpeakT3(){
        return sp.getString("peakT3","");
    }

    public String readtextC(){
        return sp.getString("textC","");
    }

    public String readplaceC(){
        return sp.getString("placeC","");
    }

    public String readpeakC(){
        return sp.getString("peakC","");
    }



    public void saveUpdateFlag(String flag){
        editor.putString("updateflag",flag);
        editor.commit();
    }

    public String readUpdateFlag(){
        return sp.getString("updateflag","0");
    }

    public void savevirtual_key(String virtual_key) {
        editor.putString("virtual_key", virtual_key);
        editor.commit();
    }

    public String readvirtual_key() {
        return sp.getString("virtual_key", "0");
    }


    public void saveGps(boolean flag) {
        editor.putBoolean("gps", flag);
        editor.commit();
    }

    public boolean readGps() {
        return sp.getBoolean("gps", false);
    }

    public void saveWifi(boolean wifi) {
        editor.putBoolean("wifi", wifi);
        editor.commit();
    }

    public boolean readWifi() {
        return sp.getBoolean("wifi", false);
    }

    public void saveBluetooth(boolean bluetooth) {
        editor.putBoolean("bluetooth", bluetooth);
        editor.commit();
    }

    public boolean readBluetooth() {
        return sp.getBoolean("bluetooth", false);
    }


    public void saveCardNum(int num) {
        editor.putInt("cardnum", num);
        editor.commit();
    }

    public int readCardNum() {
        return sp.getInt("cardnum", 0);
    }

    /**
     * 存储数据库数据数量
     */
    public void savePeopleNum(int num) {
        editor.putInt("peoplenum", num);
        editor.commit();
    }

    public int readPeopleNum() {
        return sp.getInt("peoplenum", 0);
    }

    public void saveProjectNum(int num) {
        editor.putInt("projectnum", num);
        editor.commit();
    }

    public int readProjectNum() {
        return sp.getInt("projectnum", 0);
    }

    public void saveInstrument(int num) {
        editor.putInt("instrument", num);
        editor.commit();
    }

    public int readInstrument() {
        return sp.getInt("instrumentnum", 0);
    }

    /**
     * 存储实时倒计时
     *
     * @param time
     */
    public void saveTime(int time) {
        editor.putInt("time", time);
        editor.commit();
    }

    public int readTime() {
        return sp.getInt("time", 0);
    }

    /**
     * 存储质控高值
     *
     * @param highCon
     */
    public void saveHighCon(String highCon) {
        editor.putString("highcon", highCon);
        editor.commit();
    }

    public String readHighCon() {
        return sp.getString("highcon", "1");
    }

    /**
     * 存储质控低值
     *
     * @param lowCon
     */
    public void saveLowCon(String lowCon) {
        editor.putString("lowcon", lowCon);
        editor.commit();
    }

    public String readLowCon() {
        return sp.getString("lowcon", "0");
    }

    public String readPro(int id) {
        String pro = "";
        switch (id) {
            case 1:
                pro = sp.getString("pro1", "");
                break;
            case 2:
                pro = sp.getString("pro2", "");
                break;
            case 3:
                pro = sp.getString("pro3", "");
                break;
            case 4:
                pro = sp.getString("pro4", "");
                break;
            case 5:
                pro = sp.getString("pro5", "");
                break;
            case 6:
                pro = sp.getString("pro6", "");
                break;
            case 7:
                pro = sp.getString("pro7", "");
                break;
            case 8:
                pro = sp.getString("pro8", "");
                break;
        }
        return pro;
    }

    /**
     * 存储开始时间
     *
     * @param time
     */
    public void saveBeginTime(long time) {
        editor.putLong("begintime", time);
        editor.commit();
    }

    public long readBeginTime() {
        return sp.getLong("begintime", 0);
    }

    /**
     * 存储结束时间
     *
     * @param time
     */
    public void saveEndTime(long time) {
        editor.putLong("endtime", time);
        editor.commit();
    }

    public long readEndTime() {
        return sp.getLong("endtime", 0);
    }

    /**
     * 存储项目
     *
     * @param str
     */
    public void savePro(String str) {
        editor.putString("project", str);
        editor.commit();
    }

    public String readPro() {
        return sp.getString("project", "");
    }


    /**
     * 存储批次号
     *
     * @param bat
     */
    public void saveBatch(String bat) {
        editor.putString("batch", bat);
        editor.commit();
    }

    public String readBatch() {
        return sp.getString("batch", "");
    }

    /**
     * @param lan
     */
    public void saveLan(String lan) {
        editor.putString("language", lan);
        editor.commit();
    }

    public String readLan() {
        return sp.getString("language", "zh");
    }


    /**
     * 存储检测状态       1是即使检测  2孵育检测
     */
    public void saveFlap(String Flap) {
        editor.putString("Flap", Flap);
        editor.commit();
    }

    public String readFlap() {
        return sp.getString("Flap", "0");
    }

    /**
     *
     * @param flag  有无logo
     */
    public void saveLogo(boolean flag){
        editor.putBoolean("logo",flag);
        editor.commit();
    }

    public boolean readLogo(){
        return sp.getBoolean("logo",true);
    }

    /**
     * 存储是否显示试剂质控的状态
     *
     * @param flag
     */
    public void saveCon(boolean flag) {
        editor.putBoolean("conmana", flag);
        editor.commit();
    }

    public boolean readCon() {
        return sp.getBoolean("conmana", false);
    }

    /**
     * 存储打开上位机通信接口的状态
     *
     * @param flag
     */
    public void saveComputer(boolean flag) {
        editor.putBoolean("computer", flag);
        editor.commit();
    }

    public boolean readComputer() {
        return sp.getBoolean("computer", false);
    }

    /**
     * 存储打开LIS接口的状态
     *
     * @param flag
     */
    public void saveLis(boolean flag) {
        editor.putBoolean("lis", flag);
        editor.commit();
    }

    public boolean readLis() {
        return sp.getBoolean("lis", false);
    }

    /**
     * PCT
     *
     * @param flag
     */
    public void savePCT(boolean flag) {
        editor.putBoolean("pct", flag);
        editor.commit();
    }

    public boolean readPCT() {
        return sp.getBoolean("pct", false);
    }

    /**
     * CRP
     *
     * @param flag
     */
    public void saveCRP(boolean flag) {
        editor.putBoolean("crp", flag);
        editor.commit();
    }

    public boolean readCRP() {
        return sp.getBoolean("crp", false);
    }

    /**
     * NT_proBNP
     *
     * @param flag
     */
    public void saveNT_proBNP(boolean flag) {
        editor.putBoolean("nt_probnp", flag);
        editor.commit();
    }

    public boolean readNT_proBNP() {
        return sp.getBoolean("nt_probnp", false);
    }

    /**
     * CTNI
     *
     * @param flag
     */
    public void saveCTNI(boolean flag) {
        editor.putBoolean("cTnl", flag);
        editor.commit();
    }

    public boolean readCTNI() {
        return sp.getBoolean("cTnl", false);
    }

    /**
     * CK_MB
     *
     * @param flag
     */
    public void saveCK_MB(boolean flag) {
        editor.putBoolean("ck_mb", flag);
        editor.commit();
    }

    public boolean readCK_MB() {
        return sp.getBoolean("ck_mb", false);
    }

    /**
     * D_DIMER
     *
     * @param flag
     */
    public void saveD_DIMER(boolean flag) {
        editor.putBoolean("dimer", flag);
        editor.commit();
    }

    public boolean readD_DIMER() {
        return sp.getBoolean("dimer", false);
    }

    /**
     * HBALC
     *
     * @param flag
     */
    public void saveHBALC(boolean flag) {
        editor.putBoolean("hbalc", flag);
        editor.commit();
    }

    public boolean readHBALC() {
        return sp.getBoolean("hbalc", false);
    }

    /**
     * 存储WIFI状态
     */
    public void saveWifiState(boolean wifi) {
        editor.putBoolean("Wifi", wifi);
        editor.commit();
    }

    public boolean readWifiState() {
        return sp.getBoolean("Wifi", false);
    }


    /**
     * 存储蓝牙状态
     */
    public void saveBluetoothState(boolean bluetooth) {
        editor.putBoolean("Bluetooth", bluetooth);
        editor.commit();
    }

    public boolean readBluetoothState() {
        return sp.getBoolean("Bluetooth", false);
    }


    /**
     * 软件跟新
     * @param flag
     */
    public void saveUpgradeFlag(String flag){
        editor.putString("upgradeflag",flag);
        editor.commit();
    }

    public String readUpgradeFlag(){
        return sp.getString("upgradeflag","0");
    }


    /*工作人员调试连卡的选择*/
    public void saveAmount(String amount){
        editor.putString("amount",amount);
        editor.commit();
    }

    public String readAmount(){
        return sp.getString("amount","1");
    }

    /*判断是选择普通用户还是VIP用户*/
    public void saveAdminChoice(String choice){
        editor.putString("choice",choice);
        editor.commit();
    }

    /*1：是普通用户，2：是VIP用户*/
    public String readAdminChoice(){
        return sp.getString("choice","");
    }

    /*是否记住密码*/
    public void saveRememberPW(boolean pass){
        editor.putBoolean("pass",pass);
        editor.commit();
    }

    public boolean readRememberPW(){
        return sp.getBoolean("pass",false);
    }


    /*Admin用户名*/
    public void saveAdmin(String admin){
        editor.putString("admin",admin);
        editor.commit();
    }

    public String readAdmin(){
        return sp.getString("admin","user");
    }

    /*PassWord密码*/
    public void savePassWord(String password){
        editor.putString("password",password);
        editor.commit();
    }

    public String readPassWord(){
        return sp.getString("password","ly123456");
    }


    /*VIPAdmin用户名*/
    public void saveVIPAdmin(String vipadmin){
        editor.putString("vipadmin",vipadmin);
        editor.commit();
    }

    public String readVIPAdmin(){
        return sp.getString("vipadmin","admin");
    }

    /*PassWord密码*/
    public void saveVIPPassWord(String vippassword){
        editor.putString("vippassword",vippassword);
        editor.commit();
    }

    public String readVIPPassWord(){
        return sp.getString("vippassword","LY58775705");
    }



































}
