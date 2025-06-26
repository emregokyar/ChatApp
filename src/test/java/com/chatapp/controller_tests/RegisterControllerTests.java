package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.controller.RegisterController;
import com.chatapp.entity.User;
import com.chatapp.service.MailService;
import com.chatapp.service.SmsService;
import com.chatapp.service.UserService;
import com.chatapp.util.LoginOptions;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

@SpringBootTest(classes = {ChatAppApplication.class, RegisterController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Transactional
public class RegisterControllerTests {
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
    @WithMockUser
    void getSignUpPageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signUp"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"));
    }

    @Test
    @WithMockUser
    void registerTestWithEmail() throws Exception {
        String username = "emregokyar1940@gmail.com";
        LoginOptions loginOption = LoginOptions.EMAIL;

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setRegistrationType(loginOption);

        Assertions.assertEquals(userServiceMock.findByEmailOrPhone(username), Optional.empty());
        Mockito.when(userServiceMock.createNewUser(username, loginOption))
                .thenReturn(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .param("username", username)
                        .param("login_option", "EMAIL")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"));

        // Verify that a new user was created and mail was sent
        Mockito.verify(userServiceMock).createNewUser(username, loginOption);
        Mockito.verify(mailServiceMock).sendActivationNumber(username, newUser.getActivationNumber());
    }

    @Test
    @WithMockUser
    void registerTestWithPhone() throws Exception {
        String username = "+905523081940";
        LoginOptions loginOption = LoginOptions.PHONE;
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setRegistrationType(loginOption);

        Assertions.assertEquals(userServiceMock.findByEmailOrPhone(username), Optional.empty());
        Mockito.when(userServiceMock.createNewUser(username, loginOption))
                .thenReturn(newUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .param("username", username)
                        .param("login_option", "PHONE")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"));

        Mockito.verify(userServiceMock).createNewUser(username, loginOption);
        Mockito.verify(smsServiceMock).sendActivationSms(username, newUser.getActivationNumber());
    }

    @Test
    @WithMockUser
    void activationTest() throws Exception {
        Integer userId = 1;
        String username = "emregokyar1940@gmail.com";
        LoginOptions loginOption = LoginOptions.EMAIL;
        String activationNumber = "12345678";

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername(username);
        savedUser.setRegistrationType(loginOption);
        savedUser.setActivationNumber(activationNumber);
        savedUser.setIsActive(false);

        Mockito.when(userServiceMock.findByEmailOrPhone(username)).thenReturn(Optional.of(savedUser));
        Mockito.when(userServiceMock.activateUser(username, activationNumber)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/activate")
                        .param("activation_number", activationNumber)
                        .param("username", savedUser.getUsername())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));

        Mockito.verify(userServiceMock).activateUser(username, activationNumber);
    }

    @Test
    @WithMockUser
    void activationFailTest() throws Exception {
        Integer userId = 1;
        String username = "emregokyar1940@gmail.com";
        LoginOptions loginOption = LoginOptions.EMAIL;
        String activationNumber = "12345678";

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername(username);
        savedUser.setRegistrationType(loginOption);
        savedUser.setActivationNumber(activationNumber);
        savedUser.setIsActive(false);

        Mockito.when(userServiceMock.findByEmailOrPhone(username)).thenReturn(Optional.of(savedUser));
        Mockito.when(userServiceMock.activateUser(username, activationNumber)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/activate")
                        .param("activation_number", activationNumber)
                        .param("username", savedUser.getUsername())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/signUp"));
    }
}
