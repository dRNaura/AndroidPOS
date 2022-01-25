
package com.pos.salon.utils.update.base;

import com.pos.salon.utils.update.model.Update;


public abstract class UpdateParser {

    public abstract Update parse(String response) throws Exception;
}
