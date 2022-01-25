package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

public class PreviewCallback implements Camera.PreviewCallback {
    private static final String TAG = PreviewCallback.class.getSimpleName();

    private final CameraConfigurationManager configManager;
    private final boolean mUseOneShotPreviewCallback;
    // DecodeHandler的实例
    private Handler mPreviewHandler;
    private int mPreviewMessage;

    public PreviewCallback(CameraConfigurationManager configManager,
                           boolean useOneShotPreviewCallback) {
        this.configManager = configManager;
        this.mUseOneShotPreviewCallback = useOneShotPreviewCallback;
    }

    public void setHandler(Handler previewHandler, int previewMessage) {
        this.mPreviewHandler = previewHandler;
        this.mPreviewMessage = previewMessage;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // true
//        LogUtil.d(TAG, "onPreviewFrame  startPreview: " + camera + "; mUseOneShotPreviewCallback=" + mUseOneShotPreviewCallback);

        Point cameraResolution = configManager.getCameraResolution();
        if (!mUseOneShotPreviewCallback) {
            camera.setPreviewCallback(null);
        }
        if (mPreviewHandler != null) {
            // 将图像数据发送给DecodeHandler处理识别流程
            Message message = mPreviewHandler.obtainMessage(mPreviewMessage,
                    cameraResolution.x, cameraResolution.y, data);

            // 800 600
//            LogUtil.trace("cameraResolution.x=" + cameraResolution.x + "; cameraResolution.y=" + cameraResolution.y);
            message.sendToTarget();
            mPreviewHandler = null;
        } else {
//            LogUtil.d(TAG, "Got preview callback, but no handler for it");
        }
    }

}

