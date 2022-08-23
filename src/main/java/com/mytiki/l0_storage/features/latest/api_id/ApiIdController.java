/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.api_id;

import com.mytiki.spring_rest_api.ApiConstants;
import com.mytiki.spring_rest_api.reply.ApiReplyAO;
import com.mytiki.spring_rest_api.reply.ApiReplyAOFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(method = RequestMethod.GET)
    public ApiReplyAO<List<ApiIdAORsp>> getAll(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "100") int size) {
        return service.all("test", page, size);
    }

    @RequestMapping(method = RequestMethod.GET, path = PATH_KEY + "/{api-id}")
    public ApiReplyAO<ApiIdAORsp> getKey(@PathVariable(name = "api-id") String apiId){
        return ApiReplyAOFactory.ok(service.find(apiId));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = PATH_KEY + "/{api-id}")
    public ApiReplyAO<ApiIdAORsp> deleteKey(@PathVariable(name = "api-id") String apiId){
        return ApiReplyAOFactory.ok(service.revoke(apiId));
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_NEW)
    public ApiReplyAO<ApiIdAORsp> postNew(){
        return ApiReplyAOFactory.ok(service.register("test"));
    }
}
