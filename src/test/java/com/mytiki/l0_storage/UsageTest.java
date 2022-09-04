/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage;

import com.mytiki.l0_storage.features.latest.usage.UsageAORsp;
import com.mytiki.l0_storage.features.latest.usage.UsageService;
import com.mytiki.l0_storage.main.l0StorageApp;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {l0StorageApp.class}
)
@ActiveProfiles(profiles = {"test", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsageTest {

    @Autowired
    private UsageService usageService;

    @Test
    public void Test_Lookup_Success() {
        UsageAORsp rsp = usageService.lookup(UUID.randomUUID().toString());
        assertEquals(rsp.getTotalItems(), 0);
        assertEquals(rsp.getTotalBytes(), 0);
    }
}
