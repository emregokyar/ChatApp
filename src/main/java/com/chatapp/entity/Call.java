package com.chatapp.entity;

import com.chatapp.util.CallTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "calls")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private CallTypes type;

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    @JsonBackReference
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "caller_id", referencedColumnName = "id")
    @JsonBackReference
    private User caller;
}
