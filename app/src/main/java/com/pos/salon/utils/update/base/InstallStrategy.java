
package com.pos.salon.utils.update.base;

import android.content.Context;
import com.pos.salon.utils.update.model.Update;


public abstract class InstallStrategy {

    public abstract void install(Context context, String filename, Update update);
}
