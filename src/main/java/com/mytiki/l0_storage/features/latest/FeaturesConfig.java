/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest;

import com.mytiki.l0_storage.features.latest.api_id.ApiIdConfig;
import com.mytiki.l0_storage.features.latest.token.TokenConfig;
import com.mytiki.l0_storage.features.latest.report.ReportConfig;
import org.springframework.context.annotation.Import;

@Import({
        ApiIdConfig.class,
        TokenConfig.class,
        ReportConfig.class
})
public class FeaturesConfig {}
