
package com.paquetrack.shipment.domain.port.out;

import com.paquetrack.shipment.domain.model.AuthenticatedUser;

public interface JwtPort {
    AuthenticatedUser validarToken(String token);
    boolean esTokenValido(String token);
}