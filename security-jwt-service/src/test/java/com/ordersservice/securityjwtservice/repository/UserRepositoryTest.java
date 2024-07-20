package com.ordersservice.securityjwtservice.repository;

import com.ordersservice.securityjwtservice.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("password");
        userRepository.save(user);

        Optional<User> userOptional = userRepository.findByEmail("test@email.com");

        assertEquals(userOptional.get().getEmail(), "test@email.com");
        assertEquals(userOptional.get().getPassword(), "password");
    }
}