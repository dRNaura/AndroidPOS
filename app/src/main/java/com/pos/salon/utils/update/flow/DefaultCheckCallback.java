
package com.pos.salon.utils.update.flow;

import android.app.Activity;
import android.app.Dialog;
import com.pos.salon.utils.update.UpdateBuilder;
import com.pos.salon.utils.update.base.CheckCallback;
import com.pos.salon.utils.update.base.CheckNotifier;
import com.pos.salon.utils.update.model.Update;
import com.pos.salon.utils.update.utilUpdate.ActivityManager;
import com.pos.salon.utils.update.utilUpdate.SafeDialogHandle;
import com.pos.salon.utils.update.utilUpdate.Utils;

public final class DefaultCheckCallback implements CheckCallback {
    private UpdateBuilder builder;
    private CheckCallback callback;

    public void setBuilder (UpdateBuilder builder) {
        this.builder = builder;
        this.callback = builder.getCheckCallback();
    }

    @Override
    public void onCheckStart() {
        try {
            if (callback != null) {
                callback.onCheckStart();
            }
        } catch (Throwable t) {
            onCheckError(t);
        }
    }

    @Override
    public void hasUpdate(Update update) {
        try {
            if (callback != null) {
                callback.hasUpdate(update);
            }

            CheckNotifier notifier = builder.getCheckNotifier();
            notifier.setBuilder(builder);
            notifier.setUpdate(update);
            Activity current = ActivityManager.get().topActivity();

            if (Utils.isValid(current)
                    && builder.getUpdateStrategy().isShowUpdateDialog(update)) {
                Dialog dialog = notifier.create(current);
                SafeDialogHandle.safeShowDialog(dialog);
            } else {
                notifier.sendDownloadRequest();
            }
        } catch (Throwable t) {
            onCheckError(t);
        }
    }

    @Override
    public void noUpdate() {
        try {
            if (callback != null) {
                callback.noUpdate();
            }
        } catch (Throwable t) {
            onCheckError(t);
        }

    }

    @Override
    public void onCheckError(Throwable t) {
        try {
            if (callback != null) {
                callback.onCheckError(t);
            }
        } catch (Throwable ignore) {
            ignore.printStackTrace();
        }
    }

    @Override
    public void onUserCancel() {
        try {
            if (callback != null) {
                callback.onUserCancel();
            }
        } catch (Throwable t) {
            onCheckError(t);
        }

    }

    @Override
    public void onCheckIgnore(Update update) {
        try {
            if (callback != null) {
                callback.onCheckIgnore(update);
            }
        } catch (Throwable t) {
            onCheckError(t);
        }
    }

}
