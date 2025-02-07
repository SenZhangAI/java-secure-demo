package com.example.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SessionManagementService {

    @Autowired
    private SessionRegistry sessionRegistry;

    public void expireUserSessions(String username) {
        List<Object> principals = sessionRegistry.getAllPrincipals();
        principals.stream()
                .filter(principal -> principal.toString().equals(username))
                .forEach(principal -> {
                    sessionRegistry.getAllSessions(principal, false)
                            .forEach(session -> session.expireNow());
                });
    }

    public boolean isUserLoggedIn(String username) {
        return sessionRegistry.getAllPrincipals().stream()
                .anyMatch(principal -> principal.toString().equals(username));
    }

    public int getActiveSessionCount(String username) {
        return sessionRegistry.getAllSessions(username, false).size();
    }
}