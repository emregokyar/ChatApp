package com.chatapp.user_config;

import com.chatapp.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.util.Assert;

// Custom With User Details
final class WithUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithUserDetails> {
    private CustomUserDetailService customUserDetailService;

    @Autowired
    public WithUserDetailsSecurityContextFactory(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    public SecurityContext createSecurityContext(WithUserDetails withUser) {
        String username = withUser.value();
        Assert.hasLength(username, "value() must be non-empty String");
        UserDetails principal = customUserDetailService.loadUserByUsername(username);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
