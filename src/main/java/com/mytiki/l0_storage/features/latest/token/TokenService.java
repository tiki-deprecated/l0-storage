/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.token;

import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.l0_storage.utilities.RSAFacade;
import com.mytiki.l0_storage.utilities.SHAFacade;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

public class TokenService {
    private final TokenRepository repository;
    private final JWSSigner signer;
    private final long expSeconds;

    public TokenService(TokenRepository repository, JWSSigner signer, long expSeconds) {
        this.repository = repository;
        this.signer = signer;
        this.expSeconds = expSeconds;
    }

    public TokenAORsp issue(String appId, TokenAOReq req){
        if(appId == null)
            throw new ApiExceptionBuilder(HttpStatus.FORBIDDEN).message("No API Id").build();
        guardForSignature(req);
        String prefix = buildPrefix(appId, req.getPubKey());
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime exp = now.plusSeconds(expSeconds);
        TokenDO tokenDO = log(appId, prefix, now);
        String token = jwt(tokenDO.getId().toString(), prefix, now, exp);
        TokenAORsp rsp = new TokenAORsp();
        rsp.setToken(token);
        rsp.setExpires(exp);
        rsp.setUrnPrefix(prefix);
        return rsp;
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

    private String buildPrefix(String appId, String pubKey) {
        try {
            byte[] pubKeyBytes = Base64.getDecoder().decode(pubKey);
            String hashedPubKey = Base64
                    .getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(SHAFacade.sha3_256(pubKeyBytes));
            return appId + "/" + hashedPubKey + "/";
        } catch (NoSuchAlgorithmException e) {
            throw new ApiExceptionBuilder(HttpStatus.EXPECTATION_FAILED)
                    .message(e.getMessage())
                    .cause(e.getCause())
                    .build();
        }
    }

    private TokenDO log(String appId, String urnPrefix, ZonedDateTime now){
        TokenDO tokenDO = new TokenDO();
        tokenDO.setId(UUID.randomUUID());
        tokenDO.setCreated(now);
        tokenDO.setUrnPrefix(urnPrefix);
        tokenDO.setAppId(UUID.fromString(appId));
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
