package com.kursovaya.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "users")
public class UserEntity {
    @Id
    private Long id;
    private String username;
    private String email;
    private String password;
    private UserRole role;
    private String firstName;
    private String lastName;
    private String activationCode;
    private boolean enabled;

    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ToString.Include(name = "password")
    private String maskPassword() {
        return "********";
    }

}
