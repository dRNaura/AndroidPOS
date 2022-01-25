package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.pos.salon.R;
import com.pos.salon.activity.ActivityPosSale.ActivitySearchItemList;
import com.pos.salon.utilConstant.AppConstant;

import java.io.IOException;
import java.util.Vector;

import static com.pos.salon.activity.HomeActivity.MY_PERMISSIONS_REQUESTS;

public class CaptureActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    // 广播发送至Settings，并执行开启或者关闭NFC功能
    private static final String NFC_ACTION_OPEN_CLOSE_ACTION = "com.jiebaosz.nfc.detect.action";

    private static final int SCANNER_MODE_DEFAULT = 0x01;
    private static final int SCANNER_MODE_SINGLE = 0x02;
    private static final int SCANNER_MODE_CONTINUE = 0x03;
    private static final long VIBRATE_DURATION = 200L;
    private static final float BEEP_VOLUME = 0.10f;

    // 扫描默认模式
    private int mScannerMode = SCANNER_MODE_DEFAULT;
    private int mDecodeTime = 0;
    private boolean mHasSurface;
    private boolean mNeedVibrate;
    private boolean mIsPlayBeep;
    private String mCharacterSet;

    private MediaPlayer mMediaPlayer;
    private CaptureActivityHandler mCaptureHandler;
    private ViewfinderView mViewfinderView;
    private InactivityTimer mInactivityTimer;
    private Vector<BarcodeFormat> mDecodeFormats;
    private NfcAdapter mNfcAdapter;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private ScreenOnReceiver mScreenOnReceiver;

    private TextView mTvBarResult;
    private ScrollView mScrollView;
    private Button mBtnSingleScan;
    private Button mBtnContinueScan;
    private Button mBtnCancelScan;

    private LaserlightController laserlightController;

    public String  comingFrom="";

    private final MediaPlayer.OnCompletionListener mBeepListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private boolean mIsScreenOn = false;

    private class ScreenOnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            LogUtil.d(TAG, "intent action:" + intent.getAction());

            if (mCaptureHandler != null
                    && mScannerMode == SCANNER_MODE_CONTINUE) {
//                LogUtil.trace("set handler is null...continue");
                mCaptureHandler.quitSynchronously();
                mCaptureHandler = null;
            }

            mIsScreenOn = true;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        if (ContextCompat.checkSelfPermission(CaptureActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CaptureActivity.this, new String[] {Manifest.permission.CAMERA}, 123);
        }

        comingFrom = getIntent().getStringExtra("comingFrom");

        initFindView();
        initData();
        initClickListener();
        initConfigurate();
        laserlightController = LaserlightController.getInstance();

//        if (mScannerMode == SCANNER_MODE_DEFAULT) {
//            mScannerMode = SCANNER_MODE_SINGLE;
//            mViewfinderView.setVisibility(View.VISIBLE);//
//            mBtnSingleScan.setTextSize(25);
//            mBtnSingleScan.setTextColor(Color.RED);
////                    mBtnContinueScan.setTextSize(20);
////                    mBtnContinueScan.setTextColor(Color.BLACK);
//            if (mCaptureHandler == null) {
////                        LogUtil.d(TAG, "start to single scanner...");
//                mCaptureHandler = new CaptureActivityHandler(this, mDecodeFormats, mCharacterSet);
//            }
//        }

//        CameraManager.get().startPreview();//

//
    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        // false
//        LogUtil.trace("CaptureActivity onResume...mHasSurface=" + mHasSurface);
        super.onResume();
        int flag3 = laserlightController.LaserlightController_N_Open();//打开内部碗LED
        if (flag3 == 1) {
//            Toast.makeText(this, "light_Open_Success", Toast.LENGTH_SHORT).show();
        } else if (flag3 == 0) {
//            Toast.makeText(this, "light_Open_Failure", Toast.LENGTH_SHORT).show();
        }
//        keepScreenWake();

        surfaceInit();
        audioInit();


