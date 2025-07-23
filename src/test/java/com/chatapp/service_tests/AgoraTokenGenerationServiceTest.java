package com.chatapp.service_tests;

import com.chatapp.service.AgoraTokenGenerationService;
import com.chatapp.service.SmsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class AgoraTokenGenerationServiceTest {
    @MockitoBean
    private SmsService smsService;

    @Autowired
    private AgoraTokenGenerationService agoraTokenGenerationService;

    @Test
    void successTestCreatingChannel() {
        String result = agoraTokenGenerationService.generateToken(14, 10);
        Assertions.assertNotNull(result);
    }

    @Test
    void failTestCreatingChannel() {
        String result = agoraTokenGenerationService.generateToken(14, -10);
        Assertions.assertNull(result);
    }
}
