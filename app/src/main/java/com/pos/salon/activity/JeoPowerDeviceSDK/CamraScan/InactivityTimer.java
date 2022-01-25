package com.pos.salon.activity.JeoPowerDeviceSDK.CamraScan;

import android.app.Activity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class InactivityTimer {
    private static final int INACTIVITY_DELAY_SECONDS = 3 * 60;

    // 创建只有一条线程的线程池，他可以在指定延迟后执行线程任务
    private final ScheduledExecutorService mInactivityTimer = Executors
            .newSingleThreadScheduledExecutor(new DaemonThreadFactory());

    private final Activity mExecutorActivity;

    private ScheduledFuture<?> mInactivityFuture = null;

    public InactivityTimer(Activity activity) {
        this.mExecutorActivity = activity;
        onActivity();
    }

    public void onActivity() {
        cancel();
        mInactivityFuture = mInactivityTimer.schedule(new FinishListener(
                mExecutorActivity), INACTIVITY_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    public void shutdown() {
//        LogUtil.trace();

        cancel();
        mInactivityTimer.shutdown();
    }

    private void cancel() {
//        LogUtil.trace();
        if (mInactivityFuture != null) {
            mInactivityFuture.cancel(true);
            mInactivityFuture = null;
        }

    }

    private static final class DaemonThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }

}
