package com.chatapp.service_tests;

import com.chatapp.entity.Call;
import com.chatapp.entity.Channel;
import com.chatapp.entity.User;
import com.chatapp.service.CallService;
import com.chatapp.service.ChannelService;
import com.chatapp.service.SmsService;
import com.chatapp.service.UserService;
import com.chatapp.util.CallTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class CallServiceTestsH2DB {

    @MockitoBean
    private SmsService smsService;

    @Autowired
    private CallService callService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserService userService;

    @Test
    @Sql(scripts = "classpath:insert_dummy_data.sql")
    void successTestCreatingChannel() {
        Channel channel = channelService.getChannelById(1);
        Optional<User> user = userService.findByUserId(2);
        Call newCall = Call.builder()
                .type(CallTypes.VIDEO)
                .channel(channel)
                .caller(user.get())
                .build();
        Call savedCall = callService.saveCall(newCall);
        Assertions.assertNotNull(savedCall);
        Assertions.assertEquals(1, savedCall.getId());
    }
}
