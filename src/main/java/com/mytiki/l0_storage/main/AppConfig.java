/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.main;

import com.mytiki.l0_storage.features.latest.FeaturesConfig;
import com.mytiki.l0_storage.health.HealthConfig;
import com.mytiki.l0_storage.security.SecurityConfig;
import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.spring_rest_api.ApiExceptionHandlerDefault;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.JsonSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.security.Security;
import java.util.Collections;
import java.util.List;
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
                        .description("Immutable Storage Service")
                        .version(appVersion)
                        .license(new License()
                                .name("MIT")
                                .url("https://github.com/tiki/l0-storage/blob/main/LICENSE")))
                .servers(Collections.singletonList(
                        new Server()
                                .url("https://storage.l0.mytiki.com")))
                .components(new Components()
                        .addSecuritySchemes("oauth", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .clientCredentials(new OAuthFlow()
                                                .tokenUrl("https://auth.l0.mytiki.com/api/latest/oauth/token")
                                                .refreshUrl("https://auth.l0.mytiki.com/api/latest/oauth/token")
                                                .scopes(new Scopes().addString("storage","this service")))))
                        .addSecuritySchemes("jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .path("/api/latest/upload", new PathItem().post(new Operation()
                        .tags(Collections.singletonList(""))
                        .operationId(Constants.PROJECT_DASH_PATH +  "-upload-post")
                        .summary("Upload Content")
                        .security(List.of(new SecurityRequirement().addList("jwt")))
                        .description("Upload a block/pub.key to storage bucket")
                        .requestBody(new RequestBody().content(new Content()
                                .addMediaType("application/json", new MediaType()
                                        .schema(new JsonSchema().type("object")
                                                        .addProperty("key", new StringSchema())
                                                        .addProperty("content", new StringSchema())))))
                        .responses(new ApiResponses()
                                .addApiResponse("201", new ApiResponse().description("Created")))));
    }
}