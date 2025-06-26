package com.chatapp.config;

import com.chatapp.service.LoginTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomOttGenerationSuccessHandler tokenGenerationSuccessHandler;
    private final LoginTokenService customOttService;
    private final String[] publicUrls = {
            "/",
            "/resources/**",
            "/static/**",
            "/assets/**",
            "/*.jpg",
            "/js/**",
            "/css/**",
            "/*.css",
            "/*.js",
            "/error",
            "/login",
            "/signUp",
            "/register",
            "/activate"
    };

    @Autowired
    public WebSecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler, CustomOttGenerationSuccessHandler tokenGenerationSuccessHandler, LoginTokenService customOttService) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.tokenGenerationSuccessHandler = tokenGenerationSuccessHandler;
        this.customOttService = customOttService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //Filtering requests
        httpSecurity.authorizeHttpRequests(auth -> {
            auth.requestMatchers(publicUrls).permitAll();
            auth.anyRequest().authenticated();
        });

        //Creating an OTT filter chain and handling other security requests
        httpSecurity
                .oneTimeTokenLogin(ott -> {
                    ott.tokenGenerationSuccessHandler(tokenGenerationSuccessHandler);
                    ott.showDefaultSubmitPage(true);
                    ott.tokenGeneratingUrl("/create-ott");
                    ott.loginPage("/login");
                    ott.successHandler(customAuthenticationSuccessHandler);
                    ott.tokenService(customOttService);
                })
                .logout(logout -> {
                    logout.logoutUrl("/logout");
                    logout.logoutSuccessUrl("/");
                })
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }
}
