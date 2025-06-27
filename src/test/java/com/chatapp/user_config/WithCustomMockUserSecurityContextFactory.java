package com.chatapp.user_config;

import com.chatapp.entity.User;
import com.chatapp.util.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        User user = new User();
        user.setId(annotation.id());
        user.setUsername(annotation.username());
        user.setRegistrationType(annotation.registrationType());
        user.setFirstname(annotation.firstname());
        user.setLastname(annotation.lastname());
        user.setIsActive(annotation.isActive());
        if (!annotation.isActive()) user.setActivationNumber(annotation.activationNumber());
        else user.setActivationNumber(null);

        CustomUserDetails principal = new CustomUserDetails(user);
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
