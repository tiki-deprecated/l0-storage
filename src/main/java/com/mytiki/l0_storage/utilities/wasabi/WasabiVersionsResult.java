/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities.wasabi;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(
        namespace = "http://s3.amazonaws.com/doc/2006-03-01/",
        localName = "ListVersionsResult")
public class WasabiVersionsResult {
    @JacksonXmlProperty(localName = "Name")
    private String name;
    @JacksonXmlProperty(localName = "Prefix")
    private String prefix;
    @JacksonXmlProperty(localName = "KeyMarker")
    private String keyMarker;
    @JacksonXmlProperty(localName = "VersionIdMarker")
    private String versionIdMarker;
    @JacksonXmlProperty(localName = "NextKeyMarker")
    private String nextKeyMarker;
    @JacksonXmlProperty(localName = "NextVersionIdMarker")
    private String nextVersionIdMarker;
    @JacksonXmlProperty(localName = "MaxKeys")
    private long maxKeys;
    @JacksonXmlProperty(localName = "IsTruncated")
    private boolean truncated;
    @JacksonXmlProperty(localName = "Version")
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<WasabiVersion> versions = new ArrayList<>();
    @JacksonXmlProperty(localName = "DeleteMarker")
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<WasabiDeleteMarker> deleteMarkers = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getKeyMarker() {
        return keyMarker;
    }

    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }

    public String getVersionIdMarker() {
        return versionIdMarker;
    }

    public void setVersionIdMarker(String versionIdMarker) {
        this.versionIdMarker = versionIdMarker;
    }

    public String getNextKeyMarker() {
        return nextKeyMarker;
    }

    public void setNextKeyMarker(String nextKeyMarker) {
        this.nextKeyMarker = nextKeyMarker;
    }

    public String getNextVersionIdMarker() {
        return nextVersionIdMarker;
    }

    public void setNextVersionIdMarker(String nextVersionIdMarker) {
        this.nextVersionIdMarker = nextVersionIdMarker;
    }

    public long getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(long maxKeys) {
        this.maxKeys = maxKeys;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public List<WasabiVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<WasabiVersion> versions) {
        this.versions.addAll(versions);
    }

    public List<WasabiDeleteMarker> getDeleteMarkers() {
        return deleteMarkers;
    }

    public void setDeleteMarkers(List<WasabiDeleteMarker> deleteMarkers) {
        this.deleteMarkers.addAll(deleteMarkers);
    }
}
