package com.chatapp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contacts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "contacter_id", referencedColumnName = "id")
    @JsonBackReference
    private User contacter;

    @ManyToOne
    @JoinColumn(name = "contacting_id", referencedColumnName = "id")
    @JsonBackReference
    private User contacting;

    private String nickname;
}
