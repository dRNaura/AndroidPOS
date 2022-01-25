
package com.pos.salon.utils.update.base;


import java.io.File;


public interface DownloadCallback {
    void onDownloadStart();

    void onDownloadComplete(File file);

    void onDownloadProgress(long current, long total);

    void onDownloadError(Throwable t);
}
