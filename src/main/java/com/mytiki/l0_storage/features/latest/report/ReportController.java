/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.report;

import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@Tag(name = "STORAGE")
@RestController
@RequestMapping(value = ReportController.PATH_CONTROLLER)
public class ReportController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "report";

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @RolesAllowed("REMOTE")
    @Operation(summary = "Submit a usage report", security = @SecurityRequirement(name = "remote"), responses = {
            @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void post(@RequestBody ReportAOReq body){
        service.log(body);
    }
}
