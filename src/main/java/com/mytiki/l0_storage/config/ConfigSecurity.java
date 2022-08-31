/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytiki.l0_storage.features.latest.policy.PolicyController;
import com.mytiki.l0_storage.utilities.AccessDeniedHandler;
import com.mytiki.l0_storage.utilities.AuthenticationEntryPoint;
import com.mytiki.spring_rest_api.ApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@EnableWebSecurity
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

    private static final String FEATURE_POLICY = "accelerometer" + " 'none'" + "ambient-light-sensor" + " 'none'" +
            "autoplay" + " 'none'" + "battery" + " 'none'" + "camera" + " 'none'" + "display-capture" + " 'none'" +
            "document-domain" + " 'none'" + "encrypted-media" + " 'none'" + "execution-while-not-rendered" + " 'none'" +
            "execution-while-out-of-viewport" + " 'none'" + "fullscreen" + " 'none'" + "geolocation" + " 'none'" +
            "gyroscope" + " 'none'" + "layout-animations" + " 'none'" + "legacy-image-formats" + " 'none'" +
            "magnetometer" + " 'none'" + "microphone" + " 'none'" + "midi" + " 'none'" + "navigation-override" + " 'none'" +
            "oversized-images" + " 'none'" + "payment" + " 'none'" + "picture-in-picture" + " 'none'" + "publickey-credentials-get" + " 'none'" +
            "sync-xhr" + " 'none'" + "usb" + " 'none'" + "vr wake-lock" + " 'none'" + "xr-spatial-tracking" + " 'none'";

    private static final String CONTENT_SECURITY_POLICY = "default-src" + "' self'";
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public ConfigSecurity(@Autowired ObjectMapper objectMapper) {
        super(true);
        this.accessDeniedHandler = new AccessDeniedHandler(objectMapper);
        this.authenticationEntryPoint = new AuthenticationEntryPoint(objectMapper);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilter(new WebAsyncManagerIntegrationFilter())
                .servletApi().and()
                .exceptionHandling(handler -> handler
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityContext().and()
                .headers(headers -> headers
                        .cacheControl().and()
                        .contentTypeOptions().and()
                        .httpStrictTransportSecurity().and()
                        .frameOptions().and()
                        .xssProtection().and()
                        .referrerPolicy().and()
                        .permissionsPolicy().policy(FEATURE_POLICY).and()
                        .httpPublicKeyPinning().and()
                        .contentSecurityPolicy(CONTENT_SECURITY_POLICY)
                )
                .anonymous().and()
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt().and()
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeRequests(authorize -> authorize
                        .antMatchers(HttpMethod.POST, PolicyController.PATH_CONTROLLER).permitAll()
                        .antMatchers(HttpMethod.GET, ApiConstants.HEALTH_ROUTE).permitAll()
                        .anyRequest().authenticated()
                );
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","PUT","POST","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("*/**", configuration);
        return source;
    }
}
