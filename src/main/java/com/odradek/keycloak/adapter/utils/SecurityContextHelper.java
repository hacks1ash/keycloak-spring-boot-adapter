package com.odradek.keycloak.adapter.utils;

import com.odradek.keycloak.adapter.OdradekAuthenticationToken;
import com.odradek.keycloak.adapter.OdradekUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {

    public static OdradekUser getCurrentUser() {
        return ((OdradekAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getOdradekUser();
    }

}
