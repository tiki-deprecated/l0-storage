/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;

public class UtilitiesConfig {

    @Bean
    public WasabiFacade wasabiFacade(
            @Value("${com.mytiki.l0_storage.wasabi.bucket}") String bucket,
            @Value("${com.mytiki.l0_storage.wasabi.accountKey}") String key,
            @Value("${com.mytiki.l0_storage.wasabi.secretKey}") String secret,
            @Value("${com.mytiki.l0_storage.wasabi.region}") String region,
            @Autowired RestTemplateBuilder restTemplateBuilder){
        return new WasabiFacade(key, secret, bucket, region, restTemplateBuilder.build());
    }
}
