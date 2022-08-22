/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.config;

import com.mytiki.l0_storage.utilities.UtilitiesConfig;
import com.mytiki.spring_rest_api.exception.ApiExceptionHandlerDefault;
import com.mytiki.spring_rest_api.reply.ApiReplyHandlerDefault;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Import({
        ApiExceptionHandlerDefault.class,
        ApiReplyHandlerDefault.class,
        UtilitiesConfig.class,
        ConfigFeatures.class
})
@EnableScheduling
public class ConfigL0StorageApp {
}
