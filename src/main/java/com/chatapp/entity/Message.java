package com.chatapp.entity;

import com.chatapp.util.MessageType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "messages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @JsonBackReference
    private User sender;

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    @JsonBackReference
    private Channel channel;
}
