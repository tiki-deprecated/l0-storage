/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities.wasabi;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.ZonedDateTime;

public class WasabiDeleteMarker {
    @JacksonXmlProperty(localName = "Key")
    private String key;
    @JacksonXmlProperty(localName = "VersionId")
    private String versionId;
    @JacksonXmlProperty(localName = "IsLatest")
    private boolean isLatest;
    @JacksonXmlProperty(localName = "LastModified")
    private ZonedDateTime lastModified;
    @JacksonXmlProperty(localName = "Owner")
    private WasabiOwner owner;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public boolean isLatest() {
        return isLatest;
    }

    public void setLatest(boolean latest) {
        isLatest = latest;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public WasabiOwner getOwner() {
        return owner;
    }

    public void setOwner(WasabiOwner owner) {
        this.owner = owner;
    }
}
