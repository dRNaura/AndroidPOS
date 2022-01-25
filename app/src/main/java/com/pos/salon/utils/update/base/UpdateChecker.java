
package com.pos.salon.utils.update.base;

import com.pos.salon.utils.update.model.Update;

public abstract class UpdateChecker {

    public abstract boolean check(Update update) throws Exception;
}
