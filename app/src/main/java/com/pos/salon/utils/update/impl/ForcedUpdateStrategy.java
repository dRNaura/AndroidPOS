
package com.pos.salon.utils.update.impl;

import com.pos.salon.utils.update.base.UpdateStrategy;
import com.pos.salon.utils.update.model.Update;


public class ForcedUpdateStrategy extends UpdateStrategy {

    private UpdateStrategy delegate;

    public ForcedUpdateStrategy(UpdateStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isShowUpdateDialog(Update update) {
        return delegate.isShowUpdateDialog(update);
    }

    @Override
    public boolean isAutoInstall() {
        return false;
    }

    @Override
    public boolean isShowDownloadDialog() {
        return delegate.isShowDownloadDialog();
    }
}
