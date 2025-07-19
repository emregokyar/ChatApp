package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.UserController;
import com.chatapp.entity.Channel;
import com.chatapp.entity.Message;
import com.chatapp.entity.User;
import com.chatapp.service.ContactService;
import com.chatapp.service.SmsService;
import com.chatapp.service.UserService;
import com.chatapp.user_config.WithCustomMockUser;
import com.chatapp.util.ChannelType;
import com.chatapp.util.LoginOptions;
import com.chatapp.util.MessageType;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {ChatAppApplication.class, UserController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class UserControllerTests {

    @MockitoBean
    private UserService userServiceMock;
    @MockitoBean
    private ContactService contactServiceMock;
    @MockitoBean
    private SmsService smsServiceMock;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    private static User mockUser;
    private static Channel mockChannel;
    private static List<Message> mockMessages;
    private static User testUser;

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

        testUser = User.builder()
                .id(2)
                .username("test@gmail.com")
                .fullName("Test User")
                .registrationType(LoginOptions.EMAIL)
                .activationNumber(null)
                .profilePhoto("test.jpg")
                .about("WhatsApp Clone Tests")
                .isActive(true)
                .build();

        mockChannel = Channel.builder()
                .type(ChannelType.GROUP)
                .id(1)
                .groupPhoto("test.jpg")
                .messages(List.of())
                .subject("dummy sub")
                .updatedAt(Date.from(LocalDateTime.now().minusDays(2).atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        Message message1 = Message.builder()
                .channel(mockChannel)
                .content("test message 1")
                .createdAt(Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .id(1)
                .sender(mockUser)
                .type(MessageType.TEXT)
                .build();


        Message message2 = Message.builder()
                .channel(mockChannel)
                .content("test message 2")
                .createdAt(Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                .id(2)
                .sender(testUser)
                .type(MessageType.TEXT)
                .build();

        mockMessages = List.of(message1, message2);
    }

    @BeforeEach
    void beforeEachTest() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithCustomMockUser
    void successTestRetrievingUserInfo() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);
        Mockito.when(userServiceMock.findByUserId(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        Mockito.when(contactServiceMock.getContact(mockUser.getId(), testUser.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/getUserInfo/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profilePhotoPath", Matchers.is(testUser.getProfilePhotoPath())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(testUser.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname", Matchers.is(testUser.getUsername())));
    }

    @Test
    @WithCustomMockUser
    void failTestRetrievingUserInfo() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);
        Mockito.when(userServiceMock.findByUserId(testUser.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/getUserInfo/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("")); //Will Return null
    }

    @Test
    @WithCustomMockUser
    void successTestRetrievingUserArray() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);

        List<User> userInChannel = List.of(testUser);
        Mockito.when(userServiceMock.getUsersInChannel(mockUser.getId(), mockChannel.getId()))
                .thenReturn(Optional.of(userInChannel));

        mockMvc.perform(MockMvcRequestBuilders.get("/getUserArray/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]", Matchers.is(2)));
    }

    @Test
    @WithCustomMockUser
    void failTestRetrievingUserArray() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);

        Mockito.when(userServiceMock.getUsersInChannel(mockUser.getId(), mockChannel.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/getUserArray/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("")); //Will Return null
    }
}
