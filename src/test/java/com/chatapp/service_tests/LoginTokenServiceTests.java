package com.chatapp.service_tests;

import com.chatapp.entity.LoginToken;
import com.chatapp.entity.User;
import com.chatapp.repository.LoginTokenRepository;
import com.chatapp.service.LoginTokenService;
import com.chatapp.service.OttExtrasService;
import com.chatapp.service.UserService;
import com.chatapp.util.LoginOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

@SpringBootTest(classes = LoginTokenService.class)
@TestPropertySource("/application-test.properties")
public class LoginTokenServiceTests {

    @MockitoBean
    private LoginTokenRepository loginTokenRepositoryMock;

    @MockitoBean
    private OttExtrasService ottExtrasServiceMock;

    @MockitoBean
    private UserService userServiceMock;

    @Autowired
    private LoginTokenService loginTokenService;

    @Test
    void testGenerationSuccessToken() {
        GenerateOneTimeTokenRequest request = new GenerateOneTimeTokenRequest("devuser360@gmail.com");
        User user = User.builder()
                .id(2)
                .username("devuser360@gmail.com")
                .isActive(true)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .build();
        Supplier<String> stringSupplier = new Supplier<String>() {
            @Override
            public String get() {
                return "12345678";
            }
        };

        Mockito.when(userServiceMock.findByEmailOrPhone(request.getUsername()))
                .thenReturn(Optional.of(user));
        Mockito.when(ottExtrasServiceMock.createRandomOneTimePassword())
                .thenReturn(stringSupplier);
        Mockito.when(loginTokenRepositoryMock.save(Mockito.any(LoginToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OneTimeToken result = loginTokenService.generate(request);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("12345678", result.getTokenValue());
        Assertions.assertEquals(user.getUsername(), result.getUsername());
        Assertions.assertTrue(result.getExpiresAt().isAfter(new Date(System.currentTimeMillis()).toInstant()));
    }

    @Test
    void testTokenConsumeSuccess() {
        OneTimeTokenAuthenticationToken authenticationToken = new OneTimeTokenAuthenticationToken("12345678");
        User user = User.builder()
                .id(2)
                .username("devuser360@gmail.com")
                .isActive(true)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .build();

        Mockito.when(loginTokenRepositoryMock.findByToken(authenticationToken.getTokenValue()))
                .thenReturn(Optional.of(new LoginToken(1, new Date(System.currentTimeMillis() + ((long) 5L * 60 * 1000)), "12345678", user)));

        OneTimeToken result = loginTokenService.consume(authenticationToken);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("12345678", result.getTokenValue());
        Assertions.assertEquals(user.getUsername(), result.getUsername());
        Assertions.assertTrue(result.getExpiresAt().isAfter(new Date(System.currentTimeMillis()).toInstant()));
    }

    @Test
    void testTokenConsumeFail() {
        OneTimeTokenAuthenticationToken authenticationToken = new OneTimeTokenAuthenticationToken("12345678");
        User user = User.builder()
                .id(2)
                .username("devuser360@gmail.com")
                .isActive(false)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .build();

        Mockito.when(loginTokenRepositoryMock.findByToken(authenticationToken.getTokenValue()))
                .thenReturn(Optional.empty());

        OneTimeToken result = loginTokenService.consume(authenticationToken);
        Assertions.assertNull(result);
    }

    @Test
    void testTokenConsumeExpiredToken() {
        OneTimeTokenAuthenticationToken authenticationToken = new OneTimeTokenAuthenticationToken("12345678");
        User user = User.builder()
                .id(2)
                .username("devuser360@gmail.com")
                .isActive(true)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .build();

        Mockito.when(loginTokenRepositoryMock.findByToken(authenticationToken.getTokenValue()))
                .thenReturn(Optional.of(new LoginToken(1, new Date(System.currentTimeMillis() - ((long) 5L * 60 * 1000)), "12345678", user)));
        OneTimeToken result = loginTokenService.consume(authenticationToken);
        Assertions.assertNull(result);
    }
}
