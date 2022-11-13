/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.features.latest.report;

import com.mytiki.l0_storage.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(ReportConfig.PACKAGE_PATH)
@EntityScan(ReportConfig.PACKAGE_PATH)
public class ReportConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".report";

    @Bean
    public ReportService reportService(@Autowired ReportRepository repository){
        return new ReportService(repository);
    }

    @Bean
    public ReportController reportController(@Autowired ReportService service){
        return new ReportController(service);
    }
}
