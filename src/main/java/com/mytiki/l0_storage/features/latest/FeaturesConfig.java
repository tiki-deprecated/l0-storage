/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest;

import com.mytiki.l0_storage.features.latest.report.ReportConfig;
import com.mytiki.l0_storage.features.latest.token.TokenConfig;
import org.springframework.context.annotation.Import;

@Import({
        TokenConfig.class,
        ReportConfig.class
})
public class FeaturesConfig {}
