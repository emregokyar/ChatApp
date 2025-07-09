package com.chatapp.service_tests;

import com.chatapp.entity.User;
import com.chatapp.repository.UserRepository;
import com.chatapp.service.OttExtrasService;
import com.chatapp.service.UserService;
import com.chatapp.util.LoginOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = UserService.class)
@TestPropertySource("/application-test.properties")
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @MockitoBean
    private OttExtrasService ottExtrasServiceMock;

    @MockitoBean
    private UserRepository userRepositoryMock;

    @Test
    void testFindingUserByUsername() {
        User user = User.builder()
                .id(1)
                .username("test@gmail.com")
                .isActive(true)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .build();
        Mockito.when(userRepositoryMock.findByUsername("test@gmail.com"))
                .thenReturn(Optional.of(user));

        Optional<User> checkedUser = userService.findByEmailOrPhone("test@gmail.com");
        Assertions.assertNotNull(checkedUser);
        Assertions.assertEquals(Optional.of(user), checkedUser);
    }

    @Test
    void testSuccessCreatingUser() {
        Supplier<String> activationNumber = new Supplier<String>() {
            @Override
            public String get() {
                return "12345678";
            }
        };

        Mockito.when(userRepositoryMock.findByUsername("test@gmail.com"))
                .thenReturn(Optional.empty());
        Mockito.when(ottExtrasServiceMock.createRandomOneTimePassword())
                .thenReturn(activationNumber);

        User user = User.builder()
                .id(2)
                .username("test@gmail.com")
                .isActive(false)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .activationNumber(activationNumber.get())
                .build();

        Mockito.when(userRepositoryMock.save(any(User.class)))
                .thenReturn(user);

        User result = userService.createNewUser("test@gmail.com", LoginOptions.EMAIL);
        Assertions.assertEquals(user, result);
        Assertions.assertEquals(user.getUsername(), result.getUsername());
        Assertions.assertFalse(result.getIsActive());
    }

    @Test
    void testFailCreatingUser() {
        Supplier<String> activationNumber = new Supplier<String>() {
            @Override
            public String get() {
                return "12345678";
            }
        };

        User user = User.builder()
                .id(2)
                .username("test@gmail.com")
                .isActive(false)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .activationNumber(activationNumber.get())
                .build();

        Mockito.when(userRepositoryMock.findByUsername("test@gmail.com"))
                .thenReturn(Optional.of(user));

        Mockito.when(ottExtrasServiceMock.createRandomOneTimePassword())
                .thenReturn(activationNumber);

        Mockito.when(userRepositoryMock.save(any(User.class)))
                .thenReturn(user);

        User result = userService.createNewUser("test@gmail.com", LoginOptions.EMAIL);
        Assertions.assertEquals(result, user);
    }

    @Test
    void testSuccessfulActivationUser() {
        User user = User.builder()
                .id(2)
                .username("test@gmail.com")
                .isActive(false)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .activationNumber("12345678")
                .build();

        Mockito.when(userRepositoryMock.findByUsername("test@gmail.com"))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepositoryMock.save(any(User.class)))
                .thenReturn(user);

        boolean result = userService.activateUser(user.getUsername(), "12345678");
        Assertions.assertTrue(result);
    }

    @Test
    void testFailActivationUser() {
        User user = User.builder()
                .id(2)
                .username("test@gmail.com")
                .isActive(false)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .activationNumber("12345678")
                .build();

        Mockito.when(userRepositoryMock.findByUsername("test@gmail.com"))
                .thenReturn(Optional.of(user));

        boolean result = userService.activateUser(user.getUsername(), "15345646");
        Assertions.assertFalse(result);
    }

    @Test
    void testGetCurrentUserSuccess() {
        String username = "testuser@gmail.com";
        User user = User.builder()
                .id(2)
                .username(username)
                .isActive(true)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .build();

        // Creating an Authentication object with the username
        Authentication auth = new UsernamePasswordAuthenticationToken(username, null);

        // Setting it in the SecurityContext
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        // Mock userRepository
        Mockito.when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getCurrentUser();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(username, result.getUsername());
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserFail() {
        String username = "testuser@gmail.com";
        User user = User.builder()
                .id(2)
                .username(username)
                .isActive(true)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .build();

        // Creating an Anonymous Authentication object with the username
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(username, user, new ArrayList<>(List.of(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return null;
            }
        })));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.of(user));
        Assertions.assertNull(userService.getCurrentUser());
        SecurityContextHolder.clearContext();
    }

    @Test
    void testForUpdatingUser() {
        String username = "testuser@gmail.com";
        User user = User.builder()
                .id(2)
                .username(username)
                .isActive(true)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .build();

        Mockito.when(userRepositoryMock.save(user))
                .thenReturn(user);
        Assertions.assertNotNull(userService.updateUser(user));
    }

    @Test
    void testFindByUserId() {
        User user = User.builder()
                .id(1)
                .username("user")
                .build();
        Mockito.when(userRepositoryMock.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Optional<User> byUserId = userService.findByUserId(1);
        Assertions.assertEquals(byUserId.get(), user);
    }

    @Test
    void testFailTestFindById() {
        User user = User.builder()
                .id(2)
                .username("user")
                .build();
        Mockito.when(userRepositoryMock.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Optional<User> byUserId = userService.findByUserId(1);
        Assertions.assertEquals(byUserId, Optional.empty());
    }
}
