
package com.pos.salon.utils.update.flow;

import com.pos.salon.utils.update.UpdateBuilder;
import com.pos.salon.utils.update.base.CheckWorker;
import com.pos.salon.utils.update.base.DownloadWorker;
import com.pos.salon.utils.update.model.Update;


public final class Launcher {
    private static Launcher launcher;
    private Launcher() {}
    public static Launcher getInstance() {
        if (launcher == null) {
            launcher = new Launcher();
        }
        return launcher;
    }

    public void launchCheck(UpdateBuilder builder) {
        // 定义一个默认的检查更新回调监听。用于接收api检查更新任务所发出的通知。并链接后续流程。
        DefaultCheckCallback checkCB = new DefaultCheckCallback();
        checkCB.setBuilder(builder);
        checkCB.onCheckStart();

        CheckWorker checkWorker = null;
        try {
            checkWorker = builder.getCheckWorker().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Could not create instance for %s", builder.getCheckWorker().getCanonicalName()
            ), e);
        }
        checkWorker.setBuilder(builder);
        checkWorker.setCheckCB(checkCB);
        builder.getConfig().getExecutor().execute(checkWorker);
    }

    /**
     * 调起apk文件下载任务。
     *
     * @param update 更新api实体类。不能为null
     * @param builder 更新任务实例
     */
    public void launchDownload(Update update, UpdateBuilder builder) {
        // 定义一个默认的下载状态回调监听。用于接收文件下载任务所发出的通知。并链接下载后续流程
        DefaultDownloadCallback downloadCB = new DefaultDownloadCallback();
        downloadCB.setBuilder(builder);
        downloadCB.setUpdate(update);

        DownloadWorker downloadWorker = null;
        try {
            downloadWorker = builder.getDownloadWorker().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Could not create instance for %s", builder.getDownloadWorker().getCanonicalName()
            ), e);
        }
        downloadWorker.setUpdate(update);
        downloadWorker.setUpdateBuilder(builder);
        downloadWorker.setCallback(downloadCB);

        builder.getConfig().getExecutor().execute(downloadWorker);
    }

}
