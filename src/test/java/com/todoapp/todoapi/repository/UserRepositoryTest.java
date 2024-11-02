package com.todoapp.todoapi.repository;

import com.todoapp.todoapi.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void itShouldReturnUserEntityWhenUserIfExists() {
        // Arrange: Create and save a user with a specific email
        String email = "testuser@example.com";
        UserEntity user = UserEntity.builder()
                .email(email)
                .password("password123")
                .build();
        userRepository.save(user);

        // Act: Retrieve the user by email
        Optional<UserEntity> foundUser = userRepository.findByEmail(email);

        // Assert: Verify that the user is found and details match
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(email);
        assertThat(foundUser.get().getPassword()).isEqualTo("password123");
    }

    @Test
    void itShouldNotReturnUserEntityWhenUserIfNotExists(){
        // Act: Attempt to find a user with a non-existing email
        Optional<UserEntity> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert: Verify that no user is found
        assertThat(foundUser).isNotPresent();
    }
}