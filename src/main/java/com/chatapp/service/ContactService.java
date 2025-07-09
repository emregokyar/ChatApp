package com.chatapp.service;

import com.chatapp.entity.Contact;
import com.chatapp.entity.User;
import com.chatapp.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Optional<List<Contact>> getAllContacts(Integer userId) {
        return contactRepository.findAllSavedContacts(userId);
    }

    public Optional<Contact> getContact(Integer userId, Integer contactId) {
        return contactRepository.findContactIfExists(userId, contactId);
    }

    public Contact createNewContact(User currentUser, User contactedUser, String name) {
        Contact contact = Contact.builder()
                .nickname(name)
                .contacter(currentUser)
                .contacting(contactedUser)
                .build();

        return contactRepository.save(contact);
    }
}
