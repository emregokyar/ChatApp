package com.chatapp.service_tests;


import com.chatapp.repository.LoginTokenRepository;
import com.chatapp.service.OttExtrasService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.function.Supplier;

@SpringBootTest(classes = OttExtrasService.class)
@TestPropertySource("/application-test.properties")
public class OttExtrasServiceTests {

    @MockitoBean
    private LoginTokenRepository tokenRepositoryMock;

    @Autowired
    private OttExtrasService ottExtrasService;

    @Test
    void testCreatingRandomPassword() {
        Supplier<String> randomPass = ottExtrasService.createRandomOneTimePassword();
        Assertions.assertNotNull(randomPass);
        Assertions.assertEquals(8, randomPass.get().length());
    }
}
