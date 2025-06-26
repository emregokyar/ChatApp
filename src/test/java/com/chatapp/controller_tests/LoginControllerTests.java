package com.chatapp.controller_tests;

import com.chatapp.ChatAppApplication;
import com.chatapp.config.CustomOttGenerationSuccessHandler;
import com.chatapp.controller.LoginController;
import com.chatapp.entity.LoginToken;
import com.chatapp.entity.User;
import com.chatapp.repository.UserRepository;
import com.chatapp.service.LoginTokenService;
import com.chatapp.service.MailService;
import com.chatapp.service.SmsService;
import com.chatapp.service.UserService;
import com.chatapp.util.LoginOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.Optional;

@SpringBootTest(classes = {ChatAppApplication.class, LoginController.class})
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class LoginControllerTests {
    //might not use it
    @MockitoBean
    private UserService userServiceMock;

    @MockitoBean
    private MailService mailServiceMock;

    @MockitoBean
    private SmsService smsServiceMock;

    @MockitoBean
    private LoginTokenService loginTokenServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustomOttGenerationSuccessHandler customOttGenerationSuccessHandler;

    //Check here

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${sql.script.create.user}")
    private String sqlAddUser;

    @Value("${sql.script.delete.user}")
    private String sqlDeleteUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        jdbcTemplate.execute(sqlAddUser);
    }

    @AfterEach
    void cleanUp() {

        jdbcTemplate.execute(sqlDeleteUser);
    }

    @Test
    @WithMockUser
    void getLoginPageTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"));
    }

    //I am getting null pointer exception that says my ott is null
    @Test
    @WithMockUser
    void createOttTest() throws Exception {
        Integer userId = 2;
        String username = "mail@gmail.com";
        LoginOptions loginOption = LoginOptions.EMAIL;

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername(username);
        savedUser.setRegistrationType(loginOption);
        savedUser.setIsActive(true);

        LoginToken newToken = new LoginToken(1, new Date(System.currentTimeMillis() + ((long) 5L * 60 * 1000)), "12345678", savedUser); //This is my custom ott that is connected with db
        savedUser.setLoginTokens(newToken.getUser().getLoginTokens());

        Mockito.when(userServiceMock.findByEmailOrPhone(username)).thenReturn(Optional.of(savedUser));

        mockMvc.perform(MockMvcRequestBuilders.post("/create-ott") //this is a custom one time token generation endpoint
                        .param("username", "mail@gmail.com"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"));
    }

    //I have a redirection success handler if a user is authenticated, but it doesn't return redirection
    @Test
    @WithMockUser
    void loginAccountTest() throws Exception {
        Integer userId = 2;
        String username = "mail@gmail.com";
        LoginOptions loginOption = LoginOptions.EMAIL;
        String activationNumber = "12345678";

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername(username);
        savedUser.setRegistrationType(loginOption);
        savedUser.setActivationNumber(activationNumber);
        savedUser.setIsActive(true);

        LoginToken newToken = new LoginToken(1, new Date(System.currentTimeMillis() + ((long) 5L * 60 * 1000)), "12345678", savedUser);
        savedUser.setLoginTokens(newToken.getUser().getLoginTokens());

        Mockito.when(userServiceMock.findByEmailOrPhone(username)).thenReturn(Optional.of(savedUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/login/ott") //this is one time token login endpoint
                        .param("token", "12345678"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/home"));
    }

    //Check this !!
    @Test
    @WithMockUser
    void loginAccountFailTest() throws Exception {
        Integer userId = 1;
        String username = "mail@gmail.com";
        LoginOptions loginOption = LoginOptions.EMAIL;
        String activationNumber = "12345678";

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername(username);
        savedUser.setRegistrationType(loginOption);
        savedUser.setActivationNumber(activationNumber);
        savedUser.setIsActive(true);

        LoginToken newToken = new LoginToken(1, new Date(System.currentTimeMillis() + ((long) 5L * 60 * 1000)), "12345678", savedUser);
        savedUser.setLoginTokens(newToken.getUser().getLoginTokens());

        Mockito.when(userServiceMock.findByEmailOrPhone(username)).thenReturn(Optional.of(savedUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/login/ott")
                        .param("token", "324324432"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.redirectedUrl(null));
    }
}
