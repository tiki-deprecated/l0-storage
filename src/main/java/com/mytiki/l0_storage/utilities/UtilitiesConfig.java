/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities;

import org.springframework.context.annotation.Bean;

public class UtilitiesConfig {

    @Bean
    public HealthCheckController healthCheckController(){
        return new HealthCheckController();
    }
}
