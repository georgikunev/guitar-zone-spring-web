package com.example.guitarzone;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GuitarZoneApplicationTests {

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void testLoadUserByUsername() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("asd@asd.com");
        assertNotNull(userDetails);
        assertEquals("asd@asd.com", userDetails.getUsername());
    }

    @Test
    void contextLoads() {
    }

}
