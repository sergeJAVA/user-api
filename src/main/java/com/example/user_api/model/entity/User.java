package com.example.user_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "roles")
    private Set<String> roles;


}
