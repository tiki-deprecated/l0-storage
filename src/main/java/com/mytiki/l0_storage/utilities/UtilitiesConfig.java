/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class UtilitiesConfig {

    @Bean
    public HealthController healthController(){
        return new HealthController();
    }

    @Bean
    public WasabiHelper wasabiHelper(
            @Value("${com.mytiki.l0_storage.wasabi.bucket}") String bucket,
            @Value("${com.mytiki.l0_storage.wasabi.accountKey}") String key,
            @Value("${com.mytiki.l0_storage.wasabi.secretKey}") String secret,
            @Value("${com.mytiki.l0_storage.wasabi.region}") String region){
        return new WasabiHelper(key, secret, bucket, region);
    }

    @Bean
    public JwtHelper jwtHelper(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer){
        return new JwtHelper(issuer);
    }
}
