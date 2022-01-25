
package com.pos.salon.utils.update;

import android.text.TextUtils;

import com.pos.salon.utils.update.base.CheckCallback;
import com.pos.salon.utils.update.base.CheckNotifier;
import com.pos.salon.utils.update.base.CheckWorker;
import com.pos.salon.utils.update.base.DownloadCallback;
import com.pos.salon.utils.update.base.DownloadNotifier;
import com.pos.salon.utils.update.base.DownloadWorker;
import com.pos.salon.utils.update.base.FileChecker;
import com.pos.salon.utils.update.base.FileCreator;
import com.pos.salon.utils.update.base.InstallNotifier;
import com.pos.salon.utils.update.base.InstallStrategy;
import com.pos.salon.utils.update.base.UpdateChecker;
import com.pos.salon.utils.update.base.UpdateParser;
import com.pos.salon.utils.update.base.UpdateStrategy;
import com.pos.salon.utils.update.impl.DefaultCheckWorker;
import com.pos.salon.utils.update.impl.DefaultDownloadNotifier;
import com.pos.salon.utils.update.impl.DefaultDownloadWorker;
import com.pos.salon.utils.update.impl.DefaultFileChecker;
import com.pos.salon.utils.update.impl.DefaultFileCreator;
import com.pos.salon.utils.update.impl.DefaultInstallNotifier;
import com.pos.salon.utils.update.impl.DefaultInstallStrategy;
import com.pos.salon.utils.update.impl.DefaultUpdateChecker;
import com.pos.salon.utils.update.impl.DefaultUpdateNotifier;
import com.pos.salon.utils.update.impl.WifiFirstStrategy;
import com.pos.salon.utils.update.model.CheckEntity;
import com.pos.salon.utils.update.utilUpdate.L;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class UpdateConfig {

    private Class<? extends CheckWorker> checkWorker;
    private Class<? extends DownloadWorker> downloadWorker;
    private CheckEntity entity;
    private UpdateStrategy updateStrategy;
    private CheckNotifier checkNotifier;
    private InstallNotifier installNotifier;
    private DownloadNotifier downloadNotifier;
    private UpdateParser updateParser;
    private FileCreator fileCreator;
    private UpdateChecker updateChecker;
    private FileChecker fileChecker;
    private InstallStrategy installStrategy;
    private ExecutorService executor;
    private CheckCallback checkCallback;
    private DownloadCallback downloadCallback;

    private static UpdateConfig DEFAULT;


    public static UpdateConfig getConfig() {
        if (DEFAULT == null) {
            DEFAULT = new UpdateConfig();
        }
        return DEFAULT;
    }

    public static UpdateConfig createConfig() {
        return new UpdateConfig();
    }


    public UpdateConfig setUrl(String url) {
        this.entity = new CheckEntity().setUrl(url);
        return this;
    }

    public UpdateConfig setCheckEntity(CheckEntity entity) {
        this.entity = entity;
        return this;
    }

    public UpdateConfig setUpdateChecker(UpdateChecker checker) {
        this.updateChecker = checker;
        return this;
    }

    public UpdateConfig setFileChecker(FileChecker checker) {
        this.fileChecker = checker;
        return this;
    }

    public UpdateConfig setCheckWorker(Class<? extends CheckWorker> checkWorker) {
        this.checkWorker = checkWorker;
        return this;
    }

    public UpdateConfig setDownloadWorker(Class<? extends DownloadWorker> downloadWorker) {
        this.downloadWorker = downloadWorker;
        return this;
    }

    public UpdateConfig setDownloadCallback(DownloadCallback callback) {
        this.downloadCallback = callback;
        return this;
    }

    public UpdateConfig setCheckCallback(CheckCallback callback) {
        this.checkCallback = callback;
        return this;
    }

    public UpdateConfig setUpdateParser(UpdateParser updateParser) {
        this.updateParser = updateParser;
        return this;
    }

    public UpdateConfig setFileCreator(FileCreator fileCreator) {
        this.fileCreator = fileCreator;
        return this;
    }

    public UpdateConfig setDownloadNotifier(DownloadNotifier notifier) {
        this.downloadNotifier = notifier;
        return this;
    }

    public UpdateConfig setInstallNotifier(InstallNotifier notifier) {
        this.installNotifier = notifier;
        return this;
    }

    public UpdateConfig setCheckNotifier(CheckNotifier notifier) {
        this.checkNotifier = notifier;
        return this;
    }

    public UpdateConfig setUpdateStrategy(UpdateStrategy strategy) {
        this.updateStrategy = strategy;
        return this;
    }

    public UpdateConfig setInstallStrategy(InstallStrategy installStrategy) {
        this.installStrategy = installStrategy;
        return this;
    }

    public UpdateStrategy getUpdateStrategy() {
        if (updateStrategy == null) {
            updateStrategy = new WifiFirstStrategy();
        }
        return updateStrategy;
    }

    public CheckEntity getCheckEntity() {
        if (this.entity == null || TextUtils.isEmpty(this.entity.getUrl())) {
            throw new IllegalArgumentException("Do not set url in CheckEntity");
        }
        return this.entity;
    }

    public CheckNotifier getCheckNotifier() {
        if (checkNotifier == null) {
            checkNotifier = new DefaultUpdateNotifier();
        }
        return checkNotifier;
    }

    public InstallNotifier getInstallNotifier() {
        if (installNotifier == null) {
            installNotifier = new DefaultInstallNotifier();
        }
        return installNotifier;
    }

    public UpdateChecker getUpdateChecker() {
        if (updateChecker == null) {
            updateChecker = new DefaultUpdateChecker();
        }
        return updateChecker;
    }

    public FileChecker getFileChecker() {
        if (fileChecker == null) {
            fileChecker = new DefaultFileChecker();
        }
        return fileChecker;
    }

    public DownloadNotifier getDownloadNotifier() {
        if (downloadNotifier == null) {
            downloadNotifier = new DefaultDownloadNotifier();
        }
        return downloadNotifier;
    }

    public UpdateParser getUpdateParser() {
        if (updateParser == null) {
            throw new IllegalStateException("update parser is null");
        }
        return updateParser;
    }

    public Class<? extends CheckWorker> getCheckWorker() {
        if (checkWorker == null) {
            checkWorker = DefaultCheckWorker.class;
        }
        return checkWorker;
    }

    public Class<? extends DownloadWorker> getDownloadWorker() {
        if (downloadWorker == null) {
            downloadWorker = DefaultDownloadWorker.class;
        }
        return downloadWorker;
    }

    public FileCreator getFileCreator() {
        if (fileCreator == null) {
            fileCreator = new DefaultFileCreator();
        }
        return fileCreator;
    }

    public InstallStrategy getInstallStrategy() {
        if (installStrategy == null) {
            installStrategy = new DefaultInstallStrategy();
        }
        return installStrategy;
    }

    public ExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(2);
        }
        return executor;
    }

    public CheckCallback getCheckCallback() {
        return checkCallback;
    }

    public DownloadCallback getDownloadCallback() {
        return downloadCallback;
    }

    public static void LogEnable(boolean enable) {
        L.ENABLE = enable;
    }
}

