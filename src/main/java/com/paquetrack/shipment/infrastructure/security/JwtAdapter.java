package com.paquetrack.shipment.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.paquetrack.shipment.domain.model.AuthenticatedUser;
import com.paquetrack.shipment.domain.port.out.JwtPort;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtAdapter implements JwtPort {

    private static final Logger log = LoggerFactory.getLogger(JwtAdapter.class);

    @Value("${jwt.secret}")
    private String secret;

    public JwtAdapter() {
        log.info("JwtAdapter inicializado");
    }

    private SecretKey getSigningKey() {
        if (secret == null || secret.isEmpty()) {
            log.error("JWT secret no está configurado!");
            throw new IllegalStateException("JWT secret no está configurado");
        }
        log.info("JWT secret cargado. Longitud: {}", secret.length());
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean esTokenValido(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public AuthenticatedUser validarToken(String token) {
        if (!esTokenValido(token)) {
            throw new RuntimeException("Token inválido o expirado");
        }
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        String rol = claims.get("rol", String.class);

        log.info("Token válido - Usuario: {}, Rol: {}", username, rol);

        return new AuthenticatedUser(username, rol);
    }
}