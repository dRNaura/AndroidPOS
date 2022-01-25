
package com.pos.salon.utils.update;

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
import com.pos.salon.utils.update.base.RestartHandler;
import com.pos.salon.utils.update.base.UpdateChecker;
import com.pos.salon.utils.update.base.UpdateParser;
import com.pos.salon.utils.update.base.UpdateStrategy;
import com.pos.salon.utils.update.flow.CallbackDelegate;
import com.pos.salon.utils.update.flow.Launcher;
import com.pos.salon.utils.update.impl.DefaultRestartHandler;
import com.pos.salon.utils.update.model.CheckEntity;

public class UpdateBuilder {

    private boolean isDaemon;
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
    private UpdateConfig config;
    private RestartHandler restartHandler;

    private CallbackDelegate callbackDelegate;
    
    private UpdateBuilder(UpdateConfig config) {
        this.config = config;
        callbackDelegate = new CallbackDelegate();
        callbackDelegate.setCheckDelegate(config.getCheckCallback());
        callbackDelegate.setDownloadDelegate(config.getDownloadCallback());
    }


    public static UpdateBuilder create() {
        return create(UpdateConfig.getConfig());
    }


    public static UpdateBuilder create(UpdateConfig config) {
        return new UpdateBuilder(config);
    }

    public void check() {
        Launcher.getInstance().launchCheck(this);
    }

    public void checkWithDaemon(long retryTime) {
        RestartHandler handler = getRestartHandler();
        handler.attach(this, retryTime);
        this.callbackDelegate.setRestartHandler(handler);
        isDaemon = true;
        Launcher.getInstance().launchCheck(this);
    }

    public UpdateBuilder setUrl(String url) {
        this.entity = new CheckEntity().setUrl(url);
        return this;
    }

    public UpdateBuilder setCheckEntity(CheckEntity entity) {
        this.entity = entity;
        return this;
    }

    public UpdateBuilder setUpdateChecker(UpdateChecker checker) {
        this.updateChecker = checker;
        return this;
    }

    public UpdateBuilder setFileChecker(FileChecker checker) {
        this.fileChecker = checker;
        return this;
    }

    public UpdateBuilder setCheckWorker(Class<? extends CheckWorker> checkWorker) {
        this.checkWorker = checkWorker;
        return this;
    }

    public UpdateBuilder setDownloadWorker(Class<? extends DownloadWorker> downloadWorker) {
        this.downloadWorker = downloadWorker;
        return this;
    }

    public UpdateBuilder setDownloadCallback(DownloadCallback callback) {
        if (callback == null) {
            this.callbackDelegate.setDownloadDelegate(config.getDownloadCallback());
        } else {
            this.callbackDelegate.setDownloadDelegate(callback);
        }
        return this;
    }

    public UpdateBuilder setCheckCallback(CheckCallback callback) {
        if (callback == null) {
            this.callbackDelegate.setCheckDelegate(config.getCheckCallback());
        } else {
            this.callbackDelegate.setCheckDelegate(callback);
        }
        return this;
    }

    public UpdateBuilder setUpdateParser(UpdateParser updateParser) {
        this.updateParser = updateParser;
        return this;
    }

    public UpdateBuilder setFileCreator(FileCreator fileCreator) {
        this.fileCreator = fileCreator;
        return this;
    }

    public UpdateBuilder setDownloadNotifier(DownloadNotifier downloadNotifier) {
        this.downloadNotifier = downloadNotifier;
        return this;
    }

    public UpdateBuilder setInstallNotifier(InstallNotifier installNotifier) {
        this.installNotifier = installNotifier;
        return this;
    }

    public UpdateBuilder setCheckNotifier(CheckNotifier checkNotifier) {
        this.checkNotifier = checkNotifier;
        return this;
    }

    public UpdateBuilder setUpdateStrategy(UpdateStrategy strategy) {
        this.updateStrategy = strategy;
        return this;
    }

    public UpdateBuilder setInstallStrategy(InstallStrategy installStrategy) {
        this.installStrategy = installStrategy;
        return this;
    }

    public UpdateBuilder setRestartHandler(RestartHandler restartHandler) {
        this.restartHandler = restartHandler;
        return this;
    }

    public UpdateStrategy getUpdateStrategy() {
        if (updateStrategy == null) {
            updateStrategy = config.getUpdateStrategy();
        }
        return updateStrategy;
    }

    public CheckEntity getCheckEntity () {
        if (this.entity == null) {
            this.entity = config.getCheckEntity();
        }
        return this.entity;
    }

    public UpdateChecker getUpdateChecker() {
        if (updateChecker == null) {
            updateChecker = config.getUpdateChecker();
        }
        return updateChecker;
    }

    public FileChecker getFileChecker() {
        return fileChecker != null ? fileChecker : config.getFileChecker();
    }

    public CheckNotifier getCheckNotifier() {
        if (checkNotifier == null) {
            checkNotifier = config.getCheckNotifier();
        }
        return checkNotifier;
    }

    public InstallNotifier getInstallNotifier() {
        if (installNotifier == null) {
            installNotifier = config.getInstallNotifier();
        }
        return installNotifier;
    }

    public DownloadNotifier getDownloadNotifier() {
        if (downloadNotifier == null) {
            downloadNotifier = config.getDownloadNotifier();
        }
        return downloadNotifier;
    }

    public UpdateParser getUpdateParser() {
        if (updateParser == null) {
            updateParser = config.getUpdateParser();
        }
        return updateParser;
    }

    public Class<? extends CheckWorker> getCheckWorker() {
        if (checkWorker == null) {
            checkWorker = config.getCheckWorker();
        }
        return checkWorker;
    }

    public Class<? extends DownloadWorker> getDownloadWorker() {
        if (downloadWorker == null) {
            downloadWorker = config.getDownloadWorker();
        }
        return downloadWorker;
    }

    public FileCreator getFileCreator() {
        if (fileCreator == null) {
            fileCreator = config.getFileCreator();
        }
        return fileCreator;
    }

    public CheckCallback getCheckCallback() {
        return callbackDelegate;
    }

    public DownloadCallback getDownloadCallback() {
        return callbackDelegate;
    }

    public InstallStrategy getInstallStrategy() {
        if (installStrategy == null) {
            installStrategy = config.getInstallStrategy();
        }
        return installStrategy;
    }

    public RestartHandler getRestartHandler() {
        if (restartHandler == null) {
            restartHandler = new DefaultRestartHandler();
        }
        return restartHandler;
    }

    public final UpdateConfig getConfig() {
        return config;
    }


    public boolean isDaemon() {
        return isDaemon;
    }

    public void stopDaemon() {
        if (isDaemon) {
            restartHandler.detach();
        }
    }

}
