package com.chatapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
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
@Builder
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date updatedAt;

    private String groupPhoto;

    private String subject;

    @OneToMany(targetEntity = RegisteredChannel.class, mappedBy = "channel", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RegisteredChannel> registeredChannels;

    @OneToMany(targetEntity = Message.class, mappedBy = "channel", cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JsonManagedReference
    private List<Message> messages;

    @Transactional
    public String getGroupPhotoPath() {
        if (groupPhoto == null) return null;
        return "/photos/channels/" + id + "/channel_photo/" + groupPhoto;
    }
}
