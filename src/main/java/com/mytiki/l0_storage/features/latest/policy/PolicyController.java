/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.policy;

import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "POLICY")
@RestController
@RequestMapping(value = PolicyController.PATH_CONTROLLER)
public class PolicyController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "policy";

    private final PolicyService service;

    public PolicyController(PolicyService service) {
        this.service = service;
    }

    @Operation(summary = "Request a new policy", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    schema = @Schema(implementation = PolicyAORsp.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "417", description = "Expectation Failed", content = @Content)})
    @RequestMapping(method = RequestMethod.POST)
    public PolicyAORsp postNew(
            @RequestHeader(name = "x-api-id") String apiId,
            @RequestBody PolicyAOReq body){
        return service.request(apiId, body);
    }
}
