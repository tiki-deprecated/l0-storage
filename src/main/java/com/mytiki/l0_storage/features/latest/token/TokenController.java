/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.token;

import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "STORAGE")
@RestController
@RequestMapping(value = TokenController.PATH_CONTROLLER)
public class TokenController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "token";

    private final TokenService service;

    public TokenController(TokenService service) {
        this.service = service;
    }

    @Operation(operationId = Constants.PROJECT_DASH_PATH +  "-token-post",
            summary = "Request Access Token",
            description = "Request an access token for uploading to storage bucket",
            security = @SecurityRequirement(name = "apiId"),
            responses = {
                @ApiResponse(responseCode = "200", description = "OK",
                        content = @Content(schema = @Schema(implementation = TokenAORsp.class))),
                @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                @ApiResponse(responseCode = "417", description = "Expectation Failed", content = @Content)})
    @RequestMapping(method = RequestMethod.POST)
    public TokenAORsp post(
            @RequestHeader(name = "x-api-id") String apiId,
            @RequestBody TokenAOReq body){
        return service.issue(apiId, body);
    }
}
