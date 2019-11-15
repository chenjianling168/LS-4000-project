package com.example.chen.ls4000.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.ScrollerCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.example.chen.ls4000.R;
import com.example.chen.ls4000.utils.SharedHelper;
import com.example.chen.ls4000.utils.Utils;

public class GPSActivity extends Activity implements View.OnClickListener {

    private CheckBox switchGPS;
    private Button save;
    private ImageView back;
    private SharedHelper sh;
    private TextView loca;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        initviews();

        initLocation();
    }

    private void initviews(){
        switchGPS = (CheckBox) findViewById(R.id.id_gps_switch);
        back = (ImageView) findViewById(R.id.id_gps_back);
        save = (Button)findViewById(R.id.id_gps_save);
        loca = (TextView)findViewById(R.id.id_gps_location);
        loca.setMovementMethod(ScrollingMovementMethod.getInstance());
        hideBottomUIMenu();

        back.setOnClickListener(this);
        save.setOnClickListener(this);
        switchGPS.setOnClickListener(this);

        sh = new SharedHelper(this);

//        switchGPS.setChecked(false);
//        switchGPS.setChecked(checkGPSIsOpen());
    }

    private int GPS_REQUEST_CODE = 10;

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.id_gps_switch:
//                if(switchGPS.isChecked()){
//                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivityForResult(intent, GPS_REQUEST_CODE);
//                }else{
//                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivityForResult(intent, GPS_REQUEST_CODE);
//                }
                switchGPS.setClickable(false);
                break;
            case R.id.id_gps_back:
                finish();
                break;
            case R.id.id_gps_save:
                //sh.saveGps(switchGPS.isChecked());
                if(getString(R.string.string48).equals(save.getText().toString())){
                    startLocation();
                    loca.setText(getString(R.string.string49));
                }else if(getString(R.string.string50).equals(save.getText().toString())){
                    stopLocation();
                    loca.setText(getString(R.string.string51));
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        switchGPS.setChecked(checkGPSIsOpen());
    }

    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    private void toggleGPS() {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings","com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(getBaseContext(), 0, GPSIntent, 0).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    sb.append(getString(R.string.string52) + "\n");
                    //sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append(getString(R.string.string53) + ((double)Math.round(location.getLongitude()*100)/100) + "\n");
                    sb.append(getString(R.string.string54) + ((double)Math.round(location.getLatitude()*100)/100) + "\n");
                    //sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    //sb.append("提供者    : " + location.getProvider() + "\n");

                    //sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    //sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    //sb.append("星    数    : " + location.getSatellites() + "\n");
                    //sb.append("国    家    : " + location.getCountry() + "\n");
                    //sb.append("省            : " + location.getProvince() + "\n");
                    //sb.append("市            : " + location.getCity() + "\n");
                    //sb.append("城市编码 : " + location.getCityCode() + "\n");
                    //sb.append("区            : " + location.getDistrict() + "\n");
                    //sb.append("区域 码   : " + location.getAdCode() + "\n");
                    //sb.append("地    址    : " + location.getAddress() + "\n");
                    //sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append(getString(R.string.string55) + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                } else {
                    //定位失败
                    sb.append(getString(R.string.string56) + "\n");
                    //sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append(getString(R.string.string57) + location.getErrorInfo() + "\n");
                    sb.append(getString(R.string.string58) + location.getLocationDetail() + "\n");
                }
                sb.append(getString(R.string.string59)).append("\n");
                sb.append(getString(R.string.string60)).append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
                sb.append(getString(R.string.string61)).append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
                sb.append(getString(R.string.string62)).append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
                sb.append(getString(R.string.string63) + location.getLocationQualityReport().getNetworkType()).append("\n");
                sb.append(getString(R.string.string64) + location.getLocationQualityReport().getNetUseTime()).append("\n");
                sb.append("****************").append("\n");
                //定位之后的回调时间
                sb.append(getString(R.string.string65) + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

                //解析定位结果，
                String result = sb.toString();
                loca.setText(result);
            } else {
                loca.setText(getString(R.string.string56));
            }
        }
    };


    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode){
        String str = "";
        switch (statusCode){
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = getString(R.string.string66);
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = getString(R.string.string67);
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = getString(R.string.string68);
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = getString(R.string.string69);
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = getString(R.string.string70);
                break;
        }
        return str;
    }
    // 根据控件的选择，重新设置定位参数
    private void resetOption() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(false);//cbAddress.isChecked());
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        //locationOption.setGpsFirst(cbGpsFirst.isChecked());
        // 设置是否开启缓存
        //locationOption.setLocationCacheEnable(cbCacheAble.isChecked());
        // 设置是否单次定位
        //locationOption.setOnceLocation(cbOnceLocation.isChecked());
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        //locationOption.setOnceLocationLatest(cbOnceLastest.isChecked());
        //设置是否使用传感器
        //locationOption.setSensorEnable(cbSensorAble.isChecked());
        //设置是否开启wifi扫描，如果设置为false时同时会停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        //String strInterval = etInterval.getText().toString();
//        if (!TextUtils.isEmpty(strInterval)) {
//
//        }
        try{
                // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
                locationOption.setInterval(Long.valueOf("2000"));
            }catch(Throwable e){
                e.printStackTrace();
            }
        //String strTimeout = etHttpTimeout.getText().toString();
        try{
            // 设置网络请求超时时间
            locationOption.setHttpTimeOut(Long.valueOf("30000"));
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){
        //根据控件的选择，重新设置定位参数
        resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
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
}
