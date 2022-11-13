/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenAOReq {
    private String pubKey;
    private String signature;
    private String stringToSign;

    @JsonCreator
    public TokenAOReq(
            @JsonProperty(required = true) String pubKey,
            @JsonProperty(required = true) String signature,
            @JsonProperty(required = true) String stringToSign) {
        this.pubKey = pubKey;
        this.signature = signature;
        this.stringToSign = stringToSign;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getStringToSign() {
        return stringToSign;
    }

    public void setStringToSign(String stringToSign) {
        this.stringToSign = stringToSign;
    }
}
