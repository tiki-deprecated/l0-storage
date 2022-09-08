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
import java.time.format.DateTimeFormatterBuilder;
import java.util.Base64;

import static java.time.temporal.ChronoField.*;

public class WasabiFacade {
    private final String wasabiKey;
    private final String wasabiSecret;
    public final String wasabiBucket;
    public final String wasabiRegion;

    public WasabiFacade(
            String wasabiKey,
            String wasabiSecret,
            String wasabiBucket,
            String wasabiRegion) {
        this.wasabiKey = wasabiKey;
        this.wasabiSecret = wasabiSecret;
        this.wasabiBucket = wasabiBucket;
        this.wasabiRegion = wasabiRegion;
    }

    public String formatDateTime(ZonedDateTime dt){
        return dt.format(new DateTimeFormatterBuilder()
                .appendValue(YEAR, 4)
                .appendValue(MONTH_OF_YEAR, 2)
                .appendValue(DAY_OF_MONTH, 2)
                .appendLiteral('T')
                .appendValue(HOUR_OF_DAY, 2)
                .appendValue(MINUTE_OF_HOUR, 2)
                .appendValue(SECOND_OF_MINUTE, 2)
                .appendZoneOrOffsetId()
                .toFormatter());
    }

    public String formatDate(ZonedDateTime dt){
        return dt.format(new DateTimeFormatterBuilder()
                .appendValue(YEAR, 4)
                .appendValue(MONTH_OF_YEAR, 2)
                .appendValue(DAY_OF_MONTH, 2)
                .toFormatter());
    }

    public String buildPolicy(String urnPrefix, ZonedDateTime dt, String expires, String lockUntil){
        String policy = "{\n" +
                "  \"expiration\": \""+ expires + "\",\n" +
                "  \"conditions\": [\n" +
                "    {\"bucket\": \"" + wasabiBucket + "\"},\n" +
                "    [\"starts-with\", \"$key\", \"" + urnPrefix + "\"],\n" +
                "    {\"x-amz-credential\": \"" + buildCredential(dt) + "\"},\n" +
                "    {\"x-amz-algorithm\": \"AWS4-HMAC-SHA256\"},\n" +
                "    {\"x-amz-date\": \"" + dt + "\"},\n" +
                "    {\"content-type\": \"application/json\"},\n" +
                "    [\"content-length-range\", 100, 1048576],\n" +
                "    [\"starts-with\", \"$x-amz-object-lock-mode\", \"GOVERNANCE\"], \n" +
                "    [\"starts-with\", \"$x-amz-object-lock-retain-until-date\", \"" + lockUntil + "\"], \n" +
                "    [\"starts-with\", \"$Content-MD5\", \"\"] \n" +
                "  ]\n" +
                "}";
        return Base64.getEncoder().encodeToString(policy.getBytes(StandardCharsets.UTF_8));
    }

    public String signV4(String plaintext, ZonedDateTime dt) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] secret = ("AWS4" + wasabiSecret).getBytes(StandardCharsets.UTF_8);
        byte[] dateKey = SHAFacade.hmacSha256(formatDate(dt), secret);
        byte[] regionKey = SHAFacade.hmacSha256(wasabiRegion, dateKey);
        byte[] serviceKey = SHAFacade.hmacSha256("s3", regionKey);
        byte[] signingKey = SHAFacade.hmacSha256("aws4_request", serviceKey);
        return Hex.encodeHexString(SHAFacade.hmacSha256(plaintext, signingKey));
    }

    public String buildCredential(ZonedDateTime dt){
        return wasabiKey + "/" + formatDate(dt) + "/" + wasabiRegion + "/s3/aws4_request";
    }
}
