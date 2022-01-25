package com.pos.salon.activity.JeoPowerDeviceSDK.Scanner;

import android.content.Context;
import android.os.PowerManager;

public class WakeLockUtil {
    private static final String TAG = WakeLockUtil.class.getSimpleName();

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private Context mContext;

    public WakeLockUtil(Context context) {
        mContext = context;
        mPowerManager = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        // mWakeLock = pManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
        // TAG);
        mWakeLock.setReferenceCounted(false);
    }

    public void lock() {
        if (mWakeLock != null) {
            mWakeLock.acquire(15 * 1000);
        }
    }

    public void unLock() {
        if (mWakeLock != null) {
            mWakeLock.release();
        }
    }

}
