/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.config;

import com.mytiki.l0_storage.utilities.UtilitiesConfig;
import com.mytiki.spring_rest_api.ApiExceptionHandlerDefault;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.security.Security;
import java.util.TimeZone;

@Import({
        ApiExceptionHandlerDefault.class,
        UtilitiesConfig.class,
        ConfigSecurity.class,
        ConfigFeatures.class,
})
@EnableScheduling
public class ConfigL0StorageApp {
    @PostConstruct
    void starter(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }
}
