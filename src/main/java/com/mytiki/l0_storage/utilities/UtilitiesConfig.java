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
            @Value("${com.mytiki.l0_storage.wasabi.bucket}") String wasabiBucket,
            @Value("${com.mytiki.l0_storage.wasabi.accountKey}") String wasabiKey,
            @Value("${com.mytiki.l0_storage.wasabi.secretKey}") String wasabiSecret){
        return new WasabiHelper(wasabiKey, wasabiSecret, wasabiBucket);
    }
}
