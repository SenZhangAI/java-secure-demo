package com.example.security.scheduler;

import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PasswordExpirationScheduler {

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨执行
    @Transactional
    public void checkPasswordExpiration() {
        userRepository.findAll().forEach(user -> {
            user.checkPasswordExpiration();
            if (user.isPasswordExpired()) {
                userRepository.save(user);
            }
        });
    }
}