package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.LoginController;
import com.chatapp.service.MailService;
import com.chatapp.service.SmsService;
import com.chatapp.service.UserService;
import com.chatapp.user_config.WithCustomMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = {ChatAppApplication.class, LoginController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class LoginControllerTests {
    @MockitoBean
    private UserService userServiceMock;

    @MockitoBean
    private MailService mailServiceMock;

    @MockitoBean
    private SmsService smsServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

    }

    @Test
    @WithCustomMockUser
    void getLoginPageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"));
    }
}
