package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.ChannelController;
import com.chatapp.entity.Channel;
import com.chatapp.entity.Contact;
import com.chatapp.entity.Message;
import com.chatapp.entity.User;
import com.chatapp.service.ContactService;
import com.chatapp.service.MessageService;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
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

@SpringBootTest(classes = {ChatAppApplication.class, ChannelController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class ChatControllerTests {
    @MockitoBean
    private UserService userServiceMock;
    @MockitoBean
    private MessageService messageServiceMock;
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
    @WithAnonymousUser
    void anonymousUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/getMessages/1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    @WithCustomMockUser
    void successRetrievingMessages() throws Exception {
        Contact contact = Contact.builder()
                .nickname("dummy name")
                .contacter(mockUser)
                .contacting(testUser)
                .id(1)
                .build();

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);
        Mockito.when(messageServiceMock.getMessages(mockChannel.getId()))
                .thenReturn(Optional.of(mockMessages));
        Mockito.when(contactServiceMock.getContact(mockUser.getId(), testUser.getId()))
                .thenReturn(Optional.of(contact));

        mockMvc.perform(MockMvcRequestBuilders.get("/getMessages/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].userDto.nickname", Matchers.is("You")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].userDto.nickname", Matchers.is("dummy name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    @WithCustomMockUser
    void failTestRetrievingSingleChannel() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/getMessages/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
