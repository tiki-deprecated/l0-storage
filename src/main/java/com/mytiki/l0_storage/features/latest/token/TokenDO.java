/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.token;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdDO;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "token")
public class TokenDO implements Serializable {
    private UUID tid;
    private ApiIdDO apiId;
    private String urnPrefix;
    private ZonedDateTime created;

    @Id
    @Column(name = "tid")
    public UUID getTid() {
        return tid;
    }

    public void setTid(UUID tid) {
        this.tid = tid;
    }

    @ManyToOne
    @JoinColumn(name="aid")
    public ApiIdDO getApiId() {
        return apiId;
    }

    public void setApiId(ApiIdDO apiId) {
        this.apiId = apiId;
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
