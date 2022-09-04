/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.usage;

import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "USAGE")
@RestController
@RequestMapping(value = UsageController.PATH_CONTROLLER)
public class UsageController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "usage";

    private final UsageService service;

    public UsageController(UsageService service) {
        this.service = service;
    }

    @Operation(summary = "Get total usage", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    schema = @Schema(implementation = UsageAORsp.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "417", description = "Expectation Failed", content = @Content)})
    @RequestMapping(method = RequestMethod.GET)
    public UsageAORsp get(Authentication authentication){
        return service.lookup(authentication.getName());
    }
}
