/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdAORsp;
import com.mytiki.l0_storage.features.latest.api_id.ApiIdService;
import com.mytiki.l0_storage.features.latest.policy.PolicyAOReq;
import com.mytiki.l0_storage.features.latest.policy.PolicyAORsp;
import com.mytiki.l0_storage.features.latest.policy.PolicyService;
import com.mytiki.l0_storage.main.l0StorageApp;
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
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {l0StorageApp.class}
)
@ActiveProfiles(profiles = {"test", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PolicyTest {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ApiIdService apiIdService;

    @Test
    public void Test() throws NoSuchAlgorithmException, NoSuchProviderException, IOException, CryptoException {
        ApiIdAORsp register = apiIdService.register("test");

        KeyPair keyPair = generateRsa();
        BCRSAPublicKey pubKey = (BCRSAPublicKey) keyPair.getPublic();
        BCRSAPrivateKey privateKey = (BCRSAPrivateKey) keyPair.getPrivate();

        PolicyAOReq req = new PolicyAOReq(
                encodePublicKey(pubKey),
                rsaSign(privateKey, "dummy"),
                "dummy"
        );

        PolicyAORsp rsp = policyService.request(register.getApiId(), req);
        assertNotNull(rsp.getPolicy());
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
}
