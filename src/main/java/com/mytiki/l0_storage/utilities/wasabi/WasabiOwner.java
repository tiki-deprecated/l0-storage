/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities.wasabi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WasabiOwner {
    @JsonProperty(value = "ID")
    private String id;
    @JsonProperty(value = "DisplayName")
    private String displayName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
