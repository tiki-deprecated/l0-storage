/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.usage;

import java.time.ZonedDateTime;

public class UsageAORsp {
    private long totalBytes;
    private long totalItems;
    private ZonedDateTime lastUpload;
    private ZonedDateTime firstUpload;

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public ZonedDateTime getLastUpload() {
        return lastUpload;
    }

    public void setLastUpload(ZonedDateTime lastUpload) {
        this.lastUpload = lastUpload;
    }

    public ZonedDateTime getFirstUpload() {
        return firstUpload;
    }

    public void setFirstUpload(ZonedDateTime firstUpload) {
        this.firstUpload = firstUpload;
    }
}
