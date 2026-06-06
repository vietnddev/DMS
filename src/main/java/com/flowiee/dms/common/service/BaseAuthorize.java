package com.flowiee.dms.common.service;

import com.flowiee.dms.common.exception.AuthenticationException;
import com.flowiee.dms.common.exception.ForbiddenException;
import com.flowiee.dms.common.utils.AppConstants;
import com.flowiee.dms.common.utils.SecurityUtils;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BaseAuthorize {
    @SneakyThrows
    protected boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException();
        }
        if ("anonymousUser".equalsIgnoreCase(authentication.getPrincipal().toString())) {
            throw new AuthenticationException();
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ANONYMOUS".equalsIgnoreCase(authority.getAuthority())) {
                throw new AuthenticationException();
            }
        }
        return authentication.isAuthenticated();
    }

    protected boolean isAuthorized(String action, boolean throwException) {
        if (isAuthenticated()) {
            if (AppConstants.ADMINISTRATOR.equals(SecurityUtils.getCurrentUser().getUsername())) {
                return true;
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals(action)) {
                    return true;
                }
            }
            if (throwException) {
                throw new ForbiddenException("You are not authorized to use this function!");
            } else {
                return false;
            }
        }
        throw new AuthenticationException();
    }
}