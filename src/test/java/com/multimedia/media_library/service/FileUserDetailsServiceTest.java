package com.multimedia.media_library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileUserDetailsServiceTest {
    FileUserDetailsService fileUserDetailsService;

    @BeforeEach
    void setUp() throws IOException {
        Resource resource = new ClassPathResource("users.txt");
        fileUserDetailsService = new FileUserDetailsService(resource);
        fileUserDetailsService.loadUsers();
    }

    @Test
    void givenUsersResource_whenGettingUsers_thenReturnUsers() {
        List<String> users = fileUserDetailsService.getUsers();

        assertThat(users).containsExactly("user1", "user2");
    }

    @Test
    void givenUsersResource_whenGettingNonExistingUser_thenThrowUsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class,
                () -> fileUserDetailsService.loadUserByUsername("NON_EXISTING_USER")
        );
    }

    @Test
    void givenUsersResource_whenGettingExistingUser_thenReturnUserDetails() {
        UserDetails userDetails = fileUserDetailsService.loadUserByUsername("user1");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("user1");
        assertThat(userDetails.getAuthorities())
                .map(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
        assertThat(userDetails.getPassword()).isNotNull().startsWith("$2a$");
    }
}
