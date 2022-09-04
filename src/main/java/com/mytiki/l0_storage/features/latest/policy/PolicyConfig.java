/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.policy;

import com.mytiki.l0_storage.features.latest.usage.UsageController;
import com.mytiki.l0_storage.features.latest.usage.UsageRepository;
import com.mytiki.l0_storage.features.latest.usage.UsageService;
import com.mytiki.l0_storage.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(PolicyConfig.PACKAGE_PATH)
@EntityScan(PolicyConfig.PACKAGE_PATH)
public class PolicyConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".policy";

    @Bean
    public UsageService usageService(@Autowired UsageRepository repository){
        return new UsageService(repository);
    }

    @Bean
    public UsageController usageController(@Autowired UsageService service){
        return new UsageController(service);
    }
}
