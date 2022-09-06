/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.policy;

import java.util.List;

public class PolicyAORsp {
    private String expires;
    private String keyPrefix;
    private long maxBytes = 1048576;
    private List<String> compute = List.of("key", "file", "content-md5");
    private PolicyAORspFields fields;

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public long getMaxBytes() {
        return maxBytes;
    }

    public void setMaxBytes(long maxBytes) {
        this.maxBytes = maxBytes;
    }

    public List<String> getCompute() {
        return compute;
    }

    public void setCompute(List<String> compute) {
        this.compute = compute;
    }

    public PolicyAORspFields getFields() {
        return fields;
    }

    public void setFields(PolicyAORspFields fields) {
        this.fields = fields;
    }
}
