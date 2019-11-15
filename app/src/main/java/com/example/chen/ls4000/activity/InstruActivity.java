package com.example.chen.ls4000.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.ls4000.R;
import com.example.chen.ls4000.event.BleMethodCode;
import com.example.chen.ls4000.utils.MyApp;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.disposables.Disposable;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018-10-16.
 * 关于仪器
 */

public class InstruActivity extends Activity implements View.OnClickListener{
    private ImageView back;
    private Button upgrade;
    private SharedHelper sh;
    private TextView serial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instru);

        hideBottomUIMenu();

        back = (ImageView) findViewById(R.id.id_instru_back);
        upgrade = (Button)findViewById(R.id.id_instru_upgrade);
        serial = (TextView)findViewById(R.id.id_instru_serial);


        back.setOnClickListener(this);
        upgrade.setOnClickListener(this);

        Context context = getApplicationContext();
        sh = new SharedHelper(context);
        serial.setText(sh.readserial());
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.id_instru_back:
                intent = new Intent(InstruActivity.this,SettingsActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.id_instru_upgrade:
                try {
                    upUpgrade();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //软件跟新
    private void upUpgrade() throws Exception{

        //  U盘路径  /mnt/media_rw/3FFF-1605/chen/LS2100       LS4000\内部存储设备\LS4000\apk
        String apkPath = "/storage/emulated/0/LS4000/apk";   //设置本地APK的根目录
        Log.d(TAG, "initView: -当前版本号-" + getVersionName());



        String name = getApkName(apkPath);
        if(name != null){
            apkPath =  apkPath + "/" + name;    //APK的绝对路径
            Log.d(TAG, "initView:APK路径: " + apkPath);
            apkInfo(apkPath);
            updataApk(apkPath);
        }else{
            ToastUtil.showToast(this,"没找到apk文件",2000);
        }
    }

    /**
     * 获取APK文件的名字
     *
     * @param usbPath APK在U盘里的根目录
     */
    private String getApkName(String usbPath) {
        ArrayList<String> datas = new ArrayList<String>();
        File file = new File(usbPath);
       // File file = Environment.getExternalStorageDirectory();
        if(file.exists()){  //判断是否有指定文件夹
            // 得到该路径文件夹下所有的文件
            File[] files = file.listFiles();
            // 将所有的文件存入ArrayList中,并过滤所有apk格式的文件
            if(files != null){
                for (File file1: files) {
                    //判断是否是要更新的apk文件  1.是否是apk文件 2.更新apk前半部分名称是否相同 (UpdateApk)
                    if(checkIsApkFile(file1.getPath())&&file1.getName().substring(0,9).equals("UpdateApk")){
                        datas.add(file1.getName()); //添加到本地数组中
                        Log.i(TAG, "ssss::: " + file1.getName());
                    }
                }
            }
            if(datas.size() < 1){
                Log.e(TAG, "getApkName: 文件夹里为null");
                return null;
            }else{
                Log.d(TAG, "getApkName: ---有这个安装包:" + datas.get(datas.size() - 1));
                return datas.get(datas.size() -1 );//返回当前数组中最后一个APK文件名
            }
        }else{
            ToastUtil.showToast(this,getString(R.string.string311),2000);
            return null;
        }

    }

    /**
     * 检查扩展名，判断是否是.apk文件
     *
     * @param fName 文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    public boolean checkIsApkFile(String fName){
        boolean isApkFile = false;

        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase();
        if(FileEnd.equals("apk")){
            isApkFile = true;
        }else {
            isApkFile = false;
        }
        return isApkFile;
    }

    //获取版本号
    public String getVersionName()throws  Exception{
        // 获取packagemanager的实例
        PackageManager packageManager = this.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(this.getPackageName(),0);
        return packInfo.versionName;
    }

    private String appName;
    private String version;
    private Drawable drawable1;
    private Drawable drawable2;
    private Disposable disposable;
    /**
     * 获APK包的信息:版本号,名称,图标 等..
     *
     * @param usbPath APK包的绝对路径
     */
    private void apkInfo(String usbPath) {
        Log.i(TAG, "apkInfo: ----");
        PackageManager pm = this.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(usbPath,PackageManager.GET_ACTIVITIES);
        if(pkgInfo != null){
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
             /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
             appInfo.sourceDir = usbPath;
            appInfo.publicSourceDir = usbPath;

            // 得到应用名
            String appName = pm.getApplicationLabel(appInfo).toString();
            this.appName =appName;

            // 得到包名
            String packageName = appInfo.packageName;

            // 得到版本信息
            String version = pkgInfo.versionName;
            this.version = version;

            /* icon1和icon2其实是一样的 */
            Drawable icon1 = pm.getApplicationIcon(appInfo);    // 得到图标信息
            Drawable icon2 = appInfo.loadIcon(pm);

            drawable1 = icon1;
            drawable2 = icon2;
            String pkgInfoStr = String.format("PackageName:%s, Vesion: %s, AppName: %s",
                    packageName,version,appName);
            Log.i(TAG, String.format("PkgInfo: %s", pkgInfoStr));
        }else{
            Log.e(TAG, "apkInfo: null");
        }
    }

    /**
     * 判断是否需要更新APK
     *
     * @param apkPath apk绝对路径
     * @throws Exception
     */
    private void updataApk(String apkPath) throws Exception {
        if(appName.equals(getResources().getString(R.string.app_name))){

            //比较版本号的大小~
            if(Integer.valueOf(getNumber(version)) > Integer.valueOf(getNumber(getVersionName()))){
                installApk(apkPath);
            }else{
                Log.e(TAG, "updataApk: ---APK文件的版本是过低");
                //ToastUtil.showToast(this,getString(R.string.string113),2000);
                installApk(apkPath);
            }
        }else{
            Log.e(TAG, "updataApk: ---这个APK文件不能用于本地更新");
            ToastUtil.showToast(this,getString(R.string.string337),2000);
        }
    }

    /**
     * 提取String中的数字
     *
     * @param s 字符串
     * @return
     */
    public String getNumber(String s){
        String regEx ="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        return m.replaceAll("").trim();
    }


    /**
     * 安装APK
     *
     * @param apkPath U盘APK文件绝对路径
     */
    private void installApk(final String apkPath) throws Exception {
        //关闭定时器
        if(disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }

        StringBuffer sb = new StringBuffer();
        sb.append(getString(R.string.string315));
        sb.append(getVersionName());
        sb.append(getString(R.string.string316));
        sb.append(version);
        sb.append( getString(R.string.string317));
        Dialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.string318))
                .setMessage(sb.toString())
                // 设置内容
                .setPositiveButton(getString(R.string.string319), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //apk文件的本地路径
                        File apkfile = new File(apkPath);
                        //会根据用户的数据类型打开android系统相应的Activity。
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //设置intent的数据类型是应用程序application
                        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                                "application/vnd.android.package-archive");
                        //为这个新apk开启一个新的activity栈
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //开始安装
                        startActivity(intent);
                        sh.saveUpgradeFlag("1");
//                        BleMethodCode methodCode;
//                        methodCode = new BleMethodCode(3);
//                        methodCode.setComm((byte) 0x10);
//                        methodCode.setMes("");
//                        EventBus.getDefault().post(methodCode);


                    }
                }).setNegativeButton(getString(R.string.string320), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();

    }

}
