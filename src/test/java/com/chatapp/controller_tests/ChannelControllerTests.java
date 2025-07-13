package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.ChannelController;
import com.chatapp.entity.*;
import com.chatapp.service.*;
import com.chatapp.user_config.WithCustomMockUser;
import com.chatapp.util.ChannelType;
import com.chatapp.util.LoginOptions;
import com.chatapp.util.Roles;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {ChatAppApplication.class, ChannelController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class ChannelControllerTests {
    @MockitoBean
    private UserService userServiceMock;

    @MockitoBean
    private RegisteredChannelService registeredChannelServiceMock;

    @MockitoBean
    private ContactService contactServiceMock;

    @MockitoBean
    private ChannelService channelServiceMock;

    @MockitoBean
    private SmsService smsServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private static User mockUser;

    private static Channel mockChannel;

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

        mockChannel = Channel.builder()
                .type(ChannelType.GROUP)
                .id(1)
                .groupPhoto("test.jpg")
                .messages(List.of())
                .subject("dummy sub")
                .updatedAt(new Date(System.currentTimeMillis()))
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
        mockMvc.perform(MockMvcRequestBuilders.get("/getChannels"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    @WithCustomMockUser
    void successTestRetrievingChannels() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(mockUser);

        User testUser1 = User.builder().username("user1").id(2).build();
        User testUser2 = User.builder().username("user2").id(2).build();
        Contact contact1 = Contact.builder().contacter(mockUser).contacting(testUser1).id(1).nickname("dummy-name").build();

        RegisteredChannel registeredChannel1 = RegisteredChannel.builder()
                .id(1)
                .channel(mockChannel)
                .role(GroupRole.builder()
                        .id(1)
                        .role(Roles.ADMIN)
                        .build())
                .user(mockUser)
                .build();

        Mockito.when(registeredChannelServiceMock.getRegisteredChannelsByIdDesc(mockUser))
                .thenReturn(Optional.of(List.of(registeredChannel1)));

        Mockito.when(userServiceMock.getChannelGroupMembers(mockUser.getId(), mockChannel.getId()))
                .thenReturn(Optional.of(List.of(testUser1, testUser2)));

        Mockito.when(contactServiceMock.getContact(mockUser.getId(), testUser1.getId()))
                .thenReturn(Optional.of(contact1));

        Mockito.when(contactServiceMock.getContact(mockUser.getId(), testUser2.getId()))
                .thenReturn(Optional.empty());

        Mockito.when(channelServiceMock.getLastUpdatedAsString(mockChannel))
                .thenReturn(new Date(System.currentTimeMillis()).toString());

        mockMvc.perform(MockMvcRequestBuilders.get("/getChannels"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].channelId", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    @WithCustomMockUser
    void failTestRetrievingChannels() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/getChannels"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithCustomMockUser
    void successTestRetrievingSingleChannel() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(channelServiceMock.getChannelById(1)).thenReturn(mockChannel);

        User testUser1 = User.builder().username("user1").id(2).build();
        User testUser2 = User.builder().username("user2").id(2).build();
        Contact contact1 = Contact.builder().contacter(mockUser).contacting(testUser1).id(1).nickname("dummy-name").build();

        Mockito.when(userServiceMock.getChannelGroupMembers(mockUser.getId(), mockChannel.getId()))
                .thenReturn(Optional.of(List.of(testUser1, testUser2)));

        Mockito.when(contactServiceMock.getContact(mockUser.getId(), testUser1.getId()))
                .thenReturn(Optional.of(contact1));

        Mockito.when(contactServiceMock.getContact(mockUser.getId(), testUser2.getId()))
                .thenReturn(Optional.empty());

        Mockito.when(channelServiceMock.getLastUpdatedAsString(mockChannel))
                .thenReturn(new Date(System.currentTimeMillis()).toString());

        mockMvc.perform(MockMvcRequestBuilders.get("/getChannelInfo/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.channelId", Matchers.is(1)));
    }

    @Test
    @WithCustomMockUser
    void failTestRetrievingSingleChannel() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/getChannelInfo/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

