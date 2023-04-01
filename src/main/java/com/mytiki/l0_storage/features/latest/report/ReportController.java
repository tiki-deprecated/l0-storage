/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.report;

import com.mytiki.spring_rest_api.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = ReportController.PATH_CONTROLLER)
public class ReportController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "report";

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @RolesAllowed("REMOTE")
    @Operation(hidden = true)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void post(@RequestBody ReportAOReq body){
        service.log(body);
    }
}
