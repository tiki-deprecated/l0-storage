/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.usage;

import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@Tag(name = "USAGE")
@RestController
@RequestMapping(value = UsageController.PATH_CONTROLLER)
public class UsageController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "usage";

    private final UsageService service;

    public UsageController(UsageService service) {
        this.service = service;
    }

    @RolesAllowed("REMOTE")
    @Operation(summary = "Submit a usage report", responses = {
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @RequestMapping(method = RequestMethod.POST)
    public void post(@RequestBody UsageAOReq body){
        service.log(body);
    }
}
