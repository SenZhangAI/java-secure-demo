package com.example.security.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import com.example.security.annotation.Sensitive;
import com.example.security.annotation.SensitiveStrategy;
import com.example.security.converter.EncryptedAttributeConverter;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Sensitive(strategy = SensitiveStrategy.USERNAME)
    private String username;

    @Column(unique = true, nullable = false)
    @Convert(converter = EncryptedAttributeConverter.class)
    @Sensitive(strategy = SensitiveStrategy.EMAIL)
    private String email;

    @Column(name = "phone", nullable = true)
    @Convert(converter = EncryptedAttributeConverter.class)
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phone;

    @Column(name = "id_card", nullable = true)
    @Convert(converter = EncryptedAttributeConverter.class)
    @Sensitive(strategy = SensitiveStrategy.ID_CARD)
    private String idCard;

    @Column(nullable = false)
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