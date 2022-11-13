/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage;

import com.mytiki.l0_storage.features.latest.report.ReportAOReq;
import com.mytiki.l0_storage.features.latest.report.ReportDO;
import com.mytiki.l0_storage.features.latest.report.ReportRepository;
import com.mytiki.l0_storage.features.latest.report.ReportService;
import com.mytiki.l0_storage.main.App;
import com.mytiki.spring_rest_api.ApiException;
import com.nimbusds.jose.util.Base64URL;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {App.class}
)
@ActiveProfiles(profiles = {"ci", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReportTest {

    @Autowired
    private ReportService service;

    @Autowired
    private ReportRepository repository;

    @Test
    public void Test_Log_Success() {
        String urnPrefix = Base64URL.encode(UUID.randomUUID().toString())  + "/" +
                Base64URL.encode(UUID.randomUUID().toString());
        long sizeBytes = 1;
        ReportAOReq req = new ReportAOReq(urnPrefix + "/" + UUID.randomUUID(), sizeBytes);
        service.log(req);
        List<ReportDO> reports = repository.findAllByUrnPrefix(urnPrefix);

        assertEquals(1, reports.size());
        assertNotNull(reports.get(0).getCreated());
        assertEquals(sizeBytes, reports.get(0).getSizeBytes());
        assertNotNull(reports.get(0).getRid());
    }

    @Test
    public void Test_Log_BadUrn_Failure() {
        ReportAOReq req = new ReportAOReq(UUID.randomUUID().toString(), 1);
        ApiException ex = assertThrows(ApiException.class, () -> service.log(req));
        assertEquals(ex.getHttpStatus(), HttpStatus.BAD_REQUEST);
    }
}
