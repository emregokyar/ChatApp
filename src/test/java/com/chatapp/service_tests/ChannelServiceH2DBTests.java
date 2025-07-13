package com.chatapp.service_tests;


import com.chatapp.entity.Channel;
import com.chatapp.repository.ChannelRepository;
import com.chatapp.service.ChannelService;
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

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChannelServiceH2DBTests {
    @MockitoBean
    private SmsService smsService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    @Sql(scripts = "classpath:delete_dummy_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void successTestRetrievingChannelById() {
        Channel channelById = channelService.getChannelById(1);
        Assertions.assertNotNull(channelById);
        Assertions.assertEquals("Weekend Plans", channelById.getSubject());
    }
}
