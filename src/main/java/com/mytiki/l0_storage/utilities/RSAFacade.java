/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.springframework.security.crypto.codec.Utf8;

import java.io.IOException;
import java.util.Base64;

public class RSAFacade {
    public static RSAPublicKey decodePublicKey(String publicKey) throws IOException {
        try (ASN1InputStream inputStream = new ASN1InputStream(Base64.getDecoder().decode(publicKey))) {
            ASN1Sequence topLevelSeq = ASN1Sequence.getInstance(inputStream.readObject());
            ASN1Sequence algorithmSeq = ASN1Sequence.getInstance(topLevelSeq.getObjectAt(0));
            ASN1BitString publicKeyBitString = ASN1BitString.getInstance(topLevelSeq.getObjectAt(1));
            ASN1Sequence publicKeySeq = ASN1Sequence.getInstance(
                    ASN1Primitive.fromByteArray(publicKeyBitString.getBytes()));
            ASN1Integer modulus = ASN1Integer.getInstance(publicKeySeq.getObjectAt(0));
            ASN1Integer exponent = ASN1Integer.getInstance(publicKeySeq.getObjectAt(1));
            return new RSAPublicKey(modulus.getValue(), exponent.getValue());
        }
    }

    public static boolean verify(RSAPublicKey publicKey, String message, String signature){
        byte[] messageBytes = Utf8.encode(message);
        RSADigestSigner signer = new RSADigestSigner(new SHA256Digest());
        RSAKeyParameters keyParameters =
                new RSAKeyParameters(false, publicKey.getModulus(), publicKey.getPublicExponent());
        signer.init(false, keyParameters);
        signer.update(messageBytes, 0, messageBytes.length);
        return signer.verifySignature(Base64.getDecoder().decode(signature));
    }
}
