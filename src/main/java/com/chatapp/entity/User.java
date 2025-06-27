package com.chatapp.entity;

import com.chatapp.util.LoginOptions;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    @Enumerated(EnumType.STRING)
    private LoginOptions registrationType;
    private String firstname;
    private String lastname;
    private String profilePhoto;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date registrationDate;
    private Boolean isPrivate;
    private Boolean isActive;
    private String activationNumber;

    @OneToMany(targetEntity = LoginToken.class, mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<LoginToken> loginTokens;

    @OneToMany(targetEntity = RegisteredChannel.class, mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JsonManagedReference
    private List<RegisteredChannel> registeredChannels;

    @OneToMany(targetEntity = Contact.class, mappedBy = "contacter", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Contact> contactsAsContacter;

    @OneToMany(targetEntity = Contact.class, mappedBy = "contacting", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Contact> contactsAsContacting;

    @OneToMany(targetEntity = Message.class, mappedBy = "sender", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Message> messages;
}
