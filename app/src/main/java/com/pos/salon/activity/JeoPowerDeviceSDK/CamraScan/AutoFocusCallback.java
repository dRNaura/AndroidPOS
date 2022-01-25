package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

public class AutoFocusCallback implements Camera.AutoFocusCallback {
    private static final String TAG = AutoFocusCallback.class.getSimpleName();
    private static final long AUTOFOCUS_INTERVAL_MS = 1500L;

    // 实际上是CaptureActivityHandler实例
    private Handler mAutoFocusHandler;
    private int mAutoFocusMessage;

    public void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
        this.mAutoFocusHandler = autoFocusHandler;
        this.mAutoFocusMessage = autoFocusMessage;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
//        LogUtil.trace("success::" + success);

        if (success) {
            CameraManager.get().initDriver();
            camera.cancelAutoFocus();
        }
        if (mAutoFocusHandler != null) {
            Message message = mAutoFocusHandler.obtainMessage(
                    mAutoFocusMessage, success);
//            LogUtil.trace("mAutoFocusMessage::" + mAutoFocusMessage);

            // Simulate continuous autofocus by sending a focus request every
            // AUTOFOCUS_INTERVAL_MS milliseconds.
            mAutoFocusHandler
                    .sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);
            mAutoFocusHandler = null;
        } else {
//            LogUtil.d(TAG, "Got auto-focus callback, but no handler for it");
        }
    }

}
