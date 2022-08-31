/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.api_id;

import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.l0_storage.utilities.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(ApiIdConfig.PACKAGE_PATH)
@EntityScan(ApiIdConfig.PACKAGE_PATH)
public class ApiIdConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".api_id";

    @Bean
    public ApiIdController apiKeyController(@Autowired ApiIdService service, @Autowired JwtHelper jwtHelper){
        return new ApiIdController(service, jwtHelper);
    }

    @Bean
    public ApiIdService apiKeyService(@Autowired ApiIdRepository repository){
        return new ApiIdService(repository);
    }
}
