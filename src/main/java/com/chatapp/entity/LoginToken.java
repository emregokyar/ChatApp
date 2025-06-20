package com.chatapp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.ott.OneTimeToken;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "login_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginToken implements OneTimeToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date expirationDate;
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;

    @Override
    public String getTokenValue() {
        return token;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Instant getExpiresAt() {
        return expirationDate.toInstant();
    }
}
