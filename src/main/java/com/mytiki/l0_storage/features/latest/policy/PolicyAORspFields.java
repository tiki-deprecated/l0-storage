/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.policy;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PolicyAORspFields {
    private String policy;
    @JsonProperty("content-type")
    private String contentType = "application/json";
    @JsonProperty("x-amz-credential")
    private String xAmzCredential;
    @JsonProperty("x-amz-algorithm")
    private String xAmzAlgorithm = "AWS4-HMAC-SHA256";
    @JsonProperty("x-amz-date")
    private String xAmzDate;
    @JsonProperty("x-amz-signature")
    private String xAmzSignature;
    @JsonProperty("x-amz-object-lock-mode")
    private String xAmzObjectLockMode = "GOVERNANCE";
    @JsonProperty("x-amz-object-lock-retain-until-date")
    private String xAmzObjectLockRetainUntilDate;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getxAmzCredential() {
        return xAmzCredential;
    }

    public void setxAmzCredential(String xAmzCredential) {
        this.xAmzCredential = xAmzCredential;
    }

    public String getxAmzAlgorithm() {
        return xAmzAlgorithm;
    }

    public void setxAmzAlgorithm(String xAmzAlgorithm) {
        this.xAmzAlgorithm = xAmzAlgorithm;
    }

    public String getxAmzDate() {
        return xAmzDate;
    }

    public void setxAmzDate(String xAmzDate) {
        this.xAmzDate = xAmzDate;
    }

    public String getxAmzSignature() {
        return xAmzSignature;
    }

    public void setxAmzSignature(String xAmzSignature) {
        this.xAmzSignature = xAmzSignature;
    }

    public String getxAmzObjectLockMode() {
        return xAmzObjectLockMode;
    }

    public void setxAmzObjectLockMode(String xAmzObjectLockMode) {
        this.xAmzObjectLockMode = xAmzObjectLockMode;
    }

    public String getxAmzObjectLockRetainUntilDate() {
        return xAmzObjectLockRetainUntilDate;
    }

    public void setxAmzObjectLockRetainUntilDate(String xAmzObjectLockRetainUntilDate) {
        this.xAmzObjectLockRetainUntilDate = xAmzObjectLockRetainUntilDate;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }
}
