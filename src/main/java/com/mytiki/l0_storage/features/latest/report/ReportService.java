/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.report;

import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import org.springframework.http.HttpStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ReportService {

    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

    public void log(ReportAOReq req){
        String[] splitPath = req.getPath().split("/");
        if(splitPath.length < 2)
            throw new ApiExceptionBuilder(HttpStatus.BAD_REQUEST)
                    .message("Bad path")
                    .detail("Unable to identify urn prefix")
                    .build();

        ReportDO usageDO = new ReportDO();
        usageDO.setCreated(ZonedDateTime.now(ZoneOffset.UTC));
        usageDO.setSizeBytes(req.getSizeBytes());
        usageDO.setUrnPrefix(splitPath[0] + "/" + splitPath[1]);
        repository.save(usageDO);
    }
}
