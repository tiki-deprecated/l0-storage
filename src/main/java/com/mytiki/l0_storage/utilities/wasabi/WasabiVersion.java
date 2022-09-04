/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities.wasabi;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.ZonedDateTime;

public class WasabiVersion {
    @JacksonXmlProperty(localName = "Key")
    private String key;
    @JacksonXmlProperty(localName = "VersionId")
    private String versionId;
    @JacksonXmlProperty(localName = "IsLatest")
    private boolean latest;
    @JacksonXmlProperty(localName = "LastModified")
    private ZonedDateTime lastModified;
    @JacksonXmlProperty(localName = "ETag")
    private String eTag;
    @JacksonXmlProperty(localName = "Size")
    private long size;
    @JacksonXmlProperty(localName = "StorageClass")
    private String storageClass;
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
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public WasabiOwner getOwner() {
        return owner;
    }

    public void setOwner(WasabiOwner owner) {
        this.owner = owner;
    }
}
