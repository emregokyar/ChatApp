package com.chatapp.service_tests;

import com.chatapp.entity.User;
import com.chatapp.repository.UserRepository;
import com.chatapp.service.OttExtrasService;
import com.chatapp.service.UserService;
import com.chatapp.util.LoginOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = UserService.class)
public class UserServiceTests {
    @Autowired
    private UserService userServiceMock;

    @MockitoBean
    private OttExtrasService ottExtrasServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    void createNewUser() {
        User newUser = userServiceMock.createNewUser("emre", LoginOptions.EMAIL);
        Assertions.assertNotNull(newUser);
    }
}
