/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.main;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@OpenAPIDefinition(
        info = @Info(
                title = "L0 Storage",
                description = "Long term immutable block storage.",
                version = "0.0.5",
                license = @License(
                        name = "MIT",
                        url = "https://github.com/tiki/l0-storage/blob/main/LICENSE"
                )
        ),
        servers = @Server(url = "https://storage.l0.mytiki.com")
)
@Import(AppConfig.class)
@SpringBootApplication
public class App {
    public static void main(final String... args) {
        SpringApplication.run(App.class, args);
    }
}
