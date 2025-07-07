package com.chatapp.entity;

import com.chatapp.util.LoginOptions;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
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
    private String fullName;
    private String about;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date registrationDate;
    private Boolean isPrivate;
    private Boolean isActive;
    private String activationNumber;
    private String profilePhoto;

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

    @Transactional
    public String getProfilePhotoPath() {
        if (profilePhoto == null) return null;
        return "/photos/users/" + id + "/profile_photos/" + profilePhoto;
    }
}
