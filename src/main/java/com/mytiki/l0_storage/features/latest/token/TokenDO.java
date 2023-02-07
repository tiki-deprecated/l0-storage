/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "token")
public class TokenDO implements Serializable {
    private UUID id;
    private UUID appId;
    private String urnPrefix;
    private ZonedDateTime created;

    @Id
    @Column(name = "token_id")
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Column(name = "app_id")
    public UUID getAppId() {
        return appId;
    }

    public void setAppId(UUID appId) {
        this.appId = appId;
    }

    @Column(name = "urn_prefix")
    public String getUrnPrefix() {
        return urnPrefix;
    }

    public void setUrnPrefix(String urnPrefix) {
        this.urnPrefix = urnPrefix;
    }

    @Column(name = "created_utc")
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
