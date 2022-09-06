/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.policy;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdDO;
import com.mytiki.l0_storage.features.latest.api_id.ApiIdService;
import com.mytiki.l0_storage.utilities.RSAFacade;
import com.mytiki.l0_storage.utilities.SHAFacade;
import com.mytiki.l0_storage.utilities.WasabiFacade;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public class PolicyService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final int POLICY_EXPIRATION_HOURS = 1;
    //public static final int OBJECT_LOCK_YEARS = 10;
    public static final int OBJECT_LOCK_MINUTES = 10;

    private final PolicyRepository repository;
    private final ApiIdService apiIdService;
    private final WasabiFacade wasabiFacade;

    public PolicyService(PolicyRepository repository, ApiIdService apiIdService, WasabiFacade wasabiFacade) {
        this.repository = repository;
        this.apiIdService = apiIdService;
        this.wasabiFacade = wasabiFacade;
    }

    public PolicyAORsp request(String apiId, PolicyAOReq req){
        String customerId = guardForApiId(apiId);
        guardForSignature(req);
        try {
            String hashedCustomerId = Hex.encodeHexString(SHAFacade.sha3_256(customerId));
            String hashedPubKey = Hex.encodeHexString(SHAFacade.sha3_256(req.getPubKey()));
            String urnPrefix = hashedCustomerId + "/" + hashedPubKey + "/";

            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);
            String date = now.format(DateTimeFormatter.BASIC_ISO_DATE).replace("Z", "");
            String expires = now.plusHours(POLICY_EXPIRATION_HOURS).format(DateTimeFormatter.ISO_INSTANT);
            String lockUntil = now.plusMinutes(OBJECT_LOCK_MINUTES).format(DateTimeFormatter.ISO_INSTANT);

            String policy = wasabiFacade.buildPolicy(urnPrefix, date, expires, lockUntil);
            String signature = wasabiFacade.signV4(policy, date);

            logPolicy(apiId, urnPrefix);

            PolicyAORspFields fields = new PolicyAORspFields();
            fields.setPolicy(policy);
            fields.setxAmzCredential(wasabiFacade.buildCredential(date));
            fields.setxAmzDate(now.format(DateTimeFormatter.ISO_INSTANT));
            fields.setxAmzObjectLockRetainUntilDate(lockUntil);
            fields.setxAmzSignature(signature);

            PolicyAORsp rsp = new PolicyAORsp();
            rsp.setExpires(expires);
            rsp.setKeyPrefix(urnPrefix);
            rsp.setFields(fields);
            return  rsp;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ApiExceptionBuilder(HttpStatus.EXPECTATION_FAILED)
                    .message(e.getMessage())
                    .cause(e.getCause())
                    .build();
        }
    }

    private String guardForApiId(String apiId){
        Optional<ApiIdDO> apiIdDO = apiIdService.find(apiId);
        if(apiIdDO.isEmpty())
            throw new ApiExceptionBuilder(HttpStatus.FORBIDDEN)
                    .message("Invalid API id")
                    .detail("API Id does not exist")
                    .build();
        if(!apiIdDO.get().getValid())
            throw new ApiExceptionBuilder(HttpStatus.FORBIDDEN)
                    .message("Invalid API id")
                    .detail("API Id has been revoked")
                    .build();
        return apiIdDO.get().getCustomerId();
    }

    private void guardForSignature(PolicyAOReq req){
        try{
            RSAPublicKey publicKey = RSAFacade.decodePublicKey(req.getPubKey());
            boolean isValid = RSAFacade.verify(
                    publicKey, req.getStringToSign(), req.getSignature());
            if(!isValid)
                throw new ApiExceptionBuilder(HttpStatus.BAD_REQUEST)
                        .message("Failed to validate key/signature paid")
                        .detail("Signature does not match plaintext")
                        .properties("stringToSign", req.getStringToSign(), "signature", req.getSignature())
                        .build();
        } catch (IOException | IllegalArgumentException e) {
            throw new ApiExceptionBuilder(HttpStatus.BAD_REQUEST)
                    .message("Failed to validate key/signature paid")
                    .detail("Encoding is incorrect")
                    .cause(e.getCause())
                    .build();
        }
    }

    private void logPolicy(String apiId, String urnPrefix){
        ApiIdDO apiIdDO = new ApiIdDO();
        apiIdDO.setApiId(UUID.fromString(apiId));
        PolicyDO policyDO = new PolicyDO();
        policyDO.setCreated(ZonedDateTime.now(ZoneOffset.UTC));
        policyDO.setUrnPrefix(urnPrefix);
        policyDO.setApiId(apiIdDO);
        repository.save(policyDO);
    }
}
