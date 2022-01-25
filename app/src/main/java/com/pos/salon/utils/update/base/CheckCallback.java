
package com.pos.salon.utils.update.base;

import com.pos.salon.utils.update.model.Update;


public interface CheckCallback {

    void onCheckStart();

    void hasUpdate(Update update);

    void noUpdate();
    void onCheckError(Throwable t);
    void onUserCancel();
    void onCheckIgnore(Update update);
}
