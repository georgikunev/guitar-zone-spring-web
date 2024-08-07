package com.example.guitarzone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GuitarZoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuitarZoneApplication.class, args);
    }

}
