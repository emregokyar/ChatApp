package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.ChannelController;
import com.chatapp.controller.ChatController;
import com.chatapp.dto.MessageDto;
import com.chatapp.entity.Channel;
import com.chatapp.entity.Contact;
import com.chatapp.entity.Message;
import com.chatapp.entity.User;
import com.chatapp.service.*;
import com.chatapp.user_config.WithCustomMockUser;
import com.chatapp.util.ChannelType;
import com.chatapp.util.LoginOptions;
import com.chatapp.util.MessageType;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.security.Principal;
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
    private SimpMessagingTemplate messagingTemplateMock;
    @MockitoBean
    private ChannelService channelServiceMock;
    @MockitoBean
    private SmsService smsServiceMock;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ChatController chatController;

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

    @Test
    @WithCustomMockUser
    void successTestSaveFile() throws Exception {
        Message savedMessage = Message.builder()
                .channel(mockChannel)
                .content("test.png")
                .type(MessageType.PHOTO)
                .sender(mockUser)
                .createdAt(new Date(System.currentTimeMillis()))
                .id(4)
                .build();

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);
        Mockito.when(channelServiceMock.getChannelById(mockChannel.getId()))
                .thenReturn(mockChannel);
        Mockito.when(messageServiceMock.findById(savedMessage.getId()))
                .thenReturn(Optional.of(savedMessage));
        Mockito.when(messageServiceMock.getMessageDateAsString(savedMessage))
                .thenReturn("Just now");

        ClassPathResource imageResource = new ClassPathResource("/static/assets/test.jpg");
        InputStream inputStream = imageResource.getInputStream();
        MockMultipartFile photo = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                inputStream
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/saveFile")
                        .file(photo)
                        .param("channelId", mockChannel.getId().toString())
                        .param("messageId", savedMessage.getId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(savedMessage.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.channelId", Matchers.is(savedMessage.getChannel().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.messageType", Matchers.is(savedMessage.getType().toString())));
    }

    @Test
    @WithCustomMockUser
    void failTestSaveFile() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);
        Mockito.when(channelServiceMock.getChannelById(mockChannel.getId()))
                .thenReturn(mockChannel);
        Mockito.when(messageServiceMock.findById(4))
                .thenReturn(Optional.empty());

        ClassPathResource imageResource = new ClassPathResource("/static/assets/test.jpg");
        InputStream inputStream = imageResource.getInputStream();
        MockMultipartFile photo = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                inputStream
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/saveFile")
                        .file(photo)
                        .param("channelId", mockChannel.getId().toString())
                        .param("messageId", "4")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithCustomMockUser
    void successTestSendMessage() throws Exception {
        MessageDto messageDto = MessageDto.builder()
                .channelId(1)
                .content("Hello")
                .messageType("TEXT")
                .receivers(new Integer[]{4, 5})
                .build();

        Message savedMessage = Message.builder()
                .id(100)
                .content("Hello")
                .sender(mockUser)
                .channel(mockChannel)
                .createdAt(new Date())
                .build();

        Mockito.when(userServiceMock.findByEmailOrPhone("emregokyar1940@gmail.com"))
                .thenReturn(Optional.of(mockUser));
        Mockito.when(channelServiceMock.getChannelById(mockChannel.getId()))
                .thenReturn(mockChannel);
        Mockito.when(messageServiceMock.createNewMessage(Mockito.any()))
                .thenReturn(savedMessage);
        Mockito.when(contactServiceMock.getContact(4, mockUser.getId()))
                .thenReturn(Optional.empty());
        Mockito.when(contactServiceMock.getContact(5, mockUser.getId()))
                .thenReturn(Optional.empty());
        Mockito.when(messageServiceMock.getMessageDateAsString(savedMessage))
                .thenReturn("Just now");

        Principal mockPrincipal = () -> "emregokyar1940@gmail.com";
        ResponseEntity<Boolean> response = chatController.sendMessage(messageDto, mockPrincipal);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody());
        Mockito.verify(messagingTemplateMock).convertAndSendToUser(
                ArgumentMatchers.eq("4"), ArgumentMatchers.eq("/messages"), Mockito.any(MessageDto.class));
    }

    @Test
    @WithCustomMockUser
    void failTestSendMessage() throws Exception {
        MessageDto messageDto = MessageDto.builder()
                .channelId(1)
                .content("Hello")
                .messageType("TEXT")
                .receivers(new Integer[]{4, 5})
                .build();

        Mockito.when(userServiceMock.findByEmailOrPhone("emregokyar1940@gmail.com"))
                .thenReturn(Optional.of(mockUser));
        Mockito.when(channelServiceMock.getChannelById(5))
                .thenReturn(null);

        Principal mockPrincipal = () -> "emregokyar1940@gmail.com";
        ResponseEntity<Boolean> response = chatController.sendMessage(messageDto, mockPrincipal);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
