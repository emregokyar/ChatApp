package com.chatapp.user_config;

import com.chatapp.util.LoginOptions;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
    int id() default 1;
    String username() default "emregokyar1940@gmail.com";
    LoginOptions registrationType() default LoginOptions.EMAIL;
    String fullName() default "Emre Gokyar";
    boolean isActive() default true;
    String activationNumber() default "12345678";
    String about() default "Hey there I'm using whatsapp clone";
}
