package com.example.chen.ls4000.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.DropBoxManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.EventLog;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;



/**
 * <p>BatteryService monitors the charging status, and charge level of the device
 * battery.  When these values change this service broadcasts the new values
 * to all {@link android.content.BroadcastReceiver IntentReceivers} that are
 * watching the {@link android.content.Intent#ACTION_BATTERY_CHANGED
 * BATTERY_CHANGED} action.</p>
 * <p>The new values are stored in the Intent data and can be retrieved by
 * calling  with the
 * following keys:</p>
 * <p>"scale" - int, the maximum value for the charge level</p>
 * <p>"level" - int, charge level, from 0 through "scale" inclusive</p>
 * <p>"status" - String, the current charging status.<br />
 * <p>"health" - String, the current battery health.<br />
 * <p>"present" - boolean, true if the battery is present<br />
 * <p>"icon-small" - int, suggested small icon to use for this state</p>
 * <p>"plugged" - int, 0 if the device is not plugged in; 1 if plugged
 * into an AC power adapter; 2 if plugged in via USB.</p>
 * <p>"voltage" - int, current battery voltage in millivolts</p>
 * <p>"temperature" - int, current battery temperature in tenths of
 * a degree Centigrade</p>
 * <p>"technology" - String, the type of battery installed, e.g. "Li-ion"</p>
 *
 * <p>
 * The battery service may be called by the power manager while holding its locks so
 * we take care to post all outcalls into the activity manager to a handler.
 *
 * FIXME: Ideally the power manager would perform all of its calls into the battery
 * service asynchronously itself.
 * </p>

 ---------------------
 作者：daweibalang717
 来源：CSDN
 原文：https://blog.csdn.net/daweibalang717/article/details/40615453
 版权声明：本文为博主原创文章，转载请附上博文链接！
 */
