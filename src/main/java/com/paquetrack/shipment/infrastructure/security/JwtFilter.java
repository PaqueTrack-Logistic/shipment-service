package com.paquetrack.shipment.infrastructure.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.paquetrack.shipment.domain.model.AuthenticatedUser;
import com.paquetrack.shipment.domain.port.out.JwtPort;
import com.paquetrack.shipment.infrastructure.dto.ErrorResponseDTO;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtPort jwtPort;
    private final ObjectMapper objectMapper;

    // Rutas públicas
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/swagger-resources",
            "/webjars",
            "/diagnostic",
            "/dev",
            "/health");

    // Rutas protegidas (requieren token)
    private static final List<String> PROTECTED_PATHS = Arrays.asList(
            "/api/shipments");

    public JwtFilter(JwtPort jwtPort, ObjectMapper objectMapper) {
        this.jwtPort = jwtPort;
        this.objectMapper = objectMapper;
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isProtectedPath(String path) {
        return PROTECTED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        
        if (isProtectedPath(path) && (authHeader == null || !authHeader.startsWith("Bearer "))) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED,
                    "AUTHENTICATION_REQUIRED",
                    "Se requiere autenticación para acceder a este recurso",
                    "Incluya el header 'Authorization: Bearer <su-token>' en la petición",
                    request);
            return;
        }

        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED,
                    "AUTHENTICATION_REQUIRED",
                    "Se requiere autenticación para acceder a este recurso",
                    "Incluya el header 'Authorization: Bearer <su-token>'",
                    request);
            return;
        }

        // Validar el token
        String token = authHeader.substring(7);

        try {
            AuthenticatedUser usuario = jwtPort.validarToken(token);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    usuario,
                    token,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED,
                    "TOKEN_EXPIRED",
                    "El token ha expirado",
                    "Solicite un nuevo token al servicio de autenticación",
                    request);

        } catch (SignatureException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED,
                    "INVALID_SIGNATURE",
                    "La firma del token es inválida",
                    "Verifique que está usando el token correcto",
                    request);

        } catch (MalformedJwtException e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED,
                    "MALFORMED_TOKEN",
                    "El formato del token es inválido",
                    "El token debe tener 3 partes separadas por puntos",
                    request);

        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("rol")) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED,
                        "MISSING_ROLE",
                        "El token no contiene el campo 'rol' requerido",
                        "El token debe incluir 'rol' en el payload: ADMIN u OPERADOR",
                        request);
            } else {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED,
                        "INVALID_TOKEN",
                        "Token inválido: " + e.getMessage(),
                        "Verifique que el token sea correcto",
                        request);
            }

        } catch (Exception e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED,
                    "TOKEN_INVALIDO",
                    "Token inválido: " + e.getMessage(),
                    "Verifique que el token sea correcto",
                    request);
        }
    }

    private void sendErrorResponse(HttpServletResponse response,
            HttpStatus status,
            String errorCode,
            String message,
            String suggestion,
            HttpServletRequest request) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                errorCode,
                message,
                status.value(),
                request.getRequestURI(),
                LocalDateTime.now(),
                suggestion);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}