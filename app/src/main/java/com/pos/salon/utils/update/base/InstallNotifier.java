
package com.pos.salon.utils.update.base;

import android.app.Activity;
import android.app.Dialog;
import com.pos.salon.utils.update.UpdateBuilder;
import com.pos.salon.utils.update.model.Update;
import com.pos.salon.utils.update.utilUpdate.ActivityManager;
import com.pos.salon.utils.update.utilUpdate.UpdatePreference;
import java.io.File;

public abstract class InstallNotifier {

    protected UpdateBuilder builder;
    protected Update update;
    protected File file;

    public final void setBuilder(UpdateBuilder builder) {
        this.builder = builder;
    }

    public final void setUpdate(Update update) {
        this.update = update;
    }

    public final void setFile(File file) {
        this.file = file;
    }

    public abstract Dialog create(Activity activity);

    public final void sendToInstall() {
        builder.getInstallStrategy().install(ActivityManager.get().getApplicationContext(), file.getAbsolutePath(), update);
    }
    public final void sendUserCancel() {
        if (builder.getCheckCallback() != null) {
            builder.getCheckCallback().onUserCancel();
        }

    }

    public final void sendCheckIgnore() {
        if (builder.getCheckCallback() != null) {
            builder.getCheckCallback().onCheckIgnore(update);
        }
        UpdatePreference.saveIgnoreVersion(update.getVersionCode());
    }

}
