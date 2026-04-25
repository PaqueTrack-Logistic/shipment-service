package com.paquetrack.shipment.domain.model;

public class AuthenticatedUser {
    private String username;
    private String rol;

    public AuthenticatedUser(String username, String rol) {
        this.username = username;
        this.rol = rol;
    }

    public String getUsername() {
        return username;
    }

    public String getRol() {
        return rol;
    }

    public boolean puedeCrearEnvios() {
        return "ADMIN".equals(rol) || "OPERADOR".equals(rol);
    }
}