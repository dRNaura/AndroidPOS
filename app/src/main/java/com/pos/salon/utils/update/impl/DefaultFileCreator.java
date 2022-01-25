
package com.pos.salon.utils.update.impl;

import android.content.Context;
import android.os.Build;
import com.pos.salon.utils.update.base.FileCreator;
import com.pos.salon.utils.update.model.Update;
import com.pos.salon.utils.update.utilUpdate.ActivityManager;
import java.io.File;


public class DefaultFileCreator extends FileCreator {
    @Override
    public File create(Update update) {
        File cacheDir = getCacheDir();
        cacheDir.mkdirs();
        return new File(cacheDir, "update_normal_" + update.getVersionName() + ".apk");
    }

    @Override
    public File createForDaemon(Update update) {
        File cacheDir = getCacheDir();
        cacheDir.mkdirs();
        return new File(cacheDir, "update_daemon_" + update.getVersionName() + ".apk");
    }

    private File getCacheDir() {
        Context context = ActivityManager.get().getApplicationContext();
        File cacheDir;
        if (Build.VERSION.SDK_INT < 24) {
            cacheDir = context.getExternalCacheDir();
        } else {
            cacheDir = context.getCacheDir();
        }
        //if (cacheDir == null) {
        //    cacheDir = context.getCacheDir();
        //}
        cacheDir = new File(cacheDir, "update");
        return cacheDir;
    }
}
