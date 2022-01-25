
package com.pos.salon.utils.update.base;

import com.pos.salon.utils.update.UpdateBuilder;
import com.pos.salon.utils.update.flow.DefaultCheckCallback;
import com.pos.salon.utils.update.impl.ForcedUpdateStrategy;
import com.pos.salon.utils.update.model.CheckEntity;
import com.pos.salon.utils.update.model.Update;
import com.pos.salon.utils.update.utilUpdate.Utils;

public abstract class CheckWorker implements Runnable {

    private DefaultCheckCallback checkCB;
    protected UpdateBuilder builder;
    public final void setBuilder (UpdateBuilder builder) {
        this.builder = builder;
    }
    public final void setCheckCB (DefaultCheckCallback checkCB) {
        this.checkCB = checkCB;
    }

    @Override
    public final void run() {
        try {
            if (useAsync()) {
                asyncCheck(builder.getCheckEntity());
            } else {
                onResponse(check(builder.getCheckEntity()));
            }
        } catch (Throwable t) {
            onError(t);
        }
    }

    protected String check(CheckEntity entity) throws Exception {
        throw new RuntimeException("You must implements this method for sync request");
    }

    protected void asyncCheck(CheckEntity entity) {
        throw new RuntimeException("You must implements this method for async request");
    }

    protected boolean useAsync() {
        return false;
    }

    public final void onResponse(String response) {
        try {
            UpdateParser jsonParser = builder.getUpdateParser();
            Update update = jsonParser.parse(response);
            if (update == null) {
                throw new IllegalArgumentException(String.format(
                        "Could not returns null by %s.parse()", jsonParser.getClass().getCanonicalName()
                ));
            }
            update = preHandle(update);
            if (builder.getUpdateChecker().check(update)) {
                sendHasUpdate(update);
            } else {
                sendNoUpdate();
            }
        } catch (Throwable t) {
            onError(t);
        }
    }
    public final void onError(Throwable t) {
        sendOnErrorMsg(t);
    }

    private void sendHasUpdate(final Update update) {
        if (checkCB == null) return;
        Utils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (checkCB == null) return;
                checkCB.hasUpdate(update);
            }
        });
    }

    private void sendNoUpdate() {
        if (checkCB == null) return;
        Utils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (checkCB == null) return;
                checkCB.noUpdate();
            }
        });
    }

    private void sendOnErrorMsg(final Throwable t) {
        if (checkCB == null) return;
        Utils.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (checkCB == null) return;
                checkCB.onCheckError(t);
            }
        });
    }

    private Update preHandle(Update update) {
        if (update.isForced()) {
            update.setIgnore(false);
            builder.setUpdateStrategy(new ForcedUpdateStrategy(builder.getUpdateStrategy()));
        }
        return update;
    }
}
