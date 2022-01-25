
package com.pos.salon.utils.update.base;

import com.pos.salon.utils.update.model.Update;
import java.io.File;

public abstract class FileChecker {

    protected Update update;
    protected File file;

    final void attach(Update update, File file) {
        this.update = update;
        this.file = file;
    }

    final boolean checkBeforeDownload() {
        if (file == null || !file.exists()) {
            return false;
        }

        try {
            return onCheckBeforeDownload();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected abstract boolean onCheckBeforeDownload() throws Exception;

    protected abstract void onCheckBeforeInstall() throws Exception;
}
