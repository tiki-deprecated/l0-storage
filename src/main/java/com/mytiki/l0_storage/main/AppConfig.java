/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.main;

import com.mytiki.l0_storage.features.latest.FeaturesConfig;
import com.mytiki.l0_storage.health.HealthConfig;
import com.mytiki.l0_storage.security.SecurityConfig;
import com.mytiki.l0_storage.utilities.UtilitiesConfig;
import com.mytiki.spring_rest_api.ApiExceptionHandlerDefault;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.security.Security;
import java.util.TimeZone;

@Import({
        ApiExceptionHandlerDefault.class,
        SecurityConfig.class,
        HealthConfig.class,
        FeaturesConfig.class,
        UtilitiesConfig.class
})
public class AppConfig {
    @PostConstruct
    void starter(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }
}
