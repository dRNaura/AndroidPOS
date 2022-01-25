package com.pos.salon.activity.JeoPowerDeviceSDK.Scanner;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.pos.salon.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ScanService extends Service {
    private static final String TAG = ScanService.class.getSimpleName();
    private static final int SCAN_TIME_LIMIT = 200;
    public static final String UHF_NAME = "com.jiebao.ht380k.uhf.UHFMainActivity";
    public static final String CHAOBIAO_NAME = "com.jiebao.ht380k.chaobiao.ChaoBiaoActivity";
    public static final String RS232_NAME = "com.jiebao.ht380.rs232.RS232Activity";
    public static final String PSAM_NAME = "com.jiebao.ht380k.psam.PSamActivity";
    public static final String UHF_MODE2_NAME = "com.jiebao.ht380k.uhf.UHFActivityMode2";
    public static final String UHF_MODE3_NAME = "com.jiebao.ht380k.uhf.UHFActivityMode3";
    public static final String SCAN_ACTIVITY_NAME = "com.jiebao.ht380k.scan.ScanActivity";

    private Binder myBinder = new MyBinder();
    private ActivityManager mActivityManager;
    private ComponentName mComponentName;
    // 实际是ScanActivity实例
    private ScanListener mScanListener;
    private KeyguardManager mKeyguardManager;
    private ScanController mScanController = null;
    private WakeLockUtil mWakeLockUtil = null;
    public BeepManager mBeepManager;

    private Intent mWebAddressIntent;
    private Intent mScanDataIntent;
    private Intent mEditTextIntent;

    private File mScanRecordDesFile;
    private FileOutputStream mFileOutputStream;

    private boolean mSdCardMounted;
    public static boolean keyF2DownOrNot = false;
    public static boolean mIsServiceOn = false;

    // 是否有Acitivty与服务绑定
    public static boolean isScanActivityUp = false;
    private boolean mIsActivityUp = false;

    private boolean mIsScanShortcutSupport = false;

    long mNowTime = 0;
    long mLastTime = 0;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            LogUtil.trace("msg:" + msg);

            switch (msg.what) {
                case 3001:
                    Toast.makeText(
                            ScanService.this,
                            getResources().getString(
                                    R.string.scan_init_failure_info), Toast.LENGTH_LONG)
                            .show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    ScanController.Callback mDataReciverCallback = new ScanController.Callback() {

        @Override
        public void Barcode_Read(byte[] buffer, String codeId, int errorCode) {
            LogUtil.d(TAG, "Barcode_Read");

            mBeepManager.play();
            String codeType = Tools.returnType(buffer);
            String val = null;
            if (codeType.equals("default")) {
                val = new String(buffer);
            } else {
                try {
                    val = new String(buffer, codeType);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            // ScanActivity的初始化实例，调用henResult()；将结果传递到Activity中
            if (null != mScanListener) {
                mScanListener.henResult(codeId, val);
            }
            if (null != mScanController) {
                if (!mIsActivityUp) {
                    houtai_result(codeId, val);
                }
            }
        }
    };

    // 捕获扫描物理按键广播
    private BroadcastReceiver mF4Receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.trace();

            if ("ReLoadCom".equals(intent.getAction())) {
                System.out.println("ReLoadCom()");
                if (mScanController != null) {
                    mScanController.Barcode_Close();
                    mScanController = ScanController.getInstance();
                    LogUtil.d(TAG, "ReLoadCom");
                    mScanController.Barcode_Open(ScanService.this,
                            mDataReciverCallback);
                }
                return;
            }
            if ("ReleaseCom".equals(intent.getAction())) {
                if (mScanController != null) {
                    mScanController.Barcode_Close();
                }
                return;
            }
            if (mKeyguardManager.inKeyguardRestrictedInputMode()) {// 屏已锁
                return;
            }
            if (intent.hasExtra("F4key")) {
                if (intent.getStringExtra("F4key").equals("down")) {
                    LogUtil.d("trig", "key down");
                    keyF2DownOrNot = true;
                    mHandler.removeCallbacks(mStopKeyDown);
                    if (null != mScanController) {
                        if (getScanShortcutSupport()) {
                            mNowTime = System.currentTimeMillis();
                            mWakeLockUtil.lock();

                            if (mNowTime - mLastTime > SCAN_TIME_LIMIT) {
                                LogUtil.d("ScanService trig", "key down");
                                mScanController.Barcode_Stop();

                                mLastTime = mNowTime;
                                mScanController.Barcode_Start();
                                // LogUtil.d("time", "doScan");
                            }
                        }
                    }
                } else if (intent.getStringExtra("F4key").equals("up")) {
                    LogUtil.d("trig", "key up");
                    if (null != mScanController) {
                        if (getScanShortcutSupport()
                                && getScanShortCutMode().equals("2")) {
                            mWakeLockUtil.unLock();
                            mHandler.postDelayed(mStopKeyDown, 1000);
                            if (Preference
                                    .getScanShortCutPressMode(ScanService.this) == 2) {
                                mScanController.Barcode_Stop();
                            }
                        }
                    }
                }
            }
        }
    };

    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.trace();

            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                // 开屏
                System.out.println("ACTION_SCREEN_ON");
                if (!checkIsInFirst()) {
                    if (mScanController == null) {
                        mScanController = ScanController.getInstance();
                    } else {
                        mScanController.Barcode_Close();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mScanController.Barcode_Open(ScanService.this,
                            mDataReciverCallback);
                    System.out.println("scanManager.init()");
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                // 锁屏
                System.out.println("ACTION_SCREEN_OFF");
                // 以防别使用该的串口的被下电s
                if (!checkIsInFirst()) {
                    if (mScanController != null) {
                        mScanController.Barcode_Close();
                        mScanController.Barcode_Stop();
                    }
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
                System.out.println("ACTION_USER_PRESENT");
            }
        }
    };

    private BroadcastReceiver mJbScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d(TAG,
                    "jbScanReceiver intent.getAction(): " + intent.getAction());
            if ("com.jb.action.SCAN_SWITCH".equals(intent.getAction())
                    && intent.hasExtra("extra")) {
                int extra = intent.getIntExtra("extra", -1);
                if (extra == 1) {
                    if (null != mScanController) {
                        mScanController.Barcode_Open(ScanService.this,
                                mDataReciverCallback);
                    }
                } else if (extra == 0) {
                    if (null != mScanController) {
                        mScanController.Barcode_Close();
                    }
                }
            }
            if ("com.jb.action.START_SCAN".equals(intent.getAction())) {
                keyF2DownOrNot = true;
                mHandler.removeCallbacks(mStopKeyDown);
                if (null != mScanController) {
                    mScanController.Barcode_Start();
                }
            }
            if ("com.jb.action.STOP_SCAN".equals(intent.getAction())) {
                mHandler.postDelayed(mStopKeyDown, 1000);
                if (null != mScanController) {
                    mScanController.Barcode_Stop();
                }
            }
            if ("com.jb.action.START_SCAN_CONTINUE".equals(intent.getAction())) {
                long continue_interval = 1000;
                if (intent.hasExtra("continue_interval")) {
                    continue_interval = intent.getIntExtra("continue_interval",
                            1000);
                    Log.d("ScanService", "continue_interval:"
                            + continue_interval);
                }
                if (null != mScanController) {
                    mScanController.Barcode_Continue_Start(continue_interval);
                }
            }
            if ("com.jb.action.STOP_SCAN_CONTINUE".equals(intent.getAction())) {
                if (null != mScanController) {
                    mScanController.Barcode_Continue_Stop();
                }
            }
        };
    };

    private Runnable mStopKeyDown = new Runnable() {

        @Override
        public void run() {
            keyF2DownOrNot = false;
        }
    };

    @Override
    public void onCreate() {
        LogUtil.trace();
        super.onCreate();

        mIsServiceOn = true;

        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mWakeLockUtil = new WakeLockUtil(this);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mScanDataIntent = new Intent("com.jb.action.GET_SCANDATA");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.jb.action.F4key");
        intentFilter.addAction("ReLoadCom");
        intentFilter.addAction("ReleaseCom");
        ScanService.this.registerReceiver(mF4Receiver, intentFilter);

        // 解决重新唤醒后，讯宝扫描乱码
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        ScanService.this.registerReceiver(mScreenReceiver, filter);

        IntentFilter jbScanFilter = new IntentFilter();
        jbScanFilter.addAction("com.jb.action.SCAN_SWITCH");
        jbScanFilter.addAction("com.jb.action.START_SCAN");
        jbScanFilter.addAction("com.jb.action.STOP_SCAN");
        jbScanFilter.addAction("com.jb.action.START_SCAN_CONTINUE");
        jbScanFilter.addAction("com.jb.action.STOP_SCAN_CONTINUE");
        ScanService.this.registerReceiver(mJbScanReceiver, jbScanFilter);

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                mSdCardMounted = true;
                // TODO 修改文件名，避免Linux平台下报错
                File file = new File("mnt/sdcard/Scan_Record");
                if (!file.exists()) {
                    file.mkdirs();
                }
                mScanRecordDesFile = new File("mnt/sdcard/" + "Scan_Record"
                        + "/" + "Scan_Record.txt");
                if (!mScanRecordDesFile.exists())
                    mScanRecordDesFile.createNewFile();
                mFileOutputStream = new FileOutputStream(mScanRecordDesFile,
                        true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mBeepManager = new BeepManager(this,
                Preference.getScanSound(this, true),
                Preference.getScanVibration(this, false));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.trace();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        LogUtil.trace();
        super.onStart(intent, startId);

        mIsScanShortcutSupport = Preference.getScanShortcutSupport(this, true);
        // true
        LogUtil.trace("mIsScanShortcutSupport:" + mIsScanShortcutSupport);

        if (mScanController == null) {
            mScanController = ScanController.getInstance();
        } else {
            mScanController.Barcode_Close();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // ScanService中的回调实例传递给ScanController
        mScanController.Barcode_Open(ScanService.this, mDataReciverCallback);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.trace();

        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.trace();

        mScanController.Barcode_Close();
        mScanController.Barcode_Stop();
        unregisterReceiver(mF4Receiver);
        unregisterReceiver(mScreenReceiver);
        unregisterReceiver(mJbScanReceiver);
        mWakeLockUtil.unLock();
        sendBroadcast(new Intent("ScanServiceDestroy"));
        mIsServiceOn = false;
    }

    public void setActivityUp(boolean isActivityUp) {
        this.mIsActivityUp = isActivityUp;
    }

    /**
     * <功能描述> 获取到ScanController实例
     *
     * @return [参数说明]
     * @return ScanController [返回类型说明]
     */
    public ScanController getScanManager() {
        LogUtil.trace();

        if (null == mScanController) {
            LogUtil.d(TAG, "null == mScanController");
            mScanController = ScanController.getInstance();
            mScanController
                    .Barcode_Open(ScanService.this, mDataReciverCallback);
        }
        return mScanController;
    }

    public void setOnScanListener(ScanListener scanListener) {
        this.mScanListener = scanListener;
    }

    /**
     * <功能描述> 将内容发送广播
     *
     * @param codeid
     * @param dataStr [参数说明]
     * @return void [返回类型说明]
     */
    public void sendScanBroadcast(String codeid, String dataStr) {
        LogUtil.trace();

        mScanDataIntent.putExtra("data", dataStr);
        mScanDataIntent.putExtra("codetype", codeid);
        this.sendBroadcast(mScanDataIntent);
    }

    /**
     * <功能描述> 网页支持
     *
     * @param dataStr [参数说明]
     * @return void [返回类型说明]
     */
    public void webAddressHandler(String dataStr) {
        LogUtil.trace();

        try {
            mWebAddressIntent = new Intent();
            mWebAddressIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mWebAddressIntent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(dataStr);
            mWebAddressIntent.setData(content_url);
            this.startActivity(mWebAddressIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述> 扫描内容保存至TXT
     *
     * @param data [参数说明]
     * @return void [返回类型说明]
     */
    public void saveInTxt(String data) {
        LogUtil.trace();

        byte[] bytes = data.getBytes();
        try {
            mFileOutputStream.write(bytes);
            mFileOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <功能描述>
     *
     * @param codeid
     * @param data [参数说明] 1快速扫描 2是模拟键盘 3广播
     * @return void [返回类型说明]
     */
    public void houtai_result(String codeid, String data) {
        LogUtil.trace();

        if (data != null && !data.trim().equals("")) {
            int out_mode = 3;
            switch (out_mode) {
                case 3:
                    sendScanBroadcast(codeid, data);
                    break;

                case 2:
                    break;

                case 1:
                    break;

                default:
                    break;
            }

            if (getScanNetSupport()) {
                webAddressHandler(data);
            }
        }
    }

    /**
     * <功能描述> 是否支持扫描快捷键 默认支持
     *
     * @return [参数说明] true：支持；false：不支持
     * @return boolean [返回类型说明]
     */
    public boolean getScanShortcutSupport() {
        return mIsScanShortcutSupport;
    }

    /**
     * <功能描述> 是否支持网页扫描 默认不支持
     *
     * @return [参数说明] true：支持；false：不支持
     * @return boolean [返回类型说明]
     */
    public boolean getScanNetSupport() {
        return Preference.getNetPageSupport(this, false);
    }

    /**
     * <功能描述> 是否支持扫描记录保存至TXT文档 默认不支持
     *
     * @return [参数说明] true：支持；false：不支持
     * @return boolean [返回类型说明]
     */
    public boolean getScanSaveTxt() {
        return Preference.getScanSaveTxt(this, false);
    }

    /**
     * <功能描述> 获取扫描快捷键 默认为中间橙色按键
     *
     * @return [参数说明] 1:左侧橙色按键 2:中间橙色按键 3:右边橙色按键
     * @return String [返回类型说明]
     */
    public String getScanShortCutMode() {
        return Preference.getScanShortcutMode(this, "2");
    }

    /**
     * <功能描述>后台扫描输出模式 默认快速扫描
     *
     * @return [参数说明] ScanOutMode 1.快速扫描（文本框） 2.模拟键盘 3.广播
     * @return int [返回类型说明]
     */
    public int getScanOutMode() {
        return Preference.getScanOutMode(this);
    }

    public void setIsScanShortcutSupport(boolean isScanShortcutSupport) {
        this.mIsScanShortcutSupport = isScanShortcutSupport;
    }

    /**
     * <功能描述> 检查最上层Activity 是否为使用相同串口Activity 防冲突
     *
     * @return [参数说明]
     * @return boolean [返回类型说明]
     */
    private boolean checkIsInFirst() {
        LogUtil.trace();

        mComponentName = mActivityManager.getRunningTasks(1).get(0).topActivity;
        LogUtil.d(TAG, "pkg:" + mComponentName.getPackageName());
        LogUtil.d(TAG, "cls:" + mComponentName.getClassName());
        if (mComponentName.getClassName().equals(RS232_NAME)
                || mComponentName.getClassName().equals(PSAM_NAME)
                || mComponentName.getClassName().equals(CHAOBIAO_NAME)
                || mComponentName.getClassName().equals(UHF_NAME)
                || mComponentName.getClassName().equals(UHF_MODE2_NAME)
                || mComponentName.getClassName().equals(UHF_MODE3_NAME))
            return true;
        return false;
    }

    public class MyBinder extends Binder {

        public ScanService getService() {
            return ScanService.this;
        }
    }
}
