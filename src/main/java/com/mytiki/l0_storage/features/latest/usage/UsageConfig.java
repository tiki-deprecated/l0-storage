/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.usage;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdService;
import com.mytiki.l0_storage.features.latest.policy.PolicyController;
import com.mytiki.l0_storage.features.latest.policy.PolicyRepository;
import com.mytiki.l0_storage.features.latest.policy.PolicyService;
import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.l0_storage.utilities.WasabiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(UsageConfig.PACKAGE_PATH)
@EntityScan(UsageConfig.PACKAGE_PATH)
public class UsageConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".usage";

    @Bean
    public PolicyService policyService(
            @Autowired PolicyRepository repository,
            @Autowired ApiIdService apiIdService,
            @Autowired WasabiFacade wasabiFacade){
        return new PolicyService(repository, apiIdService, wasabiFacade);
    }

    @Bean
    public PolicyController policyController(@Autowired PolicyService service){
        return new PolicyController(service);
    }
}
