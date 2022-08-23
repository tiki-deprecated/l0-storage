/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.config;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdConfig;
import org.springframework.context.annotation.Import;

@Import({
        ApiIdConfig.class
})
public class ConfigFeatures {}
