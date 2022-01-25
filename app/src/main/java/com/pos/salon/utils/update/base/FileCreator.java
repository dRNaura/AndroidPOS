
package com.pos.salon.utils.update.base;

import com.pos.salon.utils.update.UpdateBuilder;
import com.pos.salon.utils.update.model.Update;
import java.io.File;


public abstract class FileCreator {

    protected abstract File create(Update update);

    protected abstract File createForDaemon(Update update);

    final File createWithBuilder(Update update, UpdateBuilder builder) {
        File file = null;
        if (builder.isDaemon()) {
            file = createForDaemon(update);
        } else {
            file = create(update);
        }

        String name = getClass().getCanonicalName();
        if (file == null) {
            throw new RuntimeException(String.format(
                    "Could not returns a null file with FileCreator:[%s], create mode is [%s]",
                    name, builder.isDaemon() ? "Daemon" : "Normal"
            ));
        }

        if (file.isDirectory()) {
            throw new RuntimeException(String.format(
                    "Could not returns a directory file with FileCreator:[%s], create mode is [%s]",
                    name, builder.isDaemon() ? "Daemon" : "Normal"
            ));
        }

        return file;
    }
}
