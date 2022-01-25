package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.pos.salon.R;

import java.util.Vector;

public class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getSimpleName();

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    private State mHandlerState;
    private final CaptureActivity mCaptureActivity;
    public final DecodeThread mDecodeThread;

    public CaptureActivityHandler(CaptureActivity activity, Vector<BarcodeFormat> decodeFormats, String characterSet) {
        this.mCaptureActivity = activity;

        mDecodeThread = new DecodeThread(activity, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));
        // 初始化DecodeHander实例
        mDecodeThread.start();

        // 初始化CaptureActivityHandler状态
        mHandlerState = State.SUCCESS;

        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.auto_focus:
                // When one auto focus pass finishes, start another. This is the
                // closest thing to continuous AF. It does seem to hunt a bit,
                // but I'm not sure what else to do.
                if (mHandlerState == State.PREVIEW) {
//                    LogUtil.trace("cause to auto focus...");
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                }
                break;
            case R.id.restart_preview:
//                LogUtil.d(TAG, "Got restart preview message");
                restartPreviewAndDecode();
                break;
            case R.id.decode_succeeded:
//                LogUtil.d(TAG, "Got decode succeeded message");
                mHandlerState = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = bundle == null ? null : (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
                mCaptureActivity.handleDecode((Result) message.obj, barcode);
                break;
            case R.id.decode_failed:
                // We're decoding as fast as possible, so when one decode fails,
                // start another.
                mHandlerState = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
                break;
            case R.id.return_scan_result:
//                LogUtil.d(TAG, "Got return scan result message");
                mCaptureActivity.setResult(Activity.RESULT_OK,
                        (Intent) message.obj);
                mCaptureActivity.finish();
                break;
            case R.id.launch_product_query:
//                LogUtil.d(TAG, "Got product query message");
                String url = (String) message.obj;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                mCaptureActivity.startActivity(intent);
                break;
        }
    }

    public void quitSynchronously() {
        mHandlerState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
//        LogUtil.trace();

        // 开始预览Camera图像
        CameraManager.get().startPreview();
//        LogUtil.d(TAG, "restartPreviewAndDecode  state: " + mHandlerState);

        if (mHandlerState == State.SUCCESS) {
            mHandlerState = State.PREVIEW;
            // 捕获图像
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
            // TODO 为什么执行如下逻辑？
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            mCaptureActivity.drawViewfinder();
        }
    }

}


