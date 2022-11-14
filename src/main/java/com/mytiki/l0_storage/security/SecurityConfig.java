/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytiki.l0_storage.features.latest.report.ReportController;
import com.mytiki.l0_storage.features.latest.token.TokenController;
import com.mytiki.l0_storage.utilities.Constants;
import com.mytiki.spring_rest_api.ApiConstants;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
    private final String remoteWorkerId;
    private final String remoteWorkerSecret;
    private final URL jwtJwkUri;
    private final Set<JWSAlgorithm> jwtJwsAlgorithms;
    private final Set<String> jwtAudiences;
    private final String jwtIssuer;

    private static final String REMOTE_WORKER_ROLE = "REMOTE";

    public SecurityConfig(
            @Autowired ObjectMapper objectMapper,
            @Value("${com.mytiki.l0_storage.remote_worker.id}") String remoteWorkerId,
            @Value("${com.mytiki.l0_storage.remote_worker.secret}") String remoteWorkerSecret,
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") URL jwtJwkUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.jws-algorithms}") Set<JWSAlgorithm> jwtJwsAlgorithms,
            @Value("${spring.security.oauth2.resourceserver.jwt.audiences}") Set<String> jwtAudiences,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String jwtIssuer) {
        super(true);
        this.accessDeniedHandler = new AccessDeniedHandler(objectMapper);
        this.authenticationEntryPoint = new AuthenticationEntryPoint(objectMapper);
        this.remoteWorkerId = remoteWorkerId;
        this.remoteWorkerSecret = remoteWorkerSecret;
        this.jwtJwkUri = jwtJwkUri;
        this.jwtJwsAlgorithms = jwtJwsAlgorithms;
        this.jwtAudiences = jwtAudiences;
        this.jwtIssuer = jwtIssuer;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilter(new WebAsyncManagerIntegrationFilter())
                .servletApi().and()
                .exceptionHandling()
                    .accessDeniedHandler(accessDeniedHandler)
                    .authenticationEntryPoint(authenticationEntryPoint).and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .securityContext().and()
                .headers()
                    .cacheControl().and()
                    .contentTypeOptions().and()
                    .httpStrictTransportSecurity().and()
                    .frameOptions().and()
                    .xssProtection().and()
                    .referrerPolicy().and()
                    .permissionsPolicy().policy(FEATURE_POLICY).and()
                    .httpPublicKeyPinning().and()
                    .contentSecurityPolicy(CONTENT_SECURITY_POLICY).and().and()
                .anonymous().and()
                .cors().configurationSource(corsConfigurationSource()).and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, ApiConstants.HEALTH_ROUTE).permitAll()
                    .antMatchers(HttpMethod.GET, Constants.API_DOCS_PATH).permitAll()
                    .antMatchers(HttpMethod.POST, TokenController.PATH_CONTROLLER).permitAll()
                    .antMatchers(HttpMethod.POST, ReportController.PATH_CONTROLLER).hasRole(REMOTE_WORKER_ROLE)
                    .anyRequest().authenticated().and()
                .httpBasic()
                    .authenticationEntryPoint(authenticationEntryPoint).and()
                .oauth2ResourceServer()
                    .jwt().decoder(jwtDecoder()).and()
                    .accessDeniedHandler(accessDeniedHandler)
                    .authenticationEntryPoint(authenticationEntryPoint);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(remoteWorkerId)
                .password(remoteWorkerSecret)
                .roles(REMOTE_WORKER_ROLE);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","PUT","POST","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public JwtDecoder jwtDecoder() {
        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        RemoteJWKSet<SecurityContext> remoteJWKSet = new RemoteJWKSet<>(jwtJwkUri);
        jwtProcessor.setJWSKeySelector(
                new JWSVerificationKeySelector<>(jwtJwsAlgorithms, remoteJWKSet));
        NimbusJwtDecoder decoder = new NimbusJwtDecoder(jwtProcessor);
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        validators.add(new JwtIssuerValidator(jwtIssuer));
        validators.add(new JwtClaimValidator<>(JwtClaimNames.SUB, Objects::nonNull));
        validators.add(new JwtClaimValidator<>(JwtClaimNames.IAT, Objects::nonNull));
        Predicate<List<String>> audienceTest = (audience) -> (audience != null)
                && new HashSet<>(audience).containsAll(jwtAudiences);
        validators.add(new JwtClaimValidator<>(JwtClaimNames.AUD, audienceTest));
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));
        return decoder;
    }
}
