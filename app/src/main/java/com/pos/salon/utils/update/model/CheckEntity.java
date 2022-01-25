
package com.pos.salon.utils.update.model;

import java.util.HashMap;
import java.util.Map;

public class CheckEntity {
    private String method = "GET";
    private String url;
    private Map<String, String> params;
    private Map<String, String> headers;

    public String getMethod() {
        return method;
    }

    public CheckEntity setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public CheckEntity setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<String, String> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public CheckEntity setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return headers;
    }

    public CheckEntity setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }
}
