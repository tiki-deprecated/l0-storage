/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.main;

import com.mytiki.l0_storage.features.latest.FeaturesConfig;
import com.mytiki.l0_storage.health.HealthConfig;
import com.mytiki.l0_storage.security.SecurityConfig;
import com.mytiki.spring_rest_api.ApiExceptionHandlerDefault;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.security.Security;
import java.util.Collections;
import java.util.TimeZone;

@Import({
        ApiExceptionHandlerDefault.class,
        SecurityConfig.class,
        HealthConfig.class,
        FeaturesConfig.class
})
public class AppConfig {
    @PostConstruct
    void starter(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
    }

    @Bean
    public OpenAPI oenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title("L0 Storage")
                        .description("Immutable Block Storage")
                        .version(appVersion)
                        .license(new License()
                                .name("MIT")
                                .url("https://github.com/tiki/l0-storage/blob/main/LICENSE")))
                .servers(Collections.singletonList(
                        new Server()
                                .url("https://storage.l0.mytiki.com")))
                .components(new Components()
                        .addSecuritySchemes("remote",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic"))
                        .addSecuritySchemes("apiId",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-ID"))
                        .addSecuritySchemes("jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .path("/api/latest/upload",
                        new PathItem().post(
                                new Operation()
                                        .tags(Collections.singletonList("STORAGE"))
                                        .operationId("post")
                                        .requestBody(new RequestBody()
                                                .content(new Content()
                                                        .addMediaType("multipart/form-data",
                                                                new MediaType()
                                                                        .schema(new JsonSchema()
                                                                                .type("object")
                                                                                .addProperty("policy", new StringSchema())
                                                                                .addProperty("content-type", new StringSchema())
                                                                                .addProperty("x-amz-credential", new StringSchema())
                                                                                .addProperty("x-amz-algorithm", new StringSchema())
                                                                                .addProperty("x-amz-date", new StringSchema())
                                                                                .addProperty("x-amz-signature", new StringSchema())
                                                                                .addProperty("x-amz-object-lock-mode", new StringSchema())
                                                                                .addProperty("x-amz-object-lock-retain-until-date", new StringSchema())
                                                                                .addProperty("key", new StringSchema())
                                                                                .addProperty("content-md5", new StringSchema())
                                                                                .addProperty("file", new BinarySchema())
                                                                                .addRequiredItem("policy")
                                                                                .addRequiredItem("content-type")
                                                                                .addRequiredItem("x-amz-credential")
                                                                                .addRequiredItem("x-amz-algorithm")
                                                                                .addRequiredItem("x-amz-date")
                                                                                .addRequiredItem("x-amz-signature")
                                                                                .addRequiredItem("x-amz-object-lock-mode")
                                                                                .addRequiredItem("x-amz-object-lock-retain-until-date")
                                                                                .addRequiredItem("key")
                                                                                .addRequiredItem("content-md5")
                                                                                .addRequiredItem("file")
                                                                        ))))
                                        .responses(new ApiResponses()
                                                .addApiResponse("204",
                                                        new ApiResponse().description("No Content")))));
    }
}