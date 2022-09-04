/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.usage;

import com.mytiki.l0_storage.utilities.SHAFacade;
import com.mytiki.l0_storage.utilities.wasabi.WasabiFacade;
import com.mytiki.l0_storage.utilities.wasabi.WasabiVersionsResult;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class UsageService {

    private final WasabiFacade wasabiFacade;

    public UsageService(WasabiFacade wasabiFacade){
        this.wasabiFacade = wasabiFacade;
    }

    public UsageAORsp lookup(String customerId) {
        try {
            String prefix = Hex.encodeHexString(SHAFacade.sha3_256(customerId));
            UsageAORsp rsp = new UsageAORsp();
            rsp.setTotalItems(0);
            rsp.setTotalBytes(0);
            rsp.setFirstUpload(ZonedDateTime.now());
            rsp.setLastUpload(ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC));
            rsp = countObjects(prefix, rsp, null, null);
            return rsp.getTotalItems() == 0 ? null : rsp;
        } catch (NoSuchAlgorithmException | URISyntaxException e) {
            throw new ApiExceptionBuilder(HttpStatus.EXPECTATION_FAILED)
                    .message(e.getMessage())
                    .cause(e.getCause())
                    .build();
        }
    }

    private UsageAORsp countObjects(
            String prefix,
            UsageAORsp usage,
            String keyMarker,
            String versionMarker)
            throws URISyntaxException {
        WasabiVersionsResult res = wasabiFacade.listObjects(prefix, keyMarker, versionMarker);
        if(res != null) {
            if (res.getVersions() != null && res.getVersions().size() > 0) {
                res.getVersions().forEach(version -> {
                    usage.setTotalBytes(usage.getTotalBytes() + version.getSize());
                    usage.setTotalItems(usage.getTotalItems() + 1);

                    if(version.getLastModified().isBefore(usage.getFirstUpload()))
                        usage.setFirstUpload(version.getLastModified());
                    if(version.getLastModified().isAfter(usage.getLastUpload()))
                        usage.setLastUpload(version.getLastModified());
                });
            }
            if (res.isTruncated())
                return countObjects(prefix, usage, res.getNextKeyMarker(), res.getNextVersionIdMarker());
        }
        return usage;
    }
}
