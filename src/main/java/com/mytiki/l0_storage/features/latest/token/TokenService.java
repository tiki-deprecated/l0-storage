/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.token;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdDO;
import com.mytiki.l0_storage.features.latest.api_id.ApiIdService;
import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.l0_storage.utilities.RSAFacade;
import com.mytiki.l0_storage.utilities.SHAFacade;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import com.nimbusds.jose.*;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class TokenService {
    public static final int POLICY_EXPIRATION_HOURS = 1;
    private final TokenRepository repository;
    private final JWSSigner signer;
    private final long expSeconds;

    private final ApiIdService apiIdService;

    public TokenService(
            TokenRepository repository,
            JWSSigner signer,
            ApiIdService apiIdService,
            long expSeconds) {
        this.repository = repository;
        this.signer = signer;
        this.apiIdService = apiIdService;
        this.expSeconds = expSeconds;
    }

    public TokenAORsp issue(String apiId, TokenAOReq req){
        String uid = guardForApiId(apiId);
        guardForSignature(req);
        String prefix = buildPrefix(uid, req.getPubKey());
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime exp = now.plusSeconds(expSeconds);
        TokenDO tokenDO = log(apiId, prefix, now);
        String token = jwt(tokenDO.getTid().toString(), prefix, now, exp);
        TokenAORsp rsp = new TokenAORsp();
        rsp.setToken(token);
        rsp.setExpires(exp);
        rsp.setUrnPrefix(prefix);
        return rsp;
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
        return apiIdDO.get().getUid();
    }

    private void guardForSignature(TokenAOReq req){
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

    private String buildPrefix(String uid, String pubKey) {
        try {
            String hashedCustomerId = Base64URL.encode(SHAFacade.sha3_256(uid)).toString();
            byte[] pubKeyBytes = Base64.getDecoder().decode(pubKey);
            String hashedPubKey = Base64URL.encode(SHAFacade.sha3_256(pubKeyBytes)).toString();
            return hashedCustomerId + "/" + hashedPubKey + "/";
        } catch (NoSuchAlgorithmException e) {
            throw new ApiExceptionBuilder(HttpStatus.EXPECTATION_FAILED)
                    .message(e.getMessage())
                    .cause(e.getCause())
                    .build();
        }
    }

    private TokenDO log(String apiId, String urnPrefix, ZonedDateTime now){
        ApiIdDO apiIdDO = new ApiIdDO();
        apiIdDO.setAid(UUID.fromString(apiId));
        TokenDO tokenDO = new TokenDO();
        tokenDO.setTid(UUID.randomUUID());
        tokenDO.setCreated(now);
        tokenDO.setUrnPrefix(urnPrefix);
        tokenDO.setApiId(apiIdDO);
        return repository.save(tokenDO);
    }

    private String jwt(String jti, String sub, ZonedDateTime iat, ZonedDateTime exp){
        JWSObject jwsObject = new JWSObject(
                new JWSHeader
                        .Builder(JWSAlgorithm.ES256)
                        .type(JOSEObjectType.JWT)
                        .build(),
                new Payload(
                        new JWTClaimsSet.Builder()
                                .issuer(Constants.MODULE_DOT_PATH)
                                .issueTime(Date.from(iat.toInstant()))
                                .expirationTime(Date.from(exp.toInstant()))
                                .subject(sub)
                                .jwtID(jti)
                                .build()
                                .toJSONObject()
                ));
        try {
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new ApiExceptionBuilder(HttpStatus.EXPECTATION_FAILED)
                    .message(e.getMessage())
                    .cause(e.getCause())
                    .build();
        }
    }
}
