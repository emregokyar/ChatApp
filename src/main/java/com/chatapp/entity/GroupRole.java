package com.chatapp.entity;

import com.chatapp.util.Roles;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "group_roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class GroupRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Roles role;

    @OneToMany(targetEntity = RegisteredChannel.class, mappedBy = "role", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REFRESH})
    @JsonManagedReference
    private List<RegisteredChannel> registeredChannels;
}
