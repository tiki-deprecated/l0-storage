/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.api_id;

import com.mytiki.spring_rest_api.ApiConstants;
import com.mytiki.spring_rest_api.ApiPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "API Id")
@RestController
@RequestMapping(value = ApiIdController.PATH_CONTROLLER)
public class ApiIdController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "api-id";
    public static final String PATH_KEY = "/key";
    public static final String PATH_NEW = "/new";
    private final ApiIdService service;

    public ApiIdController(ApiIdService service) {
        this.service = service;
    }

    @Operation(summary = "Get all provisioned API Ids")
    @RequestMapping(method = RequestMethod.GET)
    public ApiPage<ApiIdAORsp> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        return service.all("test", page, size);
    }

    @Operation(summary = "Get an API Id's properties")
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @RequestMapping(method = RequestMethod.GET, path = PATH_KEY + "/{api-id}")
    public ApiIdAORsp getKey(@PathVariable(name = "api-id") String apiId){
        return service.find(apiId);
    }

    @Operation(summary = "Revoke an API Id (permanent)")
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @RequestMapping(method = RequestMethod.DELETE, path = PATH_KEY + "/{api-id}")
    public ApiIdAORsp deleteKey(@PathVariable(name = "api-id") String apiId){
        return service.revoke(apiId);
    }

    @Operation(summary = "Request a new API Id")
    @RequestMapping(method = RequestMethod.POST, path = PATH_NEW)
    public ApiIdAORsp postNew(){
        return service.register("test");
    }
}
