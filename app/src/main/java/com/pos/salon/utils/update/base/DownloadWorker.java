
package com.pos.salon.utils.update.base;

import com.pos.salon.utils.update.UpdateBuilder;
import com.pos.salon.utils.update.flow.DefaultDownloadCallback;
import com.pos.salon.utils.update.model.Update;
import com.pos.salon.utils.update.utilUpdate.Utils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public abstract class DownloadWorker implements Runnable {

    private DefaultDownloadCallback callback;
    private static Map<DownloadWorker, File> downloading = new HashMap<>();

    protected Update update;
    protected UpdateBuilder builder;
    public final void setUpdate(Update update) {
        this.update = update;
    }

    public final void setUpdateBuilder(UpdateBuilder builder) {
        this.builder = builder;
    }

    public final void setCallback(DefaultDownloadCallback callback) {
        this.callback = callback;
    }

    @Override
    public final void run() {
        try {
            File file = builder.getFileCreator().createWithBuilder(update, builder);
            FileChecker checker = builder.getFileChecker();
            checker.attach(update, file);
            if (builder.getFileChecker().checkBeforeDownload()) {
                // check success: skip download and show install dialog if needed.
                callback.postForInstall(file);
                return;
            }
            checkDuplicateDownload(file);

            sendDownloadStart();
            String url = update.getUpdateUrl();
            file.getParentFile().mkdirs();
            download(url,file);
        } catch (Throwable e) {
            sendDownloadError(e);
        }
    }

    private void checkDuplicateDownload(File file) {
        if (downloading.containsValue(file)) {
            throw new RuntimeException(String.format(
                    "You can not download the same file using multiple download tasks simultaneously，the file path is %s",
                    file.getAbsolutePath()
            ));
        }
        downloading.put(this, file);
    }
    protected abstract void download(String url, File target) throws Exception;

    protected final void sendDownloadStart() {
        if (callback == null) return;

        Utils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (callback == null) return;
                callback.onDownloadStart();
            }
        });
    }
    protected final void sendDownloadProgress(final long current, final long total) {
        if (callback == null) return;

        Utils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (callback == null) return;
                callback.onDownloadProgress(current, total);
            }
        });
    }

    protected final void sendDownloadComplete(final File file) {
        try {
            builder.getFileChecker().onCheckBeforeInstall();
        } catch (Exception e) {
            sendDownloadError(e);
            return;
        }

        if (callback == null) return;
        Utils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (callback == null) return;
                callback.onDownloadComplete(file);
                callback.postForInstall(file);
                downloading.remove(DownloadWorker.this);
            }
        });
    }

    protected final void sendDownloadError(final Throwable t) {
        if (callback == null) return;

        Utils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (callback == null) return;
                callback.onDownloadError(t);
                downloading.remove(DownloadWorker.this);
            }
        });
    }

}
