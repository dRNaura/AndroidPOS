
package com.pos.salon.utils.update.base;

import android.app.Activity;
import com.pos.salon.utils.update.UpdateBuilder;
import com.pos.salon.utils.update.flow.Launcher;
import com.pos.salon.utils.update.model.Update;

public abstract class DownloadNotifier {

    protected Update update;
    protected UpdateBuilder builder;

    public final DownloadNotifier bind(UpdateBuilder builder, Update update) {
        this.update = update;
        this.builder = builder;
        return this;
    }

    protected final void restartDownload() {
        Launcher.getInstance().launchDownload(update, builder);
    }

    public abstract DownloadCallback create(Update update, Activity activity);


}
