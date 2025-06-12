package com.chatapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "channels")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Channel {
    @Id
    private Integer id;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date updatedAt;

    @OneToMany(targetEntity = RegisteredChannel.class, mappedBy = "channel", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RegisteredChannel> registeredChannels;

    @OneToMany(targetEntity = Message.class, mappedBy = "channel", cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JsonManagedReference
    private List<Message> messages;
}
