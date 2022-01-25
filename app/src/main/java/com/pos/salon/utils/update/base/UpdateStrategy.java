
package com.pos.salon.utils.update.base;

import com.pos.salon.utils.update.model.Update;

public abstract class UpdateStrategy {
    public abstract boolean isShowUpdateDialog(Update update);

    public abstract boolean isShowDownloadDialog();
    public abstract boolean isAutoInstall();
}
