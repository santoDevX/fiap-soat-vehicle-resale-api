package com.soat.vehicle_resale.infrastructure.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

// O conversor padrao do Spring Security so le o claim "scope"/"scp" de primeiro nivel.
// O Keycloak coloca as roles do realm em "realm_access.roles" (claim aninhado), entao
// precisamos de um conversor customizado pra mapear isso em GrantedAuthority ROLE_*.
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        var roles = (Collection<String>) realmAccess.get("roles");

        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
