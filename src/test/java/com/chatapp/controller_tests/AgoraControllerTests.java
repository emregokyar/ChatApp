package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.AgoraController;
import com.chatapp.entity.User;
import com.chatapp.service.AgoraTokenGenerationService;
import com.chatapp.service.SmsService;
import com.chatapp.service.UserService;
import com.chatapp.user_config.WithCustomMockUser;
import com.chatapp.util.LoginOptions;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

@SpringBootTest(classes = {ChatAppApplication.class, AgoraController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class AgoraControllerTests {
    @MockitoBean
    private UserService userServiceMock;

    @MockitoBean
    private AgoraTokenGenerationService agoraTokenGenerationService;

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
        mockMvc.perform(MockMvcRequestBuilders.get("/generateAgoraToken/14"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    @WithCustomMockUser
    void successGenerateAgoraToken() throws Exception {
        Mockito.when(userServiceMock.findByEmailOrPhone(mockUser.getUsername()))
                .thenReturn(Optional.of(mockUser));
        Mockito.when(agoraTokenGenerationService.generateToken(14, mockUser.getId()))
                .thenReturn("dummyToken");
        mockMvc.perform(MockMvcRequestBuilders.get("/generateAgoraToken/14")
                        .principal(() -> mockUser.getUsername()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.is("dummyToken")));
    }

    @Test
    @WithCustomMockUser
    void failGenerateAgoraToken() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/generateAgoraToken/14"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithCustomMockUser
    void successGetAppId() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);
        String agoraAppId = System.getenv("AGORA_APP_ID");
        mockMvc.perform(MockMvcRequestBuilders.get("/getAppId"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.appId", Matchers.is(agoraAppId)));
    }

    @Test
    @WithCustomMockUser
    void failGetAppId() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/getAppId"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
