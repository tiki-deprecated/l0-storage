/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.api_id;

import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import com.mytiki.spring_rest_api.ApiPage;
import com.mytiki.spring_rest_api.ApiPageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.lang.invoke.MethodHandles;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ApiIdService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ApiIdRepository repository;

    public ApiIdService(ApiIdRepository repository) {
        this.repository = repository;
    }

    public ApiIdAORsp register(String customerId) {
        ApiIdDO keyDO = new ApiIdDO();
        keyDO.setCustomerId(customerId);
        keyDO.setApiId(UUID.randomUUID());
        keyDO.setValid(true);
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        keyDO.setCreated(now);
        keyDO.setModified(now);

        keyDO = repository.save(keyDO);
        return toRsp(keyDO);
    }

    public ApiIdAORsp revoke(String apiId, String customerId) {
        Optional<ApiIdDO> exists = repository.findById(UUID.fromString(apiId));
        if(exists.isPresent() && exists.get().getCustomerId().equals(customerId)){
            ApiIdDO revoked = exists.get();
            revoked.setValid(false);
            revoked.setModified(ZonedDateTime.now(ZoneOffset.UTC));
            revoked = repository.save(revoked);
            return toRsp(revoked);
        }else
            throw new ApiExceptionBuilder(HttpStatus.NOT_FOUND)
                    .message("Api Id Not Found")
                    .help("Try GET /api/latest/api-id/")
                    .build();
    }

    public ApiIdAORsp get(String apiId, String customerId) {
        Optional<ApiIdDO> exists = find(apiId);
        if(exists.isPresent() && exists.get().getCustomerId().equals(customerId))
            return toRsp(exists.get());
        else
            throw new ApiExceptionBuilder(HttpStatus.NOT_FOUND)
                    .message("Api Id Not Found")
                    .help("Try GET /api/latest/api-id/")
                    .build();
    }

    public ApiPage<ApiIdAORsp> all(String customerId, int page, int size){
        Page<ApiIdDO> all = repository.findAllByCustomerId(customerId, PageRequest.of(page, size));
        return new ApiPageBuilder<ApiIdAORsp>()
                .elements(all.stream().map(this::toRsp).collect(Collectors.toList()))
                .page(all.getNumber())
                .totalElements(all.getTotalElements())
                .totalPages(all.getTotalPages())
                .build();
    }

    private ApiIdAORsp toRsp(ApiIdDO apiIdDO){
        ApiIdAORsp rsp = new ApiIdAORsp();
        rsp.setApiId(apiIdDO.getApiId().toString());
        rsp.setValid(apiIdDO.getValid());
        rsp.setCreated(apiIdDO.getCreated());
        rsp.setModified(apiIdDO.getModified());
        return rsp;
    }

    public Optional<ApiIdDO> find(String apiId) {
        return repository.findById(UUID.fromString(apiId));
    }
}
