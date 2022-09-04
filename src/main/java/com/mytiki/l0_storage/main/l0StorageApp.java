/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.main;

import com.mytiki.l0_storage.config.ConfigApp;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
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
                version = "0.0.1",
                license = @License(
                        name = "MIT",
                        url = "https://github.com/tiki/l0-storage/blob/main/LICENSE"
                )
        ),
        servers = @Server(url = "coming soon"),
        externalDocs = @ExternalDocumentation(
                url = "coming soon",
                description = "javadocs"
        )
)
@Import({
        ConfigApp.class
})
@SpringBootApplication
public class l0StorageApp {
    public static void main(final String... args) {
        SpringApplication.run(l0StorageApp.class, args);
    }
}
