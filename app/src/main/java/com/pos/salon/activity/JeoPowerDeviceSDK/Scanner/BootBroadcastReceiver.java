package com.pos.salon.activity.JeoPowerDeviceSDK.Scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BootBroadcastReceiver.class
            .getSimpleName();
    private static final String SCAN_SERVICE_DESTORY = "ScanServiceDestroy";

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    Context arg0 = (Context) msg.obj;
                    // if (Preference.getScanSelfopenSupport(
                    // BaseApplication.getAppContext(), true)) {
                    Intent service = new Intent(arg0, ScanService.class);
                    arg0.startService(service);
                    System.out.println("BootBroadcastReceiver serviceup");
                    // }
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "onReceive arg1.getAction: " + intent.getAction());

        if (SCAN_SERVICE_DESTORY.equals(intent.getAction())) {
            Intent service = new Intent(context, ScanService.class);
            context.startService(service);
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent service = new Intent(context, ScanService.class);
            context.startService(service);

            Message message = new Message();
            message.what = 1000;
            message.obj = context;
            mHandler.sendMessageDelayed(message, 5000);
        }
    };
}
