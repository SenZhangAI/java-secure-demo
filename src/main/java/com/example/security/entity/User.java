package com.example.security.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Column(name = "password_last_changed")
    private LocalDateTime passwordLastChanged;

    @Column(name = "password_expired")
    private boolean passwordExpired = false;

    @PrePersist
    protected void onCreate() {
        passwordLastChanged = LocalDateTime.now();
    }

    public boolean isPasswordExpired() {
        if (passwordLastChanged == null) {
            return true;
        }
        // 密码90天后过期
        return LocalDateTime.now().minusDays(90).isAfter(passwordLastChanged);
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.passwordLastChanged = LocalDateTime.now();
        this.passwordExpired = false;
    }

    public void checkPasswordExpiration() {
        this.passwordExpired = isPasswordExpired();
    }
}