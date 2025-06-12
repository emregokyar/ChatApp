package com.chatapp.entity;

import com.chatapp.util.UserType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "registered_channels")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RegisteredChannel {
    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    private UserType type;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    @JsonBackReference
    private Channel channel;
}
