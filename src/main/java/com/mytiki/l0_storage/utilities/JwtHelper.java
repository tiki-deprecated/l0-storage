/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.l0_storage.utilities;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

public class JwtHelper {
    private final NimbusJwtDecoder decoder;

    public JwtHelper(String issuer) {
        this.decoder = JwtDecoders.fromOidcIssuerLocation(issuer);
    }

    public Jwt decode(String token){
        String bearerPrefix = "Bearer ";
        if(token.startsWith(bearerPrefix)){
            token = token.replace(bearerPrefix, "");
        }
        return decoder.decode(token);
    }
}
