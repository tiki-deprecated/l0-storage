/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.config;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdConfig;
import com.mytiki.l0_storage.features.latest.policy.PolicyConfig;
import org.springframework.context.annotation.Import;

@Import({
        ApiIdConfig.class,
        PolicyConfig.class
})
public class ConfigFeatures {}
