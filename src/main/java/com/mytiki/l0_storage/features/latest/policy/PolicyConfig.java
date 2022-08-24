/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.policy;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdService;
import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.l0_storage.utilities.WasabiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(PolicyConfig.PACKAGE_PATH)
@EntityScan(PolicyConfig.PACKAGE_PATH)
public class PolicyConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".policy";

    @Bean
    public PolicyService policyService(
            @Autowired PolicyRepository repository,
            @Autowired ApiIdService apiIdService,
            @Autowired WasabiHelper wasabiHelper){
        return new PolicyService(repository, apiIdService, wasabiHelper);
    }

    @Bean
    public PolicyController policyController(@Autowired PolicyService service){
        return new PolicyController(service);
    }
}
