/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdAORsp;
import com.mytiki.l0_storage.features.latest.api_id.ApiIdService;
import com.mytiki.l0_storage.features.latest.policy.*;
import com.mytiki.l0_storage.main.l0StorageApp;
import com.mytiki.l0_storage.utilities.SHAHelper;
import com.mytiki.l0_storage.utilities.WasabiHelper;
import com.mytiki.spring_rest_api.ApiException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.*;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {l0StorageApp.class}
)
@ActiveProfiles(profiles = {"test", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PolicyTest {

    @Autowired
    private PolicyService service;

    @Autowired
    private ApiIdService apiIdService;

    @Autowired
    private PolicyRepository repository;

    @Autowired
    private WasabiHelper wasabiHelper;

    @Test
    public void Test_Request_Success() throws
            NoSuchAlgorithmException, NoSuchProviderException, IOException, CryptoException {
        ApiIdAORsp register = apiIdService.register("test");

        KeyPair keyPair = generateRsa();
        BCRSAPublicKey pubKey = (BCRSAPublicKey) keyPair.getPublic();
        BCRSAPrivateKey privateKey = (BCRSAPrivateKey) keyPair.getPrivate();

        PolicyAOReq req = new PolicyAOReq(
                encodePublicKey(pubKey),
                rsaSign(privateKey, "dummy"),
                "dummy"
        );

        PolicyAORsp rsp = service.request(register.getApiId(), req);
        assertNotNull(rsp.getFields().getPolicy());
        assertNotNull(rsp.getFields().getxAmzDate());
        assertNotNull(rsp.getExpires());
        assertNotNull(rsp.getKeyPrefix());
        assertNotNull(rsp.getFields().getxAmzSignature());

        List<PolicyDO> policyDOList = repository.findByApiIdApiId(UUID.fromString(register.getApiId()));
        assertEquals(policyDOList.size(), 1);
        assertNotNull(policyDOList.get(0).getPolicyId());
        assertNotNull(policyDOList.get(0).getCreated());
        assertEquals(policyDOList.get(0).getUrnPrefix(), rsp.getKeyPrefix());
    }

    @Test
    public void Test_Request_Failure_NoApiId() throws
            NoSuchAlgorithmException, NoSuchProviderException, IOException, CryptoException {

        KeyPair keyPair = generateRsa();
        BCRSAPublicKey pubKey = (BCRSAPublicKey) keyPair.getPublic();
        BCRSAPrivateKey privateKey = (BCRSAPrivateKey) keyPair.getPrivate();

        PolicyAOReq req = new PolicyAOReq(
                encodePublicKey(pubKey),
                rsaSign(privateKey, "dummy"),
                "dummy"
        );

        ApiException ex = assertThrows(ApiException.class,
                () -> service.request(UUID.randomUUID().toString(), req));

        assertEquals(ex.getHttpStatus(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void Test_Request_Failure_BadEncoding() throws
            NoSuchAlgorithmException, NoSuchProviderException, CryptoException {
        ApiIdAORsp register = apiIdService.register("test");

        KeyPair keyPair = generateRsa();
        BCRSAPrivateKey privateKey = (BCRSAPrivateKey) keyPair.getPrivate();

        PolicyAOReq req = new PolicyAOReq(
                UUID.randomUUID().toString(),
                rsaSign(privateKey, "dummy"),
                "dummy"
        );

        ApiException ex = assertThrows(ApiException.class,
                () -> service.request(register.getApiId(), req));

        assertEquals(ex.getHttpStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void Test_Request_Failure_BadSignature() throws
            NoSuchAlgorithmException, NoSuchProviderException, IOException, CryptoException {
        ApiIdAORsp register = apiIdService.register("test");

        KeyPair keyPair = generateRsa();
        BCRSAPublicKey pubKey = (BCRSAPublicKey) keyPair.getPublic();
        BCRSAPrivateKey privateKey = (BCRSAPrivateKey) keyPair.getPrivate();

        PolicyAOReq req = new PolicyAOReq(
                encodePublicKey(pubKey),
                rsaSign(privateKey, "wrong"),
                "dummy"
        );

        ApiException ex = assertThrows(ApiException.class,
                () -> service.request(register.getApiId(), req));

        assertEquals(ex.getHttpStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void Test_Request_Failure_InvalidApiId() throws
            NoSuchAlgorithmException, NoSuchProviderException, IOException, CryptoException {
        ApiIdAORsp register = apiIdService.register("test");
        apiIdService.revoke(register.getApiId());

        KeyPair keyPair = generateRsa();
        BCRSAPublicKey pubKey = (BCRSAPublicKey) keyPair.getPublic();
        BCRSAPrivateKey privateKey = (BCRSAPrivateKey) keyPair.getPrivate();

        PolicyAOReq req = new PolicyAOReq(
                encodePublicKey(pubKey),
                rsaSign(privateKey, "dummy"),
                "dummy"
        );

        ApiException ex = assertThrows(ApiException.class,
                () -> service.request(register.getApiId(), req));

        assertEquals(ex.getHttpStatus(), HttpStatus.FORBIDDEN);
    }

    private KeyPair generateRsa() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    private String encodePublicKey(BCRSAPublicKey publicKey) throws IOException {
        byte[] paramsBytes = {0x5, 0x0};
        ASN1Object paramsAsn1Obj = ASN1Primitive.fromByteArray(paramsBytes);
        ASN1ObjectIdentifier objectIdentifier =
                ASN1ObjectIdentifier.fromContents("1.2.840.113549.1.1.1".getBytes(StandardCharsets.UTF_8));
        ASN1EncodableVector algVector = new ASN1EncodableVector();
        algVector.add(paramsAsn1Obj);
        algVector.add(objectIdentifier);
        ASN1Sequence algorithm = new DERSequence(algVector);

        System.out.println("mod: " + publicKey.getModulus().toString());

        ASN1Integer modulus = new ASN1Integer(publicKey.getModulus());
        ASN1Integer exponent = new ASN1Integer(publicKey.getPublicExponent());
        ASN1EncodableVector publicKeyVector = new ASN1EncodableVector();
        publicKeyVector.add(modulus);
        publicKeyVector.add(exponent);
        ASN1Sequence publicKeySequence = new DERSequence(publicKeyVector);

        ASN1BitString publicKeyBitString = new DERBitString(publicKeySequence);

        ASN1EncodableVector sequenceVector = new ASN1EncodableVector();
        sequenceVector.add(algorithm);
        sequenceVector.add(publicKeyBitString);
        ASN1Sequence sequence = new DERSequence(sequenceVector);

        return Base64.getEncoder().encodeToString(sequence.getEncoded());
    }

    private String rsaSign(BCRSAPrivateKey privateKey, String plaintext) throws CryptoException {
        byte[] messageBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        RSADigestSigner signer = new RSADigestSigner(new SHA256Digest());
        RSAKeyParameters keyParameters =
                new RSAKeyParameters(true, privateKey.getModulus(), privateKey.getPrivateExponent());
        signer.init(true, keyParameters);
        signer.update(messageBytes, 0, messageBytes.length);
        return Base64.getEncoder().encodeToString(signer.generateSignature());
    }

    @Test
    public void grr() throws NoSuchAlgorithmException, InvalidKeyException {
        String policy = "{ \"expiration\": \"2015-12-30T12:00:00.000Z\",\r\n" +
                "  \"conditions\": [\r\n" +
                "    {\"bucket\": \"sigv4examplebucket\"},\r\n" +
                "    [\"starts-with\", \"$key\", \"user/user1/\"],\r\n" +
                "    {\"acl\": \"public-read\"},\r\n" +
                "    {\"success_action_redirect\": \"http://sigv4examplebucket.s3.amazonaws.com/successful_upload.html\"},\r\n" +
                "    [\"starts-with\", \"$Content-Type\", \"image/\"],\r\n" +
                "    {\"x-amz-meta-uuid\": \"14365123651274\"},\r\n" +
                "    {\"x-amz-server-side-encryption\": \"AES256\"},\r\n" +
                "    [\"starts-with\", \"$x-amz-meta-tag\", \"\"],\r\n" +
                "\r\n" +
                "    {\"x-amz-credential\": \"AKIAIOSFODNN7EXAMPLE/20151229/us-east-1/s3/aws4_request\"},\r\n" +
                "    {\"x-amz-algorithm\": \"AWS4-HMAC-SHA256\"},\r\n" +
                "    {\"x-amz-date\": \"20151229T000000Z\" }\r\n" +
                "  ]\r\n" +
                "}";

        String b64p = Base64.getEncoder().encodeToString(policy.getBytes(StandardCharsets.UTF_8));
        assertEquals(b64p, "eyAiZXhwaXJhdGlvbiI6ICIyMDE1LTEyLTMwVDEyOjAwOjAwLjAwMFoiLA0KICAiY29uZGl0aW9ucyI6IFsNCiAgICB7ImJ1Y2tldCI6ICJzaWd2NGV4YW1wbGVidWNrZXQifSwNCiAgICBbInN0YXJ0cy13aXRoIiwgIiRrZXkiLCAidXNlci91c2VyMS8iXSwNCiAgICB7ImFjbCI6ICJwdWJsaWMtcmVhZCJ9LA0KICAgIHsic3VjY2Vzc19hY3Rpb25fcmVkaXJlY3QiOiAiaHR0cDovL3NpZ3Y0ZXhhbXBsZWJ1Y2tldC5zMy5hbWF6b25hd3MuY29tL3N1Y2Nlc3NmdWxfdXBsb2FkLmh0bWwifSwNCiAgICBbInN0YXJ0cy13aXRoIiwgIiRDb250ZW50LVR5cGUiLCAiaW1hZ2UvIl0sDQogICAgeyJ4LWFtei1tZXRhLXV1aWQiOiAiMTQzNjUxMjM2NTEyNzQifSwNCiAgICB7IngtYW16LXNlcnZlci1zaWRlLWVuY3J5cHRpb24iOiAiQUVTMjU2In0sDQogICAgWyJzdGFydHMtd2l0aCIsICIkeC1hbXotbWV0YS10YWciLCAiIl0sDQoNCiAgICB7IngtYW16LWNyZWRlbnRpYWwiOiAiQUtJQUlPU0ZPRE5ON0VYQU1QTEUvMjAxNTEyMjkvdXMtZWFzdC0xL3MzL2F3czRfcmVxdWVzdCJ9LA0KICAgIHsieC1hbXotYWxnb3JpdGhtIjogIkFXUzQtSE1BQy1TSEEyNTYifSwNCiAgICB7IngtYW16LWRhdGUiOiAiMjAxNTEyMjlUMDAwMDAwWiIgfQ0KICBdDQp9");

        byte[] secret = ("AWS4" + "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY").getBytes(StandardCharsets.UTF_8);
        byte[] dateKey = SHAHelper.hmacSha256("20151229", secret);
        byte[] regionKey = SHAHelper.hmacSha256("us-east-1", dateKey);
        byte[] serviceKey = SHAHelper.hmacSha256("s3", regionKey);
        byte[] signingKey = SHAHelper.hmacSha256("aws4_request", serviceKey);

        String sig =  Hex.encodeHexString(SHAHelper.hmacSha256(b64p, signingKey));
        assertEquals(sig, "8afdbf4008c03f22c2cd3cdb72e4afbb1f6a588f3255ac628749a66d7f09699e");
    }
}
