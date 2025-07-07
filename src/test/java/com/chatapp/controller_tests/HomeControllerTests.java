package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.HomeController;
import com.chatapp.entity.User;
import com.chatapp.service.RegisteredChannelService;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

@SpringBootTest(classes = {ChatAppApplication.class, HomeController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class HomeControllerTests {
    @MockitoBean
    private UserService userServiceMock;

    @MockitoBean
    private RegisteredChannelService registeredChannelServiceMock;

    @MockitoBean
    private SmsService smsService;

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
    @WithCustomMockUser
    void testForGetHomePage() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser())
                .thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("home"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentUser"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentUser", mockUser));
    }

    @Test
    @WithCustomMockUser
    void testForSuccessUpdatingFullName() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(userServiceMock.updateUser(mockUser)).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/updateFullName")
                        .param("fullName", "Fahrettin Emre Gokyar")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("emregokyar1940@gmail.com")));
    }

    @Test
    @WithCustomMockUser
    void testForFailUpdatingFullName() throws Exception {
        User invalidUser = User.builder()
                .username("invalidUsername")
                .build();

        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/updateFullName")
                        .param("fullName", "Fahrettin Emre Gokyar")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithCustomMockUser
    void testForSuccessUpdatingAbout() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(userServiceMock.updateUser(mockUser)).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/updateAbout")
                        .param("about", "Working on a test project.")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("emregokyar1940@gmail.com")));
    }

    @Test
    @WithCustomMockUser
    void testForFailUpdatingAbout() throws Exception {
        User invalidUser = User.builder()
                .username("invalidUsername")
                .build();

        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(invalidUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/updateAbout")
                        .param("about", "Working on a test project.")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithCustomMockUser
    void successTestForUpdatingUserPhoto() throws Exception {
        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(userServiceMock.updateUser(mockUser)).thenReturn(mockUser);

        ClassPathResource imageResource = new ClassPathResource("/static/assets/test.jpg");
        InputStream inputStream = imageResource.getInputStream();
        MockMultipartFile photo = new MockMultipartFile(
                "profilePhoto",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                inputStream
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/updateProfilePhoto")
                        .file(photo)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithCustomMockUser
    void failTestForUpdatingUserPhoto() throws Exception {
        User invalidUser = User.builder()
                .username("invalidUsername")
                .build();

        Mockito.when(userServiceMock.getCurrentUser()).thenReturn(invalidUser);

        ClassPathResource imageResource = new ClassPathResource("/static/assets/test.jpg");
        InputStream inputStream = imageResource.getInputStream();
        MockMultipartFile photo = new MockMultipartFile(
                "profilePhoto",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                inputStream
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/updateProfilePhoto")
                        .file(photo)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