public class BatteryService extends Service {

//    private static final String TAG = BatteryService.class.getSimpleName();
//    private static final boolean DEBUG = false;
//
//    private static final int BATTERY_SCALE = 100;    // battery capacity is a percentage
//
//    // Used locally for determining when to make a last ditch effort to log
//    // discharge stats before the device dies.
//    private int mCriticalBatteryLevel;
//
//    private static final int DUMP_MAX_LENGTH = 24 * 1024;
//    private static final String[] DUMPSYS_ARGS = new String[] { "--checkin", "--unplugged" };
//
//    private static final String DUMPSYS_DATA_PATH = "/data/system/";
//
//    // This should probably be exposed in the API, though it's not critical
//    private static final int BATTERY_PLUGGED_NONE = 0;
//
//    private final Context mContext;
//    private final IBatteryStats mBatteryStats;
//    private final Handler mHandler;
//
//    private final Object mLock = new Object();
//
//    private BatteryProperties mBatteryProps;
//    private boolean mBatteryLevelCritical;
//    private int mLastBatteryStatus;
//    private int mLastBatteryHealth;
//    private boolean mLastBatteryPresent;
//    private int mLastBatteryLevel;
//    private int mLastBatteryVoltage;
//    private int mLastBatteryTemperature;
//    private boolean mLastBatteryLevelCritical;
//
//    private int mInvalidCharger;
//    private int mLastInvalidCharger;
//
//    private int mLowBatteryWarningLevel;
//    private int mLowBatteryCloseWarningLevel;
//    private int mShutdownBatteryTemperature;
//
//    private int mPlugType;
//    private int mLastPlugType = -1; // Extra state so we can detect first run
//
//    private long mDischargeStartTime;
//    private int mDischargeStartLevel;
//
//    private boolean mUpdatesStopped;
//
//    private Led mLed;
//
//    private boolean mSentLowBatteryBroadcast = false;
//
//    private BatteryListener mBatteryPropertiesListener;
//    private IBatteryPropertiesRegistrar mBatteryPropertiesRegistrar;
//
//    //构造函数
//    public BatteryService(Context context , LightsService lights){
//        this.mContext = context;
//        mHandler = new Handler(true);
//        mLed = new Led(context,lights);
//        mBatteryStats = BatteryStatsService.getService();
//
//        //低电量临界值，这个数我看的源码版本值是4（在这个类里只是用来写日志）
//        mCriticalBatteryLevel = mContext.getResources().getInteger(
//                com.android.internal.R.integer.config_criticalBatteryWarningLevel);
//
//        //低电量告警值，值15，下面会根据这个变量发送低电量的广播Intent.ACTION_BATTERY_LOW（这个跟系统低电量提醒没关系，只是发出去了）
//       mLowBatteryWarningLevel = mContext.getResources().getInteger(
//                com.android.internal.R.integer.config_lowBatteryWarningLevel);
//
//
//        //电量告警取消值，值20 ， 就是手机电量大于等于20的话发送Intent.ACTION_BATTERY_OKAY
//        mLowBatteryCloseWarningLevel = mContext.getResources().getInteger(
//                com.android.internal.R.integer.config_lowBatteryCloseWarningLevel);
//
//        //值是680 ，温度过高，超过这个值就发送广播，跳转到将要关机提醒。
//        mShutdownBatteryTemperature = mContext.getResources().getInteger(
//                com.android.internal.R.integer.config_shutdownBatteryTemperature);
//
//        // watch for invalid charger messages if the invalid_charger switch exists
//        if (new File("/sys/devices/virtual/switch/invalid_charger/state").exists()) {
//            mInvalidChargerObserver.startObserving(
//                    "DEVPATH=/devices/virtual/switch/invalid_charger");
//        }
//
//
//        //电池监听，这个应该是注册到底层去了。当底层电量改变会调用此监听。然后执行update(BatteryProperties props)；
//        mBatteryPropertiesListener = new BatteryListener();
//
//        IBinder b = ServiceManager.getService("batterypropreg");
//        mBatteryPropertiesRegistrar = IBatteryPropertiesRegistrar.Stub.asInterface(b);
//
//        try {
////这里注册
//            mBatteryPropertiesRegistrar.registerListener(mBatteryPropertiesListener);
//        } catch (RemoteException e) {
//            // Should never happen.
//        }
//    }
//    //开机后先去看看是否没电了或者温度太高了。如果是，就关机提示（关机提示我等会介绍）。
//    void systemReady() {
//        // check our power situation now that it is safe to display the shutdown dialog.
//        synchronized (mLock) {
//            shutdownIfNoPowerLocked();
//            shutdownIfOverTempLocked();
//        }
//    }
//
//    //返回是否在充电，这个函数在PowerManagerService.java 中调用
//    /**
//     * Returns true if the device is plugged into any of the specified plug types.
//     */
//    public boolean isPowered(int plugTypeSet) {
//        synchronized (mLock) {
//            return isPoweredLocked(plugTypeSet);
//        }
//    }
//    //就是这里，通过充电器类型判断是否充电
//    private boolean isPoweredLocked(int plugTypeSet) {
//        //我这英语小白猜着翻译下：就是开机后，电池状态不明了，那我们就认为就在充电，以便设备正常工作。
//        // assume we are powered if battery state is unknown so
//        // the "stay on while plugged in" option will work.
//        if (mBatteryProps.batteryStatus == BatteryManager.BATTERY_STATUS_UNKNOWN) {
//            return true;
//        }
//        //充电器
//        if ((plugTypeSet & BatteryManager.BATTERY_PLUGGED_AC) != 0 && mBatteryProps.chargerAcOnline) {
//            return true;
//        }
//        //USB，插电脑上充电
//        if ((plugTypeSet & BatteryManager.BATTERY_PLUGGED_USB) != 0 && mBatteryProps.chargerUsbOnline) {
//            return true;
//        }
//        //电源是无线的。 （我没见过...）
//        if ((plugTypeSet & BatteryManager.BATTERY_PLUGGED_WIRELESS) != 0 && mBatteryProps.chargerWirelessOnline) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Returns the current plug type.
//     */
////充电器类型
//    public int getPlugType() {
//        synchronized (mLock) {
//            return mPlugType;
//        }
//    }
//
//    /**
//     * Returns battery level as a percentage.
//     */
//    //电池属性：电量等级（0-100）
//    public int getBatteryLevel() {
//        synchronized (mLock) {
//            return mBatteryProps.batteryLevel;
//        }
//    }
//
//    /**
//     * Returns true if battery level is below the first warning threshold.
//     */
//    //低电量
//    public boolean isBatteryLow() {
//        synchronized (mLock) {
//            return mBatteryProps.batteryPresent && mBatteryProps.batteryLevel <= mLowBatteryWarningLevel;
//        }
//    }
//
//    /**
//     * Returns a non-zero value if an  unsupported charger is attached.
//     */
//    //不支持的充电器类型 
//    public int getInvalidCharger() {
//        synchronized (mLock) {
//            return mInvalidCharger;
//        }
//    }
//
//
//    //这里就是没电了，要关机的提示。
//    private void shutdownIfNoPowerLocked() {
//        // shut down gracefully if our battery is critically low and we are not powered.
//        // wait until the system has booted before attempting to display the shutdown dialog.
//        if (mBatteryProps.batteryLevel == 0 && (mBatteryProps.batteryStatus == BatteryManager.BATTERY_STATUS_DISCHARGING)) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (ActivityManagerNative.isSystemReady()) {
//                        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN_LOWBATTERY");//ACTION_REQUEST_SHUTDOWN
//                        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
//                        intent.putExtra("cant_be_cancel_by_button", true);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        mContext.startActivityAsUser(intent, UserHandle.CURRENT);
//                    }
//                }
//            });
//        }
//    }
//
//    //温度过高，关机提示（个人感觉这里有问题，温度过高为啥子跳转到没电关机提示界面）
//    private void shutdownIfOverTempLocked() {
//        // shut down gracefully if temperature is too high (> 68.0C by default)
//        // wait until the system has booted before attempting to display the
//        // shutdown dialog.
//        if (mBatteryProps.batteryTemperature > mShutdownBatteryTemperature) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (ActivityManagerNative.isSystemReady()) {
//                        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN_LOWBATTERY");//ACTION_REQUEST_SHUTDOWN
//                        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        mContext.startActivityAsUser(intent, UserHandle.CURRENT);
//                    }
//                }
//            });
//        }
//    }
//    //这个方法就是被JNI回调的。用来更新上层状态的方法。
//    private void update(BatteryProperties props) {
//        synchronized (mLock) {
//            if (!mUpdatesStopped) {
//                mBatteryProps = props;
//                // Process the new values.
//                processValuesLocked();
//            }
//        }
//    }
//
//    //嗯。这个就是最主要的方法了。
//    private void processValuesLocked() {
//        boolean logOutlier = false;
//        long dischargeDuration = 0;
//
//        mBatteryLevelCritical = (mBatteryProps.batteryLevel <= mCriticalBatteryLevel);
////充电器类型
//        if (mBatteryProps.chargerAcOnline) {
//            mPlugType = BatteryManager.BATTERY_PLUGGED_AC;
//        } else if (mBatteryProps.chargerUsbOnline) {
//            mPlugType = BatteryManager.BATTERY_PLUGGED_USB;
//        } else if (mBatteryProps.chargerWirelessOnline) {
//            mPlugType = BatteryManager.BATTERY_PLUGGED_WIRELESS;
//        } else {
//            mPlugType = BATTERY_PLUGGED_NONE;
//        }
//
//        if (DEBUG) {//日志，略过
//            Slog.d(TAG, "Processing new values: "
//                    + "chargerAcOnline=" + mBatteryProps.chargerAcOnline
//                    + ", chargerUsbOnline=" + mBatteryProps.chargerUsbOnline
//                    + ", chargerWirelessOnline=" + mBatteryProps.chargerWirelessOnline
//                    + ", batteryStatus=" + mBatteryProps.batteryStatus
//                    + ", batteryHealth=" + mBatteryProps.batteryHealth
//                    + ", batteryPresent=" + mBatteryProps.batteryPresent
//                    + ", batteryLevel=" + mBatteryProps.batteryLevel
//                    + ", batteryTechnology=" + mBatteryProps.batteryTechnology
//                    + ", batteryVoltage=" + mBatteryProps.batteryVoltage
//                    + ", batteryCurrentNow=" + mBatteryProps.batteryCurrentNow
//                    + ", batteryChargeCounter=" + mBatteryProps.batteryChargeCounter
//                    + ", batteryTemperature=" + mBatteryProps.batteryTemperature
//                    + ", mBatteryLevelCritical=" + mBatteryLevelCritical
//                    + ", mPlugType=" + mPlugType);
//        }
//
//        // Let the battery stats keep track of the current level.
//        try {
////把电池属性放到状态里面
//            mBatteryStats.setBatteryState(mBatteryProps.batteryStatus, mBatteryProps.batteryHealth,
//                    mPlugType, mBatteryProps.batteryLevel, mBatteryProps.batteryTemperature,
//                    mBatteryProps.batteryVoltage);
//        } catch (RemoteException e) {
//            // Should never happen.
//        }
////没电了
//        shutdownIfNoPowerLocked();
////温度过高了
//        shutdownIfOverTempLocked();
//
//        if (mBatteryProps.batteryStatus != mLastBatteryStatus ||
//                mBatteryProps.batteryHealth != mLastBatteryHealth ||
//                mBatteryProps.batteryPresent != mLastBatteryPresent ||
//                mBatteryProps.batteryLevel != mLastBatteryLevel ||
//                mPlugType != mLastPlugType ||
//                mBatteryProps.batteryVoltage != mLastBatteryVoltage ||
//                mBatteryProps.batteryTemperature != mLastBatteryTemperature ||
//                mInvalidCharger != mLastInvalidCharger) {
//
//            if (mPlugType != mLastPlugType) {//当前充电器类型与上次的不一样
////并且上次充电器类型是no one ，那就可以知道，现在是插上充电器了。
//               if (mLastPlugType == BATTERY_PLUGGED_NONE) {
//                    // discharging -> charging
//
//                    // There's no value in this data unless we've discharged at least once and the
//                    // battery level has changed; so don't log until it does.
//                    if (mDischargeStartTime != 0 && mDischargeStartLevel != mBatteryProps.batteryLevel) {
//                        dischargeDuration = SystemClock.elapsedRealtime() - mDischargeStartTime;
//                        logOutlier = true;
//                        EventLog.writeEvent(EventLogTags.BATTERY_DISCHARGE, dischargeDuration,
//                                mDischargeStartLevel, mBatteryProps.batteryLevel);
//                        // make sure we see a discharge event before logging again
//                        mDischargeStartTime = 0;
//                    }
////并且本次充电器类型是no one ，那就可以知道，现在是拔掉充电器了。
//                } else if (mPlugType == BATTERY_PLUGGED_NONE) {
//                    // charging -> discharging or we just powered up
//                    mDischargeStartTime = SystemClock.elapsedRealtime();
//                    mDischargeStartLevel = mBatteryProps.batteryLevel;
//                }
//            }
//            if (mBatteryProps.batteryStatus != mLastBatteryStatus ||//写日志，略过
//                    mBatteryProps.batteryHealth != mLastBatteryHealth ||
//                    mBatteryProps.batteryPresent != mLastBatteryPresent ||
//                    mPlugType != mLastPlugType) {
//                EventLog.writeEvent(EventLogTags.BATTERY_STATUS,
//                        mBatteryProps.batteryStatus, mBatteryProps.batteryHealth, mBatteryProps.batteryPresent ? 1 : 0,
//                        mPlugType, mBatteryProps.batteryTechnology);
//            }
//            if (mBatteryProps.batteryLevel != mLastBatteryLevel) {
//                // Don't do this just from voltage or temperature changes, that is
//                // too noisy.
//                EventLog.writeEvent(EventLogTags.BATTERY_LEVEL,
//                        mBatteryProps.batteryLevel, mBatteryProps.batteryVoltage, mBatteryProps.batteryTemperature);
//            }
//            if (mBatteryLevelCritical && !mLastBatteryLevelCritical &&
//                    mPlugType == BATTERY_PLUGGED_NONE) {
//                // We want to make sure we log discharge cycle outliers
//                // if the battery is about to die.
//                dischargeDuration = SystemClock.elapsedRealtime() - mDischargeStartTime;
//                logOutlier = true;
//            }
////本次调用，当前的充电状态
//            final boolean plugged = mPlugType != BATTERY_PLUGGED_NONE;
////本次调用，上次调用的充电状态 
//            final boolean oldPlugged = mLastPlugType != BATTERY_PLUGGED_NONE;
//
//            /* The ACTION_BATTERY_LOW broadcast is sent in these situations:
//             * - is just un-plugged (previously was plugged) and battery level is
//             *   less than or equal to WARNING, or
//             * - is not plugged and battery level falls to WARNING boundary
//             *   (becomes <= mLowBatteryWarningLevel).
//             */
////用于发送低电量广播的判断
//            final boolean sendBatteryLow = !plugged//（按sendBatteryLow = true 来说） 当前没有充电
//                    && mBatteryProps.batteryStatus != BatteryManager.BATTERY_STATUS_UNKNOWN//充电状态不是UNKNOWN
//                    && mBatteryProps.batteryLevel <= mLowBatteryWarningLevel//当前电量小于告警值 15
//                    && (oldPlugged || mLastBatteryLevel > mLowBatteryWarningLevel);//上次状态是充电或者上次电量等级大于告警值 15
//
//            sendIntentLocked();//发送电池电量改变的广播Intent.ACTION_BATTERY_CHANGED
//
//            // Separate broadcast is sent for power connected / not connected
//            // since the standard intent will not wake any applications and some
//            // applications may want to have smart behavior based on this.
//            if (mPlugType != 0 && mLastPlugType == 0) {//插上充电器了
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent statusIntent = new Intent(Intent.ACTION_POWER_CONNECTED);
//                        statusIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
//                        mContext.sendBroadcastAsUser(statusIntent, UserHandle.ALL);
//                    }
//                });
//            }
//            else if (mPlugType == 0 && mLastPlugType != 0) {//断开充电器了
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent statusIntent = new Intent(Intent.ACTION_POWER_DISCONNECTED);
//                        statusIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
//                        mContext.sendBroadcastAsUser(statusIntent, UserHandle.ALL);
//                    }
//                });
//            }
////发送低电量提醒（这个跟系统低电量提醒没关系，只是发出去了）
//            if (sendBatteryLow) {
//                mSentLowBatteryBroadcast = true;
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent statusIntent = new Intent(Intent.ACTION_BATTERY_LOW);
//                        statusIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
//                        mContext.sendBroadcastAsUser(statusIntent, UserHandle.ALL);
//                    }
//                });
//            } else if (mSentLowBatteryBroadcast && mLastBatteryLevel >= mLowBatteryCloseWarningLevel) {//电量超过20了。电池状态OK了
//                mSentLowBatteryBroadcast = false;
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent statusIntent = new Intent(Intent.ACTION_BATTERY_OKAY);
//                        statusIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
//                        mContext.sendBroadcastAsUser(statusIntent, UserHandle.ALL);
//                    }
//                });
//            }
//
//            // Update the battery LED
//            mLed.updateLightsLocked();
//
//            // This needs to be done after sendIntent() so that we get the lastest battery stats.
//            if (logOutlier && dischargeDuration != 0) {
//                logOutlierLocked(dischargeDuration);
//            }
//
//            mLastBatteryStatus = mBatteryProps.batteryStatus;
//            mLastBatteryHealth = mBatteryProps.batteryHealth;
//            mLastBatteryPresent = mBatteryProps.batteryPresent;
//            mLastBatteryLevel = mBatteryProps.batteryLevel;
//            mLastPlugType = mPlugType;
//            mLastBatteryVoltage = mBatteryProps.batteryVoltage;
//            mLastBatteryTemperature = mBatteryProps.batteryTemperature;
//            mLastBatteryLevelCritical = mBatteryLevelCritical;
//            mLastInvalidCharger = mInvalidCharger;
//        }
//    }
//    //电池电量改变，把属性发出去（系统低电量提醒接收的是这个广播）
//    private void sendIntentLocked() {
//        //  Pack up the values and broadcast them to everyone
//        final Intent intent = new Intent(Intent.ACTION_BATTERY_CHANGED);
//        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY
//                | Intent.FLAG_RECEIVER_REPLACE_PENDING);
//
//        int icon = getIconLocked(mBatteryProps.batteryLevel);
//
//        intent.putExtra(BatteryManager.EXTRA_STATUS, mBatteryProps.batteryStatus);
//        intent.putExtra(BatteryManager.EXTRA_HEALTH, mBatteryProps.batteryHealth);
//        intent.putExtra(BatteryManager.EXTRA_PRESENT, mBatteryProps.batteryPresent);
//        intent.putExtra(BatteryManager.EXTRA_LEVEL, mBatteryProps.batteryLevel);
//        intent.putExtra(BatteryManager.EXTRA_SCALE, BATTERY_SCALE);
//        intent.putExtra(BatteryManager.EXTRA_ICON_SMALL, icon);
//        intent.putExtra(BatteryManager.EXTRA_PLUGGED, mPlugType);
//        intent.putExtra(BatteryManager.EXTRA_VOLTAGE, mBatteryProps.batteryVoltage);
//        intent.putExtra(BatteryManager.EXTRA_TEMPERATURE, mBatteryProps.batteryTemperature);
//        intent.putExtra(BatteryManager.EXTRA_TECHNOLOGY, mBatteryProps.batteryTechnology);
//        intent.putExtra(BatteryManager.EXTRA_INVALID_CHARGER, mInvalidCharger);
//
//        if (DEBUG) {
//            Slog.d(TAG, "Sending ACTION_BATTERY_CHANGED.  level:" + mBatteryProps.batteryLevel +
//                    ", scale:" + BATTERY_SCALE + ", status:" + mBatteryProps.batteryStatus +
//                    ", health:" + mBatteryProps.batteryHealth +  ", present:" + mBatteryProps.batteryPresent +
//                    ", voltage: " + mBatteryProps.batteryVoltage +
//                    ", temperature: " + mBatteryProps.batteryTemperature +
//                    ", technology: " + mBatteryProps.batteryTechnology +
//                    ", AC powered:" + mBatteryProps.chargerAcOnline + ", USB powered:" + mBatteryProps.chargerUsbOnline +
//                    ", Wireless powered:" + mBatteryProps.chargerWirelessOnline +
//                    ", icon:" + icon  + ", invalid charger:" + mInvalidCharger);
//        }
//
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                ActivityManagerNative.broadcastStickyIntent(intent, null, UserHandle.USER_ALL);
//            }
//        });
//    }
//
//    private void logBatteryStatsLocked() {
//        IBinder batteryInfoService = ServiceManager.getService(BatteryStats.SERVICE_NAME);
//        if (batteryInfoService == null) return;
//
//        DropBoxManager db = (DropBoxManager) mContext.getSystemService(Context.DROPBOX_SERVICE);
//        if (db == null || !db.isTagEnabled("BATTERY_DISCHARGE_INFO")) return;
//
//        File dumpFile = null;
//        FileOutputStream dumpStream = null;
//        try {
//            // dump the service to a file
//            dumpFile = new File(DUMPSYS_DATA_PATH + BatteryStats.SERVICE_NAME + ".dump");
//            dumpStream = new FileOutputStream(dumpFile);
//            batteryInfoService.dump(dumpStream.getFD(), DUMPSYS_ARGS);
//            FileUtils.sync(dumpStream);
//
//            // add dump file to drop box
//            db.addFile("BATTERY_DISCHARGE_INFO", dumpFile, DropBoxManager.IS_TEXT);
//        } catch (RemoteException e) {
//            Slog.e(TAG, "failed to dump battery service", e);
//        } catch (IOException e) {
//            Slog.e(TAG, "failed to write dumpsys file", e);
//        } finally {
//            // make sure we clean up
//            if (dumpStream != null) {
//                try {
//                    dumpStream.close();
//                } catch (IOException e) {
//                    Slog.e(TAG, "failed to close dumpsys output stream");
//                }
//            }
//            if (dumpFile != null && !dumpFile.delete()) {
//                Slog.e(TAG, "failed to delete temporary dumpsys file: "
//                        + dumpFile.getAbsolutePath());
//            }
//        }
//    }
//
//    private void logOutlierLocked(long duration) {
//        ContentResolver cr = mContext.getContentResolver();
//        String dischargeThresholdString = Settings.Global.getString(cr,
//                Settings.Global.BATTERY_DISCHARGE_THRESHOLD);
//        String durationThresholdString = Settings.Global.getString(cr,
//                Settings.Global.BATTERY_DISCHARGE_DURATION_THRESHOLD);
//
//        if (dischargeThresholdString != null && durationThresholdString != null) {
//            try {
//                long durationThreshold = Long.parseLong(durationThresholdString);
//                int dischargeThreshold = Integer.parseInt(dischargeThresholdString);
//                if (duration <= durationThreshold &&
//                        mDischargeStartLevel - mBatteryProps.batteryLevel >= dischargeThreshold) {
//                    // If the discharge cycle is bad enough we want to know about it.
//                    logBatteryStatsLocked();
//                }
//                if (DEBUG) Slog.v(TAG, "duration threshold: " + durationThreshold +
//                        " discharge threshold: " + dischargeThreshold);
//                if (DEBUG) Slog.v(TAG, "duration: " + duration + " discharge: " +
//                        (mDischargeStartLevel - mBatteryProps.batteryLevel));
//            } catch (NumberFormatException e) {
//                Slog.e(TAG, "Invalid DischargeThresholds GService string: " +
//                        durationThresholdString + " or " + dischargeThresholdString);
//                return;
//            }
//        }
//    }
//
//    private int getIconLocked(int level) {
//        if (mBatteryProps.batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
//            return com.android.internal.R.drawable.stat_sys_battery_charge;
//        } else if (mBatteryProps.batteryStatus == BatteryManager.BATTERY_STATUS_DISCHARGING) {
//            return com.android.internal.R.drawable.stat_sys_battery;
//        } else if (mBatteryProps.batteryStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING
//                || mBatteryProps.batteryStatus == BatteryManager.BATTERY_STATUS_FULL) {
//            if (isPoweredLocked(BatteryManager.BATTERY_PLUGGED_ANY)
//                    && mBatteryProps.batteryLevel >= 100) {
//                return com.android.internal.R.drawable.stat_sys_battery_charge;
//            } else {
//                return com.android.internal.R.drawable.stat_sys_battery;
//            }
//        } else {
//            return com.android.internal.R.drawable.stat_sys_battery_unknown;
//        }
//    }
//
//    @Override
//    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
//        if (mContext.checkCallingOrSelfPermission(android.Manifest.permission.DUMP)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            pw.println("Permission Denial: can't dump Battery service from from pid="
//                    + Binder.getCallingPid()
//                    + ", uid=" + Binder.getCallingUid());
//            return;
//        }
//
//        synchronized (mLock) {
//            if (args == null || args.length == 0 || "-a".equals(args[0])) {
//                pw.println("Current Battery Service state:");
//                if (mUpdatesStopped) {
//                    pw.println("  (UPDATES STOPPED -- use 'reset' to restart)");
//                }
//                pw.println("  AC powered: " + mBatteryProps.chargerAcOnline);
//                pw.println("  USB powered: " + mBatteryProps.chargerUsbOnline);
//                pw.println("  Wireless powered: " + mBatteryProps.chargerWirelessOnline);
//                pw.println("  status: " + mBatteryProps.batteryStatus);
//                pw.println("  health: " + mBatteryProps.batteryHealth);
//                pw.println("  present: " + mBatteryProps.batteryPresent);
//                pw.println("  level: " + mBatteryProps.batteryLevel);
//                pw.println("  scale: " + BATTERY_SCALE);
//                pw.println("  voltage: " + mBatteryProps.batteryVoltage);
//
//                if (mBatteryProps.batteryCurrentNow != Integer.MIN_VALUE) {
//                    pw.println("  current now: " + mBatteryProps.batteryCurrentNow);
//                }
//
//                if (mBatteryProps.batteryChargeCounter != Integer.MIN_VALUE) {
//                    pw.println("  charge counter: " + mBatteryProps.batteryChargeCounter);
//                }
//
//                pw.println("  temperature: " + mBatteryProps.batteryTemperature);
//                pw.println("  technology: " + mBatteryProps.batteryTechnology);
//            } else if (args.length == 3 && "set".equals(args[0])) {
//                String key = args[1];
//                String value = args[2];
//                try {
//                    boolean update = true;
//                    if ("ac".equals(key)) {
//                        mBatteryProps.chargerAcOnline = Integer.parseInt(value) != 0;
//                    } else if ("usb".equals(key)) {
//                        mBatteryProps.chargerUsbOnline = Integer.parseInt(value) != 0;
//                    } else if ("wireless".equals(key)) {
//                        mBatteryProps.chargerWirelessOnline = Integer.parseInt(value) != 0;
//                    } else if ("status".equals(key)) {
//                        mBatteryProps.batteryStatus = Integer.parseInt(value);
//                    } else if ("level".equals(key)) {
//                        mBatteryProps.batteryLevel = Integer.parseInt(value);
//                    } else if ("invalid".equals(key)) {
//                        mInvalidCharger = Integer.parseInt(value);
//                    } else {
//                        pw.println("Unknown set option: " + key);
//                        update = false;
//                    }
//                    if (update) {
//                        long ident = Binder.clearCallingIdentity();
//                        try {
//                            mUpdatesStopped = true;
//                            processValuesLocked();
//                        } finally {
//                            Binder.restoreCallingIdentity(ident);
//                        }
//                    }
//                } catch (NumberFormatException ex) {
//                    pw.println("Bad value: " + value);
//                }
//            } else if (args.length == 1 && "reset".equals(args[0])) {
//                long ident = Binder.clearCallingIdentity();
//                try {
//                    mUpdatesStopped = false;
//                } finally {
//                    Binder.restoreCallingIdentity(ident);
//                }
//            } else {
//                pw.println("Dump current battery state, or:");
//                pw.println("  set ac|usb|wireless|status|level|invalid <value>");
//                pw.println("  reset");
//            }
//        }
//    }
//
//    private final UEventObserver mInvalidChargerObserver = new UEventObserver() {
//        @Override
//        public void onUEvent(UEventObserver.UEvent event) {
//            final int invalidCharger = "1".equals(event.get("SWITCH_STATE")) ? 1 : 0;
//            synchronized (mLock) {
//                if (mInvalidCharger != invalidCharger) {
//                    mInvalidCharger = invalidCharger;
//                }
//            }
//        }
//    };
//
//    private final class Led {
//        private final LightsService.Light mBatteryLight;
//
//        private final int mBatteryLowARGB;
//        private final int mBatteryMediumARGB;
//        private final int mBatteryFullARGB;
//        private final int mBatteryLedOn;
//        private final int mBatteryLedOff;
//
//        public Led(Context context, LightsService lights) {
//            mBatteryLight = lights.getLight(LightsService.LIGHT_ID_BATTERY);
//
//            mBatteryLowARGB = context.getResources().getInteger(
//                    com.android.internal.R.integer.config_notificationsBatteryLowARGB);
//            mBatteryMediumARGB = context.getResources().getInteger(
//                    com.android.internal.R.integer.config_notificationsBatteryMediumARGB);
//            mBatteryFullARGB = context.getResources().getInteger(
//                    com.android.internal.R.integer.config_notificationsBatteryFullARGB);
//            mBatteryLedOn = context.getResources().getInteger(
//                    com.android.internal.R.integer.config_notificationsBatteryLedOn);
//            mBatteryLedOff = context.getResources().getInteger(
//                    com.android.internal.R.integer.config_notificationsBatteryLedOff);
//        }
//
//        /**
//         * Synchronize on BatteryService.
//         */
//        public void updateLightsLocked() {
//            final int level = mBatteryProps.batteryLevel;
//            final int status = mBatteryProps.batteryStatus;
//            if (level < mLowBatteryWarningLevel) {
//                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
//                    // Solid red when battery is charging
//                    mBatteryLight.setColor(mBatteryLowARGB);
//                } else {
//                    // Flash red when battery is low and not charging
//                    mBatteryLight.setFlashing(mBatteryLowARGB, LightsService.LIGHT_FLASH_TIMED,
//                            mBatteryLedOn, mBatteryLedOff);
//                }
//            } else if (status == BatteryManager.BATTERY_STATUS_CHARGING
//                    || status == BatteryManager.BATTERY_STATUS_FULL) {
//                if (status == BatteryManager.BATTERY_STATUS_FULL || level >= 90) {
//                    // Solid green when full or charging and nearly full
//                    mBatteryLight.setColor(mBatteryFullARGB);
//                } else {
//                    // Solid orange when charging and halfway full
//                    mBatteryLight.setColor(mBatteryMediumARGB);
//                }
//            } else {
//                // No lights if not charging and not low
//                mBatteryLight.turnOff();
//            }
//        }
//    }
//
//    private final class BatteryListener extends IBatteryPropertiesListener.Stub {
//        public void batteryPropertiesChanged(BatteryProperties props) {
//            BatteryService.this.update(props);
//        }
//    }
//
//
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
