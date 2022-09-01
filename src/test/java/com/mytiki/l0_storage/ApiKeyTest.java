/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdAORsp;
import com.mytiki.l0_storage.features.latest.api_id.ApiIdDO;
import com.mytiki.l0_storage.features.latest.api_id.ApiIdRepository;
import com.mytiki.l0_storage.features.latest.api_id.ApiIdService;
import com.mytiki.l0_storage.main.l0StorageApp;
import com.mytiki.spring_rest_api.ApiException;
import com.mytiki.spring_rest_api.ApiPage;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {l0StorageApp.class}
)
@ActiveProfiles(profiles = {"test", "local"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiKeyTest {

    @Autowired
    private ApiIdService service;

    @Autowired
    private ApiIdRepository repository;

    @Test
    public void Test_Register_Success() {
        String customerId = UUID.randomUUID().toString();
        ApiIdAORsp rsp = service.register(customerId);
        Optional<ApiIdDO> found = repository.findById(UUID.fromString(rsp.getApiId()));

        assertTrue(found.isPresent());
        assertEquals(rsp.getApiId(), found.get().getApiId().toString());
        assertEquals(rsp.getValid(), found.get().getValid());
        assertNotNull(found.get().getCreated());
        assertNotNull(found.get().getModified());
        assertNotNull(rsp.getCreated());
        assertNotNull(rsp.getModified());
    }

    @Test
    public void Test_Revoke_Success() {
        String customerId = UUID.randomUUID().toString();
        ApiIdAORsp register = service.register(customerId);
        ApiIdAORsp rsp = service.revoke(register.getApiId(), customerId);
        Optional<ApiIdDO> found = repository.findById(UUID.fromString(rsp.getApiId()));

        assertFalse(rsp.getValid());
        assertTrue(found.isPresent());
        assertFalse(found.get().getValid());
    }

    @Test
    public void Test_Revoke_NotFound() {
        ApiException ex = assertThrows(ApiException.class,
                () -> service.revoke(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        assertEquals(ex.getHttpStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void Test_Get_Success() {
        String customerId = UUID.randomUUID().toString();
        ApiIdAORsp register = service.register(customerId);
        ApiIdAORsp get = service.get(register.getApiId(), customerId);

        assertEquals(register.getApiId(), get.getApiId());
        assertNotNull(get.getValid());
        assertNotNull(get.getCreated());
        assertNotNull(get.getModified());
    }

    @Test
    public void Test_Get_NotFound() {
        ApiException ex = assertThrows(ApiException.class,
                () -> service.get(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        assertEquals(ex.getHttpStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void Test_GetAll_00_Success() {
        String customerId = UUID.randomUUID().toString();
        service.register(customerId);
        ApiPage<ApiIdAORsp> all = service.all(customerId, 0, 100);

        assertEquals(all.getElements().size(), 1);
        assertEquals(all.getPage(), 0);
        assertEquals(all.getTotalPages(), 1);
        assertEquals(all.getSize(), 1);
        assertEquals(all.getTotalElements(), 1);
    }

    @Test
    public void Test_GetAll_12_Success() {
        String customerId = UUID.randomUUID().toString();
        service.register(customerId);
        service.register(customerId);
        service.register(customerId);
        ApiPage<ApiIdAORsp> all = service.all(customerId, 1, 2);

        assertEquals(all.getElements().size(), 1);
        assertEquals(all.getPage(), 1);
        assertEquals(all.getTotalPages(), 2);
        assertEquals(all.getSize(), 1);
        assertEquals(all.getTotalElements(), 3);
    }

    @Test
    public void Test_GetAll_Empty() {
        ApiPage<ApiIdAORsp> all = service.all(UUID.randomUUID().toString(), 0, 100);

        assertEquals(all.getElements().size(), 0);
        assertEquals(all.getPage(), 0);
        assertEquals(all.getTotalPages(), 0);
        assertEquals(all.getSize(), 0);
        assertEquals(all.getTotalElements(), 0);
    }
}
