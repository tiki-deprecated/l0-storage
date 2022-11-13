/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdService;
import com.mytiki.l0_storage.features.latest.token.TokenAOReq;
import com.mytiki.l0_storage.features.latest.token.TokenAORsp;
import com.mytiki.l0_storage.features.latest.token.TokenService;
import com.mytiki.l0_storage.main.App;
import com.mytiki.spring_rest_api.ApiException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.security.interfaces.RSAPrivateKey;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {App.class}
)
@ActiveProfiles(profiles = {"test", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TokenTest {

    @Autowired
    private TokenService service;

    @Autowired
    private ApiIdService apiIdService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    public void Test_Issue_Success() throws JOSEException, CryptoException {
        String uid = UUID.randomUUID().toString();
        String apiId = apiIdService.register(uid).getApiId();

        RSAKey rsaKey =  new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS).generate();
        RSAPrivateKey privateKey = rsaKey.toRSAPrivateKey();
        String stringToSign = "dummy";

        TokenAOReq req = new TokenAOReq(
                Base64.encodeBase64String(rsaKey.toPublicKey().getEncoded()),
                sign(privateKey, stringToSign), stringToSign);
        TokenAORsp rsp = service.issue(apiId, req);

        assertNotNull(rsp.getToken());
        assertNotNull(rsp.getUrnPrefix());
        assertTrue(rsp.getExpires().isAfter(ZonedDateTime.now()));
        assertEquals("Bearer", rsp.getType());

        Jwt jwt = jwtDecoder.decode(rsp.getToken());
        assertNotNull(jwt.getId());
        assertNotNull(jwt.getIssuedAt());
        assertEquals(rsp.getExpires().withNano(0).toInstant(), jwt.getExpiresAt());
        assertEquals(rsp.getUrnPrefix(), jwt.getSubject());
    }

    @Test
    public void Test_Issue_BadSignature_Failure() throws JOSEException, CryptoException {
        String uid = UUID.randomUUID().toString();
        String apiId = apiIdService.register(uid).getApiId();

        RSAKey rsaKey =  new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS).generate();
        RSAPrivateKey privateKey = rsaKey.toRSAPrivateKey();
        String stringToSign = "dummy";

        TokenAOReq req = new TokenAOReq(
                Base64.encodeBase64String(rsaKey.toPublicKey().getEncoded()),
                sign(privateKey, UUID.randomUUID().toString()), stringToSign);

        ApiException ex = assertThrows(ApiException.class, () -> service.issue(apiId, req));
        assertEquals(ex.getHttpStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void Test_Issue_BadKey_Failure() throws JOSEException, CryptoException {
        String uid = UUID.randomUUID().toString();
        String apiId = apiIdService.register(uid).getApiId();

        RSAKey rsaKey1 =  new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS).generate();
        RSAKey rsaKey2 =  new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS).generate();
        RSAPrivateKey privateKey = rsaKey1.toRSAPrivateKey();
        String stringToSign = "dummy";

        TokenAOReq req = new TokenAOReq(
                Base64.encodeBase64String(rsaKey2.toPublicKey().getEncoded()),
                sign(privateKey, stringToSign), stringToSign);

        ApiException ex = assertThrows(ApiException.class, () -> service.issue(apiId, req));
        assertEquals(ex.getHttpStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void Test_Issue_BadApiId_Failure() throws JOSEException, CryptoException {
        String apiId = UUID.randomUUID().toString();
        RSAKey rsaKey =  new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS).generate();
        RSAPrivateKey privateKey = rsaKey.toRSAPrivateKey();
        String stringToSign = "dummy";

        TokenAOReq req = new TokenAOReq(
                Base64.encodeBase64String(rsaKey.toPublicKey().getEncoded()),
                sign(privateKey, stringToSign), stringToSign);

        ApiException ex = assertThrows(ApiException.class, () -> service.issue(apiId, req));
        assertEquals(ex.getHttpStatus(), HttpStatus.FORBIDDEN);
    }

    private String sign(RSAPrivateKey privateKey, String message) throws CryptoException {
        byte[] bytesToSign = message.getBytes();
        RSADigestSigner signer = new RSADigestSigner(new SHA256Digest());
        signer.init(true,
                new RSAKeyParameters(true, privateKey.getModulus(), privateKey.getPrivateExponent()));
        signer.update(bytesToSign, 0, bytesToSign.length);
        return Base64.encodeBase64String(signer.generateSignature());
    }
}
