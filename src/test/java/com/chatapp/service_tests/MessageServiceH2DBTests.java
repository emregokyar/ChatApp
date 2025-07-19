package com.chatapp.service_tests;

import com.chatapp.entity.Message;
import com.chatapp.repository.MessageRepository;
import com.chatapp.service.MessageService;
import com.chatapp.service.SmsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MessageServiceH2DBTests {
    @MockitoBean
    private SmsService smsService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    @Sql(scripts = "classpath:delete_dummy_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void successTestRetrievingMessagesByChannelId() {
        Optional<List<Message>> messages = messageService.getMessages(1);
        Assertions.assertTrue(messages.isPresent());
        Assertions.assertEquals(3, messages.get().size());
    }

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    @Sql(scripts = "classpath:delete_dummy_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void failTestRetrievingMessagesByChannelId() {
        Optional<List<Message>> messages = messageService.getMessages(3);
        Assertions.assertTrue(messages.isPresent());
        Assertions.assertEquals(0, messages.get().size());
    }

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    @Sql(scripts = "classpath:delete_dummy_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void successTestFindLastMessageSentChannelByUserID() {
        Optional<Message> message = messageService.findLastMessageSentChannelByUserID(2, 4);
        Assertions.assertTrue(message.isPresent());
        Assertions.assertEquals(5, message.get().getId());
    }
}
