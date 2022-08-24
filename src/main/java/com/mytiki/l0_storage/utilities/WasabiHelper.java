/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class WasabiHelper {
    public static final int EXPIRATION_HOURS = 1;

    private final String wasabiKey;
    private final String wasabiSecret;
    private final String wasabiBucket;

    public WasabiHelper(String wasabiKey, String wasabiSecret, String wasabiBucket) {
        this.wasabiKey = wasabiKey;
        this.wasabiSecret = wasabiSecret;
        this.wasabiBucket = wasabiBucket;
    }

    public String buildPolicy(String urnPrefix, String date){
        ZonedDateTime exp = ZonedDateTime.now().plusHours(EXPIRATION_HOURS);
        String policy = "{\n" +
                "  \"expiration\": \""+ exp.format(DateTimeFormatter.ISO_INSTANT) + "\",\n" +
                "  \"conditions\": [\n" +
                "    {\"bucket\": \""+ wasabiBucket + "\"},\n" +
                "    [\"starts-with\", \"$key\", \"" + urnPrefix + "/\"],\n" +
                "    {\"x-amz-credential\": \"" + wasabiKey + "/" + date + "/us-east-1/s3/aws4_request\"},\n" +
                "    {\"x-amz-algorithm\": \"AWS4-HMAC-SHA256\"},\n" +
                "    {\"x-amz-date\": \"" + date + "T000000Z\"},\n" +
                "    {\"content-type\": \"application/json\"},\n" +
                "    [\"starts-with\", \"$x-amz-object-lock-mode\", \"\"], \n" +
                "    [\"starts-with\", \"$x-amz-object-lock-retain-until-date\", \"\"], \n" +
                "    [\"starts-with\", \"$Content-MD5\", \"\"] \n" +
                "  ]\n" +
                "}";
        return Base64.getEncoder().encodeToString(policy.getBytes(StandardCharsets.UTF_8));
    }

    public String signPolicy(String policy, String date) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] secret = ("AWS4" + wasabiSecret).getBytes(StandardCharsets.UTF_8);
        byte[] dateKey = SHAHelper.hmacSha256(date, secret);
        byte[] regionKey = SHAHelper.hmacSha256("us-east-1", dateKey);
        byte[] serviceKey = SHAHelper.hmacSha256("s3", regionKey);
        byte[] signingKey = SHAHelper.hmacSha256("aws4_request", serviceKey);
        return Hex.encodeHexString(SHAHelper.hmacSha256(policy, signingKey));
    }
}
