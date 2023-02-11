/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.token;

import com.mytiki.l0_storage.security.SecurityConfig;
import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.spring_rest_api.ApiConstants;
import com.mytiki.spring_rest_api.ApiExceptionBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
            security = @SecurityRequirement(name = "jwt"),
            responses = {
                @ApiResponse(responseCode = "200", description = "OK",
                        content = @Content(schema = @Schema(implementation = TokenAORsp.class))),
                @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
                @ApiResponse(responseCode = "417", description = "Expectation Failed", content = @Content)})
    @RequestMapping(method = RequestMethod.POST)
    public TokenAORsp post(
            Authentication authentication,
            Principal principal,
            @RequestBody TokenAOReq body){
       if(authentication.getAuthorities()
               .contains(new SimpleGrantedAuthority("ROLE_" + SecurityConfig.REMOTE_WORKER_ROLE)))
           throw new ApiExceptionBuilder(HttpStatus.FORBIDDEN)
                   .message("Requires a valid Bearer token")
                   .detail("Provide Basic auth")
                   .help("Get a valid JWT from https://auth.l0.mytiki.com")
                   .build();
        return service.issue(principal.getName(), body);
    }
}
