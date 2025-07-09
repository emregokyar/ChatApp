package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.ContactAndGroupController;
import com.chatapp.entity.Channel;
import com.chatapp.entity.GroupRole;
import com.chatapp.entity.RegisteredChannel;
import com.chatapp.entity.User;
import com.chatapp.service.*;
import com.chatapp.user_config.WithCustomMockUser;
import com.chatapp.util.LoginOptions;
import com.chatapp.util.Roles;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
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
import java.util.Arrays;
import java.util.Optional;

@SpringBootTest(classes = {ChatAppApplication.class, ContactAndGroupController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class ContactAndGroupControllerTests {
    @MockitoBean
    private UserService userServiceMock;

    @MockitoBean
    private ContactService contactServiceMock;

    @MockitoBean
    private ChannelService channelServiceMock;

    @MockitoBean
    private RegisteredChannelService registeredChannelServiceMock;

    @MockitoBean
    private GroupRoleService groupRoleServiceMock;

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
    void testForAnonymousUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/addNewContact"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }


    @Test
    @WithCustomMockUser
    void successTestAddNewContact() throws Exception {
        String nickname = "My Contact";
        User dummyUser = User.builder()
                .id(2)
                .username("dummy@gmail.com")
                .fullName("dummy user")
                .registrationType(LoginOptions.EMAIL)
                .activationNumber(null)
                .profilePhoto("test.jpg")
                .about("WhatsApp Clone Tests User Two")
                .isActive(true)
                .build();

        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);
        Mockito.when(userServiceMock.findByEmailOrPhone("dummy@gmail.com"))
                .thenReturn(Optional.of(dummyUser));
        Mockito.when(contactServiceMock.getContact(mockUser.getId(), dummyUser.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/addNewContact")
                        .param("nickname", nickname)
                        .param("username", dummyUser.getUsername()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/home"));

        Mockito.verify(contactServiceMock)
                .createNewContact(mockUser, dummyUser, nickname);
    }

    @Test
    @WithCustomMockUser
    void failTestAddNewContact() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);
        Mockito.when(userServiceMock.findByEmailOrPhone("dummy@gmail.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/addNewContact")
                        .param("nickname", "")
                        .param("username", ""))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/home"));
    }

    @Test
    @WithCustomMockUser
    void successTestCreateGroup() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);

        Channel savedChannel = Channel.builder()
                .id(2)
                .groupPhoto("test.jpg")
                .subject("dummy subject")
                .build();
        Mockito.when(channelServiceMock.createChannel(Mockito.any(Channel.class)))
                .thenReturn(savedChannel);

        GroupRole admin = GroupRole.builder()
                .id(1)
                .role(Roles.ADMIN)
                .build();
        Mockito.when(groupRoleServiceMock.getAdminRole())
                .thenReturn(admin);

        RegisteredChannel currentUserRegister = RegisteredChannel.builder()
                .channel(savedChannel)
                .role(admin)
                .user(mockUser)
                .build();
        Mockito.when(registeredChannelServiceMock.register(currentUserRegister))
                .thenReturn(currentUserRegister);

        User dummyUser = User.builder()
                .id(2)
                .username("dummy@gmail.com")
                .fullName("dummy user")
                .registrationType(LoginOptions.EMAIL)
                .activationNumber(null)
                .profilePhoto("test.jpg")
                .about("WhatsApp Clone Tests User Two")
                .isActive(true)
                .build();
        Mockito.when(userServiceMock.findByUserId(2))
                .thenReturn(Optional.of(dummyUser));

        GroupRole regular = GroupRole.builder()
                .id(2)
                .role(Roles.REGULAR)
                .build();
        Mockito.when(groupRoleServiceMock.getRegularRole())
                .thenReturn(regular);

        RegisteredChannel registry = RegisteredChannel.builder()
                .channel(savedChannel)
                .user(dummyUser)
                .role(regular)
                .build();
        Mockito.when(registeredChannelServiceMock.register(registry))
                .thenReturn(currentUserRegister);

        ClassPathResource imageResource = new ClassPathResource("/static/assets/test.jpg");
        InputStream inputStream = imageResource.getInputStream();
        MockMultipartFile photo = new MockMultipartFile(
                "groupPhoto",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                inputStream
        );

        int[] userIds = new int[]{2};
        mockMvc.perform(MockMvcRequestBuilders.multipart("/createGroup")
                        .file(photo)
                        .param("userIds", Arrays.stream(userIds).mapToObj(String::valueOf).toArray(String[]::new))
                        .param("subject", "dummy subject")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/home"));

        Mockito.verify(channelServiceMock).createChannel(Mockito.any(Channel.class));
        Mockito.verify(userServiceMock).findByUserId(2);
    }
}