//        mBtnSingleScan.performClick();

        // 熄屏 --> 亮屏，执行onResume()，重新启动
//        LogUtil.d(TAG, "onResume::mIsScreenOn=" + mIsScreenOn+" flag3:"+flag3);
        if (mIsScreenOn) {
            if (mScannerMode == SCANNER_MODE_CONTINUE) {
                mBtnSingleScan.setTextSize(20);
                mBtnContinueScan.setTextSize(30);

                if (mCaptureHandler == null) {
//                    LogUtil.d(TAG, "start to continue scanner...");

                    // 开始扫描线程
                    mCaptureHandler = new CaptureActivityHandler(
                            CaptureActivity.this, mDecodeFormats, mCharacterSet);
                }
                mIsScreenOn = false;
            }
        }
    }

    @Override
    protected void onPause() {
//        LogUtil.d(TAG, "onPause");
        super.onPause();
        if (mCaptureHandler != null) {
            mCaptureHandler.quitSynchronously();
            mCaptureHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onStop() {
//        LogUtil.d(TAG, "onStop");
        super.onStop();

//        releaseWakeLock();
    }

    @Override
    protected void onDestroy() {
//        LogUtil.d(TAG, "onDestory");
        mInactivityTimer.shutdown();
        super.onDestroy();

        if (null == mNfcAdapter) {
//            LogUtil.d(TAG, "NFC module is unable to use...");
            return;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                closeOrOpenNfcModel();
            }
        }
//        int flag4 = laserlightController.LaserlightController_N_Close();//关闭内部碗LED灯
//        if (flag4 == 1) {
//            Toast.makeText(this, "light_Close_Success", Toast.LENGTH_SHORT).show();
//        } else if (flag4 == 0) {
//            Toast.makeText(this, "light_Close_Failure", Toast.LENGTH_SHORT).show();
//        }
//        unregisterReceiver(mScreenOnReceiver);

        try{
            if(mScreenOnReceiver!=null)
                unregisterReceiver(mScreenOnReceiver);

        }catch(Exception e){}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_single: {
                if (mScannerMode == SCANNER_MODE_DEFAULT) {
                    mScannerMode = SCANNER_MODE_SINGLE;
                    mViewfinderView.setVisibility(View.VISIBLE);//
                    mBtnSingleScan.setTextSize(25);
                    mBtnSingleScan.setTextColor(Color.RED);
//                    mBtnContinueScan.setTextSize(20);
//                    mBtnContinueScan.setTextColor(Color.BLACK);
                    if (mCaptureHandler == null) {
//                        LogUtil.d(TAG, "start to single scanner...");
                        mCaptureHandler = new CaptureActivityHandler(this, mDecodeFormats, mCharacterSet);
                    }
                }
            }
            break;

            case R.id.btn_continue: {
                if (mScannerMode == SCANNER_MODE_DEFAULT) {
                    mViewfinderView.setVisibility(View.VISIBLE);//
                    mScannerMode = SCANNER_MODE_CONTINUE;
                    mBtnSingleScan.setTextSize(20);
                    mBtnSingleScan.setTextColor(Color.BLACK);
                    mBtnContinueScan.setTextSize(30);
                    mBtnContinueScan.setTextColor(Color.RED);

                    if (mCaptureHandler == null) {
//                        LogUtil.d(TAG, "start to continue scanner...");

                        // 开始扫描线程
                        mCaptureHandler = new CaptureActivityHandler(this, mDecodeFormats, mCharacterSet);
                    }
                }
            }
            break;

            case R.id.btn_cancel: {
//                LogUtil.d(TAG, "stop to scanner...");
                mViewfinderView.setVisibility(View.INVISIBLE);//
                mBtnSingleScan.setTextSize(20);
                mBtnContinueScan.setTextSize(20);
                mBtnSingleScan.setTextColor(Color.WHITE);
                mBtnContinueScan.setTextColor(Color.BLACK);
                if (mCaptureHandler != null && mScannerMode == SCANNER_MODE_CONTINUE) {
//                    LogUtil.trace("set handler is null...continue");
                    mCaptureHandler.quitSynchronously();
                    mCaptureHandler = null;
                } /*else if (mCaptureHandler != null
                        && mScannerMode == SCANNER_MODE_SINGLE) {
                    LogUtil.trace("set handler is null...single");
                    mCaptureHandler = null;
                  }*/

                CameraManager.get().setIsPreviewing(false);
                CameraManager.get().startPreview();//

                mScannerMode = SCANNER_MODE_DEFAULT;

                finish();
            }
            break;

            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        LogUtil.d(TAG, "surfaceCreated holder :mHasSurface=" + mHasSurface);

        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }

        // getSurfaceAngle()::270
//        LogUtil.d(TAG, "getSurfaceAngle()::" + getSurfaceAngle());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
//        LogUtil.d(TAG, "surfaceChanged holder");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return mViewfinderView;
    }

    public Handler getHandler() {
        return mCaptureHandler;
    }

    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();
    }

    public void handleDecode(final Result obj, Bitmap barcode) {
//        LogUtil.d(TAG, "handleDecode::get scanner result...");
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        switch (mScannerMode) {
            case SCANNER_MODE_SINGLE:
                mDecodeTime++;
                mTvBarResult.setText(mDecodeTime + ": " + obj.getText() + "\n");
                mCaptureHandler.quitSynchronously();

                mBtnSingleScan.setTextSize(20);
                mBtnContinueScan.setTextSize(20);
                mBtnSingleScan.setTextColor(Color.BLACK);
                mBtnContinueScan.setTextColor(Color.BLACK);
                // 一旦反馈结果，则停止扫描线程，等待下一次触发
                if (mCaptureHandler != null && mScannerMode == SCANNER_MODE_CONTINUE) {
//                    LogUtil.trace("set handler is null...continue");
                    mCaptureHandler.quitSynchronously();
                    mCaptureHandler = null;
                } else if (mCaptureHandler != null
                        && mScannerMode == SCANNER_MODE_SINGLE) {
//                    LogUtil.trace("set handler is null...single");
                    mCaptureHandler = null;
                }

                CameraManager.get().setIsPreviewing(false);
                CameraManager.get().startPreview();//
                mViewfinderView.setVisibility(View.INVISIBLE);//
                mScannerMode = SCANNER_MODE_DEFAULT;

                if (comingFrom.equalsIgnoreCase("fromRepair")) {

                }
                //when scan to search product
                else {
                    ActivitySearchItemList.strScannedBarCode = String.valueOf(obj.getText());
                }

                finish();

                break;
            case SCANNER_MODE_CONTINUE:
                // 在append之前清空内容，以免遗漏
                if (mDecodeTime % 10 == 0) {
                    mTvBarResult.setText("");
                }

                mDecodeTime++;
                mTvBarResult.append(mDecodeTime + ": " + obj.getText() + "\n");
//                LogUtil.trace("decodeTime = " + mDecodeTime + "; handleDecode::get result:" + obj.getText());
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                decodeAgain();
                break;
            default:
                break;
        }
    }

    private void initFindView() {
        mViewfinderView = (ViewfinderView) CaptureActivity.this.findViewById(R.id.viewfinder_view);
        mScrollView = (ScrollView) CaptureActivity.this.findViewById(R.id.scroll_result);
        mTvBarResult = (TextView) CaptureActivity.this.findViewById(R.id.tv_result);
        mBtnSingleScan = (Button) CaptureActivity.this.findViewById(R.id.btn_single);
        mBtnContinueScan = (Button) CaptureActivity.this.findViewById(R.id.btn_continue);
        mBtnCancelScan = (Button) CaptureActivity.this.findViewById(R.id.btn_cancel);
    }

    private void initData() {
//        LogUtil.trace();

        mScreenOnReceiver = new ScreenOnReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenOnReceiver, screenStatusIF);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(CaptureActivity.this);
        CameraManager.init(getApplication());
        mInactivityTimer = new InactivityTimer(this);
        mHasSurface = false;
    }

    private void initClickListener() {
        mBtnContinueScan.setOnClickListener(this);
        mBtnSingleScan.setOnClickListener(this);
        mBtnCancelScan.setOnClickListener(this);
    }

    /**
     * <功能描述> 是否开启屏幕常亮，NFC 功能是否需要关闭等
     *
     * @return void [返回类型说明]
     */
    private void initConfigurate() {
        if (null == mNfcAdapter) {
//            LogUtil.d(TAG, "NFC module is unable to use...");
            return;
        } else {
            if (mNfcAdapter.isEnabled()) {
                closeOrOpenNfcModel();
            }
        }
    }

    private void surfaceInit() {
        SurfaceView surfaceView = (SurfaceView) CaptureActivity.this.findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    private void audioInit() {
        mIsPlayBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            // RingerMode：RINGER_MODE_SILENT/RINGER_MODE_VIBRATE
            mIsPlayBeep = false;
        }

        // RingerMode：RINGER_MODE_NORMAL
        initBeepSound();
        mNeedVibrate = true;
    }

    private void initBeepSound() {
        if (mIsPlayBeep && mMediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud, so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mBeepListener);

            // ./res/raw/beep.ogg文件
            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
//        LogUtil.trace();

        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }

        mBtnSingleScan.performClick();

        /*if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }*/

    }

    private void decodeAgain() {
        CameraManager.get().requestPreviewFrame(mCaptureHandler.mDecodeThread.getHandler(), R.id.decode);
        CameraManager.get().requestAutoFocus(mCaptureHandler, R.id.auto_focus);

        this.drawViewfinder();
    }

    private void playBeepSoundAndVibrate() {
        if (mIsPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mNeedVibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private int getSurfaceAngle() {
        int rotation = this.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 270;
                break;
            case Surface.ROTATION_90:
                degrees = 0;
                break;
            case Surface.ROTATION_180:
                degrees = 90;
                break;
            case Surface.ROTATION_270:
                degrees = 180;
                break;
        }

        return degrees;
    }

    /**
     * <功能描述> 若在扫码过程中，NFC模块启动会导致执行onPause()，从而扫码停止；解决办法停止NFC功能
     * 发送NFC当前状态的广播，在com.android.settings中对当前状态做处理：打开的 --> 关闭，反之亦然
     *
     * @return void [返回类型说明]
     */
    private void closeOrOpenNfcModel() {
        //       if (mNfcAdapter.isEnabled()) {
        //           // NFC模块可用，且已打开
        //           int nfcState = mNfcAdapter.getAdapterState();
        //           LogUtil.d(TAG, "nfcAdapter.getAdapterState()::" + nfcState);

        sendNfcActionBroadcast(true);
        //       } else {
        //           sendNfcActionBroadcast(false);
        //       }
    }

    private void sendNfcActionBroadcast(boolean state) {
        Intent intent = new Intent();
        intent.setAction(NFC_ACTION_OPEN_CLOSE_ACTION);
        // 当前是open状态，传递true
        intent.putExtra("state_action", state);
        CaptureActivity.this.sendBroadcast(intent);
    }

//    /**
//     * <功能描述> 保持屏幕常亮
//     *
//     * @return void [返回类型说明]
//     */
//    private void keepScreenWake() {
//        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        mPowerManager.wakeUp(SystemClock.uptimeMillis());
//        mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, this
//                .getClass().getCanonicalName());
//        mWakeLock.acquire();
//    }

//    /**
//     * <功能描述> 释放WakeLock
//     *
//     * @return void [返回类型说明]
//     */
//    private void releaseWakeLock() {
//        if (mWakeLock != null && mWakeLock.isHeld()) {
//            mWakeLock.release();
//            mWakeLock = null;
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUESTS) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                AppConstant.showToast(CaptureActivity.this, "Camera Permission Is Required To Use This Feature");
                finish();

            }
        }
}
}