package com.chatapp.service_tests;

import com.chatapp.entity.RegisteredChannel;
import com.chatapp.entity.User;
import com.chatapp.repository.RegisteredChannelRepository;
import com.chatapp.service.RegisteredChannelService;
import com.chatapp.util.LoginOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = RegisteredChannelService.class)
@TestPropertySource("/application-test.properties")
public class RegisteredChannelServiceTests {
    @MockitoBean
    private RegisteredChannelRepository registeredChannelRepositoryMock;

    @Autowired
    private RegisteredChannelService registeredChannelService;

    @Test
    void testGettingRegisteredChannels() {
        User user = User.builder()
                .id(1)
                .username("test@gmail.com")
                .isActive(true)
                .isPrivate(false)
                .registrationType(LoginOptions.EMAIL)
                .build();

        RegisteredChannel registeredChannel1 = new RegisteredChannel();
        RegisteredChannel registeredChannel2 = new RegisteredChannel();
        RegisteredChannel registeredChannel3 = new RegisteredChannel();
        List<RegisteredChannel> registeredChannels = List.of(registeredChannel1, registeredChannel2, registeredChannel3);

        user.setRegisteredChannels(registeredChannels);
        Mockito.when(registeredChannelRepositoryMock.getRegisteredChannelsOrderByUpdateDateDesc(user.getId()))
                .thenReturn(Optional.of(registeredChannels));
        Assertions.assertNotNull(registeredChannelService.getRegisteredChannelsByIdDesc(user));
        Assertions.assertEquals(registeredChannelService.getRegisteredChannelsByIdDesc(user).get(), registeredChannels);
    }
}
