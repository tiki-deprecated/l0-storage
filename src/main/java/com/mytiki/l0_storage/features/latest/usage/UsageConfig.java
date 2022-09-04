/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.usage;

import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.l0_storage.utilities.wasabi.WasabiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(UsageConfig.PACKAGE_PATH)
@EntityScan(UsageConfig.PACKAGE_PATH)
public class UsageConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".usage";

    @Bean
    public UsageService usageService(@Autowired WasabiFacade wasabiFacade){
        return new UsageService(wasabiFacade);
    }

    @Bean
    public UsageController usageController(@Autowired UsageService usageService){
        return new UsageController(usageService);
    }
}
