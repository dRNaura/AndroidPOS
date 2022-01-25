
package com.pos.salon.utils.update.base;

import android.app.Activity;
import android.app.Dialog;
import com.pos.salon.utils.update.UpdateBuilder;
import com.pos.salon.utils.update.flow.Launcher;
import com.pos.salon.utils.update.model.Update;
import com.pos.salon.utils.update.utilUpdate.UpdatePreference;

public abstract class CheckNotifier {

    protected UpdateBuilder builder;
    protected Update update;
    private CheckCallback callback;
    public final void setBuilder(UpdateBuilder builder) {
        this.builder = builder;
        this.callback = builder.getCheckCallback();
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public abstract Dialog create(Activity context);

    public final void sendDownloadRequest() {
        Launcher.getInstance().launchDownload(update,builder);
    }
    protected final void sendUserCancel() {
        if (this.callback != null) {
            this.callback.onUserCancel();
        }
    }
    protected final void sendUserIgnore() {
        if (this.callback != null) {
            this.callback.onCheckIgnore(update);
        }
        UpdatePreference.saveIgnoreVersion(update.getVersionCode());
    }

}