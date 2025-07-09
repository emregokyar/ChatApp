package com.chatapp.service_tests;

import com.chatapp.entity.Contact;
import com.chatapp.entity.User;
import com.chatapp.repository.ContactRepository;
import com.chatapp.service.ContactService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = ContactService.class)
@TestPropertySource("/application-test.properties")
public class ContactServiceTests {
    @Autowired
    private ContactService contactService;

    @MockitoBean
    private ContactRepository contactRepositoryMock;

    @Test
    void testCreateNewContact() {
        User contacter = User.builder()
                .id(1)
                .username("user1")
                .build();

        User contacting = User.builder()
                .id(2)
                .username("user2")
                .build();

        Contact contact = Contact.builder()
                .id(1)
                .nickname("dummy name")
                .contacter(contacter)
                .contacting(contacting)
                .build();

        Mockito.when(contactRepositoryMock.save(ArgumentMatchers.any(Contact.class)))
                .thenReturn(contact);
        Contact result = contactService.createNewContact(contacter, contacting, "dummy name");
        Assertions.assertEquals(contact, result);
    }
}
