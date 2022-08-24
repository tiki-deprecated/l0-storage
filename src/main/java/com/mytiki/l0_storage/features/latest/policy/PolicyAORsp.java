/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.policy;

public class PolicyAORsp {
    private String policy;
    private String policySignature;
    private String policyDate;
    private String urnPrefix;
    private Long expiresIn;

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getPolicySignature() {
        return policySignature;
    }

    public void setPolicySignature(String policySignature) {
        this.policySignature = policySignature;
    }

    public String getPolicyDate() {
        return policyDate;
    }

    public void setPolicyDate(String policyDate) {
        this.policyDate = policyDate;
    }

    public String getUrnPrefix() {
        return urnPrefix;
    }

    public void setUrnPrefix(String urnPrefix) {
        this.urnPrefix = urnPrefix;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
