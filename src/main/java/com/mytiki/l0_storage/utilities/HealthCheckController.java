/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities;

import com.mytiki.spring_rest_api.ApiConstants;
import com.mytiki.spring_rest_api.reply.ApiReplyAO;
import com.mytiki.spring_rest_api.reply.ApiReplyAOFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiConstants.HEALTH_ROUTE)
public class HealthCheckController {

    @RequestMapping(method = RequestMethod.GET)
    public ApiReplyAO<?> get(){
        return ApiReplyAOFactory.ok();
    }
}
