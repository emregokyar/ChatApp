package com.chatapp.service_tests;

import com.chatapp.entity.Channel;
import com.chatapp.repository.ChannelRepository;
import com.chatapp.service.ChannelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = ChannelService.class)
@TestPropertySource("/application-test.properties")
public class ChannelServiceTests {

    @Autowired
    private ChannelService channelService;

    @MockitoBean
    private ChannelRepository channelRepositoryMock;

    @Test
    void testCreatingChannel() {
        Channel channel = Channel.builder()
                .id(1)
                .subject("dummy subject")
                .groupPhoto("test.jpg")
                .build();

        Mockito.when(channelRepositoryMock.save(ArgumentMatchers.any(Channel.class)))
                .thenReturn(channel);
        Channel result = channelService.createChannel(channel);
        Assertions.assertEquals(channel, result);
    }
}
