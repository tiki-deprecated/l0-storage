/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdConfig;
import com.mytiki.l0_storage.features.latest.policy.PolicyConfig;
import com.mytiki.l0_storage.features.latest.usage.UsageConfig;
import org.springframework.context.annotation.Import;

@Import({
        ApiIdConfig.class,
        PolicyConfig.class,
        UsageConfig.class
})
public class FeaturesConfig {}
