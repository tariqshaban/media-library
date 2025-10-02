package com.multimedia.media_library.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileUserDetailsService implements UserDetailsService {
    private final Map<String, UserDetails> users = new HashMap<>();

    @PostConstruct
    public void loadUsers() throws IOException {
        ClassPathResource resource = new ClassPathResource("users.txt");
        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.lines()
                    .filter(line -> !line.trim().isEmpty() && !line.startsWith("#"))
                    .map(line -> line.split(":", 2))
                    .filter(parts -> parts.length == 2)
                    .forEach(parts -> {
                        UserDetails user = User.builder()
                                .username(parts[0])
                                .password(parts[1])
                                .roles("USER")
                                .build();
                        users.put(parts[0], user);
                    });
        }
    }

    public List<String> getUsers() {
        return new ArrayList<>(users.keySet());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}
