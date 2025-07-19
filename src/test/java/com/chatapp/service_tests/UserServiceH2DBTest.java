package com.chatapp.service_tests;

import com.chatapp.entity.User;
import com.chatapp.repository.UserRepository;
import com.chatapp.service.SmsService;
import com.chatapp.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//Prevent Spring from replacing your DB config with H2 during tests.

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//Allows using non-static @BeforeAll and reusing the same test class instance.
@Transactional
public class UserServiceH2DBTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private SmsService smsService;

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    @Sql(scripts = "classpath:delete_dummy_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void successTestRetrievingGroupMembers() {
        Optional<List<User>> channelGroupMembers = userService.getChannelGroupMembers(2, 1);
        Assertions.assertTrue(channelGroupMembers.isPresent());
        Assertions.assertFalse(channelGroupMembers.get().isEmpty());
        Assertions.assertEquals(1, channelGroupMembers.get().size());
    }

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    @Sql(scripts = "classpath:delete_dummy_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void failTestRetrievingGroupMembers() {
        Optional<List<User>> channelGroupMembers = userService.getChannelGroupMembers(45, 43);
        Assertions.assertEquals(0, channelGroupMembers.get().size());
    }

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    @Sql(scripts = "classpath:delete_dummy_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void successTestRetrievingChannelUser() {
        Optional<User> channelUser = userService.getChannelUser(2, 2);
        Assertions.assertTrue(channelUser.isPresent());
        Assertions.assertEquals(4, channelUser.get().getId());
    }

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    @Sql(scripts = "classpath:delete_dummy_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void failTestRetrievingChannelUser() {
        Optional<User> channelUser = userService.getChannelUser(2, 1);
        Assertions.assertTrue(channelUser.isEmpty());
    }

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    @Sql(scripts = "classpath:delete_dummy_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void successTestRetrievingUsersInChannel() {
        Optional<List<User>> usersInChannel = userService.getUsersInChannel(2, 1);
        Assertions.assertTrue(usersInChannel.isPresent());
        Assertions.assertEquals(1, usersInChannel.get().size());
    }
}
