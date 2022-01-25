package com.pos.salon.utils.update.impl;

import com.pos.salon.utils.update.base.RestartHandler;
import com.pos.salon.utils.update.model.Update;
import java.io.File;

public class DefaultRestartHandler extends RestartHandler {

    // ====复写对应的回调并进行任务重启======
    @Override
    public void onDownloadComplete(File file) {
        retry();
    }

    @Override
    public void onDownloadError(Throwable t) {
        retry();
    }

    @Override
    public void noUpdate() {
        retry();
    }

    @Override
    public void onCheckError(Throwable t) {
        retry();
    }

    @Override
    public void onUserCancel() {
        retry();
    }

    @Override
    public void onCheckIgnore(Update update) {
        retry();
    }
}
