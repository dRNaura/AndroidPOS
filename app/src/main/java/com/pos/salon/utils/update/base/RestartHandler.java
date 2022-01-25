package com.pos.salon.utils.update.base;

import com.pos.salon.utils.update.UpdateBuilder;
import com.pos.salon.utils.update.model.Update;
import com.pos.salon.utils.update.utilUpdate.L;
import com.pos.salon.utils.update.utilUpdate.Utils;
import java.io.File;

public abstract class RestartHandler implements CheckCallback, DownloadCallback{

    protected UpdateBuilder builder;
    protected long retryTime;
    private RetryTask task;

    public final void attach(UpdateBuilder builder, long retryTime) {
        this.builder = builder;
        this.retryTime = Math.max(1, retryTime);
    }

    public final void detach() {
        builder = null;
    }

    protected final void retry() {
        if (builder == null) {
            return;
        }
        if (task == null) {
            task = new RetryTask();
        }
        Utils.getMainHandler().removeCallbacks(task);
        Utils.getMainHandler().postDelayed(task, retryTime * 1000);
    }

    private class RetryTask implements Runnable {

        @Override
        public void run() {
            if (builder != null) {
                L.d("Restart update for daemon");
                builder.checkWithDaemon(retryTime);
            }
        }
    }
    @Override
    public void onDownloadStart() {}

    @Override
    public void onCheckStart() {}

    @Override
    public void onDownloadComplete(File file) {}

    @Override
    public void hasUpdate(Update update) {}

    @Override
    public void onDownloadProgress(long current, long total) {}

    @Override
    public void noUpdate() {}

    @Override
    public void onDownloadError(Throwable t) {}

    @Override
    public void onCheckError(Throwable t) {}

    @Override
    public void onUserCancel() {}

    @Override
    public void onCheckIgnore(Update update) {}
}
