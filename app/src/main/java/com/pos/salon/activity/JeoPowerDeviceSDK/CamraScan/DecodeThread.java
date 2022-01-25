package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class DecodeThread  extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";

    private final CaptureActivity mCaptureActivity;
    private final CountDownLatch mHandlerInitLatch;
    private final Hashtable<DecodeHintType, Object> mHints;
    // DecodeHandler实例
    private Handler handler;

    public DecodeThread(CaptureActivity activity, Vector<BarcodeFormat> decodeFormats,
                        String characterSet, ResultPointCallback resultPointCallback) {
        this.mCaptureActivity = activity;
        mHandlerInitLatch = new CountDownLatch(1);
        mHints = new Hashtable<DecodeHintType, Object>(3);

        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);

        }
        mHints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        if (characterSet != null) {
            mHints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }
        // 识别点获取后，执行回调
        mHints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK,
                resultPointCallback);
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(mCaptureActivity, mHints);
        mHandlerInitLatch.countDown();
        Looper.loop();
    }

    public Handler getHandler() {
        try {
            mHandlerInitLatch.await();
        } catch (InterruptedException exception) {
            // continue?
            exception.printStackTrace();
        }
        return handler;
    }

}
