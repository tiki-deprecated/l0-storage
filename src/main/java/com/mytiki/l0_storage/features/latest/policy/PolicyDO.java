/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.policy;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdDO;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "policy")
public class PolicyDO implements Serializable {
    private Long policyId;
    private ApiIdDO apiId;
    private String urnPrefix;
    private ZonedDateTime created;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    @ManyToOne
    @JoinColumn(name="api_id")
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
