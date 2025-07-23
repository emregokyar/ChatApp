package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.AgoraController;
import com.chatapp.entity.User;
import com.chatapp.service.*;
import com.chatapp.util.LoginOptions;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = {ChatAppApplication.class, AgoraController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class CallControllerTests {
    @MockitoBean
    private UserService userServiceMock;

    @MockitoBean
    private CallService callServiceMock;

    @MockitoBean
    private ChannelService channelServiceMock;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplateMock;

    @MockitoBean
    private ContactService contactServiceMock;

    @MockitoBean
    private SmsService smsServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private static User mockUser;

    @BeforeAll
    static void setUp() {
        mockUser = User.builder()
                .id(1)
                .username("emregokyar1940@gmail.com")
                .fullName("Emre Gokyar")
                .registrationType(LoginOptions.EMAIL)
                .activationNumber(null)
                .profilePhoto("test.jpg")
                .about("WhatsApp Clone Tests")
                .isActive(true)
                .build();
    }

    @BeforeEach
    void beforeEachTest() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    void authenticationFailTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/makeCall"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

}
